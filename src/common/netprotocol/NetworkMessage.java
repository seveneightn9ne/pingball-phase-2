package common.netprotocol;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import physics.Vect;
import common.Constants.BoardSide;


/**
 * NetworkMessage is an abstract base class for messages passed over the network.
 * It also contains static helper methods for deserializing messages.
 *
 * The serialization specification is in the spec for the serialize method below.
 *
 * NetworkMessage.deserialize can only deserialize messages with headers
 * that it knows about. For this reason, every implementation
 * must have a correspoding entry in the known message type
 * list in the deserialize method.
 * This is an unfortunate compromise. It was deemed better
 * than the alternative of using fragile language introspection
 * to find implementations.
 *
 */
public abstract class NetworkMessage {
    // Standard separator for message units.
    protected static final String STD_SEP = "#";

    /**
     * Decode from a message received to an instance of a NetworkMessage.
     * See NetworkMessage.serialize for a description of the serialization grammar.
     *
     * Implementations of NetworkMessage should override this method
     * to decode only the BODY of the message and return an instance
     * of that NetworkMessage.
     *
     * This implementation is only responsible for determining which
     * implementation of NetworkMessage should do the rest of the deserialization.
     *
     * @param message string to decode
     * @return decoded NetworkMessage
     * @throws DecodeException indicator of failure
     */
    public static NetworkMessage deserialize(String message) throws DecodeException {
        // Extract the header
        Pattern headerPattern = Pattern.compile("^(.*?)" + STD_SEP);
        Matcher headerMatcher = headerPattern.matcher(message);
        if (! headerMatcher.find()) {
            throw new DecodeException("No valid header found.");
        }
        String header = headerMatcher.group(1);
        if (message.length() <= header.length()) {
            throw new DecodeException("Message body missing.");
        }
        // Extract the message body
        String body = message.substring(header.length() + 1);

        // Pass work on to a known NetworkMessage implementation.
        if (header.equals(BallInMessage.class.getSimpleName())) {
            return BallInMessage.deserialize(body);
        } else if (header.equals(BallOutMessage.class.getSimpleName())) {
            return BallOutMessage.deserialize(body);
        } else if (header.equals(BoardFuseMessage.class.getSimpleName())) {
            return BoardFuseMessage.deserialize(body);
        } else if (header.equals(BoardUnfuseMessage.class.getSimpleName())) {
            return BoardUnfuseMessage.deserialize(body);
        } else if (header.equals(ClientConnectMessage.class.getSimpleName())) {
            return ClientConnectMessage.deserialize(body);
        } else if (header.equals(ConnectionRefusedMessage.class.getSimpleName())) {
            return ConnectionRefusedMessage.deserialize(body);
        } else {
            throw new DecodeException("Unrecognized header: " + header);
        }
    }

    /**
     * Serialize a network message to be sent over the network.
     * All NetworkMessages can be serialized.
     *
     * All serializations result in a string which
     * begins with a header consisting of a string unique
     * to that message type followed by ": " followed
     * by any data which the message class serializes and deserializes.
     *
     * This is the grammar for a NetworkMessage:
     * serialization ::= header body
     * header ::= messagetype separator
     * messagetype ::= <message class name>
     * body ::= (field separator)* field
     * field ::= <data specific serialization>
     * separator ::= '#'
     *
     * No data encoded in a message can contain the standard separator '#'.
     * If data to be serialized includes the standard separator, a EncodeException is thrown.
     * If data to be decoded includes the standard separator, behavior is undefined,
     * but the message will probably not successfully decode.
     *
     * The field component is incompletely specified because each
     * data type (String, BoardSide, etc.) is handled differently.
     * How the fields in the body are handled is up to the implementation
     * of the NetworkMessage subclass.
     * Some recommended data serializers are described in the static
     * helper methods in this class.
     *
     * Here is a simple reference example:
     * For a hypothetical FooMessage containing a string and a vector
     * a serialization could be:
     * "FooMessage#hello world#1.0 0.0"
     *
     * @return string serialization of NetworkMessage
     */
    public abstract String serialize();


    /**
     * Exception thrown when a NetworkMessage message cannot be decoded.
     */
    public static class DecodeException extends Exception {
        private static final long serialVersionUID = 1L;

