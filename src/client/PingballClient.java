package client;

import java.util.concurrent.BlockingQueue;


/**
 * Pingball game client.
 *
 * Pingball client creates a game board from a text file, connects
 * to a pingball server, and runs the pingball game while
 * communicating with the server about inter-board fuses and ball
 * transfers.
 *
 * Thread Safety Argument:
 * - board is confined to the main thread.
 * - incomingMessages is a threadsafe datatype.
 */
public class PingballClient {
    private BlockingQueue incomingMessages; //will be final when implemented

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
     * If no port is specified, the default port 10987 will be used.
     *
     */
    public static void main(String[] args) {
        Board board;

        // parse command line arguments
        // load board
        // start client


    }
}
