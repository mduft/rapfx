/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.widgets.jfx.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.GridPaneBuilder;
import javafx.scene.layout.Pane;
import javafx.scene.layout.PaneBuilder;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rapfx.client.protocol.theme.ThemeTypeHelper;
import org.rapfx.client.protocol.theme.ThemeValues.BorderDefinition;
import org.rapfx.client.protocol.types.RemoteObject;
import org.rapfx.client.protocol.types.operations.NotifyOperation;
import org.rapfx.client.protocol.types.operations.SetOperation;
import org.rapfx.client.widgets.jfx.JfxBoundSetOperation;
import org.rapfx.client.widgets.jfx.JfxNodeHandler;
import org.rapfx.client.widgets.jfx.JfxNodeObject;
import org.rapfx.client.widgets.jfx.JfxTypeHelper;
import org.rapfx.client.widgets.jfx.impl.JfxStyleHolder.Style;
import org.rapfx.client.widgets.jfx.theming.JfxSsClass;
import org.rapfx.client.widgets.jfx.theming.JfxSsDirectMapping;
import org.rapfx.client.widgets.jfx.theming.JfxSsType;
import org.rapfx.client.widgets.jfx.theming.JfxStylesheet;

/**
 * JavaFX implementation of a Shell, combining {@link Stage}, {@link Scene} and {@link Pane} to
 * model it.
 */
public class JfxShell extends JfxNodeObject<Pane> {

    private static final int MOVE_SNAP_DISTANCE = 30;
    private static final int RESIZE_HANDLE_WIDTH = 3;
    private static final String TITLE_BAR_CLASS = "JfxShell-TitleBar";
    private static final String TITLE_BAR_ACTIVE_CLASS = "JfxShell-TitleBar-Active";
    private static final String TITLE_BAR_LBL_CLASS = "JfxShell-TitleBar-Label";
    private static final String TITLE_BAR_MIN_CLASS = "JfxShell-TitleBar-Min";
    private static final String TITLE_BAR_MAX_CLASS = "JfxShell-TitleBar-Max";
    private static final String TITLE_BAR_CLOSE_CLASS = "JfxShell-TitleBar-Close";
    private static final int MIN_TITLE_HEIGHT = 20;

    private static final Log log = LogFactory.getLog(JfxShell.class);

    private final Pane root = new Pane();
    private final Pane clientArea = new Pane();
    private final ShellEventHandler eventHandler = new ShellEventHandler();
    private final ShellPropertyListener sizeListener = new ShellPropertyListener("Resize");
    private final ShellPropertyListener moveListener = new ShellPropertyListener("Move");
    private final ShellFocusListener focusListener = new ShellFocusListener();
    private final MaximizeHandler maximizeHandler = new MaximizeHandler();

    private Stage stage;
    private BorderDefinition border;
    private GridPane title;
    private Point2D minimumSize;

    private Pane resizeSouth;
    private Pane resizeNorth;
    private Pane resizeWest;
    private Pane resizeEast;
    private Pane resizeSouthEast;

    @Override
    protected Pane createNode(JfxStyleHolder style) {
        stage = new Stage(StageStyle.TRANSPARENT);
        stage.setScene(new Scene(root));
        stage.getScene().setFill(Color.TRANSPARENT);
        root.getChildren().add(clientArea);

        try {
            stage.getScene().getStylesheets()
                    .add(getStylesheet().getTemporary().toURI().toString());
        } catch (IOException e) {
            log.warn("error loading stylesheet for " + this, e);
        }

        if (style.hasStyle(Style.SYSTEM_MODAL)) {
            stage.initModality(Modality.APPLICATION_MODAL);
        }

        stage.setOnCloseRequest(eventHandler);
        stage.setOnShown(eventHandler);

        stage.widthProperty().addListener(sizeListener);
        stage.heightProperty().addListener(sizeListener);
        stage.xProperty().addListener(moveListener);
        stage.yProperty().addListener(moveListener);
        stage.focusedProperty().addListener(focusListener);
        border = ThemeTypeHelper.toBorderDefinition(Handler.borderAttr.getRawValue(getTheme()));

        if (border == null) {
            border = new BorderDefinition();
        }

        if (style.hasStyle(Style.TITLE)) {
            createTitleBar(root, style);
        }

        Rectangle2D radii = JfxTypeHelper.toRectangle(Handler.radiusAttr.getRawValue(getTheme()));
        int arc = radii == null ? 0 : (int) radii.getMinX();
        createStageClip(root, arc);

        clientArea.prefHeightProperty().bind(root.heightProperty().subtract(border.width * 2));
        clientArea.prefWidthProperty().bind(root.widthProperty().subtract(border.width * 2));
        clientArea.setLayoutX(border.width);
        clientArea.setLayoutY(border.width);

        if (style.hasStyle(Style.RESIZE)) {
            createResizeHandles(root);
        }

        root.getStyleClass().add(getStyleClass());
        clientArea.getStyleClass().add(JfxNodeHandler.getStyleClass(JfxComposite.class));

        return clientArea;
    }

