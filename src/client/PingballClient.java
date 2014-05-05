package client;


import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import common.Constants;
import common.RepInvariantException;
import common.netprotocol.*;

import java.io.File;
import java.net.Socket;

import physics.Vect;


/**
 * Pingball game client.
 *
 * Pingball client creates a game board from a text file, connects
 * to a pingball server, and runs the pingball game while
 * communicating with the server about inter-board fuses and ball
 * transfers.
 *
 * Thread Safety Argument:
 * - board and gadgets are confined to the main thread.
 * - incomingMessages is a threadsafe datatype.
 * - serverHandler is a separate thread that only shares data via incomingMessages.
 *
 * Rep Invariant:
 * - socket and serverHandler must either both be null or both be non-null.
 */
public class PingballClient {
    private final Board board;
    private final Socket socket;
    private final ServerHandler serverHandler;
    private final BlockingQueue<NetworkMessage> incomingMessages;
    

    /**
     * Instantiate a PingballClient.
     *
     * @param board the board to start the client with
     * @param socket socket to communicate with the server
     *               if socket is null, server communication is disabled.
     * @throws IOException if there is a problem establishing a connection.
     */
    public PingballClient(Board board, Socket socket) throws IOException {
        this.board = board;
        this.socket = socket;
        this.incomingMessages = new LinkedBlockingQueue<NetworkMessage>();
        if (socket != null) {
            serverHandler = new ServerHandler(socket, incomingMessages);
            if (Constants.DEBUG) System.out.println("Sending initial message.");
            serverHandler.send(new ClientConnectMessage(board.getName()));
            if (Constants.DEBUG) System.out.println("Sent initial message.");
            board.setServerHandler(serverHandler);
            if (Constants.DEBUG) System.out.println("Already set serverHandler.");
        } else {
            this.serverHandler = null;
        }
        checkRep();
    }

    /**
     * startClient starts the serverHandler if necessary, then loops forever
     * stepping the board, processing incomingMessages, and printing the board
     *
     */
    public void startClient() {
        checkRep();
        if (socket != null) {
            Thread serverHandlerThread = new Thread(serverHandler);
            serverHandlerThread.start();
        }

        if (Constants.DEBUG) System.out.println("Reached main loop.");
        while(true){
            try {
                // Sleep to limit framerate.
                Thread.sleep((int) (Constants.TIMESTEP * 1000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            while (!incomingMessages.isEmpty()) {
                NetworkMessage message = incomingMessages.remove();
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
                }
            }

            board.update(Constants.TIMESTEP);
            
        }
    }

    /**
     * Verify that the rep invariant is not violated
     */
    private void checkRep() {
        if ((socket == null) != (serverHandler == null)) {
            throw new RepInvariantException("socket and serverHandler must have the same nullness");
        }
    }

    /**
     * Start a PingballClient using the given arguments.
     *
     * Usage: PingballClient [--host HOST] [--port PORT] FILE
     *
     * HOST is an optional hostname or IP address of the server to connect to.
     * If no HOST is provided, then the client starts in single-machine play mode.
     *
     * PORT is an optional integer in the range 0 to 65535 inclusive, specifying the port
     * where the server is listening for incoming connections. The default port is 10987.
     *
     * FILE is a required argument specifying a file pathname
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

            if (boardFilePath == null) {
                throw new IllegalArgumentException("Missing BOARD filepath");
            }
        } catch (IllegalArgumentException iae) {
            System.err.println(iae.getMessage());
            System.err.println("Usage: PingballClient [--host HOST] [--port PORT] FILE");
            return;
        }

        // Create assets for the client and start it.

        Board board = Parser.makeBoard(new File(boardFilePath));

        Socket socket = null;
        if (hostname != null) {
            try {
                socket = new Socket(hostname, port);
            } catch (IOException e) {
                System.err.println("Could not connect to server " + hostname + ":" + port);
                return;
            }
        }

        PingballClient client = null;
        try {
            client = new PingballClient(board, socket);
        } catch (IOException e) {
            System.err.println("Error while connecting to server");
        }

        if (client != null) {
            client.startClient();
        }
    }
}
