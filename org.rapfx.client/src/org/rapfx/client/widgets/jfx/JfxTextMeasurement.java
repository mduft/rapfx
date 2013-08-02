/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.widgets.jfx;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javafx.geometry.Bounds;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rapfx.client.ApplicationGlobals;
import org.rapfx.client.protocol.types.AbstractRemoteObject;
import org.rapfx.client.protocol.types.ReflectiveTypeHandler;
import org.rapfx.client.protocol.types.operations.CallOperation;

/**
 * Text size calculation handler.
 */
public class JfxTextMeasurement extends AbstractRemoteObject {

    private static final Log log = LogFactory.getLog(JfxTextMeasurement.class);

    /**
     * Measures the text sizes of the given items.
     * 
     * @param items
     *            a list of raw protocol values representing a measurement item (containing font,
     *            text, wrapping and such information).
     */
    @SuppressWarnings("unchecked")
    public void measureItems(List<List<Object>> items) {
        Map<String, int[]> result = new TreeMap<>();
        for (List<Object> item : items) {
            if (item.size() < 6) {
                log.warn("measurement item has invalid size " + item.size() + " (" + item + ")");
                continue;
            }

            String id = (String) item.get(0);
            String text = (String) item.get(1);
            List<String> fontNames = (List<String>) item.get(2);

            // TODO: check sizes for plausibility
            double fontSize = (double) item.get(3);
            boolean bold = (boolean) item.get(4);
            boolean italic = (boolean) item.get(5);
            double wrapWidth = 0;

            if (item.size() >= 7) {
                wrapWidth = (double) item.get(6);
            }

            result.put(
                    id,
                    measure(text, JfxTypeHelper.findFont(fontNames, fontSize, bold, italic),
                            wrapWidth));
        }

        Map<String, ? extends Object> arguments = Collections.singletonMap("results", result);
        CallOperation op = new CallOperation(getObjectId(), "storeMeasurements", arguments);
        ApplicationGlobals.getInstance().getLifeCycle().send(op);
    }

    /**
     * Measures the bounds of the given text with the given font, possibly wrapping at a specified
     * width.
     * 
     * @param text
     *            the text to measure
     * @param font
     *            the font to use for calculation
     * @param wrapWidth
     *            the width to wrap at, or <= 0 to not wrap.
     * @return
     */
    private int[] measure(String text, Font font, double wrapWidth) {
        Text tx = new Text();
        tx.setText(text);
        tx.setFont(font);
        tx.setWrappingWidth(wrapWidth);
        tx.snapshot(null, null);
        Bounds bounds = tx.getLayoutBounds();

        // add some buffer value :)
        return new int[] { (int) (bounds.getWidth() + 0.75), (int) bounds.getHeight() };
    }

    public static class Handler extends ReflectiveTypeHandler<JfxTextMeasurement> {
        public static final String ID = "rwt.client.TextSizeMeasurement";

        @Override
        public JfxTextMeasurement create(String target, Map<String, ?> properties) {
            return new JfxTextMeasurement();
        }

    }
}
