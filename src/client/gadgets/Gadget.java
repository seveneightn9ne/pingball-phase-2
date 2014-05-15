package client.gadgets;

import java.awt.Color;
import java.awt.Shape;

import physics.Vect;
import client.Ball;
import client.Board;

/**
 * Interface for objects present on the board that are not balls.
 * 
 * Gadget have actions and triggers. A trigger is another gadget thats action is
 * triggered by an event that happens at the first gadget, such as a ball
 * colliding with it. An action is a response that a gadget can make to a
 * trigger happening somewhere on the board.
 */
public interface Gadget {

	/**
	 * @return name of this gadget
	 */
	public String getName();

	/**
	 * @return coordinate of the upper-left-hand corner of this gadget
	 */
	public Vect getOrigin();

	/**
	 * @return int[] corresponding to the width and height of this object
	 */
	public int[] getSize();

	/**
	 * React when a ball collides with the Gadget
	 * 
	 * @param ball
	 *            the ball
	 * @param board
	 *            the board
	 * @return true if the ball is still in play. false if this is a wall that
	 *         took the ball.
	 */
	public boolean hit(Ball ball, Board board);

	/**
	 * Calculate the time until a Ball at fixed velocity will collide with this
	 * Gadget
	 * 
	 * @param ball
	 *            the Ball in question
	 * @return the time until collision
	 */
	public double timeUntilCollision(Ball ball);

	/**
	 * The action to take when this Gadget is triggered
	 * 
	 * @param board
	 *            - Board that this gadget is on
	 */
	public void action(Board board);

	/**
	 * Add a triggered Gadget to this Gadget. Hence, whenever this gadget is
	 * triggered, it will cause each Gadget added to perform its triggered
	 * action
	 * 
	 * @param g
	 *            the Gadget to add to this Gadget's list of triggered Gadgets
	 */
	public void addTrigger(Gadget g);

	/**
	 * Alter the 2D char array boardRep in board to reflect this Gadget at its
	 * current position
	 * 
	 * @param board
	 *            the Board that this Gadget is on
	 * @param remove
	 *            true if we are removing a previous representation of this
	 *            gadget in the board, false if we are adding the current
	 *            representation
	 */
	public void putInBoardRep(Board board, boolean remove);

	/**
	 * Returns the Shape representation for it to be drawn in the Pingbal GUI.
	 * 
	 * @return shape representation of the gadget
	 */
	public Shape getShape();

	/**
	 * Returns the color given to the gadget from the chosen colorscheme.
	 * 
	 * @return color for the gadget
	 */
	public Color getColor();

}
