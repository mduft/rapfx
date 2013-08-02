/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.widgets.jfx;

import java.util.Collections;
import java.util.Set;

import org.rapfx.client.protocol.types.ReflectiveTypeHandler;
import org.rapfx.client.protocol.types.TypeHandler;
import org.rapfx.client.widgets.jfx.impl.JfxStyleHolder.Style;
import org.rapfx.client.widgets.jfx.theming.JfxStylesheet;

/**
 * Base class for {@link TypeHandler}s that handle {@link JfxControlObject}
 * 
 * @param <T>
 *            the type of the handled object
 */
public abstract class JfxNodeHandler<T extends JfxNodeObject<?>> extends ReflectiveTypeHandler<T> {

    public JfxNodeHandler() {
        registerThemeContributions();
    }

    /**
     * Can be used by subclasses to register theme contributions.
     */
    public void registerThemeContributions() {
        // default: do nothing.
    }

    /**
     * Returns the style class name for the given {@link Class}. Make sure to call
     * {@link JfxStylesheet#setRelevantStyles(String, java.util.EnumSet)} before calling this!
     * 
     * @param clazz
     *            the {@link Class} to calculate the style class name for.
     * @return the style class name that can be used as selector into the stylesheet.
     */
    public static String getStyleClass(Class<?> clazz) {
        return getStyleClass(clazz, Collections.<Style> emptySet());
    }

    /**
     * See {@link #getStyleClass(Class)}. Additionally passes styles to be taken into account when
     * calculating the selector name.
     * 
     * @param clazz
     *            the {@link Class} for which to calculate the name.
     * @param styles
     *            the styles to take into account. see
     *            {@link JfxStylesheet#setRelevantStyles(String, java.util.EnumSet)}. usually,
     *            relevant styles will be part of the class name somehow.
     * @return
     */
    public static String getStyleClass(Class<?> clazz, Set<Style> styles) {
        return JfxStylesheet.getStyledName(clazz.getSimpleName(), styles);
    }

}
