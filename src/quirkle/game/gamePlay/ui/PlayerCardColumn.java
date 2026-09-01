package quirkle.game.gamePlay.ui;

import quirkle.engine.Entity;
import quirkle.game.gamePlay.Game;
import quirkle.game.gamePlay.player.Player;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 * The stack of player cards down the left edge.
 *
 * @note Shows every player <em>except</em> the one whose turn it is; that player is already
 *       represented by the rack along the bottom. With four players this reproduces the
 *       mockup's three cards, with two players a single card is left.
 * @implNote Builds one card per player once and only repositions and hides them afterwards,
 *           so switching turns allocates nothing.
 */
public class PlayerCardColumn extends Entity {

    private final Game game;
    private final List<PlayerCard> cards = new ArrayList<>();

    public PlayerCardColumn(Game game) {
        this.game = game;
        this.renderOrder = UiTheme.LAYER_HUD;

        for (Player player : game.getPlayers()) {
            cards.add(new PlayerCard(player));
        }
    }

    @Override
    public void onTick(double dt) {
        Player currentPlayer = game.getCurrentPlayer();
        int visibleIndex = 0;

        for (PlayerCard card : cards) {
            boolean isWaiting = card.getPlayer() != currentPlayer;
            card.visible = isWaiting;

            if (!isWaiting) continue;

            card.setTop(UiTheme.CARD_TOP + visibleIndex * UiTheme.CARD_SPACING);
            visibleIndex++;
        }
    }

    /**
     * @note The cards are drawn from here instead of being registered as scene entities, so the
     *       column stays a single unit the scene adds and removes in one go.
     */
    @Override
    public void onRender(Graphics2D g) {
        for (PlayerCard card : cards) {
            card.render(g);
        }
    }
}
