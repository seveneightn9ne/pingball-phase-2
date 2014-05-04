package server;

import common.netprotocol.NetworkMessage;

/**
 * A NetworkMessage, and the ClientHandler who received that message.
 *
 * This class will be used to attribute an author to a NetworkMessage,
 * so that the server is able to respond to the client who sent the message.
 *
 * ADT: {NetworkMessage m, ClientHandler c}
 *
 * Thread Safety:
 * * All fields are final; NetworkMessage is immutable
 * * See thread safety argument for ClientHandler for its thread safety details
 *
 * Rep Invariant:
 * * ch is the ClientHandler that produced the message (though there is no way to check this)
 *
 */
public class AuthoredMessage {

    private final NetworkMessage message;
    private final ClientHandler ch;

    /**
     * create an AuthoredMessage
     * @param message the NetworkMessage
     * @param ch the ClientHandler that produced the message
     */
    public AuthoredMessage(NetworkMessage message, ClientHandler ch) {
        this.message = message;
        this.ch = ch;

        checkRep();
    }

    /**
     * getter for message
     * @return the message
     */
    public NetworkMessage getMessage() {
        return message;
    }

    /**
     * getter for client
     * @return the clientHandler who received message
     */
    public ClientHandler getClientHandler() {
        return ch;
    }

    /**
     * Rep invariant:
     * * ch is the ClientHandler that produced the message (though there is no way to check this)
     *
     * this is a do-nothing method
     */
    private void checkRep() {
        return;
    }
}