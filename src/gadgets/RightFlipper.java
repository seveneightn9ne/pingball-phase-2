package gadgets;

import java.util.HashSet;
import java.util.Set;

import client.Ball;
import client.Board;
import physics.Angle;
import physics.Geometry;
import physics.LineSegment;
import physics.Vect;

public class RightFlipper implements Gadget {

    /**
     * Rep invariant: 0 <= (pivotCoord.x/y, rotatedCoord.x/y,
     * nonRotatedCoord.x/y) <= 19 and are integer-valued doubles line represents
     * the flipper
     */

    private LineSegment line;
    private boolean rotated = false;
    private int orientation;
    private Vect pivot;
    private double reflection = 0.95;
    private double angularVelocity = 0;
    private Set<Gadget> triggers = new HashSet<Gadget>();
    private Vect pivotCoord;
    private Vect rotatedCoord;
    private Vect nonRotatedCoord;
    private String name;

    /**
     * Constructor for RightFlipper
     * 
     * @param position
     *            the position of the upper left end of the flipper
     * @param orientation
     *            0 is the flipper with fixed end at position and non-fixed end
     *            below it. the other orientations represent a 90 degree
     *            rotation from this
     * @param name
     *            unique name of this flipper
     */
    public RightFlipper(String name, int xPos, int yPos, int orientation) {
        this.name = name;

        this.orientation = orientation;

        this.orientationConstructor(xPos, yPos, orientation);

    }

    /**
     * Constructor for RightFlipper
     * 
     * @param position
     *            the position of the upper left end of the flipper
     * @param orientation
     *            0 is the flipper with fixed end at position and non-fixed end
     *            below it. the other orientations represent a 90 degree
     *            rotation from this
     */
    public RightFlipper(int xPos, int yPos, int orientation) {
        this.name = null;

        this.orientation = orientation;

        this.orientationConstructor(xPos, yPos, orientation);

    }

    private void orientationConstructor(int xPos, int yPos, int orientation) {
        if (orientation == 0) {
            pivot = new Vect(xPos + 1.5, yPos - 0.5);
            line = new LineSegment(pivot.x(), pivot.y(), pivot.x(),
                    pivot.y() + 2);
            pivotCoord = new Vect(xPos + 1, yPos);
            rotatedCoord = new Vect(xPos, yPos);
            nonRotatedCoord = new Vect(xPos + 1, yPos + 1);
        } else if (orientation == 90) {
            pivot = new Vect(xPos + 1.5, yPos + 1.5);
            line = new LineSegment(pivot.x() - 2, pivot.y(), pivot.x(),
                    pivot.y());
            pivotCoord = new Vect(xPos + 1, yPos + 1);
            rotatedCoord = new Vect(xPos + 1, yPos);
            nonRotatedCoord = new Vect(xPos, yPos + 1);
        } else if (orientation == 180) {
            pivot = new Vect(xPos - 0.5, yPos + 1.5);
            line = new LineSegment(pivot.x(), pivot.y() - 2, pivot.x(),
                    pivot.y());
            pivotCoord = new Vect(xPos, yPos + 1);
            rotatedCoord = new Vect(xPos + 1, yPos + 1);
            nonRotatedCoord = new Vect(xPos, yPos);
        } else if (orientation == 270) {
            pivot = new Vect(xPos - 0.5, yPos - 0.5);
            line = new LineSegment(pivot.x(), pivot.y(), pivot.x() + 2,
                    pivot.y());
            pivotCoord = new Vect(xPos, yPos);
            rotatedCoord = new Vect(xPos, yPos + 1);
            nonRotatedCoord = new Vect(xPos + 1, yPos);
        }
    }

    /**
     * Check rep invariant. Integer-valued doubles enforced by constructor, as
     * is line representation
     */
    public void checkRep() {
        assert (pivotCoord.x() >= 0 && pivotCoord.x() <= 19
                && pivotCoord.y() >= 0 && pivotCoord.y() <= 19);
        assert (rotatedCoord.x() >= 0 && rotatedCoord.x() <= 19
                && rotatedCoord.y() >= 0 && rotatedCoord.y() <= 19);
        assert (nonRotatedCoord.x() >= 0 && nonRotatedCoord.x() <= 19
                && nonRotatedCoord.y() >= 0 && nonRotatedCoord.y() <= 19);
    }

    @Override
    public void action(Board board) {

        this.putInBoardRep(board, true);
        if (rotated) {
            line = Geometry.rotateAround(line, pivot, new Angle(Math.PI / 2.0));
            rotated = false;
        } else {
            line = Geometry
                    .rotateAround(line, pivot, new Angle(3*Math.PI / 2.0));
            rotated = true;
        }

        this.putInBoardRep(board, false);
    }

    @Override
    public int hit(Ball ball, Board board) {
        Vect velocity = Geometry.reflectRotatingWall(line, pivot,
                angularVelocity, ball.getCircle(), ball.getVelocity(),
                reflection);
        ball.setVelocity(velocity);
        for (Gadget g : triggers) {
            g.action(board);
        }
        return -1;
    }

    @Override
    public void putInBoardRep(Board board, boolean remove) {
        char[][] boardRep = board.getBoardRep();
        char h = '-';
        char v = '|';
        if (remove) {
            h = ' ';
            v = ' ';
        }

        if (!rotated) {
            if (orientation == 90 || orientation == 270) {
                boardRep[(int) pivotCoord.y() + 1][(int) pivotCoord.x() + 1] = h;
                boardRep[(int) nonRotatedCoord.y() + 1][(int) nonRotatedCoord
                        .x() + 1] = h;
            } else {
                boardRep[(int) pivotCoord.y() + 1][(int) pivotCoord.x() + 1] = v;
                boardRep[(int) nonRotatedCoord.y() + 1][(int) nonRotatedCoord
                        .x() + 1] = v;
            }
        }

        else {
            if (orientation == 90 || orientation == 270) {
                boardRep[(int) pivotCoord.y() + 1][(int) pivotCoord.x() + 1] = v;
                boardRep[(int) rotatedCoord.y() + 1][(int) rotatedCoord.x() + 1] = v;
            } else {
                boardRep[(int) pivotCoord.y() + 1][(int) pivotCoord.x() + 1] = h;
                boardRep[(int) rotatedCoord.y() + 1][(int) rotatedCoord.x() + 1] = h;
            }
        }

        board.setBoardRep(boardRep);
    }

    @Override
    public void addTrigger(Gadget g) {
        triggers.add(g);
    }

    @Override
    public double timeUntilCollision(Ball ball) {
        return Geometry.timeUntilWallCollision(line, ball.getCircle(),
                ball.getVelocity());
    }

    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public Vect getOrigin() {
        return this.pivotCoord;
    }
    @Override
    public int[] getSize() {
        return new int[]{2,2};
    }
}