    private void createResizeHandles(Pane parent) {
        PaneBuilder<?> horizBuilder = PaneBuilder.create().prefHeight(RESIZE_HANDLE_WIDTH)
                .layoutX(0).layoutY(0).cursor(Cursor.S_RESIZE).mouseTransparent(false);
        PaneBuilder<?> vertBuilder = PaneBuilder.create().prefWidth(RESIZE_HANDLE_WIDTH).layoutX(0)
                .layoutY(0).cursor(Cursor.E_RESIZE).mouseTransparent(false);

        resizeSouth = horizBuilder.build();
        resizeSouth.prefWidthProperty().bind(parent.widthProperty());
        resizeSouth.layoutYProperty().bind(parent.heightProperty().subtract(RESIZE_HANDLE_WIDTH));

        resizeNorth = horizBuilder.build();
        resizeNorth.prefWidthProperty().bind(parent.widthProperty());

        resizeEast = vertBuilder.build();
        resizeEast.prefHeightProperty().bind(parent.heightProperty());
        resizeEast.layoutXProperty().bind(parent.widthProperty().subtract(RESIZE_HANDLE_WIDTH));

        resizeWest = vertBuilder.build();
        resizeWest.prefHeightProperty().bind(parent.heightProperty());

        resizeSouthEast = PaneBuilder.create().prefHeight(RESIZE_HANDLE_WIDTH * 2)
                .prefWidth(RESIZE_HANDLE_WIDTH * 2).cursor(Cursor.SE_RESIZE)
                .mouseTransparent(false).build();
        resizeSouthEast.layoutXProperty().bind(
                parent.widthProperty().subtract(RESIZE_HANDLE_WIDTH * 2));
        resizeSouthEast.layoutYProperty().bind(
                parent.heightProperty().subtract(RESIZE_HANDLE_WIDTH * 2));

        parent.getChildren().addAll(resizeSouth, resizeNorth, resizeEast, resizeWest,
                resizeSouthEast);

        new ResizeDragHandler(resizeNorth);
        new ResizeDragHandler(resizeEast);
        new ResizeDragHandler(resizeSouthEast);
        new ResizeDragHandler(resizeSouth);
        new ResizeDragHandler(resizeWest);

        updateResizeHandles();
    }

    /**
     * Re-calculates visibility of resize handles based on maximized state of the shell
     */
    private void updateResizeHandles() {
        boolean maximized = isMaximized(getScreenFor(stage.getX(), stage.getY()));
        resizeSouth.setVisible(!maximized);
        resizeNorth.setVisible(!maximized);
        resizeEast.setVisible(!maximized);
        resizeWest.setVisible(!maximized);
        resizeSouthEast.setVisible(!maximized);
    }

    @Override
    protected void initializeStyleClass() {
        // need special treatment for components of a shell. see above.
    }

    /**
     * Sets the parent of this {@link JfxShell}. The given widget must be another {@link JfxShell},
     * and this method may only be called before this {@link JfxShell}s {@link Stage} becomes
     * visible.
     * 
     * @param parentId
     *            the remote object id of the parent shell.
     */
    public void setParentShell(String parentId) {
        JfxShell parent = getLifeCycle().getObjectRegistry().get(parentId);
        stage.initOwner(parent.stage);
    }

