package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import common.netprotocol.NetworkMessage;

/**
 * Runnable that accepts socket connections and starts ClientHandler threads
 *
 * Thread Safety Argument:
 * * This is the only thread that will use the serverSocket.
 * * There will only ever be one SocketAcceptor.
 * * Sockets given to ClientHandlers are no longer touched by this thread.
 *
 */
public class SocketAcceptor implements Runnable {

    private ServerSocket serverSocket;
    private BlockingQueue<NetworkMessage> queue;

    /**
     * Create a new SocketAcceptor on the specified port
     * @param port the port on which to start a server socket. requires 0 <= port <= 65535
     * @throws IOException if the ServerSocket instantiation fails
     */
    public SocketAcceptor(int port, BlockingQueue<NetworkMessage> queue) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.queue = queue;
    }

    /**
     * Waits for server socket connections and creates ClientHandler threads from them
     */
    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                Thread thread = new Thread(new ClientHandler(socket, queue));
                thread.start();
            } catch (IOException e) {}
        }
    }

}
