/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.lifecycle;

import java.util.Timer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rapfx.client.protocol.Message;
import org.rapfx.client.protocol.types.Header;
import org.rapfx.client.protocol.types.Operation;
import org.rapfx.client.protocol.types.RemoteObject;
import org.rapfx.client.protocol.types.TypeHandler;
import org.rapfx.client.transport.Transport;
import org.rapfx.client.widgets.WidgetToolkit;

/**
 * {@link LifeCycle} implementation based on HTTP and JSON. Default for RAP.
 */
public class LifeCycle {

    private static final Log log = LogFactory.getLog(LifeCycle.class);

    /**
     * Timeout after which the queue is automatically flushed if no new operations are added.
     */
    private static final int QUEUE_TIMEOUT = 100;

    /**
     * The {@link Transport} used by this {@link LifeCycle}.
     */
    private final Transport transport;

    /**
     * The {@link WidgetToolkit} used by this {@link LifeCycle}.
     */
    private final WidgetToolkit toolkit;

    /**
     * The currently pending {@link Message} that will be sent with the next {@link #flush()}.
     */
    private Message currentMessage = new Message();

    /**
     * Registry which keeps track of all supported type handlers.
     */
    private final ObjectRegistry<TypeHandler<?>> typeHandlerRegistry = new ObjectRegistry<>();

    /**
     * Registry which keeps track of all remote objects created by the server.
     */
    private final ObjectRegistry<RemoteObject> objectRegistry = new ObjectRegistry<>();

    /**
     * Internal state book-keeping helper.
     */
    private final LifeCycleState state = new LifeCycleState();

    /**
     * Runnable that flushes the current message to the server.
     */
    private final Runnable flush = new Runnable() {
        @Override
        public void run() {
            flush();
        }
    };

    /**
     * @param transport
     *            the transport to use for communication
     * @param toolkit
     *            the toolkit to use for rendering
     */
    public LifeCycle(Transport transport, WidgetToolkit toolkit) {
        this.transport = transport;
        this.toolkit = toolkit;
    }

    /**
     * @return the associated transport layer used by this {@link LifeCycle}
     */
    public Transport getTransport() {
        return transport;
    }

    /**
     * Runs the {@link LifeCycle}. This initializes the {@link WidgetToolkit} and the
     * {@link Transport} and only returns after the {@link WidgetToolkit} exits.
     */
    public void start() {
        log.debug("initializing " + this);

        // intialize toolkit (type handlers, ...)
        toolkit.initialize(this);

        // fire initial request to server to get communication started.
        Message initial = transport.get();

        for (Header header : initial.getHeaders()) {
            if (header.getName().equals("url")) {
                String value = header.getValue().toString();
                String sessionId = value.substring(value.indexOf(';') + 1);
                transport.setSessionId(sessionId);
            }
        }

        // the first message posted to the server shall carry this flag!
        currentMessage.addHeader(new Header("rwt_initialize", true));

        // signal that the lifecycle is initailzed.
        state.initialized();

        // process the initial message to get the basic requirements initialized
        // (display, ...). the toolkit might decide to delay processing until
        // more setup has been done in start().
        toolkit.dispatch(initial);

        // start the event loop of the toolkit.
        toolkit.run();
    }

    /**
     * Queues an {@link Operation} to be sent to the server on the next {@link #flush()}. A
     * {@link Timer} is started to automatically flush the message to the server if no more
     * {@link Operation}s are queued within the next few (currently 50) milliseconds.
     * 
     * @param op
     *            operation to execute remotely
     */
    public synchronized void send(Operation... ops) {
        queueUnlocked(0, ops);
    }

    /**
     * @param ops
     */
    public synchronized void queue(Operation... ops) {
        queueUnlocked(QUEUE_TIMEOUT, ops);
    }

    private void queueUnlocked(int millis, Operation... ops) {
        if (currentMessage == null) {
            currentMessage = new Message();
        }

        for (Operation op : ops) {
            currentMessage.addOperation(op);
        }

        if (millis == 0) {
            flush();
        } else {
            toolkit.execute(flush, millis);
        }
    }

    /**
     * Flushes the currently pending {@link Message} to the server, if there is one. This keeps on
     * going until the response from the server does not immediately cause widgets to flush again.
     */
    public synchronized void flush() {
        // if we are already dispatching, just request a flush for afterwards.
        if (state.isDispatching() || !state.initialized) {
            state.requestFlush();
            return;
        }

        // as long as the response causes new requests, keep going.
        do {
            state.beginDispatch();
            Message response = null;
            Message toSend;

            try {
                synchronized (this) {
                    if (currentMessage == null) {
                        return;
                    }

                    toSend = currentMessage;
                    currentMessage = null;
                }

                if (log.isTraceEnabled()) {
                    log.trace("sending: " + toSend);
                }

                response = transport.post(toSend);

                if (response != null) {
                    toolkit.dispatch(response);
                }

            } finally {
                state.endDispatch();
            }
        } while (state.isFlushRequired());
    }

    /**
     * @return whether the lifecycle is actively communicating with the server.
     */
    public boolean isDispatching() {
        return state.isDispatching();
    }

    /**
     * @return the registry for all {@link TypeHandler}s in this {@link LifeCycle}.
     *         {@link TypeHandler}s are registered by the {@link WidgetToolkit} implementation.
     */
    public ObjectRegistry<TypeHandler<?>> getTypeHandlerRegistry() {
        return typeHandlerRegistry;
    }

    /**
     * @return the registry for all remote objects created on behalf of the server.
     */
    public ObjectRegistry<RemoteObject> getObjectRegistry() {
        return objectRegistry;
    }

    /**
     * Internal state bookkeeping helper.
     */
    private final class LifeCycleState {

        private boolean initialized;
        private boolean dispatchActive;
        private boolean wantFlushAfterDispatch;

        /**
         * Signals that dispatching is now active.
         */
        public void beginDispatch() {
            dispatchActive = true;
            wantFlushAfterDispatch = false;
        }

        /**
         * Indicate that the LifeCycle's initialization is finished, which allows to start the send
         * {@link Timer} from now on.
         */
        public void initialized() {
            initialized = true;
        }

        /**
         * Indicates that dispatching is now done. This may still mean that {@link Operation}s are
         * dispatched later in another {@link Thread}, depending on the {@link WidgetToolkit}s
         * internals.
         */
        public void endDispatch() {
            dispatchActive = false;
        }

        /**
         * @return whether dispatch is active or not (see {@link #endDispatch()}).
         */
        public boolean isDispatching() {
            return dispatchActive;
        }

        /**
         * @return whether another flush is required after dispatching of the current message has
         *         finished.
         */
        public boolean isFlushRequired() {
            return wantFlushAfterDispatch;
        }

        /**
         * Requests another flush after the current dispatch cycle is done.
         */
        public void requestFlush() {
            wantFlushAfterDispatch = true;
        }

    }
}
