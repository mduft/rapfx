/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.protocol.theme;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.rapfx.client.protocol.theme.ThemeProperty.Type;

/**
 * Holds all named values for the various {@link Type}s supported by the RAP protocol.
 */
public class ThemeValues {

    public Map<String, Integer> dimensions = new TreeMap<>();
    public Map<String, List<Double>> boxdimensions = new TreeMap<>();
    public Map<String, ImageDefinition> images = new TreeMap<>();
    public Map<String, Map<String, Object>> gradients = new TreeMap<>();
    public Map<String, Map<String, Object>> fonts = new TreeMap<>();
    public Map<String, Object> colors = new TreeMap<>();
    public Map<String, BorderDefinition> borders = new TreeMap<>();
    public Map<String, String> cursors = new TreeMap<>();
    public Map<String, AnimationDefinition> animations = new TreeMap<>();
    public Map<String, Object> shadows = new TreeMap<>();

    public static class BorderDefinition {
        public double width;
        public Object color;
        public String type;

        @Override
        public String toString() {
            return "border{" + width + "," + color + "," + type + "}";
        }
    }

    public static class AnimationDefinition {
        // TODO
    }

    public static class ImageDefinition {
        public String name;
        public int width;
        public int height;

        public ImageDefinition(String name, int width, int height) {
            this.name = name;
            this.width = width;
            this.height = height;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('\t').append("dimensions: ").append(dimensions).append('\n');
        builder.append('\t').append("box-dimens: ").append(boxdimensions).append('\n');
        builder.append('\t').append("images    : ").append(images).append('\n');
        builder.append('\t').append("gradients : ").append(gradients).append('\n');
        builder.append('\t').append("fonts     : ").append(fonts).append('\n');
        builder.append('\t').append("colors    : ").append(colors).append('\n');
        builder.append('\t').append("borders   : ").append(borders).append('\n');
        builder.append('\t').append("cursors   : ").append(cursors).append('\n');
        builder.append('\t').append("animations: ").append(animations).append('\n');
        builder.append('\t').append("shadows   : ").append(shadows).append('\n');
        return builder.toString();
    }

}
