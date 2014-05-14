package client.gadgets;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import common.netprotocol.TeleportOutMessage;
import physics.Circle;
import physics.Geometry;
import physics.Vect;
import client.Ball;
import client.Board;
import client.ServerHandler;

public class Portal implements Gadget {
    
    private String otherPortal;
    private String otherBoard;
    private List<Gadget> triggers = new ArrayList<Gadget>();
    private List<Ball> exiting = new ArrayList<Ball>();
    private Circle circle;
    private Vect position;
    private String name;
    public ServerHandler serverHandler;
    
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
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Vect getOrigin() {
        return this.position;
    }
    
    /**
     * @return the center of this portal
     */
    public Vect getCenter() {
        return this.circle.getCenter();
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
    
    public void setServerHandler(ServerHandler sh) {
        serverHandler = sh;
    }
    
    public void giveBall(Ball ball) {
        exiting.add(ball);
    }

    @Override
    public void putInBoardRep(Board board, boolean remove) {
        char[][] boardRep = board.getBoardRep();
        boardRep[(int) Math.round(position.y() + 1)][(int) Math.round(position
                .x() + 1)] = '@';
        board.setBoardRep(boardRep);
    }
    
    @Override
    public String toString() {
        return "@";
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
