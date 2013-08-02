/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.widgets.jfx;

import java.util.List;

import javafx.scene.control.Control;
import javafx.scene.control.Labeled;
import javafx.scene.control.Tooltip;

public abstract class JfxControlObject<T extends Control> extends JfxNodeObject<T> {

    /**
     * @param tooltip
     *            the new tooltip for the control
     */
    public void setToolTip(String tooltip) {
        getNode().setTooltip(new Tooltip(tooltip));
    }

    public void setFont(Object raw) {
        Control node = getNode();
        if (node instanceof Labeled) {
            Labeled labeled = (Labeled) node;
            labeled.setFont(JfxTypeHelper.getFont(raw));
        }
    }

    public void setBounds(List<Double> raw) {
        Control node = getNode();
        node.setLayoutX(raw.get(0));
        node.setLayoutY(raw.get(1));
        node.setPrefSize(raw.get(2), raw.get(3));
    }

}
