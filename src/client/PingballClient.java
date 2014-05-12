package client;

import java.awt.EventQueue;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import client.gadgets.Portal;
import common.Constants;
import common.RepInvariantException;
import common.netprotocol.*;

import java.io.File;
import java.net.Socket;

import kineticpingball.KineticModel;
import javax.swing.SwingUtilities;

import physics.Vect;
import threadBoard.Skeleton;

/**
 *
 * Pingball client creates a game board from a text file, connects
 * to a pingball server, and runs the pingball game while
 * communicating with the server about inter-board fuses and ball
 * transfers.
 *
 * Thread Safety Argument:
 * - board and its gadgets are confined to the PingballClient thread.
 * - the serverHandler thread passes messages via incomingMessages which is a threadsafe datatype.
 * - any GUI thread accessing the client does so by adding runnables to the threadsafe invokeLater queue.
 *
 * Rep Invariant:
 * - incomingMessages must not be null. 
 * - if board is null, so is serverHandler and boardPath. 
 */
public class PingballClient {
    private Board board;
    private String boardPath;
//    private Socket socket;
    private ServerHandler serverHandler;
    private final BlockingQueue<NetworkMessage> incomingMessages;
    private final AtomicBoolean paused;
    private final BlockingQueue<Runnable> invokeLaterQueue;
    
    /**
     * Create a Pingball client (This should only be called via PingballClient.main)
     */
    public PingballClient() {
    	incomingMessages = new LinkedBlockingQueue<NetworkMessage>();
    	invokeLaterQueue = new LinkedBlockingQueue<Runnable>();
    	paused = new AtomicBoolean(false);
    }

    /**
     * startClient starts the serverHandler if necessary, then loops forever
     * stepping the board, processing incomingMessages, and printing the board
     *
     */
    public void startClient() {
        checkRep();
        
        final PingballClient client = this;
        String hostnameValue = null;
        if(serverHandler != null) {
        	hostnameValue = serverHandler.getHostName();
        }
        final String hostname = hostnameValue;
        final Board b = board;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	
                PingballGUI main = new PingballGUI(client, hostname, b);

                main.setVisible(true);
            }
        });

        if (Constants.DEBUG) System.out.println("Reached main loop.");
        
        while(true){
			while (!invokeLaterQueue.isEmpty()) {
				Runnable r = invokeLaterQueue.remove();
				r.run();
			}
			if (!paused.get()) {
	            try {
	                // Sleep to limit framerate.
	                Thread.sleep((int) (Constants.TIMESTEP));
	            } catch (InterruptedException e) {
	                Thread.currentThread().interrupt();
	            }
	
	            while (!incomingMessages.isEmpty() && board != null) {
	                NetworkMessage message = incomingMessages.remove();
	                System.out.println(message);
	                if (message instanceof BallInMessage) {
	                    // The sending board is responsible for making ballPos on the correct side of the receiving board.
	                    Vect ballPos = ((BallInMessage) message).getBallPos();
	                    Vect ballVel = ((BallInMessage) message).getBallVel();
	                    board.addBall(new Ball(ballPos, ballVel));
	                } else if (message instanceof BoardFuseMessage) {
	                    Constants.BoardSide side = ((BoardFuseMessage) message).getSide();
	                    String name = ((BoardFuseMessage) message).getBoardName();
	                    board.connectWallToServer(side, name);
	                } else if (message instanceof BoardUnfuseMessage) {
	                    Constants.BoardSide side = ((BoardUnfuseMessage) message).getSide();
	                    board.disconnectWallFromServer(side);
	                } else if (message instanceof ConnectionRefusedMessage) {
	                    // when the serverHandler receives a ConnectionRefusedMessage it
	                    // kills itself (calls this.kill()) and then passes the message to PingballClient.
	                    if (Constants.DEBUG) {
	                        System.err.println("Connection refused by server. Reason: " + ((ConnectionRefusedMessage) message).getReason());
	                    }
	                } else if (message instanceof TeleportInMessage) {
	                    String portalTo = ((TeleportInMessage) message).getPortalTo();
	                    Vect ballVel = ((TeleportInMessage) message).getBallVel();
	                    Portal portal = board.getPortal(portalTo);
	                    if (portal == null) {
	                        String boardTo = ((TeleportInMessage) message).getBoardTo();
	                        String boardFrom = ((TeleportInMessage) message).getBoardFrom();
	                        String portalFrom = ((TeleportInMessage) message).getPortalFrom();
	                        serverHandler.send(new TeleportFailMessage(ballVel, boardFrom, portalFrom, boardTo, portalTo));
	                    } else {
	                        Ball ball = new Ball(portal.getCenter(), ballVel);
	                        board.addBall(ball);
	                        portal.giveBall(ball);
	                    }
	                } else if (message instanceof TeleportFailMessage) {
	                    String portalFrom = ((TeleportFailMessage) message).getPortalFrom();
	                    Vect ballVel = ((TeleportFailMessage) message).getBallVel();
	                    Portal portal = board.getPortal(portalFrom);
	                    Ball ball = new Ball(portal.getCenter(), ballVel);
	                    board.addBall(ball);
	                    portal.giveBall(ball);
	                }
	            }

				if (board != null) board.update(Constants.TIMESTEP);
			}
        }
    }

    /**
     * Verify that the rep invariant is not violated
     */
    private void checkRep() {
        if ((board == null ) && (serverHandler != null || boardPath != null)) {
            throw new RepInvariantException("serverHandler and boardPath must be null if board is");
        }
        if (incomingMessages == null) {
        	throw new RepInvariantException("incomingMessages must not be null");
        }
    }

    /**
     * Start a PingballClient using the given arguments.
     * 
     * @param args must be in the following format: 
     * 
     * Usage: PingballClient [--host HOST] [--port PORT] [FILE]
     *
     * HOST is an optional hostname or IP address of the server to connect to.
     * If no HOST is provided, then the client starts in single-machine play mode.
     *
     * PORT is an optional integer in the range 0 to 65535 inclusive, specifying the port
     * where the server is listening for incoming connections. The default port is 10987.
     *
     * FILE is an optional argument specifying a file pathname
     * of the Pingball board that this client should run.
     *
     */
    public static void main(String[] args) {
        int port = Constants.DEFAULT_PORT;
        String hostname = null;
        String boardFilePath = null;

        Queue<String> arguments = new LinkedList<String>(Arrays.asList(args));
        try {
            while ( ! arguments.isEmpty()) {
                String flag = arguments.remove();
                try {
                    if (flag.equals("--port")) {
                        port = Integer.parseInt(arguments.remove());
                        if (port < Constants.MIN_PORT || port > Constants.MAX_PORT) {
                            throw new IllegalArgumentException("port " + port + " out of range");
                        }
                    } else if (flag.equals("--host")) {
                        hostname = arguments.remove();
                    } else {
                        if (boardFilePath != null) {
                            throw new IllegalArgumentException("Extra argument: " + flag);
                        }
                        boardFilePath = flag;
                        // throw new IllegalArgumentException("unknown option: \"" + flag + "\"");
                    }
                } catch (NoSuchElementException nsee) {
                    throw new IllegalArgumentException("missing argument for " + flag);
                } catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException("unable to parse number for " + flag);
                }
            }

        } catch (IllegalArgumentException iae) {
            System.err.println(iae.getMessage());
            System.err.println("Usage: PingballClient [--host HOST] [--port PORT] [FILE]");
            return;
        }


        PingballClient client = new PingballClient();
        if (boardFilePath != null)
        	client.setBoard(boardFilePath);
        if (hostname != null) {
        	try {client.connectToServer(hostname, port);} 
        	catch (IOException e) {
        		System.out.println("Could not connect to " + hostname + ":" + port);
        	}
        	catch (RuntimeException e) {
        		System.err.println("Tried to connect to server without a valid board");
        	}
        }
        client.startClient();
