package client;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import common.netprotocol.NetworkMessage;

/**
 * ServerHandler is a runnable meant to run as a thread.
 * It handles communication with the server including wire protocol and non-blockng communication with the socket.
 *
 * Thread Safety Argument:
 * - the socket reference is confined to this thread.
 *     - reading from the socket is confined to the ServerHandler thread.
 *     - writing to the socket is confined to the PingballClient thread.
 * - incomingMessages is a threadsafe datatype.
 */
public class ServerHandler implements Runnable {
    private final Socket socket;
    private final BlockingQueue incomingMessages;

    /**
     * Create a ServerHandler.
     *
     * @param socket to communicate with the server.
     *        socket must already be connected.
     *        the caller must throw away their reference to socket after creating a ServerHandler
     * @param incomingMessages threadsafe queue to receive messages
     */
    ServerHandler(Socket socket, BlockingQueue incomingMessages) {
        this.socket = socket;
        this.incomingMessages = incomingMessages;
    }

    /**
     * Start listening for messages from the server.
     */
    @Override
    public void run() {
        // TODO listen to and send messages in a loop
    }

    /**
     * Send a message to the server. Requires the thread to be running!
     * @param message message to send
     */
    public void send(NetworkMessage message) {
        String strMessage = message.serialize();
        //out.println(strMessage);
    }
}
