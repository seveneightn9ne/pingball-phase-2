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
	
//	public static int boardSideToWallNumber(BoardSide s) {
//		if (s == BoardSide.TOP) return 0;
//		if (s == BoardSide.RIGHT) return 1;
//		if (s == BoardSide.BOTTOM) return 2;
//		return 3;
//	}
}
