package org.quirkle.resourceEngine;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameLoop implements ActionListener {
    private final RenderPanel panel;
    private final Timer timer;
    private final double deltaTime;

    public GameLoop(RenderPanel panel) {
        this.panel = panel;
        this.deltaTime = 1.0 / EngineConfig.FPS;
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
        SceneManager.update(deltaTime);
        panel.repaint();
    }
}