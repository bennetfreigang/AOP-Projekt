package quirkle.game.gamePlay.hand;

import quirkle.engine.InputManager;
import quirkle.engine.NineSlice;
import quirkle.game.gamePlay.player.Player;
import quirkle.game.gamePlay.tiles.Tile;
import quirkle.game.gamePlay.ui.PanelEntity;
import quirkle.game.gamePlay.ui.UiTheme;

import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.List;

/**
 * The bar along the bottom of the screen showing the current player's tiles, their score and
 * what they scored last turn.
 *
 * @note Lives in screen space: it deliberately ignores the board camera so it stays put while
 *       the board is panned and zoomed.
 * @implNote Draws its tiles itself instead of registering them as scene entities. That keeps them
 *           glued to the panel's draw order and spares the scene from adding/removing six entities
 *           on every turn.
 */
public class TileRack extends PanelEntity {

    /** Vertical center of the tile row, measured from the panel's top edge. */
    private static final int SLOT_CENTER_OFFSET_Y = 127;
    private static final int LABEL_LINE_HEIGHT = 27;

    private final HandTileEntity[] slots = new HandTileEntity[Player.HAND_SIZE];

    private Player player;
    private Tile selectedTile;

    /**
     * @param centerX horizontal screen center of the rack
     * @param bottomY screen y of the rack's lower edge
     */
    public TileRack(int centerX, int bottomY) {
        setFrame(UiTheme.FRAME_RACK);
        setBounds(centerX, bottomY, UiTheme.RACK_WIDTH, UiTheme.RACK_HEIGHT, OriginPresets.BOTTOM_MID);
    }

    /**
     * Points the rack at {@code currentPlayer} and rebuilds it if their hand changed.
     *
     * @note Cheap enough to call every tick; it only rebuilds when the tiles actually differ.
     */
    public void showPlayer(Player currentPlayer) {
        boolean playerChanged = this.player != currentPlayer;
        if (playerChanged && matchesHand(currentPlayer.getHand())) return;

        if (playerChanged) {
            this.player = currentPlayer;
            Arrays.fill(slots, null);
        }

        syncSlots(currentPlayer.getHand());

        if (!currentPlayer.getHand().contains(selectedTile)) selectedTile = null;
        applySelection();
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
        for (HandTileEntity tileEntity : slots) {
            if (tileEntity != null) tileEntity.update(dt);
        }
    }

    @Override
    public void onRender(Graphics2D g) {
        drawFrame(g);
        drawLabels(g);

        for (int i = 0; i < slots.length; i++) {
            if (slots[i] == null) drawEmptySlot(g, i);
            else slots[i].render(g);
        }
    }

    /** Draws an empty slot as the same brush frame as a filled one, only without a tile. */
    private void drawEmptySlot(Graphics2D g, int index) {
        int size = UiTheme.RACK_TILE_SIZE;
        int left = getSlotX(index) - size / 2;
        int top = getRestingY() - size / 2;

        Graphics2D gSlot = (Graphics2D) g.create();
        gSlot.setColor(UiTheme.SLOT_FILL);
        gSlot.fillRect(left, top, size, size);
        gSlot.dispose();

        NineSlice.draw(g, UiTheme.FRAME_SLOT, left, top, size, size,
                UiTheme.CONTAINER_SOURCE_INSET, UiTheme.SLOT_BORDER);
    }

    /**
     * Draws the current player's name on the left and their scores on the right.
     *
     * @note Both sit inside the panel, unlike the old label that floated above it.
     */
    private void drawLabels(Graphics2D g) {
        if (player == null) return;

        int textY = getTop() + UiTheme.RACK_PADDING_Y;

        drawText(player.getName().toUpperCase(), UiTheme.FONT_SIZE_RACK, UiTheme.TEXT, UiTheme.FONT,
                getLeft() + UiTheme.RACK_PADDING_X, textY, 0.0, OriginPresets.TOP_LEFT, g);

        int rightX = getRight() - UiTheme.RACK_PADDING_X;

        drawText(UiTheme.text("points") + ": " + player.getScore(),
                UiTheme.FONT_SIZE_RACK, UiTheme.TEXT, UiTheme.FONT,
                rightX, textY, 0.0, OriginPresets.TOP_RIGHT, g);

        drawText(UiTheme.text("points_last_round") + ": +" + player.getLastRoundScore(),
                UiTheme.FONT_SIZE_RACK, UiTheme.TEXT, UiTheme.FONT,
                rightX, textY + LABEL_LINE_HEIGHT, 0.0, OriginPresets.TOP_RIGHT, g);
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
            layoutSlot(freeSlot);
        }
    }

    private void layoutSlot(int index) {
        HandTileEntity tileEntity = slots[index];

        tileEntity.scale = UiTheme.RACK_TILE_SIZE / tileEntity.width;
        tileEntity.x = getSlotX(index);
        tileEntity.y = getRestingY();
    }

    private void select(Tile tile) {
        this.selectedTile = tile;
        applySelection();
    }

    private void applySelection() {
        for  (HandTileEntity tileEntity : slots) {
            if (tileEntity == null) continue;

            boolean isSelected = tileEntity.getTile() == selectedTile;

            tileEntity.setSelected(isSelected);
            tileEntity.y = isSelected ? getRestingY() - UiTheme.RACK_SELECTION_LIFT : getRestingY();
        }
    }

    /** @return {@code true} if the rack already shows exactly {@code hand}, compared by identity. */
    private boolean matchesHand(List<Tile> hand) {
        int occupiedSlots = 0;

        for (HandTileEntity tileEntity : slots) {
            if (tileEntity == null) continue;

            occupiedSlots++;
            if (!hand.contains(tileEntity.getTile())) return false;
        }

        return occupiedSlots == hand.size();
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
    private int getSlotX(int index) {
        int firstSlotX = x - (Player.HAND_SIZE - 1) * UiTheme.RACK_TILE_SPACING / 2;
        return firstSlotX + index * UiTheme.RACK_TILE_SPACING;
    }

    private int getRestingY() {
        return getTop() + SLOT_CENTER_OFFSET_Y;
    }
}
