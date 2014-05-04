package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import common.Constants;

/**
 * Runnable that accepts socket connections and starts ClientHandler threads
 *
 * Thread Safety Argument:
 * * serverSocket is confined to the SocketAcceptor thread.
 * * There will only ever be one SocketAcceptor thread.
 * * Sockets given to ClientHandlers are no longer touched by the SocketAcceptor thread.
 *
 * Rep Invariant:
 * * none.
 *
 */
public class SocketAcceptor implements Runnable {

    private final ServerSocket serverSocket;
    private final BlockingQueue<AuthoredMessage> messageQueue;
    private final BlockingQueue<ClientHandler> deadClientsQueue;

    /**
     * Create a new SocketAcceptor on the specified port
     *
     * @param port the port on which to start a server socket. requires 0 <= port <= 65535
     * @param queue the queue that clients should pass AuthoredMessages to
     * @param deadClientsQueue the queue where clients should go when they die
     * @throws IOException if the ServerSocket instantiation fails
     */
    public SocketAcceptor(int port, BlockingQueue<AuthoredMessage> queue, BlockingQueue<ClientHandler> deadClientsQueue) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.messageQueue = queue;
        this.deadClientsQueue = deadClientsQueue;

        checkRep();
    }

    /**
     * Waits for server socket connections and creates ClientHandler threads from them
     */
    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                Thread thread = new Thread(new ClientHandler(socket, messageQueue, deadClientsQueue));
                thread.start();
            } catch (IOException e) {
                if (Constants.DEBUG) System.err.println(e.getMessage());
            }
        }
    }

    /**
     * Rep invariant: none
     *
     * this is a do-nothing method
     */
    private void checkRep() {
        return;
    }
}