package client.gadgets;

import java.util.HashSet;
import java.awt.*;
import java.awt.geom.Path2D;

import common.Constants;

import java.util.Set;

import client.Ball;
import client.Board;
import physics.Circle;
import physics.Geometry;
import physics.LineSegment;
import physics.Vect;

/**
 * Model for the Triangle Bumper gadget.
 * 
 * Triangle bumpers have an edge length of 1L and a hypotenuse length of
 * sqrt(2)L. They do not have actions and they generate a trigger whenever they
 * are hit.
 * 
 */
public class TriangleBumper implements Gadget {

	/**
	 * Rep invariant: 0 <= position.x() <= 19, 0 <= position.y() <= 19.
	 * position.x() and position.y() are integer-valued doubles The LineSegments
	 * in lines represent the boundaries of the triangle, rep is either / or \
	 * depending on orientation
	 */
	private Vect position;
	private LineSegment[] lines;
	private Circle[] corners;
	private char rep;
	private LineSegment nextHit;
	private Circle nextCornerHit;
	private String nextHitType;
	private String name;
	private Set<Gadget> triggers = new HashSet<Gadget>();
	private double[] xPointsD;
	private double[] yPointsD;
	private final Color TRICOLOR = new Color(86, 180, 216);
	private Shape triangleShape;

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
		corners = new Circle[3];
		this.orientationConstructor(orientation);
	}

	/**
	 * Constructs the line segments corresponding to edges of the bumper in the correct
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
			corners[0] = new Circle(northWest, 0);
			corners[1] = new Circle(northEast, 0);
			corners[2] = new Circle(southWest, 0);
			rep = '/';

		} else if (orientation == 90) {
			lines[0] = new LineSegment(northWest, northEast);
			lines[1] = new LineSegment(northEast, southEast);
			lines[2] = new LineSegment(northWest, southEast);
			corners[0] = new Circle(northWest, 0);
			corners[1] = new Circle(northEast, 0);
			corners[2] = new Circle(southEast, 0);
			rep = '\\';

		} else if (orientation == 180) {
			lines[0] = new LineSegment(southEast, northEast);
			lines[1] = new LineSegment(southWest, southEast);
			lines[2] = new LineSegment(southWest, northEast);
			corners[0] = new Circle(southWest, 0);
			corners[1] = new Circle(northEast, 0);
			corners[2] = new Circle(southEast, 0);
			rep = '/';

		} else if (orientation == 270) {
			lines[0] = new LineSegment(northWest, southWest);
			lines[1] = new LineSegment(southWest, southEast);
			lines[2] = new LineSegment(northWest, southEast);
			corners[0] = new Circle(southWest, 0);
			corners[1] = new Circle(northWest, 0);
			corners[2] = new Circle(southEast, 0);
			rep = '\\';
		}

		else {
			throw new RuntimeException(
					"Invalid orientation given. Can only be multiples of 90.");
		}
		xPointsD = new double[] { (corners[0].getCenter().x()+0.5) * Constants.SCALE + Constants.SCALE,
				(corners[1].getCenter().x()+0.5) * Constants.SCALE + Constants.SCALE,
				(corners[2].getCenter().x()+0.5) * Constants.SCALE + Constants.SCALE };
		yPointsD = new double[] { (corners[0].getCenter().y()+0.5) * Constants.SCALE + Constants.SCALE,
				(corners[1].getCenter().y()+0.5) * Constants.SCALE + Constants.SCALE,
				(corners[2].getCenter().y()+0.5) * Constants.SCALE + Constants.SCALE};

		Path2D path = new Path2D.Double();
		path.moveTo(xPointsD[0], yPointsD[0]);
		for (int i = 1; i < xPointsD.length; ++i) {
			path.lineTo(xPointsD[i], yPointsD[i]);
		}
		path.closePath();
		triangleShape = path;

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
	public String getName() {
		return this.name;
	}

	@Override
	public Vect getOrigin() {
		return this.position;
	}

	@Override
	public int[] getSize() {
		return new int[] { 1, 1 };
	}

	@Override
	public boolean hit(Ball ball, Board board) {
		if (nextHitType.equals("line")) {
			Vect velocity = Geometry.reflectWall(nextHit, ball.getVelocity());
			ball.setVelocity(velocity);
		} else if (nextHitType.equals("circle")) {
			Vect velocity = Geometry.reflectCircle(nextCornerHit.getCenter(),
					ball.getPosition(), ball.getVelocity());
			ball.setVelocity(velocity);
		}
		for (Gadget g : triggers) {
			g.action(board);
		}

		return true;
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

	/**
	 * @see Gadget#action(Board)
	 * @throws Runtime
	 *             Exception - Triangle Bumpers do not have gadgets
	 */
	@Override
	public void action(Board board) {
		throw new RuntimeException("Triangle bumpers do not have actions");
		
		// Triangle bumpers have no action
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
	public Shape getShape() {
		return triangleShape;
	}

	@Override
	public Color getColor() {
		return TRICOLOR;
	}

	@Override
	public String toString() {
		return String.valueOf(rep);
	}
}
