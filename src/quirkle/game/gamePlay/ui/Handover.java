package quirkle.game.gamePlay.ui;

/**
 * The clock the whole HUD runs a turn handover on.
 *
 * @note One instance is shared by every element that moves when the turn passes on - the rack,
 *       the banner and the column of cards. They read the same phase and the same progress, so
 *       they cannot drift apart the way three separate timers would, and retiming the handover
 *       is a change to {@link UiTheme#HANDOVER_PHASE_SECONDS} alone.
 * @note Two shapes of motion come out of it. An element that leaves and comes back - the rack,
 *       the banner - reads {@link #getPhase()} and {@link #getPhaseProgress()}, and swaps what it
 *       shows at the turn between the halves, while it is out of sight. An element that makes one
 *       continuous move - the card column shifting up a slot - reads {@link #getTotalProgress()}
 *       and ignores the halves entirely.
 */
public class Handover {

    /** Which half of the handover is playing. */
    public enum Phase {
        /** Nothing is running; every element sits at its resting place. */
        IDLE,
        /** The outgoing player's elements are moving off screen. */
        LEAVING,
        /** The incoming player's elements are moving back into place. */
        ARRIVING
    }

    /** Seconds elapsed since {@link #start()}, counted across both halves. */
    private double elapsed;

    private boolean running;

    /** Starts a handover from the top, cutting short one that is still playing. */
    public void start() {
        elapsed = 0.0;
        running = true;
    }

    /**
     * Moves the clock on by one frame.
     *
     * @note Call this exactly once per tick, before the elements read it, so they all animate off
     *       the same frame rather than off two sides of an update.
     */
    public void advance(double dt) {
        if (!running) return;

        elapsed += dt;
        if (elapsed >= 2.0 * UiTheme.HANDOVER_PHASE_SECONDS) running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public Phase getPhase() {
        if (!running) return Phase.IDLE;

        return elapsed < UiTheme.HANDOVER_PHASE_SECONDS ? Phase.LEAVING : Phase.ARRIVING;
    }

    /**
     * @return how far the current half has come, from {@code 0} to {@code 1}
     * @note Eased in opposite directions on purpose: an element leaves picking up speed, as if it
     *       were being pulled away, and comes back slowing down, as if it were settling.
     */
    public double getPhaseProgress() {
        if (!running) return 1.0;

        double half = UiTheme.HANDOVER_PHASE_SECONDS;
        double linear = clamp01(elapsed < half ? elapsed / half : elapsed / half - 1.0);

        return getPhase() == Phase.LEAVING ? easeIn(linear) : easeOut(linear);
    }

    /**
     * @return how far the handover as a whole has come, from {@code 0} to {@code 1}
     * @note Eased at both ends, since an element reading this makes one move rather than two and
     *       has no turning point to accelerate out of.
     */
    public double getTotalProgress() {
        if (!running) return 1.0;

        return smoothStep(clamp01(elapsed / (2.0 * UiTheme.HANDOVER_PHASE_SECONDS)));
    }

    /** @return the point {@code progress} of the way from {@code from} to {@code to}. */
    public static double at(double from, double to, double progress) {
        return from + (to - from) * progress;
    }

    private static double easeIn(double t) {
        return t * t;
    }

    private static double easeOut(double t) {
        double remaining = 1.0 - t;
        return 1.0 - remaining * remaining;
    }

    private static double smoothStep(double t) {
        return t * t * (3.0 - 2.0 * t);
    }

    private static double clamp01(double value) {
        return Math.clamp(value, 0.0, 1.0);
    }
}
