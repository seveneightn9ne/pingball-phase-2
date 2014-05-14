package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;

import physics.Vect;

import common.Constants;
import common.RepInvariantException;
import common.netprotocol.*;

/**
 * Makes a PingballServer that:
 *  * listens for connections on the port specified by command-line argument
 *  * creates instances of ClientHandler for each connected client
 *  * Handles interactions between ClientHandler threads including passing balls
 *  * Listens for System.in commands, parses, and handles commands, including fusing and separating boards.
 *
 *  Concurrency Exposition for the server package:
 *  * Many different threads are used on the Pingball Server. They are:
 *      * The main thread, which gets data from other threads via queues
 *      * The CommandLineInterface thread, which sends commands from System.in to the main thread via a queue
 *      * The SocketAcceptor thread, which creates ClientHandler threads when clients connect
 *      * The ClientHandler threads, which send AuthoredMessages to the main thread via a queue
 *
 *  Thread Safety Argument for PingballServer:
 *  * PingballServer calls ClientHandler.send(), but it is the only thread that does so (ClientHandler does not call its own send method)
 *  * PingballServer calls ClientHandler.getName(), but it is the only thread that does so
 *  * PingballServer accepts data from other threads via threadsafe queues:
 *      * cliQueue
 *      * messageQueue
 *      * deadClientsQueue
 *  * The following fields are confined:
 *      * clients
 *      * horizontalBoardJoins
 *      * verticalBoardJoins
 *  * The rest of the fields are immutable.
 *  * The main server thread is the only one to call any PingballServer methods
 *
 *  Rep Invariants:
 *  * MIN_PORT <= port <= MAX_PORT
 *  * for every name -> ClientHandler in clients, client.getName() must be name
 *  * Every list in horizontalBoardJoins and verticalBoardJoins is length 2.
 *  * Every String in the lists of horizontalBoardJoins/verticalBoardJoins is a key in clients.
 *  * No one String is the first of more than one pair in a list, or the second of more than one pair in a list
 *      (e.g. "board1" cannot be the left side of a horizontal join to two different boards)
 *
 */
public class PingballServer {

    private static final int DEFAULT_PORT = 10987;
    private static final int MIN_PORT = 0;
    private static final int MAX_PORT = 65535;
    private final int port;
    private final BlockingQueue<String> cliQueue;
    private final BlockingQueue<AuthoredMessage> messageQueue;
    /* deadClientsQueue is for ClientHandlers who have been killed and need to be removed from the Server's knowledge. */
    private final BlockingQueue<ClientHandler> deadClientsQueue;
    private final Map<String, ClientHandler> clients;
    private final List<List<String>> horizontalBoardJoins; // pairs of boards joined as left, right
    private final List<List<String>> verticalBoardJoins; // pairs of boards joined as top, bottom
    private SocketAcceptor socketAcceptor;
    private ServerGUI gui;

