package org.quirkle.resourceEngine;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class RenderPanel extends JPanel {

    public RenderPanel() {
        setPreferredSize(new Dimension(EngineConfig.WINDOW_WIDTH, EngineConfig.WINDOW_HEIGHT));
        setBackground(Color.WHITE);
        setFocusable(true);

        InputManager input = InputManager.getInstance();
        addMouseListener(input);
        addMouseMotionListener(input);
        addKeyListener(InputManager.getKeyHandler());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        SceneManager.render(g2d);
        g2d.dispose();
    }
}