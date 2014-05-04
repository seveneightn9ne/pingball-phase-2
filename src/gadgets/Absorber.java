package gadgets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import client.Ball;
import client.Board;

import physics.Geometry;
import physics.LineSegment;
import physics.Vect;

public class Absorber implements Gadget {
    /**
     * Rep invariant: 0 <= position.x() <= 19, 0 <= position.y() <= 19.
     * position.x() and position.y() are integer-valued doubles. 0 <
     * (position.x() + width) <= 20. 0 < (position.x() + width) <= 20. height
     * and width are positive. southEast.x() = position.x()+width-0.5.
     * southEast.y() = position.y()+width-0.5. Each Ball in balls has position =
     * southEast. No duplicate Ball in balls The LineSegments in lines represent
     * the boundaries of the absorber
     */

    //private boolean selfTrigger = false; // If true, eject the same ball that
                                         // collides with the absorber

    private LineSegment[] lines;
    private Vect southEast;
    private List<Ball> balls = new ArrayList<Ball>();
    private Set<Gadget> triggers = new HashSet<Gadget>();
    private Vect position;
    private int width;
    private int height;

    private String name;

    /**
     * Constructor for Absorber: Create line segments representing the edges of
     * the Absorber
     * 
     * @param position
     *            the coordinate position of the upper left corner
     * @param width
     *            the width of the absorber (x-direction)
     * @param height
     *            the height of the absorber (y-direction)
     */

    public Absorber(String name, int xPos, int yPos, int width, int height) {
        this.name = name;
        this.position = new Vect(xPos, yPos);
        this.width = width;
        this.height = height;

        Vect northWest = new Vect(position.x() - 0.5, position.y() - 0.5);
        Vect northEast = new Vect(position.x() + width - 0.76,
                position.y() - 0.5);
        Vect southWest = new Vect(position.x() - 0.5, position.y() + height
                - 0.5);
        southEast = new Vect(position.x() + width - 0.76, position.y() + height
                - 0.76);

        lines = new LineSegment[4];
        lines[0] = new LineSegment(northWest, northEast);
        lines[1] = new LineSegment(northEast, southEast);
        lines[2] = new LineSegment(southEast, southWest);
        lines[3] = new LineSegment(southWest, northWest);
    }

    /**
     * Check the rep invariant. The lines are taken care of in the constructor
     * and not touched again The integer valued doubles are also enforced by the
     * constructor arguments
     */
    public void checkRep() {
        assert (position.x() >= 0 && position.x() <= 19);
        assert (position.y() >= 0 && position.y() <= 19);
        assert (position.x() + width > 0 && position.x() + width <= 20);
        assert (position.y() + height > 0 && position.y() + height <= 20);
        assert (height > 0 && width > 0);

        assert (southEast.x() == position.x() + width - 0.5 && southEast.y() == position
                .y() + height - 0.5);
        for (Ball b : balls) {
            assert (b.getPosition().x() == southEast.x() && b.getPosition().y() == southEast
                    .y());
        }

        Set<Ball> ballSet = new HashSet<Ball>(balls);
        assert (ballSet.size() == balls.size());

    }

    @Override
    public int hit(Ball ball, Board board) {
        ball.putInBoardRep(board, true);
        ball.setPosition(new Vect(southEast.x(), southEast.y()));
        ball.setVelocity(new Vect(0, 0));
        ball.putInBoardRep(board, false);
        balls.add(ball);
       // if (selfTrigger) {
          //  action(board);
        //}
        for (Gadget g : triggers) {
            g.action(board);
        }
        return -1;
    }

    @Override
    public void action(Board board) {
        if (!balls.isEmpty()) {
            Ball ball = balls.remove(0);
            ball.setVelocity(new Vect(0, -50));
        }
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public void putInBoardRep(Board board, boolean remove) {
        char[][] boardRep = board.getBoardRep();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                boardRep[((int) position.y()) + j + 1][((int) position.x()) + i
                        + 1] = '=';
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
        double minTime = Double.POSITIVE_INFINITY;
        double time;
        for (LineSegment line : lines) {
            time = Geometry.timeUntilWallCollision(line, ball.getCircle(),
                    ball.getVelocity());
            if (time < minTime) {
                minTime = time;
            }
        }
        return minTime;
    }

    @Override
    public String getName() {
        return this.name;
    }

}