    /**
     * Instantiate a PingballServer
     *
     * @param port the port on which to create the socket
     * @throws IOException if the socket can not be created
     */
    public PingballServer(int port) throws IOException {
        this.port = port;
        this.cliQueue = new LinkedBlockingQueue<String>();
        this.messageQueue = new LinkedBlockingQueue<AuthoredMessage>();
        this.deadClientsQueue = new LinkedBlockingQueue<ClientHandler>();
        this.clients = new HashMap<String, ClientHandler>();
        this.horizontalBoardJoins = new ArrayList<List<String>>();
        this.verticalBoardJoins = new ArrayList<List<String>>();
        this.gui = null;
        
        final PingballServer server = this;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	ServerGUI gui = new ServerGUI(server, cliQueue);
                gui.setVisible(true);
            }
        });

        checkRep();
    }

    /**
     * Run the server, starting the SocketAcceptor thread and the CommandLineInterface thread,
     * and processes all data input from queues
     *
     * @throws IOException if the main server socket is broken
     */
    public void serve() throws IOException {

    	this.socketAcceptor = new SocketAcceptor(port, messageQueue, deadClientsQueue);
        Thread socketThread = new Thread(socketAcceptor);
        socketThread.start();

        Thread cliThread = new Thread(new CommandLineInterface(cliQueue));
        cliThread.start();

        while (true) {

            while (!deadClientsQueue.isEmpty()) {
                if (Constants.DEBUG) System.out.println("Burying dead client.");
                buryDeadClient(deadClientsQueue.remove());
            }

            while (!cliQueue.isEmpty()) {
            	String command = cliQueue.remove();
                if (Constants.DEBUG) System.out.println("Received command: " + command);
                handleCommand(command);
            }

            while (!messageQueue.isEmpty()) {
                AuthoredMessage receivedMessage = messageQueue.remove();
                if (Constants.DEBUG) System.out.println("Received message: " + receivedMessage.getClientHandler().getName() + receivedMessage.getMessage().serialize());
                handleMessage(receivedMessage);
            }

            checkRep();
        }
    }

    /**
     * "buries" a dead client, i.e. processes the disconnect of a client
     * Unfuses boards that were fused to it
     *
     * @param ch the dead client
     */
    private void buryDeadClient(ClientHandler ch) {
        // unpair board from any potential pairs
        List<List<String>> pairsToRemove = new ArrayList<List<String>>();
        for (List<String> pair : horizontalBoardJoins) {
            if (pair.get(0).equals(ch.getName())) {
                clients.get(pair.get(1)).send(new BoardUnfuseMessage(Constants.BoardSide.LEFT));
                pairsToRemove.add(pair);
            }
            if (pair.get(1).equals(ch.getName())) {
                clients.get(pair.get(0)).send(new BoardUnfuseMessage(Constants.BoardSide.RIGHT));
                pairsToRemove.add(pair);
            }
        }
        for (final List<String> pairToRemove : pairsToRemove) {
            horizontalBoardJoins.remove(pairToRemove);
            SwingUtilities.invokeLater(new Runnable() {
            	public void run() {
            		gui.removeConnection("h", pairToRemove.get(0), pairToRemove.get(1));
            	}
            });
        }
        pairsToRemove = new ArrayList<List<String>>();
        for (List<String> pair : verticalBoardJoins) {
            if (pair.get(0).equals(ch.getName())) {
                clients.get(pair.get(1)).send(new BoardUnfuseMessage(Constants.BoardSide.TOP));
                pairsToRemove.add(pair);
            }
            if (pair.get(1).equals(ch.getName())) {
                clients.get(pair.get(0)).send(new BoardUnfuseMessage(Constants.BoardSide.BOTTOM));
                pairsToRemove.add(pair);
            }
        }
        for (final List<String> pairToRemove : pairsToRemove) {
            verticalBoardJoins.remove(pairToRemove);
            SwingUtilities.invokeLater(new Runnable() {
            	public void run() {
            		gui.removeConnection("v", pairToRemove.get(0), pairToRemove.get(1));
            	}
            });
        }
        // remove from clients
        final String clientName = ch.getName();
        clients.remove(clientName);
        
        // remove from gui
        if (gui != null) {
	    	SwingUtilities.invokeLater(new Runnable() {
	    		public void run() {
	    			gui.removeClient(clientName);
	    		}
	    	});
        }

        checkRep();
    }

    /**
     * Start a PingballServer using the given arguments.
     * 
     * @param args must be in the format specified:
     *
     * Usage: PingballServer [--port PORT]
     *
     * PORT is an optional integer in the range 0 to 65535 inclusive, specifying the port the
     * server should be listening on for incoming connections. E.g. "PingballServer --port 1234"
     * starts the server listening on port 1234.
     *
     * If no port is specified, the default port 10987 will be used.
     * Prints to System.err if bad arguments are given
     *
     */
    public static void main(String[] args) {

        // parse command line arguments
        Queue<String> arguments = new LinkedList<String>(Arrays.asList(args));
        int port = DEFAULT_PORT;
        try {
            while ( ! arguments.isEmpty()) {
                String flag = arguments.remove();
                try {
                    if (flag.equals("--port")) {
                        port = Integer.parseInt(arguments.remove());
                        if (port < MIN_PORT || port > MAX_PORT) {
                            throw new IllegalArgumentException("port " + port + " out of range");
                        }
                    } else {
                        throw new IllegalArgumentException("unknown option: \"" + flag + "\"");
                    }
                } catch (NoSuchElementException nsee) {
                    throw new IllegalArgumentException("missing argument for " + flag);
                } catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException("unable to parse number for " + flag);
                }
            }
        } catch (IllegalArgumentException iae) {
            System.err.println(iae.getMessage());
            System.err.println("usage: PingballServer [--port PORT]");
            return;
        }


        // start server
        try {
            PingballServer server = new PingballServer(port);
            server.serve();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

    /**
     * handle a command from the CommandLineInterface. Will fuse two
     * boards in the dimension specified in the command, and notify
     * the boards that they have fused.
     *
     * @param command
     * Acceptable commands:
     *  * "h BOARDNAME1 BOARDNAME2" joins BOARDNAME1 to the left of BOARDNAME2
     *  * "v BOARDNAME1 BOARDNAME2" joins BOARDNAME1 on top of BOARDNAME2
     *
     * Any command in a format other than given above will print to System.err
     * Any board names specified that are not currently connected clients will print to System.err
     */
    private void handleCommand(String command) {
        Pattern headerPattern = Pattern.compile("^\\s*([hv])\\s*([a-zA-Z0-9_]+)\\s*([a-zA-Z0-9_]+)\\s*$");
        Matcher headerMatcher = headerPattern.matcher(command);
        if (! headerMatcher.find()) {
            System.err.println("Bad command: " + command);
            System.err.println("Expected format: h|v BOARDNAME1 BOARDNAME2");
            return;
        }
        final String position = headerMatcher.group(1);
        final String b1 = headerMatcher.group(2);
        final String b2 = headerMatcher.group(3);
        if (!clients.containsKey(b1) || !clients.containsKey(b2)) {
            System.err.println("One or more of those clients were not found. Current connected clients: ");
            for (String clientName : clients.keySet()) {
                System.err.println("\t" + clientName);
            }
            return;
        }
        List<List<String>> boardJoins;
        Constants.BoardSide b1Pos;
        Constants.BoardSide b2Pos;
        if (position.equals("h")) {
            boardJoins = horizontalBoardJoins;
            b1Pos = Constants.BoardSide.LEFT;
            b2Pos = Constants.BoardSide.RIGHT;
        } else { //position.equals("v")
            boardJoins = verticalBoardJoins;
            b1Pos = Constants.BoardSide.TOP;
            b2Pos = Constants.BoardSide.BOTTOM;
        }
        boolean overwrote = false; // is true if we find an existing board connection and we overwrite it with this new one
        for (List<String> pair : boardJoins) {
            final String p1 = pair.get(0);
            final String p2 = pair.get(1);
            if (p1.equals(b1)) { // overwrite existing board connection
            	SwingUtilities.invokeLater(new Runnable() {
            		public void run() {
            			gui.removeConnection(position, p1, p2);
            		}
            	});
                pair.set(1, b2);
                overwrote = true;
                break;
            }
            if (p2.equals(b2)) {
            	SwingUtilities.invokeLater(new Runnable() {
            		public void run() {
            			gui.removeConnection(position, p1, p2);
            		}
            	});
                pair.set(0, b1);
                overwrote = true;
                break;
            }
        }
        if (!overwrote) { // new connection, not replacing an old one
            boardJoins.add(Arrays.asList(b1, b2));
        }
        // tell the boards they joined
        clients.get(b1).send(new BoardFuseMessage(b2, b2Pos)); // if b1 is the LEFT board, then the fuse happens on its RIGHT side.
        clients.get(b2).send(new BoardFuseMessage(b1, b1Pos));

    	SwingUtilities.invokeLater(new Runnable() {
    		public void run() {
    			gui.addConnection(position, b1, b2);
    		}
    	});
    }

    /**
     * handle a NetworkMessage from a ClientHandler
     *
     * @param authoredMessage the AuthoredMessage containing the NetworkMessage
     * and the ClientHandler who received the message.
     *
     * handleMessage knows how to deal with the following types of NetworkMessage:
     * * ClientConnectMessage
     * * BallOutMessage
     * Any other type of NetworkMessage will be ignored.
     */
    private void handleMessage(AuthoredMessage authoredMessage) {

        NetworkMessage message = authoredMessage.getMessage();
        final ClientHandler ch = authoredMessage.getClientHandler();

        if (message instanceof ClientConnectMessage) {
            if (clients.containsKey(ch.getName())) {
                // don't let the client connect with the same name!
                ch.send(new ConnectionRefusedMessage("Board with this name already connected to server"));
                ch.kill();
            } else {
                clients.put(ch.getName(), ch);
                if (gui != null) {
                	SwingUtilities.invokeLater(new Runnable() {
                		public void run() {
                        	gui.addClient(ch.getName(), ch.getIP());
                		}
                	});
                }
                
            }
        }
        else if (message instanceof BallOutMessage) {
            List<List<String>> lookInJoinList;
            int myIndex; // 0 or 1
            int myPairsIndex; // 1 or 0
            Constants.BoardSide toSide;
            switch (((BallOutMessage) message).getFromSide()) {
                case TOP: // if a ball went out through the top, I must be the bottom board
                    lookInJoinList = verticalBoardJoins;
                    myIndex = 1;
                    myPairsIndex = 0;
                    toSide = Constants.BoardSide.BOTTOM;
                    break;
                case BOTTOM:
                    lookInJoinList = verticalBoardJoins;
                    myIndex = 0;
                    myPairsIndex = 1;
                    toSide = Constants.BoardSide.TOP;
                    break;
                case LEFT:
                    lookInJoinList = horizontalBoardJoins;
                    myIndex = 1;
                    myPairsIndex = 0;
                    toSide = Constants.BoardSide.RIGHT;
                    break;
                default: // case RIGHT:
                    lookInJoinList = horizontalBoardJoins;
                    myIndex = 0;
                    myPairsIndex = 1;
                    toSide = Constants.BoardSide.LEFT;
                    break;
            }
            for (List<String> pair : lookInJoinList) {
                if (pair.get(myIndex).equals(ch.getName())) {
                    Vect ballPos = ((BallOutMessage) message).getBallPos();
                    Vect ballVel = ((BallOutMessage) message).getBallVel();
                    clients.get(pair.get(myPairsIndex)).send(new BallInMessage(ballPos, ballVel, toSide));
                    break; // this only happens once so we can spare some looping
                }
            }

        } else if (message instanceof TeleportOutMessage) {
            String boardTo = ((TeleportOutMessage) message).getBoardTo();
            String portalTo = ((TeleportOutMessage) message).getPortalTo();
            String boardFrom = ((TeleportOutMessage) message).getBoardFrom();
            String portalFrom = ((TeleportOutMessage) message).getPortalFrom();
            Vect ballVel = ((TeleportOutMessage) message).getBallVel();
            if (clients.containsKey(boardTo)) {
                clients.get(boardTo).send(new TeleportInMessage(ballVel, boardFrom, portalFrom, boardTo, portalTo));
                
            } else {
                ch.send(new TeleportFailMessage(ballVel, boardFrom, portalFrom, boardTo, portalTo));
                
            }
            
        } else if (message instanceof TeleportFailMessage) {
            String boardFrom = ((TeleportOutMessage) message).getBoardFrom();
            if (clients.containsKey(boardFrom)) {
                clients.get(boardFrom).send(message);
                
            }
            
        } else {
            if (Constants.DEBUG) {
                System.err.println("Received unexpected NetworkMessage: " + message.serialize());
                
            }
        }
    }
    /**
     * Asserts the rep invariants by throwing a runtime exception if they fail
     *
     * Rep Invariants:
     *  * MIN_PORT <= port <= MAX_PORT
     *  * for every name -> ClientHandler in clients, client.getName() must be name
     *  * Every list in horizontalBoardJoins and verticalBoardJoins is length 2.
     *  * Every String in the lists of horizontalBoardJoins/verticalBoardJoins is a key in clients.
     *  * No one String is the first more than one pair in a list, or the second of more than one pair in a list
     *      (e.g. "board1" cannot be the left side of a horizontal join to two different boards)
     *
     */
    private void checkRep() {
        if (port < MIN_PORT || port > MAX_PORT) {
            throw new RepInvariantException("Port out of range");
        }
        for (String name : clients.keySet()) {
            if (!clients.get(name).getName().equals(name)) {
                throw new RepInvariantException("Client name inconsistent");
            }
        }
        Set<String> isLeftBoard = new HashSet<String>();
        Set<String> isRightBoard = new HashSet<String>();
        for (List<String> pair : horizontalBoardJoins) {
            if (!(pair.size() == 2)) {
                throw new RepInvariantException("A pair is not 2");
            }
            if (!clients.containsKey(pair.get(0)) || !clients.containsKey(pair.get(1))) {
                throw new RepInvariantException("A joined client is not in the list of clients");
            }
            if (isLeftBoard.contains(pair.get(0)) || isRightBoard.contains(pair.get(1))) {
                throw new RepInvariantException("A board is joined to more than one board");
            }
            isLeftBoard.add(pair.get(0));
            isRightBoard.add(pair.get(1));
        }
        Set<String> isTopBoard = new HashSet<String>();
        Set<String> isBottomBoard = new HashSet<String>();
        for (List<String> pair : verticalBoardJoins) {
            if (!(pair.size() == 2)) {
                throw new RepInvariantException("A pair is not 2");
            }
            if (!clients.containsKey(pair.get(0)) || !clients.containsKey(pair.get(1))) {
                throw new RepInvariantException("A joined client is not in the list of clients");
            }
            if (isTopBoard.contains(pair.get(0)) || isBottomBoard.contains(pair.get(1))) {
                throw new RepInvariantException("A board is joined to more than one board");
            }
            isTopBoard.add(pair.get(0));
            isBottomBoard.add(pair.get(1));
        }
    }
    
    /**
     * Tell the Server that a ServerGUI is available
     * @param gui the ServerGUI that will display the Server's info
     */
    public void notifyGUI(ServerGUI gui) {
    	this.gui = gui;
    }
    
    /**
     * @return the port by which clients can connect to the server
     */
    public int getPort() {
    	return port;
    }

    /** 
     * Get the server's IP address
     * @return the address by which clients can connect to the server
     */
    public String getIP() {
    	return socketAcceptor.getIP();
    }

}