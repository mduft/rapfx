/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.widgets.jfx.theming;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.rapfx.client.lifecycle.ObjectRegistry;
import org.rapfx.client.protocol.theme.Theme;
import org.rapfx.client.widgets.jfx.impl.JfxStyleHolder.Style;

public class JfxStylesheet {

    private File tempFile;
    private final Theme theme;
    private static final ObjectRegistry<JfxSsClass> classRegistry = new ObjectRegistry<>();
    private static final Map<String, EnumSet<Style>> relevantStyles = new TreeMap<>();

    public JfxStylesheet(Theme theme) {
        this.theme = theme;
    }

    public static final JfxSsClass get(Class<?> clazz) {
        return get(clazz.getSimpleName(), Collections.<Style> emptySet(),
                Collections.<String> emptySet());
    }

    public static final JfxSsClass get(Class<?> clazz, Set<Style> styles) {
        return get(clazz.getSimpleName(), styles, Collections.<String> emptySet());
    }

    public static final void setRelevantStyles(String fxName, EnumSet<Style> styles) {
        relevantStyles.put(fxName, styles);
    }

    static final Set<Style> getRelevantStyles(String fxName) {
        return relevantStyles.get(fxName);
    }

    public static JfxSsClass get(String fxName) {
        return get(fxName, Collections.<Style> emptySet(), Collections.<String> emptySet());
    }

    public static final JfxSsClass get(String fxName, Set<Style> styles, Set<String> pseudo) {
        String finalName = getCompleteName(fxName, styles, pseudo);
        JfxSsClass cls = classRegistry.get(finalName);

        if (cls == null) {
            cls = new JfxSsClass(finalName, fxName, styles, pseudo);
            classRegistry.set(finalName, cls);
        }

        return cls;
    }

    private static String getCompleteName(String fxName, Set<Style> styles, Set<String> pseudo) {
        StringBuilder nameBuilder = new StringBuilder(getStyledName(fxName, styles));
        for (String p : pseudo) {
            nameBuilder.append(p);
        }
        return nameBuilder.toString();
    }

    public static String getStyledName(String base, Set<Style> styles) {
        if (styles == null || styles.isEmpty()) {
            return base;
        }
        StringBuilder nameBuilder = new StringBuilder(base);
        for (Style s : styles) {
            if (isStyleRelevant(base, s)) {
                nameBuilder.append('_').append(s.name());
            }
        }
        return nameBuilder.toString();
    }

    private static boolean isStyleRelevant(String base, Style s) {
        EnumSet<Style> relevant = relevantStyles.get(base);
        if (relevant == null) {
            return false;
        }

        return relevant.contains(s);
    }

    public File getTemporary() throws IOException {
        if (tempFile != null && tempFile.exists()) {
            return tempFile;
        }

        tempFile = File.createTempFile("rwt-", ".css");
        tempFile.deleteOnExit();

        write(tempFile);

        return tempFile;
    }

    private void write(File file) throws IOException {
        StringBuilder contents = new StringBuilder();
        for (Map.Entry<String, JfxSsClass> classEntry : classRegistry.getAll()) {
            classEntry.getValue().contribute(contents, theme);
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(contents.toString());
        }
    }
}
