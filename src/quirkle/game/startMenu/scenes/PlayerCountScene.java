package quirkle.game.startMenu.scenes;

import quirkle.engine.AssetManager;
import quirkle.engine.Scene;
import quirkle.engine.SceneManager;
import quirkle.game.gamePlay.GamePlayScene;
import quirkle.game.gamePlay.player.Player;
import quirkle.game.gamePlay.ui.UiTheme;
import quirkle.game.startMenu.entities.StartMenuButton;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class PlayerCountScene extends Scene {

    private static final String[] PLAYERNAMES = {
            "Jarosch", "Bennet", "Alex", "Rambo Ramon Rainer",
            "Gandolf Merlin", "Dragon Dinoso Degen", "Hajo Donovan Benvenuto"
    };

    private int playerCount;
    private int[] selectedIndices;

    private StartMenuButton countButton;
    private StartMenuButton[] leftArrows;
    private StartMenuButton[] rightArrows;
    private StartMenuButton startButton;
    private StartMenuButton backButton;

    @Override
    public void onCreate() {
        playerCount = 2;
        selectedIndices = new int[]{0, 1, 2, 3};
        leftArrows = new StartMenuButton[4];
        rightArrows = new StartMenuButton[4];

        int centerX = getCenterX();


        countButton = new StartMenuButton("Player: " + playerCount);
        countButton.x = centerX;
        countButton.y = 330;
        addEntities(countButton);


        int slotStartY = 440;
        int slotSpacingY = 85;
        int arrowDistance = 350;

        for (int i = 0; i < 4; i++) {
            StartMenuButton left = new StartMenuButton("<");
            left.minSize = 0.5;
            left.maxSizeModifier = 0.55;
            left.x = centerX - arrowDistance;
            left.y = slotStartY + i * slotSpacingY;
            leftArrows[i] = left;

            StartMenuButton right = new StartMenuButton(">");
            right.minSize = 0.5;
            right.maxSizeModifier = 0.55;
            right.x = centerX + arrowDistance;
            right.y = slotStartY + i * slotSpacingY;
            rightArrows[i] = right;

            addEntities(left, right);
        }

        startButton = new StartMenuButton("Play");
        startButton.x = centerX;
        startButton.y = 830;

        backButton = new StartMenuButton("Back");
        backButton.x = centerX;
        backButton.y = 930;

        addEntities(startButton, backButton);

        updateVisibility();
    }

    @Override
    public void onTick(double dt) {
        if (countButton != null && countButton.isClicked()) {
            playerCount++;
            if (playerCount > 4) {
                playerCount = 2;
            }
            updateCountButtonNumber();
            updateVisibility();
        }

        if (leftArrows != null && rightArrows != null) {
            for (int i = 0; i < playerCount; i++) {
                if (leftArrows[i] != null && leftArrows[i].isClicked()) {
                    selectedIndices[i] = Math.floorMod(selectedIndices[i] - 1, PLAYERNAMES.length);
                }
                if (rightArrows[i] != null && rightArrows[i].isClicked()) {
                    selectedIndices[i] = (selectedIndices[i] + 1) % PLAYERNAMES.length;
                }
            }
        }

        if (backButton != null && backButton.isClicked()) {
            SceneManager.setScene(new StartMenuScene());
        }

        if (startButton != null && startButton.isClicked()) {
            List<Player> players = new ArrayList<>();
            for (int i = 0; i < playerCount; i++) {
                players.add(new Player(PLAYERNAMES[selectedIndices[i]]));
            }
            SceneManager.setScene(new GamePlayScene(players));
        }
    }

    private void updateCountButtonNumber() {
        int centerX = getCenterX();
        removeEntities(countButton);
        countButton = new StartMenuButton("Player: " + playerCount);
        countButton.x = centerX;
        countButton.y = 330;
        addEntities(countButton);
    }

    private void updateVisibility() {
        if (leftArrows == null || rightArrows == null) return;
        for (int i = 0; i < 4; i++) {
            boolean visible = (i < playerCount);
            if (leftArrows[i] != null) leftArrows[i].visible = visible;
            if (rightArrows[i] != null) rightArrows[i].visible = visible;
        }
    }

    @Override
    public void onRender(Graphics2D g) {
        int centerX = getCenterX();

        Font titleFont = AssetManager.getFont(UiTheme.FONT).deriveFont(70f);
        g.setFont(titleFont);
        g.setColor(UiTheme.GOLD);
        FontMetrics tfm = g.getFontMetrics(titleFont);
        String title = "Number of Players";
        g.drawString(title, centerX - tfm.stringWidth(title) / 2, 180);

        if (selectedIndices == null) return;
        int slotStartY = 440;
        int slotSpacingY = 85;

        Font font = AssetManager.getFont(UiTheme.FONT).deriveFont(30f);
        g.setFont(font);
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics(font);

        for (int i = 0; i < playerCount; i++) {
            String label = "Player " + (i + 1) + ": " + PLAYERNAMES[selectedIndices[i]];
            int textY = slotStartY + i * slotSpacingY;
            int textWidth = fm.stringWidth(label);
            int textAscent = fm.getAscent();
            g.drawString(label, centerX - textWidth / 2, textY + textAscent / 3);
        }
    }
}