package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import physics.Vect;
import common.Constants.BoardSide;
import common.netprotocol.BallInMessage;
import common.netprotocol.BallOutMessage;
import common.netprotocol.BoardFuseMessage;
import common.netprotocol.BoardUnfuseMessage;
import common.netprotocol.ClientConnectMessage;
import common.netprotocol.ConnectionRefusedMessage;
import common.netprotocol.NetworkMessage;
import common.netprotocol.NetworkMessage.DecodeException;

/**
 * Tests for NetworkMessages.
 * These tests cover serialization and deserialization of NetworkMessages
 * as well as basic functionality like extracting data from them.
 *
 * These tests are glass-box in that they know that NetworkMessages share
 * common code for serializing and deserializing individual data like
 * Vects and BoardSide's. This means that edge cases for these are not
 * tested for each NetworkMessage type but for NetworkMessages as a whole.
 * This cuts down on the number of required tests significantly.
 *
 * Testing strategy for each NetworkMessage type:
 * - test constructor and serialization into known string
 * - test data after deserialization from know string
 * - test for exception when deserializing bad data (with correct header)
 *
 * Testing strategy for Vects in NetworkMessages:
 * - Test Vects with positive components
 * - Test Vects with zero components
 * - Test Vects with negative components
 *
 * Testing strategy for BoardSides in NetworkMessages:
 * - Test serialization for each of left, right, top, and bottom sides
 * - Test deserialization for each of left, right, top, and bottom sides
 *
 * Testing strategy for Strings in NetworkMessages:
 * - Test normal string
 * - Test string with STD_SEP ('#')
 *
 * Testing strategy for malformed serializations:
 * (these tests cause various DecodeExceptions)
 * - Empty message
 * - No header
 * - Bad header
 * - Missing some body data
 * - Wrong body data
 * - Missing all body data
 * - Extra body data
 *
 */
public class NetworkMessageTests {
    private static final Vect vZero = new Vect(0, 0);
    private static final Vect vPos = new Vect(1, 1d / 3d);
    private static final Vect vNeg = new Vect(-300, -8000);

    private static final String ballInMessageString               = "BallInMessage#0.0 0.0#0.0 0.0#L";
    private static final String ballInMessageStringBad            = "BallInMessage#0.0 0.0#L"; // missing data
    private static final String ballOutMessageString              = "BallOutMessage#1.0 0.3333333333333333#-300.0 -8000.0#R";
    private static final String ballOutMessageStringBad           = "BallOutMessage#1.0 0.3#-300.0 -8000.0#R"; // wrong data
    private static final String boardFuseMessageString            = "BoardFuseMessage#fooBoard#T";
    private static final String boardFuseMessageStringBad         = "BoardFuseMessage#"; // missing body
    private static final String boardUnfuseMessageString          = "BoardUnfuseMessage#B";
    private static final String boardUnfuseMessageStringBad       = "BoardUnfuseMessage#B#L"; // extra data
    private static final String clientConnectMessageString        = "ClientConnectMessage#fooBoard";
    private static final String clientConnectMessageStringBad     = "ClientConnectMessage#L#0.0 0.0"; // extra data
    private static final String connectionRefusedMessageString    = "ConnectionRefusedMessage#I'm afraid I can't do that, Dave.";
    private static final String connectionRefusedMessageStringBad = "ConnectionRefusedMessage#"; // missing body


    @Test(expected=NetworkMessage.DecodeException.class)
    public void testDeserializeEmptyMessage() throws DecodeException {
        NetworkMessage.deserialize("");
    }

    @Test(expected=NetworkMessage.DecodeException.class)
    public void testDeserializeNoHeader() throws DecodeException {
        NetworkMessage.deserialize("ballPos 0.0#ballVel 0.0#toSide L");
    }

    @Test(expected=NetworkMessage.DecodeException.class)
    public void testDeserializeBadHeader() throws DecodeException {
        NetworkMessage.deserialize("thisisnotavalidheader#ballPos 0.0#ballVel 0.0#toSide L");
    }


    // BallInMessage

    @Test public void testBallInMessageSerialize() {
        BallInMessage msg = new BallInMessage(vZero, vZero, BoardSide.LEFT);
        assertEquals(ballInMessageString, msg.serialize());
    }

    @Test public void testBallInMessageDeserialize() throws DecodeException {
        BallInMessage msg = (BallInMessage) NetworkMessage.deserialize(ballInMessageString);
        assertEquals(msg.getBallPos(), vZero);
        assertEquals(msg.getBallVel(), vZero);
        assertEquals(msg.getToSide(), BoardSide.LEFT);
    }

