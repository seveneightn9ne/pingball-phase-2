package client;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import common.Constants;

import physics.Circle;
import physics.Geometry;
import physics.Vect;

/**
 * Represents a ball on a Pingball board.
 * 
 * Balls have a default diameter of .5.
 */
public class Ball {

	/**
	 * Rep invariant: position == circle.getCenter(), -.25 <= position.x/y <=
	 * 20.25 circle.getRadius() == 0.25
	 */

	private Vect position;
	private Vect velocity;
	private Circle circle;
	private String name;
	private final Color BALLCOLOR = new Color(210, 17, 220);

	/**
	 * Constructor for Ball
	 * 
	 * @param name
	 *            name of this ball
	 * @param x
	 *            the initial x coordinate of the ball
	 * @param y
	 *            the initial y coordinate of the ball
	 * @param xVel
	 *            the initial x velocity of the ball
	 * @param yVel
	 *            the initial y velocity of the ball
	 * @param board
	 *            the board that the ball is on
	 */
	public Ball(String name, double x, double y, double xVel, double yVel) {
		this.name = name;
		position = new Vect(x, y);
		velocity = new Vect(xVel, yVel);
		circle = new Circle(position, 0.25);
	}

	/**
	 * Constructor for ball
	 * 
	 * @param pos
	 *            Vect position of this ball
	 * @param vel
	 *            Vect velocity of this ball
	 */
	public Ball(Vect pos, Vect vel) {
		position = pos;
		velocity = vel;
		circle = new Circle(position, 0.25);
	}

	/**
	 * Checks the representation invariant.
	 */
	public void checkRep() {
		assert (position.x() >= -0.25 && position.x() <= 20.25);
		assert (position.y() >= -0.25 && position.y() <= 20.25);
		assert (position.x() == circle.getCenter().x() && position.y() == circle
				.getCenter().y());
		assert (circle.getRadius() == 0.25);
	}

	/**
	 * Accessor method that gets the name of the ball.
	 * 
	 * @return name of this ball
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Adjusts velocities of two balls that have collided
	 * 
	 * @param ball
	 *            - ball to collide with
	 */
	public void hit(Ball ball) {
		Geometry.VectPair newVelocities = Geometry.reflectBalls(this.position,
				1, this.getVelocity(), ball.position, 1, ball.getVelocity());
		this.setVelocity(newVelocities.v1);
		ball.setVelocity(newVelocities.v2);
	}

	/**
	 * Calculates time until this ball collides with another ball.
	 * 
	 * @param ball
	 *            - ball to be collided with
	 * @return double - time in seconds until collision
	 */
	public double timeUntilCollision(Ball ball) {
		return Geometry.timeUntilBallBallCollision(this.getCircle(),
				this.getVelocity(), ball.getCircle(), ball.getVelocity());
	}

	/**
	 * Alter the 2D char array boardRep in board to reflect this Ball at its
	 * current position
	 * 
	 * @param board
	 *            the Board that this Ball is on
	 * @param remove
	 *            true if we are removing a previous representation of this ball
	 *            in the board, false if we are adding the current
	 *            representation
	 */
	public void putInBoardRep(Board board, boolean remove) {

		char[][] boardRep = board.getBoardRep();
		if (!remove) {
			if (boardRep[(int) Math.round(position.y() + 1)][(int) Math
					.round(position.x() + 1)] == ' ')
				boardRep[(int) Math.round(position.y() + 1)][(int) Math
						.round(position.x() + 1)] = '*';
		} else {
			if (boardRep[(int) Math.round(position.y() + 1)][(int) Math
					.round(position.x() + 1)] == '*')
				boardRep[(int) Math.round(position.y() + 1)][(int) Math
						.round(position.x() + 1)] = ' ';
		}
		board.setBoardRep(boardRep);
	}

	/**
	 * Returns the Shape representation for it to be drawn in the Pingball GUI.
	 * 
	 * @return shape representation of the gadget
	 */
	public Shape getShape() {
		return new Ellipse2D.Double(
				getPosition().x() * Constants.SCALE + Constants.SCALE, 
				getPosition().y() * Constants.SCALE + Constants.SCALE, 
				0.5 * Constants.SCALE, 0.5 * Constants.SCALE);
	}

	/**
	 * Returns the color given to the gadget from the chosen colorscheme.
	 * 
	 * @return color for the gadget
	 */
	public Color getColor() {
		return BALLCOLOR;
	}

	@Override
	public String toString() {
		return "*";
	}

	/**
	 * Move the ball on the board according to the ball's velocity and external
	 * forces
	 * 
	 * @param gravity
	 *            the gravity of the board
	 * @param mu
	 *            the first friction coefficient of the board
	 * @param mu2
	 *            the second friction coefficient of the board
	 * @param deltaT
	 *            the time passed
	 * @param board
	 *            board this ball is on
	 */
	public void move(double gravity, double mu, double mu2, double deltaT,
			Board board) {
		checkRep();
		putInBoardRep(board, true);
		Vect oldPos = getPosition();
		Vect oldVel = getVelocity();

		Vect term1 = oldVel.times(deltaT);
		Vect term2 = new Vect(0, 0.5 * gravity * deltaT * deltaT);

		Vect newPos = oldPos.plus(term1).plus(term2);
		double frictionScalar = 1 - (mu) * (deltaT) - mu2 * oldVel.length()
				* deltaT;
		Vect frictionVel = oldVel.times(frictionScalar);
		Vect newVel = frictionVel.plus(new Vect(0, gravity * deltaT));

		position = newPos;
		velocity = newVel;
		circle = new Circle(position, 0.25);

		checkRep();
		putInBoardRep(board, false);
	}

	/**
	 * Accessor method that gets the velocity the ball is traveling at
	 * 
	 * @return a Vect representing the Ball's current velocity
	 */
	public Vect getVelocity() {
		return new Vect(velocity.x(), velocity.y());
	}

	/**
	 * Accessor method that gets the circle representation of the ball
	 * 
	 * @return the Ball's Circle representation
	 */
	public Circle getCircle() {
		return new Circle(circle.getCenter(), circle.getRadius());
	}

	/**
	 * Accessor method that gets the position of the ball
	 * 
	 * @return a Vect representing the Ball's current position
	 */
	public Vect getPosition() {
		return new Vect(position.x(), position.y());
	}

	/**
	 * Mutator method to change Ball's velocity
	 * 
	 * @param velocity
	 *            a Vect representing the velocity to set
	 */
	public void setVelocity(Vect velocity) {
		this.velocity = velocity;
	}

	/**
	 * Mutator method to change Ball's position
	 * 
	 * @param position
	 *            the Vect position to set
	 */
	public void setPosition(Vect position) {
		this.position = position;
	}

}
