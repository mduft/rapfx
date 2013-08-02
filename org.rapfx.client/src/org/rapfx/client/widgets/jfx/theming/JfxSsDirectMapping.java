/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.widgets.jfx.theming;

import java.util.Collections;
import java.util.Set;

import org.rapfx.client.protocol.theme.Theme;
import org.rapfx.client.protocol.theme.ThemeClass;
import org.rapfx.client.protocol.theme.ThemeProperty;
import org.rapfx.client.widgets.jfx.impl.JfxStyleHolder.Style;

public class JfxSsDirectMapping extends JfxSsContribution {

    private static final String FX_PREFIX = "-fx-";

    private final JfxSsClass parent;
    private final String fxAttribute;
    private final String cssAttribute;
    private final String cssClass;
    private final Set<String> cssPseudos;
    private final JfxSsType type;

    public JfxSsDirectMapping(JfxSsClass parent, JfxSsType type, String cssClass,
            String cssAttribute) {
        this(parent, type, cssClass, cssAttribute, FX_PREFIX + cssAttribute);
    }

    public JfxSsDirectMapping(JfxSsClass parent, JfxSsType type, String cssClass,
            String cssAttribute, String fxAttribute) {
        this(parent, type, cssClass, cssAttribute, fxAttribute, null);
    }

    public JfxSsDirectMapping(JfxSsClass parent, JfxSsType type, String cssClass,
            String cssAttribute, Set<String> cssPseudos) {
        this(parent, type, cssClass, cssAttribute, FX_PREFIX + cssAttribute, cssPseudos);
    }

    public JfxSsDirectMapping(JfxSsClass parent, JfxSsType type, String cssClass,
            String cssAttribute, String fxAttribute, Set<String> cssPseudos) {
        this.parent = parent;
        this.cssClass = cssClass;
        this.cssAttribute = cssAttribute;
        this.fxAttribute = fxAttribute;
        this.type = type;
        this.cssPseudos = cssPseudos;
    }

    public String getCssClass() {
        return cssClass;
    }

    public String getCssAttribute() {
        return cssAttribute;
    }

    @Override
    public String getFxAttribute() {
        return fxAttribute;
    }

    public JfxSsType getType() {
        return type;
    }

    @Override
    public void contribute(StringBuilder classContent, Theme reference) {
        String fxValue = type.getAsFxStylesheetValue(getRawValue(reference));
        if (fxValue != null) {
            classContent.append("\t").append(fxAttribute).append(": ").append(fxValue)
                    .append(";\n");
        }
    }

    /**
     * Retrieves the raw value for this attribute from the given Theme. This can be used to
     * implement custom evaluation of the raw theme data if required.
     * 
     * @param reference
     *            the theme to get the data from
     * @return the raw value for this attribute.
     */
    @Override
    public Object getRawValue(Theme reference) {
        ThemeClass themeClass = reference.getThemeClass(cssClass);
        if (themeClass != null) {
            // first "fully qualified", with styles and states
            ThemeProperty prop = themeClass.findProperty(cssAttribute, parent.getStyles(), parent
                    .getRelevantStyles(), cssPseudos == null ? parent.getPseudo() : cssPseudos);

            if (prop != null) {
                return prop.get(type.getType());
            }

            // if not found, try without state, but with styles
            prop = themeClass.findProperty(cssAttribute, parent.getStyles(),
                    parent.getRelevantStyles(), Collections.<String> emptySet());

            if (prop != null) {
                return prop.get(type.getType());
            }

            // if still not found, try without any style and state
            prop = themeClass.findProperty(cssAttribute, Collections.<Style> emptySet(),
                    Collections.<Style> emptySet(), Collections.<String> emptySet());

            if (prop != null) {
                return prop.get(type.getType());
            }
        }
        return null;
    }
}
