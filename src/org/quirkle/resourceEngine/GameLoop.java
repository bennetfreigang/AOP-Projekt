package org.quirkle.resourceEngine;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameLoop implements ActionListener {
    private final RenderPanel panel;
    private final Timer timer;

    private final double expectedIntervalMillis;
    private static final double WARNING_THRESHOLD_MILLIS = 20.0;
    private static final double MAX_DT_SECONDS = 0.5;
    private long lastTickNanos;

    public GameLoop(RenderPanel panel) {
        this.panel = panel;
        this.expectedIntervalMillis = 1000.0 / EngineConfig.FPS;
        this.timer = new Timer(1000 / EngineConfig.FPS, this);
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        long now = System.nanoTime();
        double dt = (now - lastTickNanos) / 1_000_000_000.0;
        lastTickNanos = now;
        double actualMillis = dt * 1000.0;
        double behindByMillis = actualMillis - expectedIntervalMillis;

        if (behindByMillis > WARNING_THRESHOLD_MILLIS) {
            System.err.println("[WARNING] resourceEngine / GameLoop: tick behind by " + behindByMillis + " milliseconds");
        }

        if (dt > MAX_DT_SECONDS) {
            System.err.println("[WARNING] resourceEngine / GameLoop: deltaTime clamped from " + actualMillis + "ms to  " + MAX_DT_SECONDS*1000.0 + "ms");
            dt = MAX_DT_SECONDS;
        }

        SceneManager.update(dt);
        panel.repaint();
    }
}