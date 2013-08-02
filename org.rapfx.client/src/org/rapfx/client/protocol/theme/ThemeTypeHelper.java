/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.protocol.theme;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rapfx.client.ApplicationGlobals;
import org.rapfx.client.protocol.theme.ThemeProperty.Type;
import org.rapfx.client.protocol.theme.ThemeValues.BorderDefinition;
import org.rapfx.client.protocol.theme.ThemeValues.ImageDefinition;
import org.rapfx.client.transport.Transport;

/**
 * Helper to get meaningful values out of raw {@link ThemeValues} {@link Type}d values.
 */
public class ThemeTypeHelper {

    private static final Log log = LogFactory.getLog(ThemeTypeHelper.class);

    /**
     * @param value
     *            the raw protocol value
     * @return the rgb() or rgba() or #xxxxxx value suitable for CSS / JavaFX Stylesheets.
     */
    public static String toColorString(Object value) {
        if (value instanceof String && ((String) value).startsWith("#")) {
            return (String) value;
        }

        if (value == null || !(value instanceof List)) {
            return null;
        }

        @SuppressWarnings("unchecked")
        List<Double> list = (List<Double>) value;

        if (list.size() == 3) {
            return "rgb(" + list.get(0).intValue() + "," + list.get(1).intValue() + ","
                    + list.get(2).intValue() + ")";
        } else if (list.size() == 4) {
            Double alpha = list.get(3);
            if (alpha > 1.0) {
                alpha /= 255.0;
            }
            return "rgba(" + list.get(0).intValue() + "," + list.get(1).intValue() + ","
                    + list.get(2).intValue() + "," + alpha + ")";
        } else {
            return null;
        }
    }

    /**
     * Return the given raw {@link ImageDefinition}s image URL relative to the global
     * {@link Transport}s target URL.
     * 
     * @param value
     *            the raw value deserialized from the theme definition
     * @return the CSS url(...) for this image.
     */
    public static String toImageUrl(Object value) {
        if (value == null || !(value instanceof ImageDefinition)) {
            return null;
        }

        return "url(\""
                + ApplicationGlobals
                        .getInstance()
                        .getTransport()
                        .getContextURL(
                                "rwt-resources/themes/images/" + ((ImageDefinition) value).name)
                + "\")";
    }

    /**
     * @param rawRapValue
     *            the raw value.
     * @return a string in the form "[italic] [bold] ##pt {family},..."
     */
    @SuppressWarnings("unchecked")
    public static String toFontDef(Object rawRapValue) {
        if (rawRapValue == null || !(rawRapValue instanceof Map)) {
            return null;
        }

        Map<String, Object> fontDef = (Map<String, Object>) rawRapValue;
        List<String> families = (List<String>) fontDef.get("family");
        Double size = (Double) fontDef.get("size");
        Boolean italic = (Boolean) fontDef.get("italic");
        Boolean bold = (Boolean) fontDef.get("bold");

        StringBuilder result = new StringBuilder();
        if (italic != null && italic) {
            result.append("italic ");
        }
        if (bold != null && bold) {
            result.append("bold ");
        }
        result.append(size.intValue()).append("pt ");
        result.append(join(families, true));

        return result.toString();
    }

    @SuppressWarnings("unchecked")
    public static String toGradientDef(Object rawRapValue) {
        if (rawRapValue == null || !(rawRapValue instanceof Map)) {
            return null;
        }

        Map<String, Object> values = (Map<String, Object>) rawRapValue;
        List<Double> percents = (List<Double>) values.get("percents");
        List<String> colors = (List<String>) values.get("colors");
        boolean vertical = (boolean) values.get("vertical");

        if (percents.size() != colors.size()) {
            log.warn("broken gradient definition, value count does not match");
            return null;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("linear-gradient(");
        if (!vertical) {
            builder.append("to top right, ");
        }
        for (int i = 0; i < percents.size(); ++i) {
            if (i != 0) {
                builder.append(", ");
            }
            builder.append(colors.get(i)).append(" ").append(percents.get(i).intValue())
                    .append("%");
        }
        builder.append(')');

        return builder.toString();
    }

    /**
     * @param rawValue
     *            the raw value from the {@link Theme}.
     * @return the corresponding {@link BorderDefinition} or <code>null</code>
     */
    public static BorderDefinition toBorderDefinition(Object rawValue) {
        if (rawValue == null || !(rawValue instanceof BorderDefinition)) {
            return null;
        }

        return (BorderDefinition) rawValue;
    }

    /**
     * @param rawRapValue
     *            the raw protocol value
     * @return a {@link Double} representing that value.
     */
    public static Integer toDimension(Object rawRapValue) {
        if (rawRapValue == null || !(rawRapValue instanceof Integer)) {
            return null;
        }

        return (Integer) rawRapValue;
    }

    /**
     * Helper to join a list of strings to a optionally quoted, comma separated string.
     * 
     * @param members
     *            the strings to join
     * @param quote
     *            whether to quote each string
     * @return the single string containing a comma separated list of strings
     */
    private static String join(List<String> members, boolean quote) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < members.size(); ++i) {
            if (i != 0) {
                builder.append(",");
            }
            if (quote) {
                builder.append('"').append(members.get(i)).append('"');
            } else {
                builder.append(members.get(i));
            }
        }
        return builder.toString();
    }

    /**
     * @param rawRapValue
     *            the raw protocol value
     * @return the string representation of a box (4 pixel values).
     */
    public static String toBox(Object rawRapValue) {
        if (rawRapValue == null || !(rawRapValue instanceof List)) {
            return null;
        }

        @SuppressWarnings("unchecked")
        List<Double> val = (List<Double>) rawRapValue;

        switch (val.size()) {
        case 1:
            return val.get(0).intValue() + "px";
        case 4:
            return val.get(0).intValue() + "px " + val.get(1).intValue() + "px "
                    + val.get(2).intValue() + "px " + val.get(3).intValue() + "px";
        default:
            return null;
        }
    }
}
