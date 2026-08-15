package org.quirkle.resourceEngine;

import java.awt.Color;

/************************************************************************
 * Basic Default-Setup Class for resourceEngine presets
 ***********************************************************************/
public class EngineConfig {
    public static int WINDOW_WIDTH = 1920;
    public static int WINDOW_HEIGHT = 1080;
    public static String TITLE = "resourceEngine - PREVIEW";
    public static Color BACKGROUND_COLOR = Color.WHITE;
    public static int FPS = 120;
    public static double VOLUME_MAIN = 1.0; //0.0 - 1.0 multiplyier

    public static void setSize(int width, int height) {
        WINDOW_WIDTH = width;
        WINDOW_HEIGHT = height;
    }

    public static void setTitle(String title) {
        TITLE = title;
    }
}