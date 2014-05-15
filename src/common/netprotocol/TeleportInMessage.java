package common.netprotocol;

import physics.Vect;

/**
 * Server -> Client
 * Message sent when a ball should enter a board at a portal.
 * This is an immutable class.
 *
 * Thread Safety Argument:
 * - all data is immutable.
 */
public class TeleportInMessage extends NetworkMessage {
    /**
     * Rep invariant:
     * - all data is immutable (enforced by type system)
     */
    private final Vect ballVel; // velocity of the ball
    private final String boardFrom;
    private final String portalFrom;
    private final String boardTo;
    private final String portalTo;

    /**
     * Deserialize the message.
     * See NetworkMessage.deserialize for specification.
     * See this NetworkMessage's serialize for specific serialization specification.
     * @return decoded NetworkMessage
     * @param body body of the message
     */
    public static NetworkMessage deserialize(String body) throws DecodeException {
        String units[] = body.split(STD_SEP);
        if (units.length != 5) {
            throw new DecodeException("Wrong body length: " + units.length);
        }
        Vect ballVel = NetworkMessage.deserializeVect(units[0]);
        String boardFrom = units[1];
        String portalFrom = units[2];
        String boardTo = units[3];
        String portalTo = units[4];
        return new TeleportInMessage(ballVel, boardFrom, portalFrom, boardTo, portalTo);
    }


     /**
      * Create a message.
      * @param ballVel
      * @param boardFrom
      * @param portalFrom
      * @param boardTo
      * @param portalTo
      */
    public TeleportInMessage(Vect ballVel, String boardFrom, String portalFrom, String boardTo, String portalTo) {
        this.ballVel = ballVel;
        this.boardFrom = boardFrom;
        this.portalFrom = portalFrom;
        this.boardTo = boardTo;
        this.portalTo = portalTo;
    }

    /**
     * Serialize the message according to specs in NetworkMessage.serialize
     * @return string serialization of message
     */
    public String serialize() {
        String message = this.getClass().getSimpleName() + STD_SEP;
        message += serializeVect(ballVel) + STD_SEP;
        message += serializeString(boardFrom) + STD_SEP;
        message += serializeString(portalFrom) + STD_SEP;
        message += serializeString(boardTo) + STD_SEP;
        message += serializeString(portalTo);
        return message;
    }

    /**
     * @return return ballVel
     */
    public Vect getBallVel() {
        return ballVel;
    }
    
    /**
     * @return name of board from
     */
    public String getBoardFrom() {
        return boardFrom;
    }
    
    /**
     * @return name of portal from
     */
    public String getPortalFrom() {
        return portalFrom;
    }
    
    /**
     * @return name of board to
     */
    public String getBoardTo() {
        return boardTo;
    }
    
    /**
     * @return name of portal to
     */
    public String getPortalTo() {
        return portalTo;
    }
}
