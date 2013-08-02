/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.widgets.jfx.theming;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.rapfx.client.protocol.theme.Theme;
import org.rapfx.client.widgets.jfx.impl.JfxStyleHolder.Style;

public class JfxSsClass {

    private final String internalName;
    private final Set<JfxSsContribution> contributions = new TreeSet<>();
    private final Set<Style> styles;
    private final Set<String> pseudo;
    private final String baseName;

    JfxSsClass(String internalName, String baseName, Set<Style> styles, Set<String> pseudo) {
        this.internalName = internalName;
        this.baseName = baseName;
        this.styles = styles;
        this.pseudo = pseudo;
    }

    public void inherit(JfxSsClass parent) {
        for (JfxSsContribution contrib : parent.contributions) {
            set(contrib);
        }
    }

    public void map(JfxSsType type, String clazz, String attribute) {
        set(new JfxSsDirectMapping(this, type, clazz, attribute));
    }

    public void map(JfxSsType type, String clazz, String attribute, String fxName) {
        set(new JfxSsDirectMapping(this, type, clazz, attribute, fxName));
    }

    public void map(JfxSsType type, String clazz, String attribute, String fxName,
            Set<String> cssPseudos) {
        set(new JfxSsDirectMapping(this, type, clazz, attribute, fxName, cssPseudos));
    }

    public void set(JfxSsContribution contribution) {
        if (contributions.contains(contribution)) {
            // might be a contribution with the same name previously added from a clone.
            contributions.remove(contribution);
        }
        contributions.add(contribution);
    }

    void contribute(StringBuilder classContents, Theme reference) {
        if (contributions.isEmpty()) {
            return;
        }

        classContents.append(".").append(internalName).append(" {\n");
        for (JfxSsContribution child : contributions) {
            child.contribute(classContents, reference);
        }
        classContents.append("}\n\n");
    }

    Set<Style> getStyles() {
        return styles;
    }

    Set<String> getPseudo() {
        return pseudo;
    }

    Set<Style> getRelevantStyles() {
        Set<Style> result = JfxStylesheet.getRelevantStyles(baseName);
        if (result == null) {
            result = Collections.emptySet();
        }
        return result;
    }

}
