package quirkle.game.gamePlay.hand;

import quirkle.engine.Entity;
import quirkle.engine.InputManager;
import quirkle.game.gamePlay.player.Player;
import quirkle.game.gamePlay.tiles.Tile;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * The bar along the bottom of the screen showing the current player's tiles.
 *
 * @note Lives in screen space: it deliberately ignores the board camera so it stays put while
 *       the board is panned and zoomed.
 * @implNote Draws its tiles itself instead of registering them as scene entities. That keeps them
 *           glued to the panel's draw order and spares the scene from adding/removing six entities
 *           on every turn.
 */
public class TileRack extends Entity {
    private static final int TILE_SIZE = 72;
    private static final int TILE_GAP = 10;
    private static final int PADDING = 16;
    private static final int SELECTION_LIFT = 10;
    private static final int LABEL_GAP = 10;
    private static final float LABEL_FONT_SIZE = 26f;
    private static final int CORNER_RADIUS = 18;
    private static final int SLOT_CORNER_RADIUS = 6;

    private static final Color PANEL_COLOR = new Color(38, 42, 52, 225);
    private static final Color BORDER_COLOR = new Color(255, 255, 255, 60);
    private static final Color EMPTY_SLOT_COLOR = new Color(0, 0, 0, 70);
    private static final Color LABEL_COLOR = new Color(38, 42, 52);
    private static final String LABEL_FONT = "DEBUG_Poly-Regular";

    private final HandTileEntity[] slots = new HandTileEntity[Player.HAND_SIZE];

    private Player player;
    private Tile selectedTile;

    /**
     * @param centerX horizontal screen center of the rack
     * @param bottomY screen y of the rack's lower edge
     */
    public TileRack(int centerX, int bottomY) {
        this.origin = OriginPresets.BOTTOM_MID;
        this.x = centerX;
        this.y = bottomY;
        this.width = Player.HAND_SIZE * TILE_SIZE + (Player.HAND_SIZE - 1) * TILE_GAP + 2 * PADDING;
        this.height = TILE_SIZE + 2 * PADDING;
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
        drawPanel(g);
        drawLabel(g);

        for (int i = 0; i < slots.length; i++) {
            if (slots[i] == null) drawEmptySlot(g, i);
            else slots[i].render(g);
        }
    }

    private void drawPanel(Graphics2D g) {
        Graphics2D gPanel = (Graphics2D) g.create();

        gPanel.setColor(PANEL_COLOR);
        gPanel.fillRoundRect(getLeft(), getTop(), (int) width, (int) height, CORNER_RADIUS, CORNER_RADIUS);

        gPanel.setColor(BORDER_COLOR);
        gPanel.setStroke(new BasicStroke(2f));
        gPanel.drawRoundRect(getLeft(), getTop(), (int) width, (int) height, CORNER_RADIUS, CORNER_RADIUS);

        gPanel.dispose();
    }

    private void drawEmptySlot(Graphics2D g, int index) {
        Graphics2D gSlot = (Graphics2D) g.create();

        gSlot.setColor(EMPTY_SLOT_COLOR);
        gSlot.fillRoundRect(getSlotX(index) - TILE_SIZE / 2, getRestingY() - TILE_SIZE / 2, TILE_SIZE, TILE_SIZE, SLOT_CORNER_RADIUS, SLOT_CORNER_RADIUS);

        gSlot.dispose();
    }

    private void drawLabel(Graphics2D g) {
        if (player == null) return;

        String label = player.getName() + " - " + player.getScore() + " Punkte";
        drawText(label, LABEL_FONT_SIZE, LABEL_COLOR, LABEL_FONT, x, getTop() - LABEL_GAP, 0.0, OriginPresets.BOTTOM_MID, g);
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

        tileEntity.scale = TILE_SIZE / tileEntity.width;
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
            tileEntity.y = isSelected ? getRestingY() - SELECTION_LIFT : getRestingY();
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

    private int getSlotX(int index) {
        return getLeft() + PADDING + index * (TILE_SIZE + TILE_GAP) + TILE_SIZE / 2;
    }

    private int getRestingY() {
        return getTop() + PADDING + TILE_SIZE / 2;
    }

    private int getLeft() {
        return (int) (x - origin.x * width);
    }

    private int getTop() {
        return (int) (y - origin.y * height);
    }
}
