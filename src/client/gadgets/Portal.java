package client.gadgets;


import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

import common.Constants;
import common.netprotocol.TeleportOutMessage;
import physics.Circle;
import physics.Geometry;
import physics.Vect;
import client.Ball;
import client.Board;
import client.ServerHandler;

/**
 * Model for the Portal gadget. 
 * 
 * Portals are circular and have a diameter of 1L. They do not have an action.
 * They generate a trigger when they are hit. Also, when a ball hits a portal, 
 * it is teleported with the same velocity to a specified portal on a specified board. 
 * If the board or the portal do not exist, then the ball exits the portal. 
 * 
 */
public class Portal implements Gadget {
    
    private String otherPortal;
    private String otherBoard;
    private List<Gadget> triggers = new ArrayList<Gadget>();
    private List<Ball> exiting = new ArrayList<Ball>();
    private Circle circle;
    private Vect position;
    private String name;
    public ServerHandler serverHandler;
    private Shape portalShape;
    private final Color PORTALCOLOR= new Color(0,0,0);
  
	/**
     * Creates a portal on the specified board linked to the specified other portal.
     * @param name
     *          Name of this board
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
    public Portal(String name, int posX, int posY, String otherBoard, String otherPortal) {
        this.name = name;
        this.position = new Vect(posX, posY);
        this.circle = new Circle(position, .5);
        this.otherBoard = otherBoard;
        this.otherPortal = otherPortal;
        portalShape = new Ellipse2D.Double(posX*Constants.SCALE,posY*Constants.SCALE, 1*Constants.SCALE, 1*Constants.SCALE);

    }
    
    /**
     * @return the center of this portal
     */
    public Vect getCenter() {
        return this.circle.getCenter();
    }
    
    /**
     * Sets the server handler of this portal (allows it to communicate
     * with the server)
     * @param sh - server handler for the client that this portal is on
     */
    public void setServerHandler(ServerHandler sh) {
        serverHandler = sh;
    }
    
    /**
     * Passes a ball to this portal
     * @param ball - exiting ball
     */
    public void giveBall(Ball ball) {
        exiting.add(ball);
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
        return new int[]{1,1};
    }

    @Override
    public boolean hit(Ball ball, Board board) {
        if (exiting.contains(ball)) {
            exiting.remove(ball);
            return true;
        }
        if (otherBoard == null) {
            Portal portal = board.getPortal(otherPortal);
            if (portal == null) {
                System.out.println("no connected portal");
                return true;
            }
            else {
                ball.putInBoardRep(board, true);
                System.out.println("portaling to " + portal.name);
                ball.setPosition(portal.position);
                portal.giveBall(ball);
                ball.putInBoardRep(board, false);
                return true;
            }
        }
        else if (serverHandler != null) {
            
            ball.putInBoardRep(board, true);
            serverHandler.send(new TeleportOutMessage(ball.getVelocity(), board.getName(), name, otherBoard, otherPortal));
            return false;
        }
        return true;
    }

    @Override
    public double timeUntilCollision(Ball ball) {
        double time = Geometry.timeUntilCircleCollision(circle,
                ball.getCircle(), ball.getVelocity());
        return time;
    }

    @Override
    public void action(Board board) {
        // Portals have no action
    }

    @Override
    public void addTrigger(Gadget g) {
        triggers.add(g);
    }

    @Override
    public void putInBoardRep(Board board, boolean remove) {
        char[][] boardRep = board.getBoardRep();
        boardRep[(int) Math.round(position.y() + 1)][(int) Math.round(position
                .x() + 1)] = '@';
        board.setBoardRep(boardRep);
    }

	@Override
	public Shape getShape() {
		return portalShape;
	}

	@Override
	public Color getColor() {
		return PORTALCOLOR;
	}
	
	@Override
    public String toString() {
        return "@";
    }

}
