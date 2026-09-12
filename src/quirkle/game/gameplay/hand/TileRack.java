package quirkle.game.gameplay.hand;

import quirkle.engine.AssetManager;
import quirkle.engine.InputManager;
import quirkle.engine.extensions.RecolorUtil;
import quirkle.game.gameplay.player.Player;
import quirkle.game.gameplay.tiles.Tile;
import quirkle.game.gameplay.ui.Handover;
import quirkle.game.gameplay.ui.PanelEntity;
import quirkle.game.gameplay.ui.UiTheme;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

public class TileRack extends PanelEntity {

    private final HandTileEntity[] slots = new HandTileEntity[Player.HAND_SIZE];

    private final BufferedImage slotFrame;
    private final BufferedImage selectedSlotFrame;
    private final BufferedImage rejectedSlotFrame;

    private final int restingCenterY;

    private final Handover handover;

    private Player player;
    private Tile selectedTile;

    public TileRack(int centerX, int centerY, Handover handover) {
        this.restingCenterY = centerY;
        this.handover = handover;

        // Tinted once here rather than per frame; a slot only swaps which of the three it draws.
        this.slotFrame = AssetManager.getTexture(UiTheme.SPRITE_SLOT_FRAME);
        this.selectedSlotFrame = RecolorUtil.tint(slotFrame, UiTheme.SELECTION);
        this.rejectedSlotFrame = RecolorUtil.tint(slotFrame, UiTheme.REJECTION);

        setBounds(centerX, centerY, rackWidth(), UiTheme.RACK_FRAME_SIZE, OriginPresets.CENTER);
    }

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

    private boolean isChangingHands() {
        return handover.isRunning();
    }

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

    private void takeOver(Player newPlayer) {
        this.player = newPlayer;
        Arrays.fill(slots, null);

        syncSlots(newPlayer.getHand());

        selectedTile = null;
        applySelection();
    }

    private void layoutSlots() {
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] != null) layoutSlot(i);
        }
    }

    @Override
    public void onRender(Graphics2D g) {
        // Every slot gets its own frame, so the row keeps its shape as the hand empties out.
        for (int i = 0; i < slots.length; i++) {
            drawSlotFrame(g, i);
        }

        for (HandTileEntity tileEntity : slots) {
            if (tileEntity != null) tileEntity.render(g);
        }
    }

    private void drawSlotFrame(Graphics2D g, int index) {
        int size = UiTheme.RACK_FRAME_SIZE;
        int left = (int) getSlotX(index) - size / 2;
        int top = getRestingY() - size / 2;

        g.drawImage(slotFrameFor(index), left, top, size, size, null);
    }

    /**
     * @return the frame slot {@code index} is drawn with: red while the tile in it is being
     *         refused, yellow while it is the picked one, plain otherwise
     * @note Refusal wins over selection, the two being the same tile: only the picked tile can be
     *       turned down, and the refusal is the newer thing to say about it.
     */
    private BufferedImage slotFrameFor(int index) {
        HandTileEntity tileEntity = slots[index];
        if (tileEntity == null) return slotFrame;

        if (tileEntity.isRejecting()) return rejectedSlotFrame;

        return tileEntity.isSelected() ? selectedSlotFrame : slotFrame;
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

    private double getSlotX(int index) {
        double firstSlotX = x - (Player.HAND_SIZE - 1) * UiTheme.RACK_TILE_SPACING / 2;
        return firstSlotX + index * UiTheme.RACK_TILE_SPACING;
    }

    private int getRestingY() {
        return (int) y;
    }

    /** @return the width the row of slot frames covers, which is also the rack's hover area */
    private static int rackWidth() {
        return (Player.HAND_SIZE - 1) * UiTheme.RACK_TILE_SPACING + UiTheme.RACK_FRAME_SIZE;
    }
}
