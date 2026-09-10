package quirkle.game.gamePlay.ui;

import quirkle.engine.AssetManager;
import quirkle.engine.EngineConfig;
import quirkle.engine.Entity;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * The frame the board is seen through: a full-window overlay whose center is transparent.
 *
 * @note Draws nothing but the artwork. What actually hides the board outside the opening is the
 *       clip {@link quirkle.game.gamePlay.board.BoardView} applies, since the frame's border is
 *       opaque and would otherwise only paint over tiles that were already drawn. Both read the
 *       same rectangle from {@link UiTheme#boardViewport()}, so they can never drift apart.
 * @implNote Scales the 4608x2592 asset to window size once, at construction, rather than on every
 *           frame. Rescaling twelve megapixels 120 times a second would be by far the most
 *           expensive thing this scene does, and the result never changes.
 */
public class BoardFrame extends Entity {

    /** The artwork at window size, ready to be blitted 1:1. */
    private final BufferedImage scaledFrame;

    public BoardFrame() {
        this.renderOrder = UiTheme.LAYER_FRAME;
        this.scaledFrame = scaleToWindow(AssetManager.getTexture(UiTheme.FRAME_BOARD));
    }

    @Override
    public void onRender(Graphics2D g) {
        g.drawImage(scaledFrame, 0, 0, null);
    }

    /**
     * @return {@code source} redrawn at window size, keeping its alpha
     * @note Bilinear rather than the default nearest neighbour: this is a 12:5 downscale, which
     *       nearest neighbour would turn into a ragged edge along the opening.
     */
    private static BufferedImage scaleToWindow(BufferedImage source) {
        int width = EngineConfig.WINDOW_WIDTH;
        int height = EngineConfig.WINDOW_HEIGHT;

        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(source, 0, 0, width, height, null);
        g.dispose();

        return scaled;
    }
}
