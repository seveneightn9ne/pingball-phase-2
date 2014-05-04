package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import common.netprotocol.*;
import common.*;
import common.netprotocol.NetworkMessage.DecodeException;

/**
 * Runnable which handles client's incoming requests. These include ball out messages
 * and client connect/disconnect.
 *
 * Thread safety argument:
 * * The many ClientHandler threads will all add NetworkMessages to the Server's BlockingQueue (thread safe datatype)
 * * Only client thread will read from the input stream (in run() method)
 * * Only the server thread will write to the output stream (use send() method)
 * * Sockets are safe for concurrent input and output.
 * * kill() can be called by the server thread or the client thread. It is synchronized to prevent both calling kill()
 *   on the same client at once.
 * * name is volatile because multiple threads may be reading and writing its value
 *
 * Rep invariant:
 * * in and out are bound to socket
 * * if this is in deadClientsQueue, socket is closed
 *
 */

public class ClientHandler implements Runnable{

    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final BlockingQueue<AuthoredMessage> messageQueue;
    private final BlockingQueue<ClientHandler> deadClientsQueue;
    private volatile String name;


    /**
     * Make a new ClientHandler
     * @param socket the socket through which we communicate with the client
     * @param queue the Server's queue of messages, on which to put incoming messages
     * @throws IOException if we are unable to open the input or output stream with the client
     */
    public ClientHandler(Socket socket,
            BlockingQueue<AuthoredMessage> queue,
            BlockingQueue<ClientHandler> deadClientsQueue) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.messageQueue = queue;
        this.deadClientsQueue = deadClientsQueue;

        checkRep();
    }

    /**
     * Handles the incoming client messages.
     * Listens for input from the client, and sends it to the server.
     * Ignores bad input messages, but prints an error to System.err if there is an IOException
     */
    @Override
    public void run() {
        // handle the client
        try {
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                NetworkMessage message = NetworkMessage.deserialize(line);
                if (message instanceof ClientConnectMessage) {
                    this.name = ((ClientConnectMessage) message).getBoardName();
                }
                messageQueue.add(new AuthoredMessage(message, this));

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
     * Send a message to the client. Requires the thread to be running!
     * @param message the message to send to the client
     */
    public void send(NetworkMessage message) {
        String strMessage = message.serialize();
        if (Constants.DEBUG) System.out.println("Sending message: " + strMessage);
        out.println(strMessage);
    }

    /**
     * Terminates the connection to the client.
     * This also causes the run() method to finish, because in.close() will make run() fail.
     */
    public synchronized void kill() {

        if (!socket.isClosed()) {
            try {
                out.close();
                in.close();
                socket.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        deadClientsQueue.add(this);

        checkRep();

    }

    /**
     * getter for board name
     * @return the name of the client board
     *
     * If we have not received a ClientConnectMessage yet, then name is null.
     */
    public String getName() {
        return this.name;
    }

    /**
     * asserts the Rep Invariant
     *
     * Rep invariant:
     * * in and out are bound to socket (there is no way to check this)
     * * if this is in deadClientsQueue, socket is closed
     *
     */
    private void checkRep() {
        if (deadClientsQueue.contains(this) && !socket.isClosed()) {
            throw new RepInvariantException("this is in deadClientsQueue but socket is open");
        }
    }
}