        /**
         * Constructs a DecodeException with a detail message.
         * @param message description of the error
         */
        public DecodeException(String message) {
            super(message);
        }

        /**
         * Constructs a DecodeException with a detail message and cause.
         * @param message description of the error
         * @param cause exception that caused this error
         */
        public DecodeException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Exception thrown when a NetworkMessage message cannot be encoded.
     * Note that this is an UNCHECKED exception, because it is a programming
     * error to attempt to encode a message that would throw an EncodeException.
     */
    public static class EncodeException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        /**
         * Constructs a EncodeException with a detail message.
         * @param message description of the error
         */
        public EncodeException(String message) {
            super(message);
        }

        /**
         * Constructs a EncodeException with a detail message and cause.
         * @param message description of the error
         * @param cause exception that caused this error
         */
        public EncodeException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Serialize a Vect.
     * Helper method for message serialization.
     *
     * Converts a Vect into the form:
     * "1.0 2.0"
     *
     * @param v Vect to serialize
     * @return string representation of Vect
     */
    protected static String serializeVect(Vect v) {
        return "" + v.x() + " " + v.y();
    }

    /**
     * Deserialize a Vect.
     * Helper method for message serialization.
     *
     * Converts a Vect serialized using serializeVect
     * back into the original Vect.
     *
     * @param v string representation of Vect
     * @return deserialized Vect
     */
    protected static Vect deserializeVect(String v) throws DecodeException {
        // Extract the x and y coordinates of the Vect.
        Pattern vectPattern = Pattern.compile("(-?[0-9]+\\.[0-9]+) (-?[0-9]+\\.[0-9]+)");
        Matcher vectMatcher = vectPattern.matcher(v);
        if (! vectMatcher.find()) {
            throw new DecodeException("Could not decode Vect (malformed Vect): " + v);
        }
        String stringX = vectMatcher.group(1);
        String stringY = vectMatcher.group(2);
        try {
            double x = Double.parseDouble(stringX);
            double y = Double.parseDouble(stringY);
            return new Vect(x, y);
        } catch (NumberFormatException e) {
            throw new DecodeException("Could not decode Vect (malformed double): " + v, e);
        }
    }

    /**
     * Serialize a BoardSide.
     * Helper method for message serialization.
     *
     * Converts a BoardSide into the form:
     * "L", "R", "T", or "B"
     *
     * @param bs BoardSide to serialize
     * @return string representation of BoardSide
     */
    protected static String serializeBoardSide(BoardSide bs) {
        if (bs == BoardSide.LEFT) {
            return "L";
        } else if (bs == BoardSide.RIGHT) {
            return "R";
        } else if (bs == BoardSide.TOP) {
            return "T";
        } else if (bs == BoardSide.BOTTOM) {
            return "B";
        } else {
            // this should never happen.
            throw new RuntimeException("Unknown BoardSide to serialize.");
        }
    }

    /**
     * Deserialize a BoardSide.
     * Helper method for message deserialization.
     *
     * Converts a BoardSide serialized using serializeBoardSide
     * back into the original BoardSide.
     *
     * @param bs string representation of BoardSide
     * @return deserialized BoardSide
     * @throws DecodeException indicator of failure
     */
    protected static BoardSide deserializeBoardSide(String bs) throws DecodeException {
        if (bs.equals("L")) {
            return BoardSide.LEFT;
        } else if (bs.equals("R")) {
            return BoardSide.RIGHT;
        } else if (bs.equals("T")) {
            return BoardSide.TOP;
        } else if (bs.equals("B")) {
            return BoardSide.BOTTOM;
        } else {
            throw new DecodeException("Could not deserialize BoardSide: " + bs);
        }
    }

    /**
     * Serialize a String.
     * Helper method for message serialization.
     *
     * Passes through a string verbatim.
     * Detects invalid characters ('#') and throws
     * and EncodeException if they are present.
     *
     * @param s String to serialize
     * @return encoded string
     * @throws EncodeException indication of failure
     */
    protected static String serializeString(String s) throws EncodeException {
        if (s.contains(STD_SEP)) {
            throw new EncodeException("Invalid string character for encoding: " + STD_SEP);
        } else {
            return s;
        }
    }

}
