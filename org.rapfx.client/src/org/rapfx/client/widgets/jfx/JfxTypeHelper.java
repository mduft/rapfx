/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.widgets.jfx;

import java.util.List;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import org.rapfx.client.protocol.Message;
import org.rapfx.client.protocol.types.Operation;
import org.rapfx.client.widgets.jfx.impl.JfxStyleHolder;

/**
 * JavaFX specific helper to convert raw values to consumable types.
 */
public class JfxTypeHelper {

    /**
     * List of all available {@link Font} families.
     */
    private static final List<String> allFonts = Font.getFamilies();

    /**
     * Converts a {@link Rectangle2D} into a type suitable for {@link Message}s sent to the server.
     * 
     * @param bounds
     *            the {@link Rectangle2D} to convert
     * @return the representation of the {@link Rectangle2D} suitable for an {@link Operation}.
     */
    public static int[] toMessage(Rectangle2D bounds) {
        return new int[] { (int) bounds.getMinX(), (int) bounds.getMinY(), (int) bounds.getWidth(),
                (int) bounds.getHeight() };
    }

    /**
     * Converts the protocol value for a rectangle to a {@link Rectangle2D}.
     * 
     * @param value
     *            the protocol passed value
     * @return the {@link Rectangle2D} representation or <code>null</code>
     */
    public static Rectangle2D toRectangle(Object value) {
        if (value == null || !(value instanceof List) || ((List<?>) value).size() != 4) {
            return null;
        }

        @SuppressWarnings("unchecked")
        List<Double> list = (List<Double>) value;
        return new Rectangle2D(list.get(0), list.get(1), list.get(2), list.get(3));
    }

    /**
     * Converts the protocol value for a point to a {@link Point2D}.
     * 
     * @param value
     *            the protocol passed value
     * @return the {@link Point2D} representation or <code>null</code>
     */
    public static Point2D toPoint(Object value) {
        if (value == null || !(value instanceof List) || ((List<?>) value).size() != 2) {
            return null;
        }

        @SuppressWarnings("unchecked")
        List<Double> list = (List<Double>) value;
        return new Point2D(list.get(0), list.get(1));
    }

    /**
     * @param value
     *            the raw protocol value
     * @return the style holder
     */
    @SuppressWarnings("unchecked")
    public static JfxStyleHolder toStyle(Object value) {
        if (value == null || !(value instanceof List) || ((List<?>) value).isEmpty()) {
            return new JfxStyleHolder(null);
        }

        return new JfxStyleHolder((List<String>) value);
    }

    /**
     * Converts raw protocol values to a {@link Color} instance.
     * 
     * @param value
     *            the raw protocol value.
     * @return the {@link Color} represented by the raw value.
     */
    public static Color toColor(Object value) {
        if (value instanceof String && ((String) value).startsWith("#")) {
            return Color.web((String) value);
        }

        if (value == null || !(value instanceof List)) {
            return null;
        }

        @SuppressWarnings("unchecked")
        List<Double> list = (List<Double>) value;

        if (list.size() == 3) {
            return Color
                    .rgb(list.get(0).intValue(), list.get(1).intValue(), list.get(2).intValue());
        } else if (list.size() == 4) {
            return Color.rgb(list.get(0).intValue(), list.get(1).intValue(),
                    list.get(2).intValue(), list.get(3));
        } else {
            return null;
        }
    }

    /**
     * Find a {@link Font} that best matches the given specs.
     * 
     * @param names
     *            the list of acceptable font families
     * @param size
     *            the requested size in pt.
     * @param bold
     *            whether the font should be bold
     * @param italic
     *            whether the font should be italic
     * @return a suitable {@link Font}, never <code>null</code>, might not be a perfect match
     */
    public static Font findFont(List<String> names, double size, boolean bold, boolean italic) {
        FontWeight weight = (bold ? FontWeight.BOLD : FontWeight.NORMAL);
        FontPosture posture = (italic ? FontPosture.ITALIC : FontPosture.REGULAR);
        for (String name : names) {
            for (String font : allFonts) {
                if (font.toLowerCase().equals(name.toLowerCase())) {
                    return Font.font(font, weight, posture, size);
                }
            }
        }

        // let JavaFX choose an appropriate alternative for the first font.
        return Font.font(names.get(0), weight, posture, size);
    }

    /**
     * Find a font corresponding to the given raw protocol value.
     * 
     * @param raw
     *            the raw protocol value sent from the server
     * @return a corresponding font or <code>null</code>
     */
    @SuppressWarnings("unchecked")
    public static Font getFont(Object raw) {
        if (raw == null || !(raw instanceof List)) {
            return null;
        }

        List<?> item = (List<?>) raw;

        if (item.size() != 4) {
            return null;
        }

        List<String> families = (List<String>) item.get(0);
        double size = (Double) item.get(1);
        boolean bold = (Boolean) item.get(2);
        boolean italic = (Boolean) item.get(3);

        return findFont(families, size, bold, italic);
    }

}
