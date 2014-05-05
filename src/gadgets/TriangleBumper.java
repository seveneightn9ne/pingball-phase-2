package gadgets;

import java.util.HashSet;
import java.util.Set;

import client.Ball;
import client.Board;
import physics.Geometry;
import physics.LineSegment;
import physics.Vect;

public class TriangleBumper implements Gadget {

    /**
     * Rep invariant: 0 <= position.x() <= 19, 0 <= position.y() <= 19.
     * position.x() and position.y() are integer-valued doubles The LineSegments
     * in lines represent the boundaries of the triangle, rep is either / or \
     * depending on orientation
     */

    private Vect position;
    private LineSegment[] lines;
    private char rep;
    private LineSegment nextHit;
    private String name;
    private Set<Gadget> triggers = new HashSet<Gadget>();

    /**
     * Triangle Bumper constructor: Create line segments corresponding to edges
     * of the bumper
     * 
     * @param xPos
     *            x coordinate of the upper left corner of this gadget
     * @param yPos 
     *            y coordinate of the upper left corner of this gadget
     * @param orientation
     *            0 degrees has triangle corner in the northwest corner, 90 in
     *            the northeast, 180 in the southeast, 270 in the southwest
     * @param name
     *            unique representation of the triangle bumper
     */
    public TriangleBumper(String name, int xPos, int yPos, int orientation) {
        this.name = name;

        this.position = new Vect(xPos, yPos);
        lines = new LineSegment[3];
        this.orientationConstructor(orientation);
    }

    /**
     * Triangle Bumper constructor: Create line segments corresponding to edges
     * of the bumper
     * 
     * @param xPos
     *            x coordinate of the upper left corner of this gadget
     * @param yPos 
     *            y coordinate of the upper left corner of this gadget
     * @param orientation
     *            0 degrees has triangle corner in the northwest corner, 90 in
     *            the northeast, 180 in the southeast, 270 in the southwest
     */
    public TriangleBumper(int xPos, int yPos, int orientation) {
        this.name = null;

        this.position = new Vect(xPos, yPos);
        lines = new LineSegment[3];
        this.orientationConstructor(orientation);
    }

    /**
     * Create line segments corresponding to edges of the bumper in the correct
     * orientation.
     * 
     * @param orientation
     *            0 degrees has triangle corner in the northwest corner, 90 in
     *            the northeast, 180 in the southeast, 270 in the southwest
     * @throws RuntimeException
     *             if given orientation is not a multiple of 90.
     */
    private void orientationConstructor(int orientation) {
        Vect northWest = new Vect(position.x() - 0.5, position.y() - 0.5);
        Vect northEast = new Vect(position.x() + 0.5, position.y() - 0.5);
        Vect southWest = new Vect(position.x() - 0.5, position.y() + 0.5);
        Vect southEast = new Vect(position.x() + 0.5, position.y() + 0.5);

        if (orientation == 0) {
            lines[0] = new LineSegment(northWest, northEast);
            lines[1] = new LineSegment(northWest, southWest);
            lines[2] = new LineSegment(southWest, northEast);
            rep = '/';
        } else if (orientation == 90) {
            lines[0] = new LineSegment(northWest, northEast);
            lines[1] = new LineSegment(northEast, southEast);
            lines[2] = new LineSegment(northWest, southEast);
            rep = '\\';
        }

        else if (orientation == 180) {
            lines[0] = new LineSegment(southEast, northEast);
            lines[1] = new LineSegment(southWest, southEast);
            lines[2] = new LineSegment(southWest, northEast);
            rep = '/';
        }

        else if (orientation == 270) {
            lines[0] = new LineSegment(northWest, southWest);
            lines[1] = new LineSegment(southWest, southEast);
            lines[2] = new LineSegment(northWest, southEast);
            rep = '\\';
        }

        else {
            throw new RuntimeException(
                    "Invalid orientation given. Can only be multiples of 90.");
        }
    }

    /**
     * Check the rep invariant. The integer valued doubles are enforced in the
     * constructor arguments. The lines are properly defined in the constructor
     * and never changed.
     */
    public void checkRep() {
        assert (position.x() >= 0 && position.x() <= 19);
        assert (position.y() >= 0 && position.y() <= 19);
        assert (rep == '\\' || rep == '/');
    }

    @Override
    public void action(Board board) {
        // Triangle bumpers have no action
    }

    @Override
    public int hit(Ball ball, Board board) {
        Vect velocity = Geometry.reflectWall(nextHit, ball.getVelocity());
        ball.setVelocity(velocity);
        for (Gadget g : triggers) {
            g.action(board);
        }
        
        return -1;
    }

    @Override
    public String toString() {
        return String.valueOf(rep);
    }

    @Override
    public void addTrigger(Gadget g) {
        triggers.add(g);
    }

    @Override
    public void putInBoardRep(Board board, boolean remove) {
        char[][] boardRep = board.getBoardRep();
        boardRep[(int) Math.round(position.y() + 1)][(int) Math.round(position
                .x() + 1)] = rep;
        board.setBoardRep(boardRep);

    }

    @Override
    public double timeUntilCollision(Ball ball) {
        double minTime = Double.POSITIVE_INFINITY;
        double time;
        for (LineSegment line : lines) {
            time = Geometry.timeUntilWallCollision(line, ball.getCircle(),
                    ball.getVelocity());
            if (time < minTime) {
                minTime = time;
                nextHit = line;
            }
        }
        return minTime;
    }

    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public Vect getOrigin() {
        return this.position;
    }

    @Override
    public int[] getSize() {
        return new int[]{1,1};
    }

}
