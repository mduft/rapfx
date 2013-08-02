/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.widgets.jfx;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rapfx.client.ApplicationGlobals;
import org.rapfx.client.protocol.theme.Theme;
import org.rapfx.client.protocol.theme.ThemeData;
import org.rapfx.client.protocol.theme.ThemeValues;
import org.rapfx.client.protocol.types.AbstractRemoteObject;
import org.rapfx.client.protocol.types.ReflectiveTypeHandler;
import org.rapfx.client.transport.Transport;
import org.rapfx.client.transport.Transport.RemoteFile;
import org.rapfx.client.widgets.jfx.theming.JfxStylesheet;

/**
 * Theme handling class, responsible for CSS handling.
 */
public class JfxThemeStore extends AbstractRemoteObject {

    private static final Log log = LogFactory.getLog(JfxThemeStore.class);
    private Theme fallbackTheme;
    private Theme activeTheme;
    private JfxStylesheet stylesheet;

    public JfxThemeStore() {
        activeTheme = new Theme(new ThemeValues(), new ThemeData());
    }

    public Theme getTheme() {
        return activeTheme;
    }

    public void loadFallbackTheme(String url) {
        fallbackTheme = getThemeFromResource(url);

        if (activeTheme != null) {
            activeTheme.setFallback(fallbackTheme);
        }
    }

    public void loadActiveTheme(String url) {
        activeTheme = getThemeFromResource(url);

        if (fallbackTheme != null) {
            activeTheme.setFallback(fallbackTheme);
        }

        stylesheet = new JfxStylesheet(activeTheme);
    }

    public JfxStylesheet getStylesheet() {
        return stylesheet;
    }

    private Theme getThemeFromResource(String url) {
        RemoteFile file = getRemoteResource(url);
        if (file == null) {
            return null;
        }

        String charset = file.getCharset();
        if (charset == null) {
            charset = "UTF-8";
        }
        try {
            String json = new String(file.getContent(), charset);
            return Theme.fromJson(json);
        } catch (UnsupportedEncodingException e) {
            log.error("unsupported encoding: " + charset, e);
        }

        return null;
    }

    private static RemoteFile getRemoteResource(String relative) {
        Transport tp = ApplicationGlobals.getInstance().getLifeCycle().getTransport();
        URL url = tp.getContextURL(relative);

        if (url == null) {
            return null;
        }

        return tp.getFile(url);
    }

    public static class Handler extends ReflectiveTypeHandler<JfxThemeStore> {
        public static final String ID = "rwt.theme.ThemeStore";

        @Override
        public JfxThemeStore create(String target, Map<String, ?> properties) {
            return new JfxThemeStore();
        }

    }
}
