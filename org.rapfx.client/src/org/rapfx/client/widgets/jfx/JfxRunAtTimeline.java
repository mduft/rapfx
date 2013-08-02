/*
 * Copyright (c) Markus Duft <markus.duft@salomon.at>
 */
package org.rapfx.client.widgets.jfx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

public class JfxRunAtTimeline {

    private Timeline timeline;
    private final JfxToolkit toolkit;

    public JfxRunAtTimeline(JfxToolkit toolkit) {
        this.toolkit = toolkit;
    }

    public void runIn(final Runnable runnable, int millis) {
        if (timeline != null) {
            timeline.stop();
        }
        timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(millis)));
        timeline.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                toolkit.removeAndRun(runnable);
            }
        });
        timeline.playFromStart();
    }

}
