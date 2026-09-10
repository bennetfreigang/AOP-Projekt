package quirkle.game.gameplay.hand;

import quirkle.engine.InputManager;
import quirkle.engine.extensions.NineSlice;
import quirkle.game.gameplay.player.Player;
import quirkle.game.gameplay.tiles.Tile;
import quirkle.game.gameplay.ui.Handover;
import quirkle.game.gameplay.ui.PanelEntity;
import quirkle.game.gameplay.ui.UiTheme;

import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.List;

/**
 * The current player's tiles, as a bare row of slots along the bottom of the screen.
 *
 * @note Nothing but the slots and the tiles standing in them: no panel around the row and no
 *       labels. The player's name is on the {@code TurnIndicator} above the frame, so repeating
 *       it here would only cost the tiles room.
 * @note Hands over with an animation rather than swapping in place: the outgoing player's row
 *       drops off the bottom of the window and the incoming one rises back into the band. The
 *       tiles are exchanged while the row is parked off screen, so the swap itself is never
 *       seen - what reads as one rack leaving and another arriving is the same six slots.
 * @note Lives in screen space: it deliberately ignores the board camera so it stays put while
 *       the board is panned and zoomed.
 * @implNote Draws its tiles itself instead of registering them as scene entities. That keeps them
 *           glued to the row's draw order and spares the scene from adding/removing six entities
 *           on every turn.
 */
public class TileRack extends PanelEntity {

    private final HandTileEntity[] slots = new HandTileEntity[Player.HAND_SIZE];

    /** Vertical center the row sits at while it belongs to the active player. */
    private final int restingCenterY;

    /** The HUD-wide handover clock; the row reads its phase rather than timing its own slide. */
    private final Handover handover;

    private Player player;
    private Tile selectedTile;

    /**
     * @param centerX horizontal screen center of the row of slots
     * @param centerY vertical screen center of the row of slots
     * @note The bounds are exactly the row, which is also the rack's hit area. A click just
     *       outside a slot therefore falls through to the board instead of being swallowed by a
     *       panel that is no longer there.
     */
    public TileRack(int centerX, int centerY, Handover handover) {
        this.restingCenterY = centerY;
        this.handover = handover;

        setBounds(centerX, centerY, rowWidth(), UiTheme.RACK_TILE_SIZE, OriginPresets.CENTER);
    }

    /** @return the width of the whole row: every slot plus the gaps between them. */
    private static int rowWidth() {
        return (Player.HAND_SIZE - 1) * UiTheme.RACK_TILE_SPACING + UiTheme.RACK_TILE_SIZE;
    }

    /**
     * Points the rack at {@code currentPlayer}, sliding the row out and back in if the turn has
     * passed to somebody else.
     *
     * @note Cheap enough to call every tick; it only rebuilds when the tiles actually differ.
     * @note A handover deliberately does <em>not</em> take on the new hand here. The row keeps
     *       showing the player who just finished until it is off screen, which is what makes the
     *       two racks read as two racks rather than as one that changed its mind.
     */
    public void showPlayer(Player currentPlayer) {
        if (player == null) {                       // first frame: there is nothing to hand over from
            takeOver(currentPlayer);
            return;
        }

        if (handover.isRunning()) {
            // Off screen at the turn between the halves, and therefore the one moment the
            // exchange cannot be seen. Until then the row keeps the finishing player's tiles.
            if (handover.getPhase() == Handover.Phase.ARRIVING && player != currentPlayer) {
                takeOver(currentPlayer);
            }
            return;
        }

        // Safety net for a turn that passed without the animation running at all.
        if (player != currentPlayer) takeOver(currentPlayer);

        syncSlots(currentPlayer.getHand());

        if (!currentPlayer.getHand().contains(selectedTile)) selectedTile = null;
        applySelection();
    }

    /** @return whether the row is currently sliding out or back in. */
    private boolean isChangingHands() {
        return handover.isRunning();
    }

    /** @return the tile the player picked, or {@code null} if nothing is selected. */
    public Tile getSelectedTile() {
        return selectedTile;
    }

    public void clearSelection() {
        select(null);
    }

    public void rejectSelection() {
        HandTileEntity tileEntity = findEntity(selectedTile);
        if (tileEntity != null) tileEntity.reject();
    }

