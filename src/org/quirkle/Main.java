package org.quirkle;

import org.quirkle.resourceEngine.*;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EngineConfig.setTitle("resourceEngine - DEMO");
            EngineConfig.setSize(1920, 1080);
            EngineConfig.BACKGROUND_COLOR = Color.WHITE;
            EngineConfig.FPS = 120;

            JFrame frame = new JFrame(EngineConfig.TITLE);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            SceneManager.setScene(new Scene()); //uses dummy scene to compile for now -> put in actual start scene if available
            //EXAMPLE: SceneManager.setScene(new ImplementedScene());

            RenderPanel panel = new RenderPanel();
            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            panel.requestFocusInWindow();

            GameLoop loop = new GameLoop(panel);
            loop.start();
        });
    }
}
