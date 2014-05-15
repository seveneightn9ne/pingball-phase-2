package client;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import client.gadgets.Gadget;
import client.gadgets.Portal;
import client.gadgets.Wall;
import common.Constants;
import common.Constants.BoardSide;

/**
 * a Board is an object that represents the 20x20 grid on which Pingball is played.
 * The board has gadgets and balls on it. The board can connect a wall to a server. 
 * 
 * Thread Safety: The board is not thread safe. It is meant to only run in one thread. 
 */
public class Board {

	/**
	 * Rep invariant: 
	 * * boardRep is 22x22. 
	 * * boardRep always represents the most up-to-date state of the board.
	 * * There are always 4 walls, one of each type TOP, RIGHT, BOTTOM, LEFT
	 */
	private double gravity;
	private double mu;
	private double mu2;
	private String name;
	private List<Gadget> gadgets = new ArrayList<Gadget>();
	private HashMap<String, Portal> portals = new HashMap<String, Portal>();
	private List<Ball> balls = new ArrayList<Ball>();
	private List<Ball> absorbed = new ArrayList<Ball>();
	private char[][] boardRep = new char[22][22];
	private Wall[] borders = new Wall[4];
	private Map<String, Gadget> gadgetNames = new HashMap<String, Gadget>();
	private Hashtable<String, ArrayList<String>> keyupToTriggers = new Hashtable<String, ArrayList<String>>();
    private Hashtable<String, ArrayList<String>> keydownToTriggers = new Hashtable<String, ArrayList<String>>();
	
    /**
     * Constructor for the board.
     * @param name
     *              - name of the board
     * @param gravity
     *              - value of gravity on the board (in d/s)
     * @param mu
     *              - first value of friction on the board
     * @param mu2
     *              - second value of friction on the board
     * @param keyups
     *              - mapping of keys to names of gadgets they trigger on key release
     * @param keydowns
     *              - mapping of keys to names of gadgets they trigger on key press
     */
	public Board(String name, double gravity, double mu, double mu2, Hashtable<String, ArrayList<String>> keyups, Hashtable<String, ArrayList<String>> keydowns) {
		this.name = name;
		this.gravity = gravity;
		this.mu = mu;
		this.mu2 = mu2;
		this.boardConstructor();
		this.keyupToTriggers = keyups;
		this.keydownToTriggers = keydowns;
	}
	
	/**
	 * Triggers appropriate actions on the board
	 * due to a key release
	 * @param key - String representation of key that has been released
	 */
	public void notifyKeyup(String key) {
	    if (keyupToTriggers.get(key) == null) {
	        return;
	    }
	    for (String name : keyupToTriggers.get(key)) {
	        gadgetNames.get(name).action(this);
	    }
	}
	
	/**
     * Triggers appropriate actions on the board
     * due to a key press
     * @param key - String representation of key that has been pressed
     */
    public void notifyKeydown(String key) {
        if (keydownToTriggers.get(key) == null) {
            return;
        }
        for (String name : keydownToTriggers.get(key)) {
            gadgetNames.get(name).action(this);
        }
    }
	
