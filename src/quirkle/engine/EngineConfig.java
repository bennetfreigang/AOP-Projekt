package quirkle.engine;

import java.awt.Color;

/************************************************************************
 * Basic Default-Setup Class for resourceEngine presets
 ***********************************************************************/
public class EngineConfig {
    public final static String VERSION_INFO = "[BETA] v0.2.0";

    public static boolean SUPPRES_WARNINGS = false;

    public final static String ASSET_ORIGIN = "/assets";

    public static String TEXTURE_SUBDIR = "/textures";
    public static String FONT_SUBDIR = "/fonts";
    public static String SOUND_SUBDIR = "/sounds";
    public static String LANG_SUBDIR = "/lang";

    public static String DEFAULT_LANG_IDENTIFIER = "en";

    public static int WINDOW_WIDTH = 1920;
    public static int WINDOW_HEIGHT = 1080;
    public static String TITLE = "Qwirkle - Game";
    public static Color BACKGROUND_COLOR = Color.WHITE;
    public static int FPS = 120;
    public static double VOLUME_MAIN = 1.0; //0.0 - 1.0 multiplier

    public static void setSize(int width, int height) {
        WINDOW_WIDTH = width;
        WINDOW_HEIGHT = height;
    }

    public static void setTitle(String title) {
        TITLE = title;
    }
}