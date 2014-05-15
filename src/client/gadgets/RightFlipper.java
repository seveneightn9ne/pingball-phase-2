package client.gadgets;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import common.Constants;

import client.Ball;
import client.Board;
import physics.Angle;
import physics.Geometry;
import physics.LineSegment;
import physics.Vect;


/**
 * Model for the Right Flipper gadget.  
 * 
 */
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
	private final double archHeight = 20.0;
	private final double archWidth = 20.0;
	private Shape shape;
	private Shape rotatedShape;
	private final Color FLIPCOLOR = new Color(238,172,150);
	

	

	/**
	 * Constructor for RightFlipper
	 * 
	 * @param xPos
	 *            x coordinate of the upper left corner of this gadget
	 * @param yPos
	 *            y coordinate of the upper left corner of this gadget
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
	 * @param xPos
	 *            x coordinate of the upper left corner of this gadget
	 * @param yPos
	 *            y coordinate of the upper left corner of this gadget
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

	/**
	 * Sets the orientation of the flipper
	 * 
	 * @param xPos
	 * @param yPos
	 * @param orientation
	 */
	private void orientationConstructor(int xPos, int yPos, int orientation) {
    	Vect shapeOrigin;
    	Vect rotatedShapeOrigin;
    	Vect verticalShape = new Vect(0.5, 2);
    	Vect horizontalShape = new Vect(2, 0.5);
    	Vect shapeType;
    	Vect rShapeType;
		if (orientation == 0) {
			pivot = new Vect(xPos + 1.5, yPos - 0.5);
			line = new LineSegment(pivot.x(), pivot.y(), pivot.x(),
					pivot.y() + 2);
			pivotCoord = new Vect(xPos + 1, yPos);
			rotatedCoord = new Vect(xPos, yPos);
			nonRotatedCoord = new Vect(xPos + 1, yPos + 1);
            shapeOrigin = new Vect(xPos + 1.5, yPos);
            rotatedShapeOrigin = new Vect(xPos, yPos);
            shapeType = verticalShape;
            rShapeType = horizontalShape;
		} else if (orientation == 90) {
			pivot = new Vect(xPos + 1.5, yPos + 1.5);
			line = new LineSegment(pivot.x(), pivot.y(), pivot.x() - 2,
					pivot.y());
			pivotCoord = new Vect(xPos + 1, yPos + 1);
			rotatedCoord = new Vect(xPos + 1, yPos);
			nonRotatedCoord = new Vect(xPos, yPos + 1);
            shapeOrigin = new Vect(xPos, yPos + 1.5);
            rotatedShapeOrigin = new Vect(xPos + 1.5, yPos);
            shapeType = horizontalShape;
            rShapeType = verticalShape;
		} else if (orientation == 180) {
			pivot = new Vect(xPos - 0.5, yPos + 1.5);
			line = new LineSegment(pivot.x(), pivot.y(), pivot.x(),
					pivot.y() - 2);
			pivotCoord = new Vect(xPos, yPos + 1);
			rotatedCoord = new Vect(xPos + 1, yPos + 1);
			nonRotatedCoord = new Vect(xPos, yPos);
            shapeOrigin = new Vect(xPos, yPos);
            rotatedShapeOrigin = new Vect(xPos, yPos + 1.5);
            shapeType = verticalShape;
            rShapeType = horizontalShape;
		} else {// if (orientation == 270) {
			pivot = new Vect(xPos - 0.5, yPos - 0.5);
			line = new LineSegment(pivot.x(), pivot.y(), pivot.x() + 2,
					pivot.y());
			pivotCoord = new Vect(xPos, yPos);
			rotatedCoord = new Vect(xPos, yPos + 1);
			nonRotatedCoord = new Vect(xPos + 1, yPos);
            shapeOrigin = new Vect(xPos, yPos);
            rotatedShapeOrigin = new Vect(xPos, yPos);
            shapeType = horizontalShape;
            rShapeType = verticalShape;
		}
        this.shape = new RoundRectangle2D.Double(
        		shapeOrigin.x()*Constants.SCALE + Constants.SCALE,
        		shapeOrigin.y()*Constants.SCALE + Constants.SCALE,
        		shapeType.x()*Constants.SCALE,
        		shapeType.y()*Constants.SCALE,
        		archHeight,archWidth);
        this.rotatedShape = new RoundRectangle2D.Double(
        		rotatedShapeOrigin.x()*Constants.SCALE + Constants.SCALE,
        		rotatedShapeOrigin.y()*Constants.SCALE + Constants.SCALE,
        		rShapeType.x()*Constants.SCALE,
        		rShapeType.y()*Constants.SCALE,
        		archHeight,archWidth);
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
			line = Geometry.rotateAround(line, pivot, new Angle(
					3 * Math.PI / 2.0));
			rotated = true;
		}

		this.putInBoardRep(board, false);
	}

	@Override
	public boolean hit(Ball ball, Board board) {
		Vect velocity = Geometry.reflectRotatingWall(lineToCollideWith(ball), pivot,
				angularVelocity, ball.getCircle(), ball.getVelocity(),
				reflection);
		ball.setVelocity(velocity);
		for (Gadget g : triggers) {
			g.action(board);
		}
		return true;
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
    	List<Double> times = new ArrayList<Double>();
    	for (LineSegment edge : getRect()) {
    		times.add(Geometry.timeUntilWallCollision(edge, ball.getCircle(), ball.getVelocity()));
    	}
    	return Collections.min(times);
//        return Geometry.timeUntilWallCollision(line, ball.getCircle(),
//                ball.getVelocity());
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
		return new int[] { 2, 2 };
	}

	@Override
	public Shape getShape() {
//		if (isHorizontal()) return shapeHoriz;
//		else return shapeVert;
		if (rotated) return rotatedShape;
		else return shape;
	}
	@Override
	public Color getColor() {
		return FLIPCOLOR;
	}
	
	private boolean isHorizontal() {
		boolean isHor = true;
		if (orientation == 0 || orientation == 180)
			isHor = false;
		if (rotated) isHor = !isHor;
		return isHor;
	}
	
    private LineSegment lineToCollideWith(Ball ball) {
    	double minTime = 9999;
    	LineSegment closestWall = null;
    	for (LineSegment edge : getRect()) {
    		double thisTime = Geometry.timeUntilWallCollision(edge, ball.getCircle(), ball.getVelocity());
    		if (thisTime < minTime) {
    			minTime = thisTime;
    			closestWall = edge;
    		}
    	}
    	return closestWall;
    }
	
	private List<LineSegment> getRect() {
		List<LineSegment> rect = new ArrayList<LineSegment>();
		rect.add(line);
		if (isHorizontal()) {
			rect.add(new LineSegment(line.p1().plus(new Vect(0, 0.5)), line.p2().plus(new Vect(0, 0.5))));
			rect.add(new LineSegment(line.p1(), line.p1().plus(new Vect(0, 0.5))));
			rect.add(new LineSegment(line.p2(), line.p2().plus(new Vect(0, 0.5))));
		} else {
			rect.add(new LineSegment(line.p1().plus(new Vect(-0.5, 0)), line.p2().plus(new Vect(-0.5, 0))));
			rect.add(new LineSegment(line.p1(), line.p1().plus(new Vect(-0.5, 0))));
			rect.add(new LineSegment(line.p2(), line.p2().plus(new Vect(-0.5, 0))));
		}
		return rect;
	}
}
