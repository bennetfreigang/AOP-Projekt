package org.quirkle.resourceEngine;

public class EngineConfig {
    public static int WINDOW_WIDTH = 1500;
    public static int WINDOW_HEIGHT = 800;
    public static String TITLE = "resourceEngine - PREVIEW";
    public static int FPS = 60;

    public static void setSize(int width, int height) {
        WINDOW_WIDTH = width;
        WINDOW_HEIGHT = height;
    }

    public static void setTitle(String title) {
        TITLE = title;
    }
}