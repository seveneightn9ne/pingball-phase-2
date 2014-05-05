package common;

/**
 * This is a static class containing the constants that need
 * to be used by multiple packages.
 */
public class Constants {
	public static enum BoardSide {
        LEFT, RIGHT, TOP, BOTTOM;
    }
	public static final double TIMESTEP = 1d/20d; //seconds
	public static final boolean DEBUG = false;
	public static final double BALL_RADIUS = 0.25; // in L
	
	public static final int DEFAULT_PORT = 10987;
    public static final int MIN_PORT = 0;
    public static final int MAX_PORT = 65535;
}
