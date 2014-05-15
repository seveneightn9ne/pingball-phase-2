package client.gadgets;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.HashSet;
import java.util.Set;

import common.Constants;

import client.Ball;
import client.Board;
import physics.Circle;
import physics.Geometry;
import physics.LineSegment;
import physics.Vect;

public class SquareBumper implements Gadget {
    /**
     * Rep invariant: 0 <= position.x() <= 19, 0 <= position.y() <= 19.
     * position.x() and position.y() are integer-valued doubles The LineSegments
     * in lines represent the boundaries of the square
     */
    private Vect position;
    private LineSegment[] lines;
    private Circle[] corners;
    private LineSegment nextHit;
    private Circle nextCornerHit;
    private String nextHitType;
    private String name;
    private Set<Gadget> triggers = new HashSet<Gadget>();
	private int xPos;
	private int yPos;
	private final Shape shape;
	private final Color SQUARECOLOR = new Color(18,35,113);

    /**
     * Constructor for SquareBumper
     * 
     * @param name - name of this gadget
     * @param x - x coordinate of the upper left corner of this gadget
     * @param y - y coordinate of the upper left corner of this gadget
     */
    public SquareBumper(String name, int x, int y) {
    	this.xPos = x;
    	this.yPos = y;
        this.name = name;
        this.position = new Vect(x, y);
        lines = new LineSegment[4];
        corners = new Circle[4];
        this.linesConstructor();
        shape = new Rectangle(xPos*Constants.SCALE,yPos*Constants.SCALE,Constants.SCALE,Constants.SCALE);
    }
    
    /**
     * Creates LineSegments for the square bumper
     */
    private void linesConstructor() {

        lines[0] = new LineSegment(position.x() - 0.5, position.y() - 0.5,
                position.x() + 0.5, position.y() - 0.5);
        lines[1] = new LineSegment(position.x() - 0.5, position.y() - 0.5,
                position.x() - 0.5, position.y() + 0.5);
        lines[2] = new LineSegment(position.x() - 0.5, position.y() + 0.5,
                position.x() + 0.5, position.y() + 0.5);
        lines[3] = new LineSegment(position.x() + 0.5, position.y() + 0.5,
                position.x() + 0.5, position.y() - 0.5);
        
        corners[0] = new Circle(new Vect(position.x() - .5, position.y() - .5), 0);
        corners[1] = new Circle(new Vect(position.x() + .5, position.y() - .5), 0);
        corners[2] = new Circle(new Vect(position.x() - .5, position.y() + .5), 0);
        corners[3] = new Circle(new Vect(position.x() + .5, position.y() + .5), 0);
    }

    /**
     * Check the rep invariant. The lines are defined properly in the
     * constructor and never changed, the integer valued doubles are enforced in
     * the constructor arguments
     */
    public void checkRep() {
        assert (position.x() >= 0 && position.x() <= 19);
        assert (position.y() >= 0 && position.y() <= 19);
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

    @Override
    public boolean hit(Ball ball, Board board) {
        
        if (nextHitType.equals("line")) {
            Vect velocity = Geometry.reflectWall(nextHit, ball.getVelocity()); 
            ball.setVelocity(velocity);
        }
        else if (nextHitType.equals("circle")) {
            Vect velocity = Geometry.reflectCircle(nextCornerHit.getCenter(), ball.getPosition(), ball.getVelocity());
            ball.setVelocity(velocity);
        }
        for (Gadget g : triggers) {
            g.action(board);
        }
        
        return true;
    }

    @Override
    public void putInBoardRep(Board board, boolean remove) {
        char[][] boardRep = board.getBoardRep();
        boardRep[(int) Math.round(position.y() + 1)][(int) Math.round(position
                .x() + 1)] = '#';
        board.setBoardRep(boardRep);
    }

    @Override
    public void action(Board board) {
        // Square bumpers have no action.
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
                nextHit = line;
                nextHitType = "line";
            } 
        }
        for (Circle corner : corners) {
            time = Geometry.timeUntilCircleCollision(corner, ball.getCircle(),
                    ball.getVelocity());
            if (time < minTime) {
                minTime = time;
                nextCornerHit = corner;
                nextHitType = "circle";
            }
        }
        return minTime;
    }

	@Override
	public Shape getShape() {
		return shape;
	}

	@Override
	public Color getColor() {
		return SQUARECOLOR;
	}
	
	@Override
    public String toString() {
        return "#";
    }

} 
