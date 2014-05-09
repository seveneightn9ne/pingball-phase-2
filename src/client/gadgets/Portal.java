package client.gadgets;


import physics.Circle;
import physics.Vect;
import client.Ball;
import client.Board;

public class Portal implements Gadget {
    
    private Portal otherPortal;
    private Board otherBoard;
    private Board board;
    private Circle circle;
    private Vect position;
    
    /**
     * Creates a portal on the specified board linked to the specified other portal.
     * @param posX
     *          X coordinate of the location of the center of this portal
     * @param posY
     *          Y coordinate of the location of the center of this portal
     * @param board
     *          Board this portal is on
     * @param otherBoard
     *          Board the other portal is on
     * @param otherPortal
     *          portal that this portal is linked to
     */
    public Portal(int posX, int posY, Board board, Board otherBoard, Portal otherPortal) {
        
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Vect getOrigin() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int[] getSize() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hit(Ball ball, Board board) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public double timeUntilCollision(Ball ball) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void action(Board board) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addTrigger(Gadget g) {
        // TODO Auto-generated method stub

    }

    @Override
    public void putInBoardRep(Board board, boolean remove) {
        // TODO Auto-generated method stub

    }

}