    @Test(expected=NetworkMessage.DecodeException.class)
    public void testBallInMessageDeserializeBad() throws DecodeException {
        NetworkMessage.deserialize(ballInMessageStringBad);
    }


    // BallOutMessage

    @Test public void testBallOutMessageSerialize() {
        BallOutMessage msg = new BallOutMessage(vPos, vNeg, BoardSide.RIGHT);
        assertEquals(ballOutMessageString, msg.serialize());
    }

    @Test public void testBallOutMessageDeserialize() throws DecodeException {
        BallOutMessage msg = (BallOutMessage) NetworkMessage.deserialize(ballOutMessageString);
        assertEquals(msg.getBallPos(), vPos);
        assertEquals(msg.getBallVel(), vNeg);
        assertEquals(msg.getFromSide(), BoardSide.RIGHT);
    }

    @Test public void testBallOutMessageDeserializeBad() throws DecodeException {
        BallOutMessage msg = (BallOutMessage) NetworkMessage.deserialize(ballOutMessageStringBad);
        // key difference here.
        assertFalse(msg.getBallPos().equals(vPos));
        assertEquals(msg.getBallVel(), vNeg);
        assertEquals(msg.getFromSide(), BoardSide.RIGHT);
    }


    // BoardFuseMessage

    @Test public void testBoardFuseMessageSerialize() {
        BoardFuseMessage msg = new BoardFuseMessage("fooBoard", BoardSide.TOP);
        assertEquals(boardFuseMessageString, msg.serialize());
    }

    @Test public void testBoardFuseMessageDeserialize() throws DecodeException {
        BoardFuseMessage msg = (BoardFuseMessage) NetworkMessage.deserialize(boardFuseMessageString);
        assertEquals(msg.getBoardName(), "fooBoard");
        assertEquals(msg.getSide(), BoardSide.TOP);
    }

    @Test(expected=NetworkMessage.DecodeException.class)
    public void testBoardFuseMessageDeserializeBad() throws DecodeException {
        NetworkMessage.deserialize(boardFuseMessageStringBad);
    }


    // BoardUnfuseMessage

    @Test public void testBoardUnfuseMessageSerialize() {
        BoardUnfuseMessage msg = new BoardUnfuseMessage(BoardSide.BOTTOM);
        assertEquals(boardUnfuseMessageString, msg.serialize());
    }

    @Test public void testBoardUnfuseMessageDeserialize() throws DecodeException {
        BoardUnfuseMessage msg = (BoardUnfuseMessage) NetworkMessage.deserialize(boardUnfuseMessageString);
        assertEquals(msg.getSide(), BoardSide.BOTTOM);
    }

    @Test(expected=NetworkMessage.DecodeException.class)
    public void testBoardUnfuseMessageDeserializeBad() throws DecodeException {
        NetworkMessage.deserialize(boardUnfuseMessageStringBad);
    }


    // ClientConnectMessage

    @Test public void testClientConnectMessageSerialize() {
        ClientConnectMessage msg = new ClientConnectMessage("fooBoard");
        assertEquals(clientConnectMessageString, msg.serialize());
    }

    @Test public void testClientConnectMessageDeserialize() throws DecodeException {
        ClientConnectMessage msg = (ClientConnectMessage) NetworkMessage.deserialize(clientConnectMessageString);
        assertEquals(msg.getBoardName(), "fooBoard");
    }

    @Test(expected=NetworkMessage.DecodeException.class)
    public void testClientConnectMessageDeserializeBad() throws DecodeException {
        NetworkMessage.deserialize(clientConnectMessageStringBad);
    }


    // ConnectionRefusedMessage

    @Test public void testConnectionRefusedMessageSerialize() {
        ConnectionRefusedMessage msg = new ConnectionRefusedMessage("I'm afraid I can't do that, Dave.");
        assertEquals(connectionRefusedMessageString, msg.serialize());
    }

    @Test public void testConnectionRefusedMessageDeserialize() throws DecodeException {
        ConnectionRefusedMessage msg = (ConnectionRefusedMessage) NetworkMessage.deserialize(connectionRefusedMessageString);
        assertEquals(msg.getReason(), "I'm afraid I can't do that, Dave.");
    }

    @Test(expected=NetworkMessage.DecodeException.class)
    public void testConnectionRefusedMessageDeserializeBad() throws DecodeException {
        NetworkMessage.deserialize(connectionRefusedMessageStringBad);
    }

    @Test(expected=NetworkMessage.EncodeException.class)
    public void testConnectionRefusedMessageSerializeIllegalChars() throws DecodeException {
        new ConnectionRefusedMessage("I'm afraid I can't do that ####, Dave.").serialize();
    }
}
