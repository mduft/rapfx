/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.widgets.jfx.impl;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Holder for Style combinations applied to controls.
 */
public class JfxStyleHolder {

    /**
     * Constants for all supported SWT styles
     */
    public enum Style {
        NONE, TITLE, MIN, MAX, CLOSE, SYSTEM_MODAL, BORDER, RESIZE, WRAP, PUSH, VERTICAL, HORIZONTAL, RIGHT, LEFT, DOWN, UP, SHADOW_OUT, SHADOW_IN, ICON_WARNING, ICON_INFORMATION, ICON_ERROR, ARROW, TOGGLE, RADIO, CHECK, MULTI, SEPARATOR, FLAT, SINGLE, ON_TOP, APPLICATION_MODAL, NO_TRIM
    }

    private final EnumSet<Style> styles = EnumSet.of(Style.NONE);

    public JfxStyleHolder(List<String> styles) {
        if (styles == null) {
            return;
        }

        for (String style : styles) {
            Style s = Style.valueOf(style);
            this.styles.add(s);
        }
    }

    public boolean hasStyle(Style s) {
        return styles.contains(s);
    }

    public Set<Style> getAllStyles() {
        return styles;
    }

}
