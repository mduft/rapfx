/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.widgets.jfx;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javafx.application.Platform;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Represents a System Tray Icon, which can be used to display state information. A Menu with an
 * "Exit" item is always attached to the icon.
 */
public class JfxTrayIcon {

    public enum Icon {
        NETWORK("resources/netapplet.png"), WARNING("resources/warning.png");

        private Image image;

        Icon(String path) {
            try {
                this.image = ImageIO.read(getClass().getClassLoader().getResourceAsStream(path));
            } catch (IOException e) {
                log.warn("failed to load " + path, e);
            }
        }
    }

    private static final Log log = LogFactory.getLog(JfxTrayIcon.class);
    private final SystemTray tray;
    private TrayIcon icon;
    private PopupMenu menu;
    private MenuItem exit;

    public JfxTrayIcon() {
        if (!SystemTray.isSupported()) {
            log.warn("system tray not supported!");
            this.tray = null;
        } else {
            this.tray = SystemTray.getSystemTray();
            this.menu = new PopupMenu();
            this.exit = new MenuItem("Exit");
            this.menu.add(exit);
            this.exit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    hide(); // important, otherwise AWT keeps application alive!
                    Platform.exit();
                }
            });
        }
    }

    /**
     * Updates the existing icon with the new image and tooltip, or creates a new icon if none
     * exists yet.
     * 
     * @param image
     *            the image to use
     * @param tooltip
     *            the tooltip to display
     */
    public void update(Icon image, String tooltip) {
        if (tray == null) {
            return;
        }

        if (icon == null) {
            icon = new TrayIcon(image.image, tooltip, menu);
            try {
                tray.add(icon);
                icon.setImageAutoSize(true);
            } catch (AWTException e) {
                log.warn("failed to add tray icon", e);
            }
        } else {
            if (!icon.getToolTip().equals(tooltip)) {
                icon.setToolTip(tooltip);
            }
            if (!icon.getImage().equals(image.image)) {
                icon.setImage(image.image);
            }
        }
    }

    /**
     * Hides the icon if it is showing. Nothing otherwise.
     */
    public void hide() {
        if (icon != null) {
            tray.remove(icon);
            icon = null;
        }
    }
}
