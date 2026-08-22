package quirkle;

import quirkle.engine.*;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EngineConfig.setTitle("DEMO");
            EngineConfig.setSize(1920, 1080);
            EngineConfig.BACKGROUND_COLOR = Color.BLACK;
            EngineConfig.FPS = 120;
            EngineConfig.DEFAULT_LANG_IDENTIFIER = "de";

            EngineConfig.SUPPRES_WARNINGS = false;

            AssetManager.setLang(EngineConfig.DEFAULT_LANG_IDENTIFIER);

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
