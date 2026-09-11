package quirkle.game.gamePlay.ui;

import quirkle.engine.Entity;
import quirkle.game.gamePlay.Game;
import quirkle.game.gamePlay.player.Player;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class PlayerCardColumn extends Entity {

    private final Game game;
    private final Handover handover;

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

    @Override
    public void onRender(Graphics2D g) {
        for (PlayerCard card : cards) {
            card.render(g);
        }
    }

    private void layoutAtRest() {
        int playerCount = cards.size();

        for (int index = 0; index < playerCount; index++) {
            PlayerCard card = cards.get(index);
            int slot = slotOf(index, game.getCurrentPlayerIndex());

            card.visible = !isActiveSlot(slot);
            if (card.visible) card.setTop(slotTop(slot));
        }
    }

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

    private int slotOf(int playerIndex, int activeIndex) {
        return Math.floorMod(playerIndex - activeIndex - 1, cards.size());
    }

    private boolean isActiveSlot(int slot) {
        return slot == cards.size() - 1;
    }

    private int slotTop(int slot) {
        return UiTheme.CARD_TOP + slot * UiTheme.CARD_SPACING;
    }
}
