/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.widgets.jfx.impl;

import java.util.Collections;
import java.util.Map;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rapfx.client.ApplicationGlobals;
import org.rapfx.client.lifecycle.LifeCycle;
import org.rapfx.client.protocol.PropertyBuilder;
import org.rapfx.client.protocol.types.AbstractRemoteObject;
import org.rapfx.client.protocol.types.ReflectiveTypeHandler;
import org.rapfx.client.protocol.types.RemoteObject;
import org.rapfx.client.protocol.types.TypeHandler;
import org.rapfx.client.protocol.types.operations.SetOperation;
import org.rapfx.client.widgets.jfx.JfxNodeObject;
import org.rapfx.client.widgets.jfx.JfxTypeHelper;
import org.rapfx.client.widgets.jfx.impl.JfxStyleHolder.Style;
import org.rapfx.client.widgets.jfx.theming.JfxSsClass;
import org.rapfx.client.widgets.jfx.theming.JfxSsType;
import org.rapfx.client.widgets.jfx.theming.JfxStylesheet;

public class JfxDisplay extends AbstractRemoteObject {

    private static final Log log = LogFactory.getLog(JfxDisplay.class);

    @Override
    public void initialize(TypeHandler<?> handler, String targetId, Map<String, ?> properties) {
        super.initialize(handler, targetId, properties);

        LifeCycle lc = handler.getLifeCycle();
        Screen screen = Screen.getScreens().get(0);
        Rectangle2D bounds = screen.getVisualBounds();
        int dpi = (int) screen.getDpi();

        lc.send(new SetOperation(targetId, new PropertyBuilder<Object>()
                .add("bounds", JfxTypeHelper.toMessage(bounds)).add("dpi", new int[] { dpi, dpi })
                .add("colorDepth", 24).add("cursorPosition", new int[] { 0, 0 }).build()));
    }

    /**
     * Tries to put the input focus to a certain control.
     * 
     * @param controlId
     *            the control that will receive the focus
     */
    public void setFocusControl(String controlId) {
        RemoteObject object = ApplicationGlobals.getInstance().getLifeCycle().getObjectRegistry()
                .get(controlId);
        if (object != null && object instanceof JfxNodeObject<?>) {
            ((JfxNodeObject<?>) object).setFocus();
        } else {
            log.debug("cannot put focus to " + controlId + " (" + object + ")");
        }
    }

    public static class Handler extends ReflectiveTypeHandler<JfxDisplay> {

        public static final String ID = "rwt.widgets.Display";

        static {
            JfxSsClass globalClass = JfxStylesheet.get("root", Collections.<Style> emptySet(),
                    Collections.<String> emptySet());
            globalClass.map(JfxSsType.FONT, "Display", "font");
        }

        @Override
        public JfxDisplay create(String target, Map<String, ?> properties) {
            return new JfxDisplay();
        }

    }
}
