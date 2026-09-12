package quirkle.game.gameplay.ui;

import quirkle.engine.AssetManager;
import quirkle.engine.Entity;
import quirkle.game.gameplay.Game;
import quirkle.game.gameplay.player.Player;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 * The turn order along the top edge: whoever is to move in full size, the rest lined up behind
 * them, faded back.
 *
 * @note On a turn change the row slides one place to the left: the finished player shrinks and
 *       fades out past the left edge while reappearing at the tail, and everybody else moves up
 *       one slot, so the next player takes over the leading slot's size and weight.
 */
public class TurnIndicator extends Entity {

    /** Where one name sits: its corner, the size it is drawn at, and how solid it looks */
    private record Slot(double x, double y, double fontSize, double alpha) {

        static Slot between(Slot from, Slot to, double progress) {
            return new Slot(at(from.x, to.x, progress), at(from.y, to.y, progress),
                    at(from.fontSize, to.fontSize, progress), at(from.alpha, to.alpha, progress));
        }

        private static double at(double from, double to, double progress) {
            return from + (to - from) * progress;
        }
    }

    private final Game game;

    /** The order on screen; during a shuffle it is where the row is heading */
    private List<Player> shownOrder;

    private List<Player> previousOrder = List.of();

    private double shuffleRemaining = 0.0;

    public TurnIndicator(Game game) {
        this.game = game;
        this.renderOrder = UiTheme.LAYER_HUD;

        this.y = UiTheme.TURN_TOP;

        // Read straight away rather than waiting for the first tick: the window can paint before
        // the game loop has run a single frame.
        this.shownOrder = game.getTurnOrder();
    }

    @Override
    public void onTick(double dt) {
        if (shuffleRemaining > 0.0) shuffleRemaining = Math.max(0.0, shuffleRemaining - dt);

        List<Player> order = game.getTurnOrder();
        if (order.equals(shownOrder)) return;

        previousOrder = shownOrder;
        shownOrder = order;
        shuffleRemaining = UiTheme.TURN_SHUFFLE_SECONDS;
    }

    @Override
    public void onRender(Graphics2D g) {
        if (shownOrder.isEmpty()) return;

        List<Slot> target = layout(g, shownOrder);

        if (shuffleRemaining <= 0.0) {
            for (int i = 0; i < shownOrder.size(); i++) {
                draw(g, shownOrder.get(i), target.get(i), 1.0);
            }
            return;
        }

        double progress = shuffleProgress();
        Player finished = finishedPlayer();

        // The old row with the finished player appended: that extra slot is where their name comes
        // in from, just behind the row, so it moves up with everybody else rather than waiting at
        // its destination while the name ahead of it is still on its way.
        List<Slot> previous = layout(g, rowToSlideFrom(finished));

        for (int i = 0; i < shownOrder.size(); i++) {
            Player player = shownOrder.get(i);

            int previousSlot = player == finished
                    ? previous.size() - 1
                    : previousOrder.indexOf(player);

            // Nobody on the row before: no slot to travel from, so it just fades in where it now belongs.
            if (previousSlot < 0) {
                draw(g, player, target.get(i), progress);
                continue;
            }

            // Fades in only if it is arriving; the ones already on the row stay solid as they move.
            double fade = player == finished ? progress : 1.0;
            draw(g, player, Slot.between(previous.get(previousSlot), target.get(i), progress), fade);
        }

        if (finished != null) drawFinished(g, finished, previous, progress);
    }

    /**
     * Slides the finished player's name out to the left, shrinking to the trailing size and fading
     * as it goes.
     *
     * @note Drawn on top of its own arriving copy at the tail; the two never overlap on screen,
     *       being a row apart.
     */
    private void drawFinished(Graphics2D g, Player finished, List<Slot> previous, double progress) {
        Slot from = previous.get(0);

        // Slot 1 of the old row is already a trailing name on the same baseline, so its size and
        // vertical position are exactly what the leaving name shrinks into.
        Slot trailing = previous.get(1);
        Slot out = new Slot(from.x() - UiTheme.TURN_LEAVE_SHIFT, trailing.y(),
                trailing.fontSize(), from.alpha());

        draw(g, finished, Slot.between(from, out, progress), 1.0 - progress);
    }

    /**
     * @return the row the shuffle starts from: {@link #previousOrder}, with the finished player
     *         appended once more as the slot their name slides in from
     */
    private List<Player> rowToSlideFrom(Player finished) {
        if (finished == null) return previousOrder;

        List<Player> row = new ArrayList<>(previousOrder);
        row.add(finished);
        return row;
    }

    /**
     * @return the player whose turn just ended, or {@code null} if the row did not simply rotate by
     *         one - the debug mode can reorder the players outright, and then nothing is leaving
     */
    private Player finishedPlayer() {
        if (previousOrder.size() < 2) return null;

        Player finished = previousOrder.get(0);
        return shownOrder.get(shownOrder.size() - 1) == finished ? finished : null;
    }

    /** @return the eased 0..1 course of the shuffle */
    private double shuffleProgress() {
        double linear = 1.0 - shuffleRemaining / UiTheme.TURN_SHUFFLE_SECONDS;
        return linear * linear * (3.0 - 2.0 * linear);
    }

    /**
     * @return where each name of {@code order} sits, the leading one in full size and the rest
     *         lined up behind it
     */
    private List<Slot> layout(Graphics2D g, List<Player> order) {
        List<Slot> slots = new ArrayList<>(order.size());

        int baseline = (int) y + metrics(g, UiTheme.FONT_SIZE_TURN_CURRENT).getAscent();
        double x = UiTheme.TURN_MARGIN_X;

        for (int i = 0; i < order.size(); i++) {
            boolean isLeading = i == 0;
            float fontSize = isLeading ? UiTheme.FONT_SIZE_TURN_CURRENT : UiTheme.FONT_SIZE_TURN_NEXT;

            // Every name hangs off the leading name's baseline, whatever size it is drawn at.
            double top = baseline - metrics(g, fontSize).getAscent();
            double alpha = isLeading ? UiTheme.TEXT.getAlpha() : UiTheme.TEXT_DIMMED.getAlpha();

            slots.add(new Slot(x, top, fontSize, alpha));

            x += textWidth(g, order.get(i).getName(), fontSize)
                    + (isLeading ? UiTheme.TURN_CURRENT_GAP : UiTheme.TURN_NAME_GAP);
        }

        return slots;
    }

    /**
     * @param fade scales the slot's own alpha, for a name on its way in or out
     * @implNote Only the alpha is interpolated, not the color: {@link UiTheme#TEXT} and
     *           {@link UiTheme#TEXT_DIMMED} are the same white and differ in nothing else.
     */
    private void draw(Graphics2D g, Player player, Slot slot, double fade) {
        int alpha = (int) Math.round(Math.clamp(slot.alpha() * fade, 0.0, 255.0));
        Color color = new Color(UiTheme.TEXT.getRed(), UiTheme.TEXT.getGreen(), UiTheme.TEXT.getBlue(), alpha);

        drawText(player.getName(), (float) slot.fontSize(), color, UiTheme.FONT,
                slot.x(), slot.y(), 0.0, OriginPresets.TOP_LEFT, g);
    }

    private static int textWidth(Graphics2D g, String text, float fontSize) {
        return metrics(g, fontSize).stringWidth(text);
    }

    private static FontMetrics metrics(Graphics2D g, float fontSize) {
        Font font = AssetManager.getFont(UiTheme.FONT).deriveFont(fontSize);
        return g.getFontMetrics(font);
    }
}
