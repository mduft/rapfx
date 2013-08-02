/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.protocol.theme;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.rapfx.client.widgets.jfx.impl.JfxStyleHolder.Style;

/**
 * Holds all root data for a {@link Theme}, being the {@link ThemeClass} that in turn hold all data
 * for each contained {@link ThemeProperty}
 */
public class ThemeData {

    private final Map<String, ThemeClass> classes = new TreeMap<>();
    private Theme theme;

    /**
     * Ties this {@link ThemeData} instance to the given {@link Theme}.
     * 
     * @param theme
     *            the {@link Theme} that this {@link ThemeData} belongs to. The {@link Theme} has to
     *            set this as soon as it gets hold of a {@link ThemeData} instance.
     */
    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    /**
     * @return the associated {@link Theme}, which in turn can be used to find {@link ThemeValues},
     *         which in turn can be used to look up named {@link ThemeProperty} values.
     */
    Theme getTheme() {
        return theme;
    }

    /**
     * Adds a {@link ThemeProperty} to this {@link ThemeData}, creating non-existent
     * {@link ThemeClass} and {@link ThemeProperty} on the fly.
     * <p>
     * This should only be used during de-serialization of the {@link Theme} from a server request!
     * 
     * @param className
     *            the name of the {@link ThemeClass}
     * @param propertyName
     *            the name of the {@link ThemeProperty}
     * @param styles
     *            the required styles for theme'd controls
     * @param pseudos
     *            the required pseudo-classes for the theme's controls
     * @param valueId
     *            the ID of the value, used for lookup in a {@link ThemeValues} instance.
     */
    public void addData(String className, String propertyName, Set<Style> styles,
            Set<String> pseudos, String valueId) {
        ThemeClass cls = classes.get(className);
        if (cls == null) {
            cls = new ThemeClass(this, className);
            classes.put(className, cls);
        }

        cls.setProperty(propertyName, styles, pseudos, valueId);
    }

    /**
     * Looks up the named {@link ThemeClass}
     * 
     * @param name
     *            the name of the requested {@link ThemeClass} without any styles or states (e.g.
     *            "Composite").
     * @return the {@link ThemeClass} or <code>null</code>.
     */
    public ThemeClass getThemeClass(String name) {
        return classes.get(name);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("ThemeData {");

        for (Entry<String, ThemeClass> entry : classes.entrySet()) {
            builder.append(' ').append(entry.getValue()).append('\n');
        }

        builder.append('}');
        return builder.toString();
    }

}