    private void createStageClip(Pane client, int arc) {
        Rectangle rect = new Rectangle();
        rect.widthProperty().bind(client.widthProperty());
        rect.heightProperty().bind(client.heightProperty());

        rect.setArcHeight(arc);
        rect.setArcWidth(arc);

        client.setClip(rect);
    }

    private void createTitleBar(Pane parent, JfxStyleHolder style) {
        Integer height = ThemeTypeHelper.toDimension(Handler.tbHeightAttr.getRawValue(getTheme()));

        if (height == null || height < MIN_TITLE_HEIGHT) {
            height = MIN_TITLE_HEIGHT;
        }

        title = GridPaneBuilder.create().layoutX(border.width).layoutY(border.width)
                .prefHeight(height).build();
        title.getStyleClass().add(TITLE_BAR_CLASS);
        title.prefWidthProperty().bind(parent.widthProperty().subtract(border.width * 2));
        createTitleDragHandlers(title);
        parent.getChildren().add(title);

        // create real contents
        createTitleLabel(title);
        createTitleButtons(title, style);
    }

    private void createTitleLabel(final GridPane title) {
        final Label text = new Label();
        text.textProperty().bind(stage.titleProperty());
        text.setPrefHeight(title.getPrefHeight());
        text.getStyleClass().add(TITLE_BAR_LBL_CLASS);
        title.getChildren().add(text);
        GridPane.setHgrow(text, Priority.ALWAYS);
    }

