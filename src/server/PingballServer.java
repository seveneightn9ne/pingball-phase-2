package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import common.netprotocol.NetworkMessage;

/**
 * Makes a PingballServer that:
 *  * listens for connections on the port specified by command-line argument
 *  * creates instances of ClientHandler for each connected client
 *  * Handles interactions between ClientHandler threads including passing balls
 *  * Listens for System.in commands, parses, and handles commands, including fusing and separating boards.
 *
 *  Thread Safety Argument:
 *  * Many different threads are used on the Pingball Server. They are:
 *      * The main thread, which gets data from other threads via queues
 *      * The CommandLineInterface thread, which sends commands from System.in to the main thread via a queue
 *      * The SocketAcceptor thread, which creates ClientHandler threads when clients connect
 *      * The ClientHandler threads, which send NetworkMessages to the main thread via a queue
 *  * All data is transferred by passing to constructors or through BlockingQueues.
 *  * No mutable data is passed between threads.
 *
 */

public class PingballServer {

    /*
     * Rep Invariant:
     * MIN_PORT <= port <= MAX_PORT
     */
    private static final int DEFAULT_PORT = 10987;
    private static final int MIN_PORT = 0;
    private static final int MAX_PORT = 65535;
    private final int port;
    private final BlockingQueue<String> cliQueue;
    private final BlockingQueue<NetworkMessage> messageQueue;
    private final List<ClientHandler> clients;

    /**
     * Instantiate a PingballServer
     * @param port the port on which to create the socket
     * @throws IOException if the socket can not be created
     */
    public PingballServer(int port) throws IOException {
        this.port = port;
        this.cliQueue = new LinkedBlockingQueue<String>();
        this.messageQueue = new LinkedBlockingQueue<NetworkMessage>();
        this.clients = Collections.synchronizedList(new ArrayList<ClientHandler>());
    }

    /**
     * Run the server, listening for client connections and handling them.
     * Never returns unless an exception is thrown.
     *
     * @throws IOException if the main server socket is broken
     */
    public void serve() throws IOException {

        Thread socketThread = new Thread(new SocketAcceptor(port, messageQueue));
        socketThread.start();

        Thread cliThread = new Thread(new CommandLineInterface(cliQueue));
        cliThread.start();

        while (true) {
            // TODO empty cliQueue

            // TODO empty messageQueue
            // this will include adding new clients to the clients list
        }
    }

    /**
     * Start a PingballServer using the given arguments.
     *
     * Usage: PingballServer [--port PORT]
     *
     * PORT is an optional integer in the range 0 to 65535 inclusive, specifying the port the
     * server should be listening on for incoming connections. E.g. "PingballServer --port 1234"
     * starts the server listening on port 1234.
     *
     * If no port is specified, the default port 10987 will be used.
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
        } catch (IOException e) {}

    }

}
