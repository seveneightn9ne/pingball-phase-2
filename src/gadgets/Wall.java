package gadgets;

import client.Ball;
import client.Board;
import physics.Geometry;
import physics.LineSegment;
import physics.Vect;

public class Wall implements Gadget {
    /**
     * Rep invariant: line.length() == 20
     */
    private LineSegment line;
    public boolean open = false;
    private int wallNumber;
    
    /**
     * Wall constructor: creates line segments representing the walls of a board
     * @param wallNumber 1, 2, 3, or 4, corresponding to top, right, bottom, and left walls respectively
     */
    public Wall(int wallNumber){
        this.wallNumber = wallNumber;
        
        // top wall
        if (wallNumber == 0){
            line = new LineSegment(-.5, -.5, 19.5, -.5);
        }
        
        // right wall
        else if (wallNumber == 1){
            line = new LineSegment(19.5, -.5, 19.5, 19.5);
        }
        
        // bottom wall
        else if (wallNumber == 2){
            line = new LineSegment(19.5, 19.5, -.5, 19.5);
        }
        
        // left wall
        else if (wallNumber == 3){
            line = new LineSegment(-.5, 19.5, -.5, -.5);
        }
    }
    
    public void checkRep(){
        assert (line.length() == 20);
    }
    
    @Override
    public String getName(){
        return String.valueOf(wallNumber);
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
    public int hit(Ball ball, Board board) {
        if (!open){
            Vect velocity = Geometry.reflectWall(line, ball.getVelocity());
            ball.setVelocity(velocity);
            return -1;
        }
        else{
            return wallNumber;
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