    private void createTitleButtons(final GridPane title, JfxStyleHolder style) {
        final Label minBtn = new Label();
        final Label maxBtn = new Label();
        final Label closeBtn = new Label();

        minBtn.getStyleClass().add(TITLE_BAR_MIN_CLASS);
        maxBtn.getStyleClass().add(TITLE_BAR_MAX_CLASS);
        closeBtn.getStyleClass().add(TITLE_BAR_CLOSE_CLASS);

        title.getChildren().add(minBtn);
        title.getChildren().add(maxBtn);
        title.getChildren().add(closeBtn);

        GridPane.setColumnIndex(minBtn, 1);
        GridPane.setColumnIndex(maxBtn, 2);
        GridPane.setColumnIndex(closeBtn, 3);

        minBtn.setVisible(style.hasStyle(Style.MIN));
        maxBtn.setVisible(style.hasStyle(Style.MAX));
        closeBtn.setVisible(style.hasStyle(Style.CLOSE));

        minBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setIconified(true);
            }
        });

        maxBtn.setOnMouseClicked(maximizeHandler);

        closeBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.close();
            }
        });

        title.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() > 1) {
                    event.consume();
                    maxBtn.fireEvent(event);
                }
            }
        });
    }

    private void createTitleDragHandlers(final Pane title) {
        final Delta initialPos = new Delta();
        final Delta delta = new Delta();

        title.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                initialPos.x = event.getScreenX();
                initialPos.y = event.getScreenY();
                delta.x = stage.getX() - event.getScreenX();
                delta.y = stage.getY() - event.getScreenY();
            }
        });
        title.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double screenX = event.getScreenX();
                double screenY = event.getScreenY();

                boolean maximized = isMaximized(getScreenFor(screenX, screenY));
                if (!maximized || isOutsideSnap(screenX, initialPos.x)
                        || isOutsideSnap(screenY, initialPos.y)) {

                    if (maximized) {
                        // restore bounds and place under cursor.
                        maximizeHandler.restore();
                        delta.x = -(stage.getWidth() / 2.0);
                    }

                    stage.setX(screenX + delta.x);
                    stage.setY(screenY + delta.y);
                }
            }

            private boolean isOutsideSnap(double screen, double initial) {
                return (screen < initial && screen + MOVE_SNAP_DISTANCE < initial)
                        || (screen > initial && screen - MOVE_SNAP_DISTANCE > initial);
            }
        });
    }

    private Screen getScreenFor(double x, double y) {
        ObservableList<Screen> screens = Screen.getScreensForRectangle(x, y, 1, 1);
        if (screens.isEmpty()) {
            return Screen.getPrimary();
        }
        return screens.get(0);
    }

    private boolean isMaximized(Screen screen) {
        Rectangle2D bounds = screen.getVisualBounds();
        return bounds.getMinX() == stage.getX() && bounds.getMinY() == stage.getY()
                && bounds.getWidth() == stage.getWidth() && bounds.getHeight() == stage.getHeight();
    }

    /**
     * @param text
     *            the text that should be used as title for the {@link Stage}
     */
    public void setText(String text) {
        stage.setTitle(text);
    }

    /**
     * Sets the bounds of the client area of this {@link JfxShell}.
     * 
     * @param raw
     *            the raw protocol value for the bounds.
     */
    public void setBounds(Object raw) {
        Rectangle2D bounds = JfxTypeHelper.toRectangle(raw);
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        root.setPrefSize(bounds.getWidth(), bounds.getHeight());

        stage.sizeToScene();
    }

    /**
     * @param active
     *            brings the current {@link Stage} to the front.
     */
    public void setActive(Boolean active) {
        if (active) {
            stage.toFront();
        }
    }

    /**
     * sets the given control to be "active". In this implementation, this means that the control
     * receives the input focus.
     * 
     * @param id
     *            the id of the control to be active
     */
    public void setActiveControl(String id) {
        RemoteObject object = getLifeCycle().getObjectRegistry().get(id);
        if (object != null && object instanceof JfxNodeObject<?>) {
            ((JfxNodeObject<?>) object).setFocus();
        } else {
            log.debug("cannot put focus to " + id + " (" + object + ")");
        }
    }

    /**
     * Sets the minimum size of the {@link Stage}, preventing it from getting smaller than this.
     * 
     * @param the
     *            raw protocol value for the minimum size
     */
    public void setMinimumSize(Object raw) {
        Point2D sz = JfxTypeHelper.toPoint(raw);
        if (sz != null) {
            minimumSize = sz;
        }
    }

    /**
     * @param visible
     *            whether the {@link Stage} should be shown or hidden.
     */
    @Override
    public void setVisibility(Boolean visible) {
        if (visible) {
            stage.show();
        } else {
            stage.hide();
        }
    }

    public void setMode(String mode) {
        log.debug("need setMode implementation");
    }

    /**
     * Small helper to store coordinate deltas.
     */
    private final static class Delta {
        double x, y;
    }

    /**
     * Handler for resize handles. Calculates new size and position of the stage based on drag
     * events.
     */
    private final class ResizeDragHandler implements EventHandler<MouseEvent> {

        final Pane handle;
        final Delta dragStart = new Delta();

        private final class ResizeDragStartHandler implements EventHandler<MouseEvent> {
            @Override
            public void handle(MouseEvent event) {
                dragStart.x = event.getScreenX();
                dragStart.y = event.getScreenY();
            }
        }

        public ResizeDragHandler(Pane handle) {
            this.handle = handle;

            handle.setOnMouseDragged(this);
            handle.setOnMousePressed(new ResizeDragStartHandler());
        }

        @Override
        public void handle(MouseEvent event) {
            Delta diff = new Delta();
            diff.x = event.getScreenX() - dragStart.x;
            diff.y = event.getScreenY() - dragStart.y;

            Delta newPos = new Delta();
            Delta newSize = new Delta();

            newPos.x = stage.getX();
            newPos.y = stage.getY();
            newSize.x = stage.getWidth();
            newSize.y = stage.getHeight();

            if (handle == resizeNorth) {
                newPos.y += diff.y;
                newSize.y -= diff.y;
            } else if (handle == resizeEast) {
                newSize.x += diff.x;
            } else if (handle == resizeSouthEast) {
                newSize.x += diff.x;
                newSize.y += diff.y;
            } else if (handle == resizeSouth) {
                newSize.y += diff.y;
            } else if (handle == resizeWest) {
                newPos.x += diff.x;
                newSize.x -= diff.x;
            }

            if (minimumSize.getX() <= newSize.x) {
                stage.setX(newPos.x);
                stage.setWidth(newSize.x);
            }

            if (minimumSize.getY() <= newSize.y) {
                stage.setY(newPos.y);
                stage.setHeight(newSize.y);
            }

            dragStart.x = event.getScreenX();
            dragStart.y = event.getScreenY();
        }

    }

    /**
     * Handler for the maximize button. Also used for restoring when dragging out of maximized snap.
     */
    private final class MaximizeHandler implements EventHandler<MouseEvent> {
        private Rectangle2D backupWindowBounds;

        @Override
        public void handle(MouseEvent event) {
            final Screen screen = getScreenFor(event.getX(), event.getY());
            if (isMaximized(screen)) {
                restore();
            } else {
                maximize(screen);
            }
        }

        void maximize(final Screen screen) {
            backupWindowBounds = new Rectangle2D(stage.getX(), stage.getY(), stage.getWidth(),
                    stage.getHeight());
            stage.setX(screen.getVisualBounds().getMinX());
            stage.setY(screen.getVisualBounds().getMinY());
            root.setPrefWidth(screen.getVisualBounds().getWidth());
            root.setPrefHeight(screen.getVisualBounds().getHeight());
            update();
        }

        void restore() {
            if (backupWindowBounds != null) {
                stage.setX(backupWindowBounds.getMinX());
                stage.setY(backupWindowBounds.getMinY());
                root.setPrefWidth(backupWindowBounds.getWidth());
                root.setPrefHeight(backupWindowBounds.getHeight());
                update();
            }
        }

        void update() {
            stage.sizeToScene();
            updateResizeHandles();
        }
    }

    /**
     * Handler for events occurring on the {@link Stage}
     */
    private class ShellEventHandler implements EventHandler<WindowEvent> {

        @Override
        public void handle(WindowEvent event) {
            if (event.getEventType() == WindowEvent.WINDOW_CLOSE_REQUEST && isEventEnabled("Close")) {
                NotifyOperation op = new NotifyOperation(getObjectId(), "Close", null);
                getLifeCycle().send(op);
            }
        }

    }

    /**
     * Listener that notifies about size and location updates on the {@link Stage}
     */
    private class ShellPropertyListener implements ChangeListener<Number> {

        private final String event;

        public ShellPropertyListener(String event) {
            this.event = event;
        }

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue,
                Number newValue) {
            SetOperation set = new JfxBoundSetOperation(Integer.class, getObjectId(),
                    Collections.singletonMap("bounds", new ObservableValue<?>[] {
                            stage.xProperty(), stage.yProperty(), clientArea.widthProperty(),
                            clientArea.heightProperty() }));
            NotifyOperation notify = new NotifyOperation(getObjectId(), event, null);
            getLifeCycle().queue(set, notify);
        }

    }

    /**
     * Handles activation/de-activation of the Stage. Must set style classes for the titlebar
     * manually, since there is no pseudo-class for stage focus.
     */
    private class ShellFocusListener implements ChangeListener<Boolean> {

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
                Boolean newValue) {

            if (title != null) {
                title.getStyleClass().remove(newValue ? TITLE_BAR_CLASS : TITLE_BAR_ACTIVE_CLASS);
                title.getStyleClass().add(newValue ? TITLE_BAR_ACTIVE_CLASS : TITLE_BAR_CLASS);
            }

            if (newValue && isEventEnabled("Activate")) {
                NotifyOperation op = new NotifyOperation(getObjectId(), "Activate", null);
                getLifeCycle().send(op);
            }
        }

    }

    public static class Handler extends JfxNodeHandler<JfxShell> {

        public static final String ID = "rwt.widgets.Shell";
        private static JfxSsDirectMapping radiusAttr;
        private static JfxSsDirectMapping borderAttr;
        private static JfxSsDirectMapping tbHeightAttr;

        @Override
        public JfxShell create(String target, Map<String, ?> properties) {
            return new JfxShell();
        }

        @Override
        public void destroy(JfxShell object) {
            object.setVisibility(false);
        }

        @Override
        public void registerThemeContributions() {
            Set<Style> noStyle = Collections.emptySet();
            Set<String> hoverPseudo = Collections.singleton(":hover");

            JfxStylesheet.setRelevantStyles(getStyleClass(JfxShell.class),
                    EnumSet.of(Style.BORDER, Style.TITLE));

            // Shell Main Class
            JfxSsClass defClass = JfxStylesheet.get(JfxShell.class);
            radiusAttr = new JfxSsDirectMapping(defClass, JfxSsType.BORDER_RADIUS, "Shell",
                    "border-radius");

            defClass.map(JfxSsType.COLOR, "Shell", "background-color");
            defClass.set(radiusAttr);

            // Shell with Style BORDER
            JfxSsClass borderClass = JfxStylesheet.get(JfxShell.class, EnumSet.of(Style.BORDER));
            borderClass.inherit(defClass);
            borderAttr = new JfxSsDirectMapping(borderClass, JfxSsType.BORDER_WIDTH, "Shell",
                    "border", "-fx-border-width");

            // split border attribute of RAP CSS to the three equivalent attributes in JavaFX
            borderClass.map(JfxSsType.BORDER_TYPE, "Shell", "border", "-fx-border-type");
            borderClass.map(JfxSsType.BORDER_COLOR, "Shell", "border", "-fx-border-color");
            borderClass.set(borderAttr);

            // Shell with Style TITLE
            JfxSsClass titleClass = JfxStylesheet.get(JfxShell.class, EnumSet.of(Style.TITLE));
            titleClass.inherit(borderClass);

            // Shell Title Bar style
            JfxSsClass tbClass = JfxStylesheet.get(TITLE_BAR_CLASS);
            // reuse the border radius (top only!) from the shell, to align properly.
            tbClass.map(JfxSsType.TOP_ONLY_BORDER_RADIUS, "Shell", "border-radius");
            tbClass.map(JfxSsType.COLOR, "Shell-Titlebar", "background-color");
            tbClass.map(JfxSsType.BACKGROUND_COLOR_GRADIENT, "Shell-Titlebar", "background-image",
                    "-fx-background-color", Collections.singleton(":inactive"));
            tbClass.map(JfxSsType.BOX, "Shell-Titlebar", "padding");

            JfxSsClass tbActiveClass = JfxStylesheet.get(TITLE_BAR_ACTIVE_CLASS);
            tbActiveClass.inherit(tbClass);
            tbActiveClass.map(JfxSsType.BACKGROUND_COLOR_GRADIENT, "Shell-Titlebar",
                    "background-image", "-fx-background-color", Collections.<String> emptySet());

            tbHeightAttr = new JfxSsDirectMapping(tbClass, JfxSsType.DIMENSION_PX,
                    "Shell-Titlebar", "height");

            JfxSsClass tbLblClass = JfxStylesheet.get(TITLE_BAR_LBL_CLASS);
            tbLblClass.map(JfxSsType.FONT, "Shell-Titlebar", "font");
            tbLblClass.map(JfxSsType.COLOR, "Shell-Titlebar", "color", "-fx-text-fill");

            JfxSsClass tbMin = JfxStylesheet.get(TITLE_BAR_MIN_CLASS);
            JfxSsClass tbMax = JfxStylesheet.get(TITLE_BAR_MAX_CLASS);
            JfxSsClass tbClose = JfxStylesheet.get(TITLE_BAR_CLOSE_CLASS);

            tbMin.map(JfxSsType.IMAGE_URL, "Shell-MinButton", "background-image", "-fx-graphic");
            tbMax.map(JfxSsType.IMAGE_URL, "Shell-MaxButton", "background-image", "-fx-graphic");
            tbClose.map(JfxSsType.IMAGE_URL, "Shell-CloseButton", "background-image", "-fx-graphic");

            JfxSsClass tbMinHover = JfxStylesheet.get(TITLE_BAR_MIN_CLASS, noStyle, hoverPseudo);
            JfxSsClass tbMaxHover = JfxStylesheet.get(TITLE_BAR_MAX_CLASS, noStyle, hoverPseudo);
            JfxSsClass tbCloseHover = JfxStylesheet
                    .get(TITLE_BAR_CLOSE_CLASS, noStyle, hoverPseudo);

            tbMinHover.map(JfxSsType.IMAGE_URL, "Shell-MinButton", "background-image",
                    "-fx-graphic");
            tbMaxHover.map(JfxSsType.IMAGE_URL, "Shell-MaxButton", "background-image",
                    "-fx-graphic");
            tbCloseHover.map(JfxSsType.IMAGE_URL, "Shell-CloseButton", "background-image",
                    "-fx-graphic");
        }
    }

}
