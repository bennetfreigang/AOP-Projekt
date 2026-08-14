package org.quirkle;

import org.quirkle.resourceEngine.*;
import org.quirkle.scenes.ExampleScene;


import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EngineConfig.setTitle("resourceEngine - DEMO");
            EngineConfig.setSize(1500, 800);
            EngineConfig.FPS = 120;

            JFrame frame = new JFrame(EngineConfig.TITLE);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            SceneManager.setScene(new ExampleScene());

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