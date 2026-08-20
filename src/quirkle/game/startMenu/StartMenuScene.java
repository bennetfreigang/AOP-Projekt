package quirkle.game.startMenu;

import quirkle.resourceEngine.Scene;

public class StartMenuScene extends Scene {

    @Override
    public void onCreate() {
        StartMenuButton playButton = new StartMenuButton("Play");
        StartMenuButton settingsButton = new StartMenuButton("Settings");
        StartMenuButton creditsButton = new StartMenuButton("Credits");
        StartMenuButton quitButton = new StartMenuButton("Quit");

        addEntities(playButton, settingsButton, creditsButton, quitButton);

        playButton.x = getCenterX();
        playButton.y = getCenterY();
    }
}
