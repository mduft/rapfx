/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.widgets.jfx.impl;

import java.util.Collections;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

import org.rapfx.client.protocol.types.operations.SetOperation;
import org.rapfx.client.widgets.jfx.JfxControlObject;
import org.rapfx.client.widgets.jfx.JfxNodeHandler;
import org.rapfx.client.widgets.jfx.theming.JfxSsClass;
import org.rapfx.client.widgets.jfx.theming.JfxSsType;
import org.rapfx.client.widgets.jfx.theming.JfxStylesheet;

public class JfxText extends JfxControlObject<TextField> {

    @Override
    protected TextField createNode(JfxStyleHolder style) {
        TextField node = new TextField();

        node.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                getLifeCycle()
                        .queue(new SetOperation(getObjectId(), Collections.singletonMap("text",
                                newValue)));
            }
        });

        return node;
    }

    public void setText(String text) {
        getNode().setText(text);
    }

    public static class Handler extends JfxNodeHandler<JfxText> {

        public static final String ID = "rwt.widgets.Text";

        @Override
        public JfxText create(String target, Map<String, ?> properties) {
            return new JfxText();
        }

        @Override
        public void registerThemeContributions() {
            JfxSsClass cls = JfxStylesheet.get(JfxText.class);
            cls.map(JfxSsType.FONT, "Text", "font");
        }
    }

}
