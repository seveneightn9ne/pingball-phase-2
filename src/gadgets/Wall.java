package gadgets;

import common.Constants;
import common.netprotocol.BallOutMessage;

import client.Ball;
import client.ServerHandler;
import physics.Geometry;
import physics.LineSegment;
import physics.Vect;

public class Wall implements Gadget {
    /**
     * Rep invariant: line.length() == 20
     */
    private LineSegment line;
    public boolean open = false;
    private Constants.BoardSide boardSide;
    private String connectedBoardName;
    private ServerHandler serverHandler;
    
    /**
     * Wall constructor: creates line segments representing the walls of a board
     * @param wallNumber 1, 2, 3, or 4, corresponding to top, right, bottom, and left walls respectively
     */
    public Wall(Constants.BoardSide side){
    	boardSide = side;
        
        // top wall
        if (side == Constants.BoardSide.TOP){
            line = new LineSegment(-.5, -.5, 19.5, -.5);
        }
        
        // right wall
        else if (side == Constants.BoardSide.RIGHT){
            line = new LineSegment(19.5, -.5, 19.5, 19.5);
        }
        
        // bottom wall
        else if (side == Constants.BoardSide.BOTTOM){
            line = new LineSegment(19.5, 19.5, -.5, 19.5);
        }
        
        // left wall
        else if (side == Constants.BoardSide.LEFT){
            line = new LineSegment(-.5, 19.5, -.5, -.5);
        }
    }
    
    public void checkRep(){
        assert (line.length() == 20);
    }
    
    @Override
    public String getName(){
        return boardSide.toString();
    }
    
    @Override
    public void putInBoardRep(Board board, boolean remove){
        // walls automatically represented in boardRep
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
    public boolean hit(Ball ball, Board board) {
        if (!open){
            Vect velocity = Geometry.reflectWall(line, ball.getVelocity());
            ball.setVelocity(velocity);
            return true;
        }
        else{
        	Vect newBallPosition = ball.getPosition().plus(ball.getVelocity().times(Constants.TIMESTEP));
            if (boardSide == Constants.BoardSide.TOP)    newBallPosition = new Vect(newBallPosition.x(), 20d);
            if (boardSide == Constants.BoardSide.RIGHT)  newBallPosition = new Vect(newBallPosition.x(), 0d);
            if (boardSide == Constants.BoardSide.BOTTOM) newBallPosition = new Vect(20d, newBallPosition.y());
            if (boardSide == Constants.BoardSide.LEFT)   newBallPosition = new Vect(0d, newBallPosition.y());
            ball.setPosition(newBallPosition);

            // Send the ball to the server
            serverHandler.send(new BallOutMessage(ball.getPosition(), ball.getVelocity(), boardSide));
            return false;
        }
    }
    
    @Override
    public double timeUntilCollision(Ball ball){
        return Geometry.timeUntilWallCollision(line, ball.getCircle(), ball.getVelocity());
    }
    
    @Override
    public Vect getOrigin() {
        return this.line.p1();
    }

    @Override
    public int[] getSize() {
        return new int[]{1,1};
    }

}
