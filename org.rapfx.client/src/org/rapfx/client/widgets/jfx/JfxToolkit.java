/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.widgets.jfx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rapfx.client.ApplicationGlobals;
import org.rapfx.client.lifecycle.LifeCycle;
import org.rapfx.client.lifecycle.ObjectRegistry;
import org.rapfx.client.lifecycle.OperationDispatcher;
import org.rapfx.client.protocol.Message;
import org.rapfx.client.protocol.types.Operation;
import org.rapfx.client.protocol.types.RemoteObject;
import org.rapfx.client.protocol.types.TypeHandler;
import org.rapfx.client.protocol.types.operations.CreateOperation;
import org.rapfx.client.widgets.WidgetToolkit;
import org.rapfx.client.widgets.jfx.JfxTrayIcon.Icon;
import org.rapfx.client.widgets.jfx.impl.JfxButton;
import org.rapfx.client.widgets.jfx.impl.JfxComposite;
import org.rapfx.client.widgets.jfx.impl.JfxDisplay;
import org.rapfx.client.widgets.jfx.impl.JfxLabel;
import org.rapfx.client.widgets.jfx.impl.JfxShell;
import org.rapfx.client.widgets.jfx.impl.JfxText;

/**
 * Implementation of the JavaFX frontend ({@link WidgetToolkit})
 */
public class JfxToolkit implements WidgetToolkit {

    private static final Log log = LogFactory.getLog(JfxToolkit.class);

    /**
     * The dispatcher responsible for handling server responses.
     */
    private OperationDispatcher dispatcher;

    /**
     * All registered runnables and their associated executing timeline.
     */
    private final Map<Runnable, JfxRunAtTimeline> runQueue = new HashMap<>();

    /**
     * Collection of {@link Message}s received before the actual JavaFX framework has been started
     */
    private final List<Message> initialMessages = new ArrayList<>();

    /**
     * Keeps track of the initizlization state of the JavaFX framework.
     */
    private final AtomicBoolean jfxInitialized = new AtomicBoolean(false);

    /**
     * The lifecycle that manages this {@link WidgetToolkit}.
     */
    private LifeCycle lifecycle;

    @Override
    public void initialize(LifeCycle lifecycle) {
        // initialize the lifecycle dependent parts of the toolkit.
        this.dispatcher = new OperationDispatcher(lifecycle);
        this.lifecycle = lifecycle;

        JfxApplication.initTray();

        // initialize the handlers (may already require lifecycle to be set!)
        ObjectRegistry<TypeHandler<? extends RemoteObject>> handlers = lifecycle
                .getTypeHandlerRegistry();

        // register handlers for global objects.
        handlers.set(JfxClientInfo.Handler.ID, new JfxClientInfo.Handler());
        handlers.set(JfxThemeStore.Handler.ID, new JfxThemeStore.Handler());
        handlers.set(JfxTextMeasurement.Handler.ID, new JfxTextMeasurement.Handler());

        // register handlers for widgets
        handlers.set(JfxDisplay.Handler.ID, new JfxDisplay.Handler());
        handlers.set(JfxShell.Handler.ID, new JfxShell.Handler());
        handlers.set(JfxComposite.Handler.ID, new JfxComposite.Handler());
        handlers.set(JfxLabel.Handler.ID, new JfxLabel.Handler());
        handlers.set(JfxButton.Handler.ID, new JfxButton.Handler());
        handlers.set(JfxText.Handler.ID, new JfxText.Handler());

        // create global singletons
        createSingleton(JfxClientInfo.Handler.ID);
        createSingleton(JfxThemeStore.Handler.ID);
        createSingleton(JfxTextMeasurement.Handler.ID);
    }

    /**
     * @param id
     *            the ID of the type (and object) to create
     */
    private void createSingleton(String id) {
        dispatcher.dispatch(new CreateOperation(id, id, null));
    }