//		if (client != null) {
//			// new Skeleton(board);
//			EventQueue.invokeLater(new Runnable() {
//				public void run() {
//					// new KineticModel(board);
//					new Skeleton(board);
//				}
//			});
//			client.startClient();
    }
    
    /**
     * Change the board to the one at path
     * This will disconnect the client from the server
     * @param path the file to read the board from. Must be a valid .pb board file. 
     */
    protected void setBoard(String path) {
    	disconnectFromServer();
        this.board = Parser.makeBoard(new File(path));
        this.boardPath = path;

    	if(Constants.DEBUG) System.out.println(board.toString());
    }
    
    /**
     * Connect to the server at hostname on the given port. 
     * If the client is already connected to a server, that connection will be ended. 
     * @param hostname the host name of the server. Must not be null
     * @param port the port on which to connect to the server
     * @throws IOException if the connection cannot be established. In this case, 
     * any existing server connection will still be terminated. 
     */
    protected void connectToServer(String hostname, int port) throws IOException {
    	
    	if (board == null) {
    		throw new RuntimeException("Board must be set before connecting to server.");
    	}
    
    	disconnectFromServer();
    	Socket socket = null;
        if (hostname != null) {
        	System.out.print("Connecting to server...");
            socket = new Socket(hostname, port);
            System.out.println(" done.");
        }
        
        // make serverHandler and send connection message to server
        if (socket != null) {
        	serverHandler = new ServerHandler(socket, incomingMessages);
            serverHandler.send(new ClientConnectMessage(board.getName()));
            Thread serverHandlerThread = new Thread(serverHandler);
            serverHandlerThread.start();
            board.setServerHandler(serverHandler);
            
        }
    }
    
    /**
     * Disconnects from server
     */
    protected void disconnectFromServer() {
    	if (serverHandler != null)
    		serverHandler.kill();
    	serverHandler = null;
    	incomingMessages.clear();
    	if (board != null) board.setServerHandler(null);
    }
    
    /**
     * Stops the board from updating or from handling server messages
     * TODO: should we disconnect from the server? or what?
     */
    protected void pause() {
    	paused.set(true);
    }
    
    /**
     * Resume board updates and server message handling
     */
    protected void resume() {
    	paused.set(false);
    }
    
    /**
     * Restart the client with the same board as is currently set.
     * Requires that the client currently has a board.
     */
    protected void restart() {
    	setBoard(boardPath);
    }
    
    /**
     * Execute client actions on the main client thread
     * @param r the runnable to run
     */
    public void invokeLater(Runnable r) {
    	invokeLaterQueue.add(r);
    }
}
