/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.widgets.jfx.impl;

import java.util.Map;

import javafx.geometry.Pos;
import javafx.scene.control.Label;

import org.rapfx.client.widgets.jfx.JfxControlObject;
import org.rapfx.client.widgets.jfx.JfxNodeHandler;
import org.rapfx.client.widgets.jfx.impl.JfxStyleHolder.Style;
import org.rapfx.client.widgets.jfx.theming.JfxSsClass;
import org.rapfx.client.widgets.jfx.theming.JfxSsType;
import org.rapfx.client.widgets.jfx.theming.JfxStylesheet;

public class JfxLabel extends JfxControlObject<Label> {

    @Override
    protected Label createNode(JfxStyleHolder style) {
        Label lbl = new Label();

        lbl.setEllipsisString(null);
        lbl.wrapTextProperty().set(style.hasStyle(Style.WRAP));
        lbl.alignmentProperty().set(Pos.TOP_LEFT);

        return lbl;
    }

    public void setText(String value) {
        getNode().setText(value);
    }

    public static class Handler extends JfxNodeHandler<JfxLabel> {

        public static final String ID = "rwt.widgets.Label";

        @Override
        public JfxLabel create(String target, Map<String, ?> properties) {
            return new JfxLabel();
        }

        @Override
        public void registerThemeContributions() {
            JfxSsClass cls = JfxStylesheet.get(JfxLabel.class);
            cls.map(JfxSsType.FONT, "Label", "font");
        }

    }
}
