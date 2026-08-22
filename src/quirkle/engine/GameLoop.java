package quirkle.engine;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameLoop implements ActionListener {
    private final RenderPanel gamePanel;
    private final Timer timer;

    private static final double WARNING_THRESHOLD_MILLIS = 20.0;
    private static final double MAX_DT_SECONDS = 0.5;

    private final double expectedIntervalMillis;
    private long lastTickNanoSec;

    public GameLoop(RenderPanel gamePanel) {
        this.gamePanel = gamePanel;
        this.expectedIntervalMillis = 1000 / EngineConfig.FPS;
        this.timer = new Timer( 1000 / EngineConfig.FPS, this);
    }

    public void start() { timer.start(); }
    public void stop() { timer.stop(); }

    @Override
    public void actionPerformed(ActionEvent event) {
        long now = System.nanoTime();
        double dt = (now - lastTickNanoSec) / 1_000_000_000.0;
        lastTickNanoSec = now;

        double realMilliSec = dt * 1000;
        double behindByMilliSec = realMilliSec - expectedIntervalMillis;

        if (behindByMilliSec > WARNING_THRESHOLD_MILLIS) {
            if (!EngineConfig.SUPPRES_WARNINGS) System.err.println("[WARNING] resourceEngine / GameLoop: tick behind by " + behindByMilliSec + " milliseconds");
        }

        if (dt > MAX_DT_SECONDS) {
            if (!EngineConfig.SUPPRES_WARNINGS) System.err.println("[WARNING] resourceEngine / GameLoop: deltaTime clamped from " + realMilliSec + "ms to  " + MAX_DT_SECONDS*1000.0 + "ms");
            dt = MAX_DT_SECONDS;
        }

        SceneManager.update(dt);
        gamePanel.repaint();
    }
}