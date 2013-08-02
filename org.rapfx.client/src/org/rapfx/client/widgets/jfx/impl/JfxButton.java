/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.widgets.jfx.impl;

import java.util.Map;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;

import org.rapfx.client.protocol.types.operations.NotifyOperation;
import org.rapfx.client.widgets.jfx.JfxControlObject;
import org.rapfx.client.widgets.jfx.JfxNodeHandler;
import org.rapfx.client.widgets.jfx.theming.JfxSsClass;
import org.rapfx.client.widgets.jfx.theming.JfxSsType;
import org.rapfx.client.widgets.jfx.theming.JfxStylesheet;

public class JfxButton extends JfxControlObject<ButtonBase> {

    private final ActionHandler actionHandler = new ActionHandler();

    @Override
    protected ButtonBase createNode(JfxStyleHolder style) {
        Button btn = new Button();

        btn.setEllipsisString(null);
        btn.setOnAction(actionHandler);

        return btn;
    }

    public void setText(String value) {
        getNode().setText(value);
    }

    private class ActionHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            if (isEventEnabled("Selection")) {
                NotifyOperation op = new NotifyOperation(getObjectId(), "Selection", null);
                getLifeCycle().send(op);
            }
        }

    }

    public static class Handler extends JfxNodeHandler<JfxButton> {

        public static final String ID = "rwt.widgets.Button";

        @Override
        public JfxButton create(String target, Map<String, ?> properties) {
            return new JfxButton();
        }

        @Override
        public void registerThemeContributions() {
            JfxSsClass cls = JfxStylesheet.get(JfxButton.class);
            cls.map(JfxSsType.FONT, "Button", "font");
        }

    }
}
