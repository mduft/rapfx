/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.widgets.jfx.impl;

import java.util.Map;

import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Pane;

import org.rapfx.client.widgets.jfx.JfxNodeHandler;
import org.rapfx.client.widgets.jfx.JfxNodeObject;
import org.rapfx.client.widgets.jfx.JfxTypeHelper;
import org.rapfx.client.widgets.jfx.theming.JfxSsClass;
import org.rapfx.client.widgets.jfx.theming.JfxSsType;
import org.rapfx.client.widgets.jfx.theming.JfxStylesheet;

/**
 * Implementation of a composite. Implemented using {@link Pane} in JavaFX.
 */
public class JfxComposite extends JfxNodeObject<Pane> {

    @Override
    protected Pane createNode(JfxStyleHolder style) {
        return new Pane();
    }

    /**
     * @param raw
     *            the protocol value for the bounds
     */
    public void setClientArea(Object raw) {
        Rectangle2D bounds = JfxTypeHelper.toRectangle(raw);
        Pane node = getNode();
        node.setPrefSize(bounds.getWidth(), bounds.getHeight());
    }

    /**
     * @param raw
     *            the protocol value for the bounds
     */
    public void setBounds(Object raw) {
        Rectangle2D bounds = JfxTypeHelper.toRectangle(raw);
        Pane node = getNode();
        node.setLayoutX(bounds.getMinX());
        node.setLayoutY(bounds.getMinY());
        setClientArea(raw);
    }

    public static class Handler extends JfxNodeHandler<JfxComposite> {

        public static final String ID = "rwt.widgets.Composite";

        @Override
        public JfxComposite create(String target, Map<String, ?> properties) {
            return new JfxComposite();
        }

        @Override
        public void registerThemeContributions() {
            JfxSsClass defaultClass = JfxStylesheet.get(JfxComposite.class);
            defaultClass.map(JfxSsType.COLOR, "Composite", "background-color");
        }

    }

}
