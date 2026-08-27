package quirkle.game.gamePlay.hand;

import quirkle.engine.Entity;
import quirkle.engine.InputManager;
import quirkle.game.gamePlay.player.Player;
import quirkle.game.gamePlay.tiles.Tile;

import javax.print.attribute.standard.OrientationRequested;
import java.awt.*;
import java.util.ArrayList;
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

    private static final Color PANEL_COLOR = new Color(38, 42, 52, 225);
    private static final Color BORDER_COLOR = new Color(255, 255, 255, 60);
    private static final Color LABEL_COLOR = new Color(38, 42, 52);
    private static final String LABEL_FONT = "DEBUG_Poly-Regular";

    private final List<HandTileEntity> tileEntities = new ArrayList<>();

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
        if (this.player == currentPlayer && matchesHand(currentPlayer.getHand())) return;

        this.player = currentPlayer;
        rebuildTiles(currentPlayer.getHand());
    }

    /** @return the tile the player picked, or {@code null} if nothing is selected. */
    public Tile getSelectedTile() {
        return selectedTile;
    }

    public void clearSelection() {
        select(null);
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

        for (HandTileEntity tileEntity : tileEntities) {
            if (!tileEntity.isHovered()) continue;

            // clicking the selected tile again deselects it
            select(tileEntity.isSelected() ? null : tileEntity.getTile());
            return true;
        }

        return isHovered();
    }

    @Override
    public void onRender(Graphics2D g) {
        drawPanel(g);
        drawLabel(g);

        for  (HandTileEntity tileEntity : tileEntities) {
            tileEntity.render(g);
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

    private void drawLabel(Graphics2D g) {
        if (player == null) return;

        String label = player.getName() + " - " + player.getScore() + " Punkte";
        drawText(label, LABEL_FONT_SIZE, LABEL_COLOR, LABEL_FONT, x, getTop() - LABEL_GAP, 0.0, OriginPresets.BOTTOM_MID, g);
    }

    private void rebuildTiles(List<Tile> hand) {
        tileEntities.clear();
        for (Tile tile : hand) {
            tileEntities.add(new HandTileEntity(tile));
        }

        if (!hand.contains(selectedTile)) selectedTile = null;

        layoutTiles();
        applySelection();
    }

    private void layoutTiles() {
        for (int i = 0; i < tileEntities.size(); i++) {
            HandTileEntity tileEntity = tileEntities.get(i);

            tileEntity.scale = TILE_SIZE / tileEntity.width;
            tileEntity.x = getLeft() + PADDING + i * (TILE_SIZE + TILE_GAP) + TILE_SIZE / 2;
            tileEntity.y = getTop() + PADDING + TILE_SIZE / 2;
        }
    }

    private void select(Tile tile) {
        this.selectedTile = tile;
        applySelection();
    }

    private void applySelection() {
        int restingY = getTop() + PADDING + TILE_SIZE / 2;

        for (HandTileEntity tileEntity : tileEntities) {
            boolean isSelected = tileEntity.getTile() == selectedTile;

            tileEntity.setSelected(isSelected);
            tileEntity.y = isSelected ? restingY - SELECTION_LIFT : restingY;
        }
    }

    /** @return {@code true} if the rack already shows exactly {@code hand}, compared by identity. */
    private boolean matchesHand(List<Tile> hand) {
        if (hand.size() != tileEntities.size()) return false;

        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i) != tileEntities.get(i).getTile()) return false;
        }
        return true;
    }

    private int getLeft() {
        return (int) (x - origin.x * width);
    }

    private int getTop() {
        return (int) (y - origin.y * height);
    }
}
