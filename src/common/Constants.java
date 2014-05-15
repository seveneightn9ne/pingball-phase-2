package common;


/**
 * This is a static class containing the constants that need
 * to be used by multiple packages.
 */
public class Constants {
	/**
	 * BoardSide represents any of four values: TOP, RIGHT, BOTTOM, LEFT
	 * which is used to specify the wall on that side of a board
	 */
	public static enum BoardSide {
        LEFT, RIGHT, TOP, BOTTOM;
    }
	public static final double TIMESTEP = 1d/20d; //seconds
	/**
	 * Enable debug to see additional status messages printed to System.out
	 */
	public static final boolean DEBUG = false;
	public static final double BALL_RADIUS = 0.25; // in L
	
	public static final int DEFAULT_PORT = 10987;
    public static final int MIN_PORT = 0;
    public static final int MAX_PORT = 65535;
    /**
     * The scaling factor between the board's 20L wide board 
     * and the GUI's 400px wide board
     */
    public static final int SCALE = 20;
    
}