    /**
     * Lets the rack react to the current frame's click.
     *
     * @return {@code true} if the click hit the rack and the scene should not also act on it
     * @note Call this before any board input handling, since {@link InputManager#isMouseClicked()}
     *       is a global flag that both would otherwise consume.
     */
    public boolean handleInput() {
        // A row in transit belongs to nobody yet; let the click through rather than swallow it.
        if (isChangingHands()) return false;
        if (!InputManager.isMouseClicked()) return false;

        for (HandTileEntity tileEntity : slots) {
            if (tileEntity == null || !tileEntity.isHovered()) continue;

            // clicking the selected tile again deselects it
            select(tileEntity.isSelected() ? null : tileEntity.getTile());
            return true;
        }

        return isHovered();
    }

    @Override
    public void onTick(double dt) {
        applySlide();
        layoutSlots();

        for (HandTileEntity tileEntity : slots) {
            if (tileEntity != null) tileEntity.update(dt);
        }
    }

    /** Puts the row where the handover clock says it should be: out of the window and back. */
    private void applySlide() {
        if (!handover.isRunning()) {
            y = restingCenterY;
            return;
        }

        int hiddenCenterY = UiTheme.rackHiddenCenterY();
        double progress = handover.getPhaseProgress();

        y = handover.getPhase() == Handover.Phase.LEAVING
                ? Handover.at(restingCenterY, hiddenCenterY, progress)
                : Handover.at(hiddenCenterY, restingCenterY, progress);
    }

    /** Puts {@code newPlayer}'s hand into the slots outright, wherever the row happens to be. */
    private void takeOver(Player newPlayer) {
        this.player = newPlayer;
        Arrays.fill(slots, null);

        syncSlots(newPlayer.getHand());

        selectedTile = null;
        applySelection();
    }

    /**
     * Puts every tile on its slot.
     *
     * @note Run every tick, not just when a slot is filled: the tiles have to ride along while
     *       the row slides, and they take their position from it.
     */
    private void layoutSlots() {
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] != null) layoutSlot(i);
        }
    }

    @Override
    public void onRender(Graphics2D g) {
        // Every slot gets its socket, so the row keeps its shape as the hand empties out.
        for (int i = 0; i < slots.length; i++) {
            drawSlot(g, i);
        }

        for (HandTileEntity tileEntity : slots) {
            if (tileEntity != null) tileEntity.render(g);
        }
    }

    /** Draws the socket of slot {@code index}: the same brush frame whether or not a tile sits in it. */
    private void drawSlot(Graphics2D g, int index) {
        int size = UiTheme.RACK_TILE_SIZE;
        int left = (int) (getSlotX(index) - size / 2);
        int top = getRestingY() - size / 2;

        Graphics2D gSlot = (Graphics2D) g.create();
        gSlot.setColor(UiTheme.SLOT_FILL);
        gSlot.fillRect(left, top, size, size);
        gSlot.dispose();

        NineSlice.draw(g, UiTheme.FRAME_SLOT, left, top, size, size,
                UiTheme.CONTAINER_SOURCE_INSET, UiTheme.SLOT_BORDER);
    }

    private void syncSlots(List<Tile> hand) {
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] != null && !hand.contains(slots[i].getTile())) slots[i] = null;
        }

        for (Tile tile : hand) {
            if (findEntity(tile) != null) continue;

            int freeSlot = firstFreeSlot();
            if (freeSlot < 0) break;

            slots[freeSlot] = new HandTileEntity(tile);
        }
    }

    private void layoutSlot(int index) {
        HandTileEntity tileEntity = slots[index];

        tileEntity.setTileSize(UiTheme.RACK_TILE_SIZE);
        tileEntity.x = getSlotX(index);
        tileEntity.y = getRestingY();
    }

    private void select(Tile tile) {
        this.selectedTile = tile;
        applySelection();
    }

    /**
     * @note A selected tile stays in its slot; the outline
     *       {@link HandTileEntity#onRender(Graphics2D)} draws is the only thing that marks it.
     */
    private void applySelection() {
        for  (HandTileEntity tileEntity : slots) {
            if (tileEntity == null) continue;

            tileEntity.setSelected(tileEntity.getTile() == selectedTile);
        }
    }

    private HandTileEntity findEntity(Tile tile) {
        if (tile == null) return null;

        for (HandTileEntity tileEntity : slots) {
            if (tileEntity != null && tileEntity.getTile() == tile) return tileEntity;
        }
        return null;
    }

    private int firstFreeSlot() {
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] == null) return i;
        }
        return -1;
    }

    /** @return the screen x of the center of slot {@code index}, laid out around the panel center. */
    private double getSlotX(int index) {
        double firstSlotX = x - (Player.HAND_SIZE - 1) * UiTheme.RACK_TILE_SPACING / 2;
        return firstSlotX + index * UiTheme.RACK_TILE_SPACING;
    }

    /** @return the screen y every slot is centered on; the row is one slot tall. */
    private int getRestingY() {
        return (int) y;
    }
}
