/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.protocol.theme;

import java.util.Set;
import java.util.TreeSet;

import org.rapfx.client.widgets.jfx.impl.JfxStyleHolder.Style;

/**
 * Holds metadata about a {@link ThemeClass}es property. The data consists of a name, a set of
 * requires {@link Style}s, a set of required pseudo-classes (states) and an ID of the actual value,
 * used for retrieving it from a {@link ThemeValues} instance. The actual <b>{@link Type}</b> of the
 * property is not part of the metadata. Whoever requests the {@link ThemeProperty}s value /has/ to
 * know the target {@link Type}.
 */
public class ThemeProperty {

    /**
     * Describes the type of the references value in the {@link ThemeValues}.
     */
    public enum Type {
        DIMENSION, BOX_DIMENSION, IMAGE, GRADIENT, FONT, COLOR, BORDER, CURSOR, ANIMATION, SHADOW
    }

    private String valueId;
    private final String name;
    private final ThemeData data;
    private final Set<Style> requiredStyles;
    private final Set<String> requiredPseudos;

    ThemeProperty(ThemeData data, String name, Set<Style> requiredStyles,
            Set<String> requiredPseudos) {
        this.name = name;
        this.data = data;
        this.requiredStyles = requiredStyles;
        this.requiredPseudos = requiredPseudos;
    }

    /**
     * Checks whether this {@link ThemeProperty} matches all required styles and states.
     * 
     * @param styles
     *            the required styles
     * @param pseudoClasses
     *            the requires states
     * @return whether this {@link ThemeProperty} applies
     */
    boolean matches(Set<Style> styles, Set<Style> relevantStyles, Set<String> pseudoClasses) {
        if (relevantStyles == null || relevantStyles.isEmpty()) {
            return requiredStyles.equals(styles) && requiredPseudos.equals(pseudoClasses);
        } else {
            if (!requiredPseudos.equals(pseudoClasses)) {
                return false;
            }

            if (requiredStyles.isEmpty() && (styles == null || styles.isEmpty())) {
                return true;
            }

            Set<Style> retained = new TreeSet<>(requiredStyles);
            retained.retainAll(relevantStyles);
            return retained.equals(styles);
        }
    }

    /**
     * @param valueId
     *            the internal values ID used for lookup in the {@link ThemeValues}.
     */
    void setValueId(String valueId) {
        this.valueId = valueId;
    }

    /**
     * Retrieves the {@link ThemeProperty}s values as the given {@link Type}. The caller has to know
     * the {@link Type}.
     * 
     * @param type
     *            the {@link Type}.
     * @return the value for the {@link Type} or <code>null</code>.
     */
    public Object get(Type type) {
        ThemeValues v = data.getTheme().getValues();
        switch (type) {
        case ANIMATION:
            return v.animations.get(valueId);
        case BORDER:
            return v.borders.get(valueId);
        case BOX_DIMENSION:
            return v.boxdimensions.get(valueId);
        case COLOR:
            return v.colors.get(valueId);
        case CURSOR:
            return v.cursors.get(valueId);
        case DIMENSION:
            return v.dimensions.get(valueId);
        case FONT:
            return v.fonts.get(valueId);
        case IMAGE:
            return v.images.get(valueId);
        case SHADOW:
            return v.shadows.get(valueId);
        case GRADIENT:
            return v.gradients.get(valueId);
        default:
            throw new IllegalStateException("unsupported type " + type);
        }
    }

    @Override
    public String toString() {
        return name + "[" + requiredStyles + requiredPseudos + "](" + valueId + ")";
    }

}
