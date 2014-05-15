package client.gadgets;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

import common.Constants;
import common.netprotocol.BallOutMessage;
import client.Ball;
import client.Board;
import client.ServerHandler;
import physics.Geometry;
import physics.LineSegment;
import physics.Vect;

/**
 * Model for the Wall gadget. 
 * 
 *  
 * 
 */
public class Wall implements Gadget {
    /**
     * Rep invariant: line.length() == 20
     */
    private LineSegment line;
    private boolean open = false;
    private Constants.BoardSide boardSide;
    private String connectedBoardName=null;
    private ServerHandler serverHandler;
    private String sidething;
    
    
    /**
     * Wall constructor: creates line segments representing the walls of a board
     * @param wallNumber 1, 2, 3, or 4, corresponding to top, right, bottom, and left walls respectively
     */
    public Wall(Constants.BoardSide side){
    	boardSide = side;
        
        // top wall
        if (side == Constants.BoardSide.TOP){
            line = new LineSegment(-.5, -.5, 19.5, -.5);
            sidething = "TOP";
        }
        
        // right wall
        else if (side == Constants.BoardSide.RIGHT){
            line = new LineSegment(19.5, -.5, 19.5, 19.5);
            sidething = "bottom";
        }
        
        // bottom wall
        else if (side == Constants.BoardSide.BOTTOM){
            line = new LineSegment(19.5, 19.5, -.5, 19.5);
            sidething = "bottom";
        }
        
        // left wall
        else if (side == Constants.BoardSide.LEFT){
            line = new LineSegment(-.5, 19.5, -.5, -.5);
            sidething = "left";
        }
    }
    
    /**
     * @return the side of the board this wall is on
     */
    public Constants.BoardSide getSide()
    {
    	return boardSide;
    }
    
    /**
     * @return the name of the board that is connected to this wall
     */
    public String wallConnectedName()
    {
    	if (connectedBoardName != null)
    	{
        	return new String(connectedBoardName);
    	}
    	return null;
    }
    
    /**
     * Connects this wall to the board given
     * @param name - name of connecting board
     */
    public void connectToServer(String name) {
        this.open = true;
        this.connectedBoardName = name;
        System.out.println("BoardName Added: "+this.connectedBoardName +" on "+sidething);
 
    }
    
    /**
     * Disconnects this wall from any connected board
     */
    public void disconnectFromServer() {
        this.open = false;
        this.connectedBoardName = null;
    }
    
    /**
     * Sets the server handler of this portal (allows it to communicate
     * with the server)
     * @param sh - server handler for the client that this portal is on
     */
    public void setServerHandler(ServerHandler sh) {
        this.serverHandler = sh;
    }
    
    /**
     * Check the rep invariant.
     */
    public void checkRep(){
        assert (line.length() == 20);
    }
    
    @Override
    public String getName(){
        return boardSide.toString();
    }
    
    @Override
    public Vect getOrigin() {
        return this.line.p1();
    }

    @Override
    public int[] getSize() {
        return new int[]{1,1};
    }
    
    @Override
    public boolean hit(Ball ball, Board board) {
        if (!open){
            Vect velocity = Geometry.reflectWall(line, ball.getVelocity());
            ball.setVelocity(velocity);
            return true;
        }
        else{
            Vect newBallPosition = ball.getPosition();
            if (boardSide == Constants.BoardSide.TOP)    newBallPosition = new Vect(newBallPosition.x(), 19d);
            if (boardSide == Constants.BoardSide.RIGHT)  newBallPosition = new Vect(0d, newBallPosition.y());
            if (boardSide == Constants.BoardSide.BOTTOM) newBallPosition = new Vect(newBallPosition.x(), 0d);
            if (boardSide == Constants.BoardSide.LEFT)   newBallPosition = new Vect(19d, newBallPosition.y());
            ball.putInBoardRep(board, true);
            ball.setPosition(newBallPosition);

            // Send the ball to the server
            serverHandler.send(new BallOutMessage(ball.getPosition(), ball.getVelocity(), boardSide));
            return false;
        }
    }
    
    @Override
    public double timeUntilCollision(Ball ball){
        double tt = Geometry.timeUntilWallCollision(line, ball.getCircle(), ball.getVelocity());
        return tt;
    }
    
    @Override
    public void action(Board board){
        // walls have no action
    }
    
    /**
     * @throws Runtime Exception - Walls do not have triggers
     */
    @Override
    public void addTrigger(Gadget g){
        throw new RuntimeException("Walls do not have triggers");
        //Walls cannot trigger: throw exception
    }
    
    @Override
    public void putInBoardRep(Board board, boolean remove){
        // walls automatically represented in boardRep
    }

	@Override
	public Shape getShape() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getColor() {
		// TODO Auto-generated method stub
		return null;
	}

}
