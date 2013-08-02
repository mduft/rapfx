/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.protocol.theme;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rapfx.client.widgets.jfx.impl.JfxStyleHolder.Style;

/**
 * Represents a CSS "class" in a {@link Theme}. The class has a name, and various
 * {@link ThemeProperty}s, that might apply only if the theme'd control has certain styles and/or
 * states
 */
public class ThemeClass {

    private static final Log log = LogFactory.getLog(ThemeClass.class);
    private final String className;
    private final Map<String, List<ThemeProperty>> properties = new TreeMap<>();
    private final ThemeData data;

    /**
     * Creates a new {@link ThemeClass} with the given name, using the given {@link ThemeData} as
     * source for property values
     * 
     * @param data
     *            the associated {@link ThemeData}
     * @param className
     *            the name of the CSS class without any states/pseudo-classes (e.g. "Composite")
     */
    ThemeClass(ThemeData data, String className) {
        this.data = data;
        this.className = className;
    }

    /**
     * Looks up a certain property in this {@link ThemeClass}. The property has to have a certain
     * name and fulfill certain criteria.
     * 
     * @param name
     *            the requested {@link ThemeProperty}s name
     * @param styles
     *            the required styles for the {@link ThemeProperty}
     * @param pseudos
     *            the required pseudo classes for the {@link ThemeProperty}
     * @return the {@link ThemeProperty} that fulfills all criteria, or <code>null</code> if none
     *         was found
     */
    public ThemeProperty findProperty(String name, Set<Style> styles, Set<Style> relevantStyles,
            Set<String> pseudos) {
        List<ThemeProperty> props = properties.get(name);
        if (props == null || props.isEmpty()) {
            return null;
        }

        for (ThemeProperty prop : props) {
            if (prop.matches(styles, relevantStyles, pseudos)) {
                return prop;
            }
        }
        return null;
    }

    /**
     * Finds a property with the given name that has no style and pseudo-class requirements (see
     * {@link #findProperty(String, Set, Set)}
     * 
     * @param name
     *            the requested {@link ThemeProperty}s name.
     * @return the {@link ThemeProperty} if found, otherwise <code>null</code>
     */
    public ThemeProperty findProperty(String name) {
        return findProperty(name, Collections.<Style> emptySet(), Collections.<Style> emptySet(),
                Collections.<String> emptySet());
    }

    /**
     * Sets the given valueId for the named {@link ThemeProperty}. If the {@link ThemeProperty} does
     * not exist, it is created. If it does exist, the value is overwritten.
     * 
     * @param propertyName
     *            the name of the {@link ThemeProperty}
     * @param styles
     *            the required styles for the {@link ThemeProperty}
     * @param pseudos
     *            the required pseudo-classes for the {@link ThemeProperty}
     * @param valueId
     *            the ID of the named value used to lookup the actual value from {@link ThemeValues}
     */
    void setProperty(String propertyName, Set<Style> styles, Set<String> pseudos, String valueId) {
        ThemeProperty prop = findProperty(propertyName, styles, null, pseudos);

        if (prop == null) {
            prop = new ThemeProperty(data, propertyName, styles, pseudos);
            prop.setValueId(valueId);

            log.trace("cls=" + className + ", prop=" + propertyName + ", styles=" + styles
                    + ", pseudos=" + pseudos + ", vId=" + valueId);

            List<ThemeProperty> props = properties.get(propertyName);
            if (props == null) {
                props = new ArrayList<>();
                properties.put(propertyName, props);
            }

            props.add(prop);
        } else {
            prop.setValueId(valueId);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(className);
        builder.append(" {\n");
        for (Entry<String, List<ThemeProperty>> prop : properties.entrySet()) {
            builder.append('\t').append(prop.getValue()).append('\n');
        }
        builder.append('}');
        return builder.toString();
    }

}
