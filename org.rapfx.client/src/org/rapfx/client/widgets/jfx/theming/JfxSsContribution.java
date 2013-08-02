/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.widgets.jfx.theming;

import org.rapfx.client.protocol.theme.Theme;

public abstract class JfxSsContribution implements Comparable<JfxSsContribution> {

    public abstract String getFxAttribute();

    public abstract void contribute(StringBuilder classContent, Theme reference);

    public abstract Object getRawValue(Theme theme);

    @Override
    public int compareTo(JfxSsContribution o) {
        return getFxAttribute().compareTo(o.getFxAttribute());
    }

}
