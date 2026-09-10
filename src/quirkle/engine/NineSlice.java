package quirkle.engine;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Draws a border sprite at an arbitrary size without smearing its stroke.
 *
 * @note The brush-stroke frames in {@code textures/Ui} are square (1123x1118) and their stroke is
 *       about a tenth of that wide. Stretching one straight onto a 5:1 bar would smear it into
 *       wide bands, and keeping it at source scale would swamp a small panel. So the sprite is
 *       cut into nine fields: the corners are scaled to {@code targetInset} and drawn as a block,
 *       the edges are stretched along one axis only and the middle fills the rest. The stroke
 *       then reads at the same weight on a 231px card and on a 1048px bar.
 */
public final class NineSlice {

    private NineSlice() {}

    /**
     * Draws {@code spriteIdentifier} into the rectangle at ({@code x}, {@code y}).
     *
     * @param sourceInset how much of the sprite's edge is frame rather than fill, in source pixels
     * @param targetInset how thick that frame should end up on screen, in screen pixels
     */
    public static void draw(Graphics2D g, String spriteIdentifier,
                            int x, int y, int width, int height, int sourceInset, int targetInset) {
        draw(g, AssetManager.getTexture(spriteIdentifier), x, y, width, height, sourceInset, targetInset);
    }

    /** @see #draw(Graphics2D, String, int, int, int, int, int, int) */
    public static void draw(Graphics2D g, BufferedImage sprite,
                            int x, int y, int width, int height, int sourceInset, int targetInset) {
        if (sprite == null || width <= 0 || height <= 0) return;

        int sourceWidth = sprite.getWidth();
        int sourceHeight = sprite.getHeight();

        int source = Math.min(sourceInset, Math.min(sourceWidth, sourceHeight) / 2);
        // Shrink the border along with the panel, so two of them always fit inside it.
        int target = Math.min(targetInset, Math.min(width, height) / 2);

        if (source <= 0 || target <= 0) {
            g.drawImage(sprite, x, y, width, height, null);
            return;
        }

        // Column and row edges, in the source sprite and on screen.
        int[] sourceX = { 0, source, sourceWidth - source, sourceWidth };
        int[] sourceY = { 0, source, sourceHeight - source, sourceHeight };
        int[] targetX = { x, x + target, x + width - target, x + width };
        int[] targetY = { y, y + target, y + height - target, y + height };

        for (int column = 0; column < 3; column++) {
            for (int row = 0; row < 3; row++) {
                g.drawImage(sprite,
                        targetX[column], targetY[row], targetX[column + 1], targetY[row + 1],
                        sourceX[column], sourceY[row], sourceX[column + 1], sourceY[row + 1],
                        null);
            }
        }
    }
}
