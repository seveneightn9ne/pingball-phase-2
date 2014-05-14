package client.gadgets;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import common.Constants;
import client.Ball;
import client.Board;
import physics.Circle;
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


    private LineSegment[] lines;
    private Circle[] corners;
    private Vect southEast;
    private List<Ball> balls = new ArrayList<Ball>();
    private Set<Gadget> triggers = new HashSet<Gadget>();
    private Vect position;
    private int width;
    private int height;
    private int xPos;
    private int yPos;
    private final Shape shape;
    private final Color ABSORBCOLOR = new Color(125,138,24);

    private String name;

    /**
     * Constructor for Absorber: Create line segments representing the edges of
     * the Absorber
     * 
     * @param xPos
     *            x coordinate of the upper left corner of this gadget
     * @param yPos 
     *            y coordinate of the upper left corner of this gadget
     * @param width
     *            the width of the absorber (x-direction)
     * @param height
     *            the height of the absorber (y-direction)
     */

    public Absorber(String name, int xPos, int yPos, int width, int height) {
        this.name = name;
        this.xPos = xPos;
        this.yPos = yPos;
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
        corners = new Circle[4];
        corners[0] = new Circle(northWest, 0);
        corners[1] = new Circle(northEast, 0);
        corners[2] = new Circle(southWest, 0);
        corners[3] = new Circle(southEast, 0);
        this.shape = new Rectangle(xPos*Constants.SCALE,yPos*Constants.SCALE,width*Constants.SCALE,height*Constants.SCALE);

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
    public boolean hit(Ball ball, Board board) {
        ball.putInBoardRep(board, true);
        ball.setPosition(new Vect(southEast.x(), southEast.y()));
        ball.setVelocity(new Vect(0, 0));
        balls.add(ball);
        board.notifyAbsorbed(ball);
        System.out.println("Ow! I got hit!");
        for (Gadget g : triggers) {
            g.action(board);
        }
        return true;
    }

    @Override
    public void action(Board board) {
        if (!balls.isEmpty()) {
            System.out.println("Released");
            Ball ball = balls.get(0);
            balls.remove(ball);
            ball.setPosition(new Vect(southEast.x(), southEast.y()));
            ball.setVelocity(new Vect(0, -50));
            ball.putInBoardRep(board, false);
            board.notifyReleased(ball);
        }
    }

    @Override
    public String toString() {
        return "=";
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
        for (Circle corner : corners) {
            time = Geometry.timeUntilCircleCollision(corner, ball.getCircle(),
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

    @Override
    public Vect getOrigin() {
        return this.position;
    }

    @Override
    public int[] getSize() {
        return new int[]{this.width, this.height};
    }


	@Override
	public Shape getShape() {
		return shape;
	}

	@Override
	public Color getColor() {
		return ABSORBCOLOR;
	}

}
