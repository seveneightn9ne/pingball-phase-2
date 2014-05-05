package gadgets;

import physics.Vect;
import common.Constants;
import client.Ball;
import client.Board;


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
     * @param ball the ball
     * @param board the board
     * @return true if the ball is still in play. false if this is a wall that took the ball.
     */
    public boolean hit(Ball ball, Board board);
    
    /**
     * Calculate the time until a Ball at fixed velocity will collide with this Gadget
     * @param ball the Ball in question
     * @return the time until collision
     */
    public double timeUntilCollision(Ball ball);
    
    /**
     * The action to take when this Gadget is triggered
     */
    public void action(Board board);
    
    /**
     * Add a triggered Gadget to this Gadget. Hence, whenever this gadget is triggered, it
     * will cause each Gadget added to perform its triggered action
     * @param g the Gadget to add to this Gadget's list of triggered Gadgets
     */
    public void addTrigger(Gadget g);
    
    /**
     * Alter the 2D char array boardRep in board to reflect this Gadget at its current position
     * @param board the Board that this Gadget is on
     * @param remove true if we are removing a previous representation of this gadget in the board,
     *        false if we are adding the current representation
     */
    public void putInBoardRep(Board board, boolean remove);
    
    
}
