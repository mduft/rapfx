/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.widgets.jfx;

import java.util.Map;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

import org.rapfx.client.protocol.theme.ThemeTypeHelper;
import org.rapfx.client.protocol.types.RemoteObject;
import org.rapfx.client.protocol.types.TypeHandler;

/**
 * Base class for objects that manage JavaFX {@link Node} subclasses. Implement handling for common
 * properties of those {@link Node}s
 * 
 * @param <T>
 *            the type of the managed {@link Node}
 */
public abstract class JfxNodeObject<T extends Node> extends JfxBaseObject<T> {

    private JfxBaseObject<Pane> parent;

    @Override
    public void initialize(TypeHandler<? extends RemoteObject> handler, String targetId,
            Map<String, ?> properties) {
        super.initialize(handler, targetId, properties);
        initializeStyleClass();
    }

    /**
     * Initializes the style class on this node. Subclasses may override to implement special
     * treatment for nodes snd (anonymous) subnodes.
     * <p>
     * The default implementation initializes the style class of the created node to the default
     * class for this object, returned by getStyleClass.
     */
    protected void initializeStyleClass() {
        getNode().getStyleClass().add(getStyleClass());
    }

    /**
     * Re-parents the managed {@link Node}.
     * 
     * @param parentId
     *            the ID of the new parent
     */
    public void setParent(String parentId) {
        if (parent != null) {
            parent.getChildren().remove(getNode());
        }

        parent = getLifeCycle().getObjectRegistry().get(parentId);
        if (parent == null) {
            throw new IllegalStateException("parent " + parentId + " of " + this
                    + " does not exist");
        }

        Pane node = parent.getNode();
        node.getChildren().add(getNode());
    }

    /**
     * @param enabled
     *            the enabled state of this node.
     */
    public void setEnabled(Boolean enabled) {
        getNode().setDisable(!enabled);
    }

    /**
     * Sets the background color of the managed node to the given value.
     * 
     * @param raw
     *            the raw protocol value
     */
    public void setBackground(Object raw) {
        String rgba = ThemeTypeHelper.toColorString(raw);
        getNode().setStyle("-fx-background-color: " + rgba + ";");
    }

    /**
     * Shows or hides the node.
     * 
     * @param visible
     *            <code>true</code> is node should be visible, <code>false</code> to hide.
     */
    public void setVisibility(Boolean visible) {
        getNode().setVisible(visible);
    }

    /**
     * Requests that the managed {@link Node} receives the input focus.
     */
    public void setFocus() {
        getNode().requestFocus();
    }

    /**
     * @return the style class name for this object.
     */
    protected String getStyleClass() {
        return JfxNodeHandler.getStyleClass(this.getClass(), getStyle().getAllStyles());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " (" + getObjectId() + ")";
    }
}
