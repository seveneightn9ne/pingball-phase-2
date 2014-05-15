package client.gadgets;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import common.Constants;

import client.Ball;
import client.Board;
import physics.Circle;
import physics.Geometry;
import physics.Vect;

/**
 * Model for the Circle Bumper gadget. 
 * 
 * Circle bumpers have a diameter of 1L. They do not have actions and they generate
 * a trigger whenever they are hit. 
 */
public class CircleBumper implements Gadget {
    /**
     * Rep invariant: 0 <= position.x() <= 19, 0 <= position.y() <= 19.
     * position.x() and position.y() are integer-valued doubles
     * circle.getCenter() = position. circle has radius 0.5.
     */
    private Vect position;
    private Circle circle;
    private double reflection = 1;
    private List<Gadget> triggers = new ArrayList<Gadget>();
    private String name;
	private final Shape shape;
	private final Color CIRCLECOLOR = new Color(25,116,171);

    /**
     * Circle Bumper constructor: create a circle of radius 0.5 at specified
     * position
     * 
     * @param xPos
     *            the x position of the gadget
     * @param yPos
     *            the y position of the gadget
     * @param name
     *            name of the bumper
     * 
     */
    public CircleBumper(String name, int xPos, int yPos) {
        this.name = name;

        this.position = new Vect(xPos, yPos);
        circle = new Circle(position, 0.5);
//    	this.shape = new Ellipse2D.Double(xPos*Constants.SCALE,yPos*Constants.SCALE,Constants.SCALE,Constants.SCALE);
    	this.shape = new Ellipse2D.Double((xPos-Constants.OFFSET)*Constants.SCALE,(yPos-Constants.OFFSET)*Constants.SCALE,Constants.SCALE,Constants.SCALE);

    }

    /**
     * Check the rep invariant. The integer-valued doubles are automatically
     * enforced by the constructor
     */
    public void checkRep() {
        assert (position.x() >= 0 && position.x() <= 19);
        assert (position.y() >= 0 && position.y() <= 19);
        assert (circle.getRadius() == 0.5);
        assert (circle.getCenter().x() == position.x() && circle.getCenter()
                .y() == position.y());

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
        Vect velocity = Geometry.reflectCircle(circle.getCenter(),
                ball.getPosition(), ball.getVelocity());
        ball.setVelocity(velocity);
        for (Gadget g : triggers) {
            g.action(board);
        }
        return true;
    }
    
    @Override
    public double timeUntilCollision(Ball ball) {
        double time = Geometry.timeUntilCircleCollision(circle,
                ball.getCircle(), ball.getVelocity());
        return time;
    }

    @Override
    public void action(Board board) {
        // Circle Bumpers have no action
    }

    @Override
    public void addTrigger(Gadget g) {
        triggers.add(g);
    }

    @Override
    public void putInBoardRep(Board board, boolean remove) {
        char[][] boardRep = board.getBoardRep();
        boardRep[(int) Math.round(position.y() + 1)][(int) Math.round(position
                .x() + 1)] = 'O';
        board.setBoardRep(boardRep);

    }
    
	@Override
	public Shape getShape() {
		return shape;
	}
	@Override
	public Color getColor() {
		return CIRCLECOLOR;
	}
	
	@Override
    public String toString() {
        return "O";
    }
	
}