    /**
     * @return list of balls currently on this board
     */
	public List<Ball> getBalls()
	{
		return balls;
	}
	/**
	 * Initializes appropriate borders and walls for board object
	 */
	private void boardConstructor() {

		borders[0] = new Wall(Constants.BoardSide.TOP);
		borders[1] = new Wall(Constants.BoardSide.RIGHT);
		borders[2] = new Wall(Constants.BoardSide.BOTTOM);
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
	 * Set the server handler so that the walls and portals can inform other Boards over the
	 * network if a ball is transferred.
	 * 
	 * @param sh
	 *            the server handler
	 */
	public void setServerHandler(ServerHandler sh) {
		for (Wall wall : borders) {
			wall.setServerHandler(sh);
		}
		for (Portal portal : portals.values()) {
		    portal.setServerHandler(sh);
		}
	}
	
	/**
	 * Disconnects this board from the server
	 */
	public void disconnectFromServer() {
		for (BoardSide side : BoardSide.values()) {
			disconnectWallFromServer(side);
			getWall(side).setServerHandler(null); // just to be safe
		}
	}

	/**
	 * connects a wall to a board across the network.
	 * @param side the side of this board on which to attach the other board
	 * @param name the name of the other board, so we can display it
	 */
	public void connectWallToServer(Constants.BoardSide side, String name) {
		getWall(side).connectToServer(name);
		for (int i = 1; i <= name.length() && i <= 22; i++) {
			if (side == Constants.BoardSide.TOP) {
				boardRep[0][i] = name.charAt(i - 1);
			}

			else if (side == Constants.BoardSide.RIGHT) {
				boardRep[i][21] = name.charAt(i - 1);
			}

			else if (side == Constants.BoardSide.BOTTOM) {
				boardRep[21][i] = name.charAt(i - 1);
			}

			else if (side == Constants.BoardSide.LEFT) {
				boardRep[i][0] = name.charAt(i - 1);
			}
		}
	}

	/**
	 * Disconnect the wall on side side from the server
	 * @param side the side from which to disconnect 
	 */
	public void disconnectWallFromServer(Constants.BoardSide side) {
		Wall wall = getWall(side);
		int nameLen = wall.getName().length();
		for (int i = 1; i <= nameLen && i <= 22; i++) {
			if (side == Constants.BoardSide.TOP) {
				boardRep[0][i] = '.';
			}

			else if (side == Constants.BoardSide.RIGHT) {
				boardRep[i][21] = '.';
			}

			else if (side == Constants.BoardSide.BOTTOM) {
				boardRep[21][i] = '.';
			}

			else if (side == Constants.BoardSide.LEFT) {
				boardRep[i][0] = '.';
			}
		}
		getWall(side).disconnectFromServer();
	}

	/**
	 * given a side, return the wall on that side
	 * @param side the side of the board
	 * @return the wall on that side
	 */
	public Wall getWall(Constants.BoardSide side) {
		if (side == Constants.BoardSide.TOP)
			return borders[0];
		if (side == Constants.BoardSide.RIGHT)
			return borders[1];
		if (side == Constants.BoardSide.BOTTOM)
			return borders[2];
		else
			return borders[3];
	}
	
	/**
	 * @return list of this board's walls
	 */
	public Wall[] getWallList()
	{
		return borders;
	}

	/**
	 * Accessor method
	 * @return the boardRep of this Board
	 */
	public char[][] getBoardRep() {
		return boardRep;
	}

	/**
	 * Accessor method
	 * @return the name of this Board
	 */
	public String getName() {
		return name;
	}

	/**
	 * Mutator method
	 * @param boardRep
	 *            the 2D char array boardRep to assign to this Board
	 *            Must be 22x22
	 */
	public void setBoardRep(char[][] boardRep) {
		this.boardRep = boardRep;
		checkRep();
	}
	
	/**
	 * @return list of gadgets on this board
	 */
	public List<Gadget> returnGadgets()
	{
		return gadgets;
	}

	/**
	 * Add a gadget to the board at the position of the Gadget object
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
					if (gadget.toString().equals("@")) {
	                    portals.put(name, (Portal) gadget);
	                }
				}
			}
		}
	}
	
	/**
	 * Gets the portal with the specified name on this board.
	 * @param name 
	 *         name of portal
	 * @return
	 *         portal with name "name"
	 */
	public Portal getPortal(String name) {
	    if (portals.containsKey(name)) {
	        return portals.get(name);
	    }
	    else {
	        return null;
	    }
	}
	
	/**
	 * Add a Ball to the board with position and velocity specified in the Ball
	 * object
	 * @param ball
	 *            Ball to add
	 */
	public void addBall(Ball ball) {
		balls.add(ball);
     	ball.putInBoardRep(this, false);
	}

	/**
	 * @return the Pingball board representation as described by the Project Phase 1 spec.
	 */
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
	 * Update the board assuming timestep has passed since the last update.
	 * Includes collisions with gadgets & gadget action triggers, and the ball moving 
	 * according to gravity, friction, etc. 
	 * Also prints the board to System.out
	 * @param timestep the amount of time since the last update has been called
	 */
    public void update(double timestep) {

    	List<Ball> ballsToRemove = new ArrayList<Ball>();

        for (Ball ball : balls) {

            boolean ballStillInPlay = true;
            
            for (Ball ball2 : balls) {
                if (ball2 != ball) {
                    if (ball.timeUntilCollision(ball2) <= timestep && ballStillInPlay) {
                        ball.hit(ball2);
                    }
                }
            }
            
        	for (Gadget gadget : gadgets) {
        		if (gadget.timeUntilCollision(ball) <= timestep && ballStillInPlay) {
        			if (! gadget.hit(ball, this)) ballStillInPlay = false;
        		}
        	}

            for (Wall wall : borders) {
            	if (wall.timeUntilCollision(ball) <= timestep && ballStillInPlay) {
            		if (! wall.hit(ball, this)) ballStillInPlay = false;
            	}
            }
            
                
            if (ballStillInPlay) {
                if (!absorbed.contains(ball)) {
                    ball.move(gravity, mu, mu2, timestep, this);
                }
            } else {
                ballsToRemove.add(ball);
                ball.putInBoardRep(this, true);
            } 
        }
        
        for (Ball ball : ballsToRemove) {
        	balls.remove(ball);
        }
        
		// System.out.println(this.toString());
        checkRep();
    }
    
    /**
     * @param ball ball that has been absorbed
     */
    public void notifyAbsorbed(Ball ball) {
        absorbed.add(ball);
    }
    
    /**
     * @param ball ball that has been released
     */
    public void notifyReleased(Ball ball) {
        absorbed.remove(ball);
    }
    
    /**
     * Throws a runtime exception if the boardRep is the wrong size
     */
    private void checkRep() {
    	if (boardRep.length != 22) throw new RuntimeException("BoardRep is not 22 tall");
    	if (boardRep[0].length != 22) throw new RuntimeException("BoardRep is not 22 wide");
    }
}