    /**
     * @return the {@link LifeCycle} associated with this {@link JfxToolkit}. This is only valid
     *         after {@link #initialize(LifeCycle)} has been called.
     */
    public LifeCycle getLifeCycle() {
        return lifecycle;
    }

    @Override
    public void run() {
        Application.launch(JfxApplication.class, (String) null);
    }

    @Override
    public void dispatch(final Message message) {
        if (!jfxInitialized.get()) {
            initialMessages.add(message);
            return;
        }

        if (log.isTraceEnabled()) {
            log.trace("dispatching: " + message);
        }

        for (Operation op : message.getOperations()) {
            try {
                if (!dispatcher.dispatch(op)) {
                    log.warn("unable to handle " + op);
                }
            } catch (Exception ex) {
                log.error("exception while dispatching operation=" + op, ex);
            }
        }

    }

    @Override
    public void execute(Runnable runnable, int millis) {
        synchronized (runQueue) {
            JfxRunAtTimeline timeline = runQueue.get(runnable);
            if (timeline == null) {
                timeline = new JfxRunAtTimeline(this);
            }

            timeline.runIn(runnable, millis);
        }
    }

    void removeAndRun(Runnable runnable) {
        synchronized (runQueue) {
            runQueue.remove(runnable);
        }

        runnable.run();
    }

    /**
     * The JavaFX {@link Application} implementation that actually only signals startup success for
     * the JavaFX Application Thread.
     * <p>
     * When starting, all initially pending {@link Message}s from the {@link JfxToolkit} are
     * dispatched by this implementation
     */
    public static class JfxApplication extends Application {
        private static final int SHUTDOWN_INTERVAL = 2;
        private static final int SHUTDOWN_TIMEOUT = 10;
        private static JfxTrayIcon trayIcon;
        private static int shutdownCounter = SHUTDOWN_TIMEOUT;
        private static Timer timer;

        @Override
        public void start(Stage primaryStage) throws Exception {
            JfxToolkit tk = (JfxToolkit) ApplicationGlobals.getInstance().getToolkit();
            tk.jfxInitialized.set(true);
            for (Message msg : tk.initialMessages) {
                tk.dispatch(msg);
            }
            tk.initialMessages.clear();

            Platform.setImplicitExit(false);
        }

        /**
         * Create a tray icon which displays the application status to the user. This icon also
         * allows to exit the client in case of a broken/missing/whatever UI.
         */
        private static void initTray() {
            trayIcon = new JfxTrayIcon();
            resetStatus();

            timer = new Timer(true);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    checkStatus();
                }
            }, SHUTDOWN_INTERVAL, TimeUnit.SECONDS.toMillis(SHUTDOWN_INTERVAL));
        }

        /**
         * Checks whether the application is in a "working" state, and/or there are open shells
         * left. Having no shells and no request in progress means that the user will very likely
         * not be able to continue, so start a count-down to exit the application in that case.
         */
        private static void checkStatus() {
            if (ApplicationGlobals.getInstance().getLifeCycle().isDispatching()) {
                resetStatus();
                return;
            }

            for (Map.Entry<String, ?> entry : ApplicationGlobals.getInstance().getLifeCycle()
                    .getObjectRegistry().getAll()) {
                if (entry.getValue() instanceof JfxShell) {
                    if (((JfxShell) entry.getValue()).getNode().getScene().getWindow().isShowing()) {
                        resetStatus();
                        return;
                    }
                }
            }

            if (shutdownCounter <= 0) {
                trayIcon.hide();
                timer.cancel();
                Platform.exit();
                return;
            }

            String tooltip = "RAP Client: No more Windows, shutting down in " + shutdownCounter
                    + "s.";
            trayIcon.update(Icon.WARNING, tooltip);
            log.info(tooltip);

            shutdownCounter -= SHUTDOWN_INTERVAL;
        }

        private static void resetStatus() {
            trayIcon.update(Icon.NETWORK, "RAP Client operational");
            shutdownCounter = SHUTDOWN_TIMEOUT;
        }
    }

}
