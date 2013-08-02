/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.widgets.jfx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.rapfx.client.ApplicationGlobals;
import org.rapfx.client.lifecycle.LifeCycle;
import org.rapfx.client.lifecycle.ObjectRegistry;
import org.rapfx.client.protocol.theme.Theme;
import org.rapfx.client.protocol.types.AbstractRemoteObject;
import org.rapfx.client.protocol.types.RemoteObject;
import org.rapfx.client.protocol.types.TypeHandler;
import org.rapfx.client.widgets.jfx.impl.JfxStyleHolder;
import org.rapfx.client.widgets.jfx.theming.JfxStylesheet;

/**
 * Base class for all {@link RemoteObject}s that represent a Widget or Control.
 */
public abstract class JfxBaseObject<T> extends AbstractRemoteObject {

    private List<String> children;
    private int tabIndex = -1;
    private T node;
    private JfxStyleHolder style;

    @SuppressWarnings("unchecked")
    @Override
    public void initialize(TypeHandler<? extends RemoteObject> handler, String targetId,
            Map<String, ?> properties) {
        super.initialize(handler, targetId, properties);

        // if there is a style, we need it during creation.
        setStyle(properties.get("style"));
        properties.remove("style");

        // create the real control.
        node = createNode(getStyle());

        // set the rest of the properties as if a SetOperation was fired.
        ((TypeHandler<JfxBaseObject<T>>) handler).set(this, properties);
    }

    /**
     * Create the "real" control managed by this object
     * 
     * @param style
     *            the initial style of the control
     * @return responsible for creating the managed node.
     */
    protected abstract T createNode(JfxStyleHolder style);

    /**
     * @return the managed node.
     */
    protected T getNode() {
        return node;
    }

    /**
     * @return shortcut to retrieve the active {@link LifeCycle}
     */
    protected LifeCycle getLifeCycle() {
        return ApplicationGlobals.getInstance().getLifeCycle();
    }

    /**
     * @param children
     *            all immediate children of this object.
     */
    public void setChildren(List<String> children) {
        this.children = children;
    }

    /**
     * @return the immediate children as object ids
     */
    public List<String> getChildren() {
        if (children == null) {
            return Collections.emptyList();
        }

        return children;
    }

    /**
     * @return the immediate children as {@link RemoteObject}s
     */
    public List<RemoteObject> getChildrenAsObjects() {
        List<RemoteObject> result = new ArrayList<>();
        ObjectRegistry<RemoteObject> objectRegistry = getLifeCycle().getObjectRegistry();
        for (String child : getChildren()) {
            RemoteObject o = objectRegistry.get(child);
            if (o == null) {
                throw new IllegalStateException("child " + child + " of " + this
                        + " does not (yet?) exist");
            }
            result.add(o);
        }
        return result;
    }

    /**
     * @param index
     *            the tab index of this control in the UI.
     */
    public void setTabIndex(Double index) {
        tabIndex = index.intValue();
    }

    /**
     * @return the tab index of this control in the UI. A negative return means to skip this
     *         control.
     */
    public int getTabIndex() {
        return tabIndex;
    }

    /**
     * @param value
     *            the new style that should be applied.
     */
    public void setStyle(Object value) {
        style = JfxTypeHelper.toStyle(value);
    }

    /**
     * @return the current {@link JfxStyleHolder} associated with this object
     */
    protected JfxStyleHolder getStyle() {
        return style;
    }

    /**
     * Shortcut to retrieve the current Theme.
     * 
     * @return the current {@link Theme}
     */
    protected Theme getTheme() {
        JfxThemeStore store = getLifeCycle().getObjectRegistry().get(JfxThemeStore.Handler.ID);
        return store.getTheme();
    }

    /**
     * Shortcut to retrieve the current {@link Theme}s {@link JfxStylesheet}.
     * 
     * @return the {@link JfxStylesheet} for the current theme.
     */
    protected JfxStylesheet getStylesheet() {
        JfxThemeStore store = getLifeCycle().getObjectRegistry().get(JfxThemeStore.Handler.ID);
        return store.getStylesheet();
    }

}
