package quirkle.game.startMenu.entities;

import java.awt.Color;
import java.awt.Graphics2D;

import quirkle.engine.*;

import quirkle.game.startMenu.entities.*;
import quirkle.game.startMenu.scenes.*;


public class Title extends Entity {

    String title;

    public Title(String title) {
        this.title = title;
        setSprite("startmenu/gold_titleBg");
        scale = 0.8;
        origin = OriginPresets.CENTER;
        x = SceneManager.getCurrentScene().getCenterX();
        y = (int) (SceneManager.getCurrentScene().getHeight() / 2 / 1.8);
    }

    @Override
    public void onRender(Graphics2D g) {
        drawText(AssetManager.getMessage("title"), 220, Color.white, "edosz", x, y, 0.0, OriginPresets.CENTER, g);
    }
}
