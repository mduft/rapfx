/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.widgets.jfx.theming;

import javafx.geometry.Rectangle2D;

import org.rapfx.client.protocol.theme.ThemeProperty.Type;
import org.rapfx.client.protocol.theme.ThemeTypeHelper;
import org.rapfx.client.protocol.theme.ThemeValues.BorderDefinition;
import org.rapfx.client.widgets.jfx.JfxTypeHelper;

public enum JfxSsType {

    COLOR(Type.COLOR), FONT(Type.FONT), BOX(Type.BOX_DIMENSION), DIMENSION_PX(Type.DIMENSION) {
        @Override
        public String getAsFxStylesheetValue(Object rawRapValue) {
            return ThemeTypeHelper.toDimension(rawRapValue) + "px";
        }
    },
    BORDER_RADIUS(Type.BOX_DIMENSION) {
        @Override
        public String getAsFxStylesheetValue(Object rawRapValue) {
            Rectangle2D box = JfxTypeHelper.toRectangle(rawRapValue);
            return (int) box.getMinX() + "px " + (int) box.getMinY() + "px " + (int) box.getWidth()
                    + "px " + (int) box.getHeight() + "px";
        }
    },
    TOP_ONLY_BORDER_RADIUS(Type.BOX_DIMENSION) {
        @Override
        public String getAsFxStylesheetValue(Object rawRapValue) {
            Rectangle2D box = JfxTypeHelper.toRectangle(rawRapValue);
            return (int) box.getMinX() + "px " + (int) box.getMinY() + "px 0px 0px";
        }
    },
    BORDER_WIDTH(Type.BORDER) {
        @Override
        public String getAsFxStylesheetValue(Object rawRapValue) {
            BorderDefinition def = ThemeTypeHelper.toBorderDefinition(rawRapValue);
            if (def == null || def.width <= 0.0) {
                return null;
            }
            return def.width + "px";
        }
    },
    BORDER_TYPE(Type.BORDER) {
        @Override
        public String getAsFxStylesheetValue(Object rawRapValue) {
            BorderDefinition def = ThemeTypeHelper.toBorderDefinition(rawRapValue);
            if (def == null) {
                return null;
            }
            return def.type;
        }
    },
    BORDER_COLOR(Type.BORDER) {
        @Override
        public String getAsFxStylesheetValue(Object rawRapValue) {
            BorderDefinition def = ThemeTypeHelper.toBorderDefinition(rawRapValue);
            if (def == null) {
                return null;
            }
            return ThemeTypeHelper.toColorString(def.color);
        }
    },
    BACKGROUND_COLOR_GRADIENT(Type.GRADIENT) {
        @Override
        public String getAsFxStylesheetValue(Object rawRapValue) {
            return ThemeTypeHelper.toGradientDef(rawRapValue);
        }
    },
    IMAGE_URL(Type.IMAGE) {
        @Override
        public String getAsFxStylesheetValue(Object rawRapValue) {
            return ThemeTypeHelper.toImageUrl(rawRapValue);
        }
    };

    private final Type type;

    private JfxSsType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public String getAsFxStylesheetValue(Object rawRapValue) {
        switch (type) {
        case COLOR:
            return ThemeTypeHelper.toColorString(rawRapValue);
        case FONT:
            return ThemeTypeHelper.toFontDef(rawRapValue);
        case BOX_DIMENSION:
            return ThemeTypeHelper.toBox(rawRapValue);
        default:
            throw new IllegalStateException("unsupported type " + type);
        }
    }

}
