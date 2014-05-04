package gadgets;

import client.Ball;


public interface Gadget {
    
    //public double getReflection();  //Coefficient of reflection: default 1
    //public Vect getPosition();  //Position of the Gadget
    
    public String getName();
    
    /**
     * React when a ball collides with the Gadget
     * @param velocity the velocity of the ball incident on the Gadget
     * @return Vect of the new velocity of the ball after collision
     */
    public int hit(Ball ball, Board board);
    
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
