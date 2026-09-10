package quirkle.game.gameplay.ui;

import quirkle.engine.Entity;
import quirkle.game.gameplay.Game;
import quirkle.game.gameplay.player.Player;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 * The queue of waiting players down the left edge, topmost card first in line.
 *
 * @note Ordered by turn order rather than by the order the players were created in, so the card
 *       at the top really is whoever plays next. That is what makes the handover legible: when a
 *       turn ends the top card is the one taking over, and it leaves off the top while everybody
 *       below moves up a slot and the player who just finished joins at the bottom.
 * @note The player whose turn it is has no card - they are the one the banner names. That leaves
 *       one slot fewer than there are players, which is exactly the room the queue needs.
 * @implNote Builds one card per player once and only repositions and hides them afterwards, so
 *           switching turns allocates nothing.
 */
public class PlayerCardColumn extends Entity {

    private final Game game;
    private final Handover handover;

    /** One card per player, in {@link Game#getPlayers()} order, so the list index is the player index. */
    private final List<PlayerCard> cards = new ArrayList<>();

    public PlayerCardColumn(Game game, Handover handover) {
        this.game = game;
        this.handover = handover;
        this.renderOrder = UiTheme.LAYER_HUD;

        for (Player player : game.getPlayers()) {
            cards.add(new PlayerCard(player));
        }

        // Lay the queue out straight away: the window can paint before the first tick runs.
        layoutAtRest();
    }

    @Override
    public void onTick(double dt) {
        if (handover.isRunning()) layoutHandover();
        else layoutAtRest();
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

    /** Puts every waiting player on their slot and takes the active player's card off screen. */
    private void layoutAtRest() {
        int playerCount = cards.size();

        for (int index = 0; index < playerCount; index++) {
            PlayerCard card = cards.get(index);
            int slot = slotOf(index, game.getCurrentPlayerIndex());

            card.visible = !isActiveSlot(slot);
            if (card.visible) card.setTop(slotTop(slot));
        }
    }

    /**
     * Slides the whole queue up by one slot.
     *
     * @note Every card is on screen for the length of the handover, one more than the queue has
     *       slots for: the player who just took over is still leaving off the top while the one
     *       who just finished rises into the bottom slot.
     */
    private void layoutHandover() {
        int playerCount = cards.size();
        int previousPlayerIndex = Math.floorMod(game.getCurrentPlayerIndex() - 1, playerCount);
        double progress = handover.getTotalProgress();

        for (int index = 0; index < playerCount; index++) {
            PlayerCard card = cards.get(index);

            int fromSlot = slotOf(index, previousPlayerIndex);
            int toSlot = slotOf(index, game.getCurrentPlayerIndex());

            // Whoever lands on the active slot is the player taking their turn: they leave the
            // column altogether rather than wrapping around to the bottom of the queue.
            double toTop = isActiveSlot(toSlot) ? UiTheme.CARD_EXIT_TOP : slotTop(toSlot);

            card.visible = true;
            card.setTop((int) Math.round(Handover.at(slotTop(fromSlot), toTop, progress)));
        }
    }

    /**
     * @param playerIndex   the player being placed
     * @param activeIndex   whose turn the arrangement is for
     * @return how far down the queue that player stands, {@code 0} being next in line
     * @note The player whose turn it is comes out last, one past the queue - see
     *       {@link #isActiveSlot(int)}.
     */
    private int slotOf(int playerIndex, int activeIndex) {
        return Math.floorMod(playerIndex - activeIndex - 1, cards.size());
    }

    /** @return whether {@code slot} is the one belonging to the player whose turn it is. */
    private boolean isActiveSlot(int slot) {
        return slot == cards.size() - 1;
    }

    /** @return the top edge of the card standing at {@code slot}. */
    private int slotTop(int slot) {
        return UiTheme.CARD_TOP + slot * UiTheme.CARD_SPACING;
    }
}
