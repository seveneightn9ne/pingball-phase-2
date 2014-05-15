package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import common.Constants;
import common.netprotocol.*;
import common.netprotocol.NetworkMessage.DecodeException;

/**
 * ServerHandler is a runnable meant to run as a thread.
 * It handles communication with the server including wire protocol and non-blockng communication with the socket.
 *
 * Thread Safety Argument:
 * - the socket reference is confined to this thread.
 *     - reading from the socket is confined to the ServerHandler thread.
 *     - writing to the socket is confined to the PingballClient thread.
 *     - sockets support full duplex communication, so this is ok.
 * - incomingMessages is a threadsafe datatype.
 */
public class ServerHandler implements Runnable {
    private final Socket socket;
    private final BlockingQueue<NetworkMessage> incomingMessages;
    private final BufferedReader in;
    private final PrintWriter out;

    /**
     * Create a ServerHandler.
     *
     * @param socket to communicate with the server.
     *        socket must already be connected.
     *        the caller must throw away their reference to socket after creating a ServerHandler
     * @param incomingMessages threadsafe queue to receive messages
     * @throws IOException if an error occurs while initializing the connection
     */
    ServerHandler(Socket socket, BlockingQueue<NetworkMessage> incomingMessages) throws IOException {
        this.socket = socket;
        this.incomingMessages = incomingMessages;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    /**
     * Handles the incoming server messages.
     * Listens for input from the server, and sends it to the client.
     * Ignores bad input messages, but prints an error to System.err if there is an IOException
     * Run ensures that a failure is noticed by calling kill() at the end.
     */
    @Override
    public void run() {
        // handle the client
        try {
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                NetworkMessage message = NetworkMessage.deserialize(line);
                if (message instanceof ConnectionRefusedMessage) {
                    this.kill();
                }
                incomingMessages.add(message);

            }
        } catch (IOException e) {
            if (Constants.DEBUG) {
                System.err.println(e.getMessage());
            }
        } catch (DecodeException e) {
            // ignore bad input
            if (Constants.DEBUG) {
                System.err.println(e.getMessage());
            }
        } finally {
            this.kill();
        }
    }

    /**
     * Send a message to the server.
     * Requires the thread to be running!
     * @param message message to send
     */
    public void send(NetworkMessage message) {
        String strMessage = message.serialize();
        out.println(strMessage);
    }

    /**
     * Terminates the connection to the server.
     * This also causes the run() method to finish, because in.close() will make run() fail.
     */
    protected void kill() {
        if (!socket.isClosed()) {
            try {
                out.close();
                in.close();
                socket.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }
    
    /**
     * @return host name
     */
    public String getHostName() {
    	return socket.getRemoteSocketAddress().toString();
    }
}