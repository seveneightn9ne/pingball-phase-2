package client;

import gadgets.Gadget;
import gadgets.Wall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import physics.Geometry;
import common.Constants;

public class Board {

    /**
     * Rep invariant: boardRep is the 2D char representation of the board
     */
	private ServerHandler sh;
    private double gravity;
    private double mu;
    private double mu2;
    private String name;
    private List<Gadget> gadgets = new ArrayList<Gadget>();
    private List<Ball> balls = new ArrayList<Ball>();
    private char[][] boardRep = new char[22][22];
    private Wall[] borders = new Wall[4];
    public Map<String, Gadget> gadgetNames = new HashMap<String, Gadget>();
    

    /**
     * Constructor for a default empty Board with one ball
     */
    public Board(String name, double gravity, double mu, double mu2) {
        this.name = name;
        this.gravity = gravity;
        this.mu = mu;
        this.mu2 = mu2;
        this.boardConstructor();
    }

    /**
     * Constructor for a default empty Board with one ball
     */
    public Board(double gravity, double mu, double mu2) {
        this.name = null;
        this.gravity = gravity;
        this.mu = mu;
        this.mu2 = mu2;
        this.boardConstructor();
    }

    /**
     * Initializes appropriate boarders and walls for board object
     */
    private void boardConstructor() {
        // top wall
        borders[0] = new Wall(Constants.BoardSide.TOP);

        // right wall
        borders[1] = new Wall(Constants.BoardSide.RIGHT);

        // bottom wall
        borders[2] = new Wall(Constants.BoardSide.BOTTOM);

        // left wall
        borders[3] = new Wall(Constants.BoardSide.LEFT);

        for (int i = 0; i < 22; i++) {
            boardRep[0][i] = '.';
            boardRep[i][0] = '.';
            boardRep[21][i] = '.';
            boardRep[i][21] = '.';
            if (i > 0 && i < 21) {
                for (int j = 1; j < 21; j++) {
                    boardRep[i][j] = ' ';
                }
            }
        }
    }
    
    /**
     * Set the server handler so that the walls can inform
     * other Boards over the network if a ball is transferred.
     * @param sh the server handler
     */

    public void setServerHandler(ServerHandler sh) {
    	this.sh = sh;
    }
    
    public void connectWallToServer(Constants.BoardSide side, String name) {
    	getWall(side).connectToServer(name);
    	for (int i = 1; i <= name.length() && i <=22; i++){
            if (side == Constants.BoardSide.TOP){
                boardRep[0][i] = name.charAt(i-1);
            }
            
            else if (side == Constants.BoardSide.RIGHT){
                boardRep[i][21] = name.charAt(i-1);
            }
            
            else if (side == Constants.BoardSide.BOTTOM){
                boardRep[22][i] = name.charAt(i-1);
            }
            
            else if (side == Constants.BoardSide.LEFT){
                boardRep[i][0] = name.charAt(i-1);
            }
        }
    }
    
    public void disconnectWallFromServer(Constants.BoardSide side) {
    	Wall wall = getWall(side);
    	int nameLen = wall.getName().length();
    	for (int i = 1; i <= nameLen && i <=22; i++){
            if (side == Constants.BoardSide.TOP){
                boardRep[0][i] = '.';
            }
            
            else if (side == Constants.BoardSide.RIGHT){
                boardRep[i][21] = '.';
            }
            
            else if (side == Constants.BoardSide.BOTTOM){
                boardRep[22][i] = '.';
            }
            
            else if (side == Constants.BoardSide.LEFT){
                boardRep[i][0] = '.';
            }
        }
    	getWall(side).disconnectFromServer();
    }
    
    private Wall getWall(Constants.BoardSide side) {
    	if (side == Constants.BoardSide.TOP) return borders[0];
    	if (side == Constants.BoardSide.RIGHT) return borders[1];
    	if (side == Constants.BoardSide.BOTTOM) return borders[2];
    	else return borders[3];
    }

    /**
     * Accessor method
     * 
     * @return the boardRep of this Board
     */
    public char[][] getBoardRep() {
        return boardRep;
    }

    public void triggerActions(String[] triggers) {
        String trigger = triggers[0];
        String action = triggers[1];
    }

    /**
     * Accessor method
     * 
     * @return the name of this Board
     */
    public String getName() {
        return name;
    }

    /**
     * Mutator method
     * 
     * @param boardRep
     *            the 2D char array boardRep to assign to this Board
     */
    public void setBoardRep(char[][] boardRep) {
        this.boardRep = boardRep;
    }

    /**
     * Add a gadget to the board at the position of the Gadget object
     * 
     * @param gadget
     *            Gadget to add to the board
     */
    public void addGadget(Gadget gadget) {
        synchronized (gadgets) {
            synchronized (gadgetNames) {
                gadgets.add(gadget);
                gadget.putInBoardRep(this, false);
                String name = gadget.getName();
                if (name != null) {
                    gadgetNames.put(name, gadget);
                }
            }
        }

    }

    /**
     * Add a Ball to the board with position and velocity specified in the Ball
     * object
     * 
     * @param ball
     *            Ball to add
     */
    public void addBall(Ball ball) {
        balls.add(ball);
        int x = (int) Math.round(ball.getPosition().x());
        int y = (int) Math.round(ball.getPosition().y());
        boardRep[y + 1][x + 1] = '*';
    }

    @Override
    public String toString() {
        String string = "";
        for (char[] line : boardRep) {
            string += String.valueOf(line);
            string += "\n";
        }
        return string;
    }

    /**
     * Refresh the board, taking into account elapsed time causing motion, as
     * well as possible collisions
     */
    public void update(double timestep) {

    	List<Ball> ballsToRemove = new ArrayList<Ball>();

        for (Ball ball : balls) {
        	System.out.println("Ball velocity: " + ball.getVelocity());

            boolean ballStillInPlay = true;
            
        	for (Gadget gadget : gadgets) {
        		if (gadget.timeUntilCollision(ball) <= timestep && ballStillInPlay) {
//        			System.out.println("Colliding with gadget!");
        			if (! gadget.hit(ball, this)) ballStillInPlay = false;
//        			break;
        		}
        	}

            for (Wall wall : borders) {
            	if (wall.timeUntilCollision(ball) <= timestep && ballStillInPlay) {
//            		System.out.println("Time until wall collision: " + wall.timeUntilCollision(ball));
            		if (! wall.hit(ball, this)) ballStillInPlay = false;
//            		break;
            	}
            }
                
            if (ballStillInPlay) {
            	ball.move(gravity, mu, mu2, timestep, this);
            } else {
                ballsToRemove.add(ball);
                ball.putInBoardRep(this, true);
            } 
        }
        
        for (Ball ball : ballsToRemove) {
        	balls.remove(ball);
        }
        
        System.out.println(this.toString());
    }

}
