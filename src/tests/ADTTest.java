package tests;
import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import client.*;
import client.gadgets.*;

/**
 * Testing strategies for ADTs:
 * 
 * > test empty board toString
 * > test toString of board with one stationary ball
 * > test toString of board with miscellaneous gadgets
 * > test that absorber absorbs and holds the ball in the bottom right corner
 * > test that absorber in self-trigger mode ejects an absorbed ball
 * > test that a ball hitting a bumper that triggers a flipper changes the state of the flipper correctly
 *
 */
public class ADTTest {
    
    @BeforeClass
    public static void setUpBeforeClass() {
        
    }
    
    @Test
    public void emptyBoardTest(){
        Board board = new Board("board1", 0, 0, 0);
        assertEquals("......................\n.                    .\n.                    .\n.                    .\n"
                + ".                    .\n.                    .\n.                    .\n.                    .\n"
                + ".                    .\n.                    .\n.                    .\n.                    .\n"
                + ".                    .\n.                    .\n.                    .\n.                    .\n"
                + ".                    .\n.                    .\n.                    .\n.                    .\n"
                + ".                    .\n......................\n", board.toString());
    }
    
    @Test
    public void boardPlusStationaryBallTest(){
        Board board = new Board("board1", 0, 0, 0);
        board.addBall(new Ball("ball1",1,2,0,0));
        assertEquals("......................\n.                    .\n.                    .\n. *                  .\n"
                + ".                    .\n.                    .\n.                    .\n.                    .\n"
                + ".                    .\n.                    .\n.                    .\n.                    .\n"
                + ".                    .\n.                    .\n.                    .\n.                    .\n"
                + ".                    .\n.                    .\n.                    .\n.                    .\n"
                + ".                    .\n......................\n", board.toString());
    }
    
    @Test
    public void boardVariousGadgetsTest(){
        Board board = new Board("board1", 0, 0, 0);
        Gadget g1 = new SquareBumper("c1",1,1);
        Gadget g2 = new CircleBumper("c1",5, 15);
        Gadget g3 = new TriangleBumper("c1",1,2,0);
        Gadget g4 = new TriangleBumper("c1",0,2,90);
        Gadget g5 = new TriangleBumper("c1",0,0,180);
        Gadget g6 = new TriangleBumper("c1",3,3,270);
        Gadget g7 = new RightFlipper("c1",5,5,90);
        Gadget g8 = new RightFlipper("c1",7,7,180);
        Gadget g9 = new RightFlipper("c1",8,8,0);
        Gadget g10 = new RightFlipper("c1",1,0,270);
        Gadget g11 = new LeftFlipper("c1",18,19,90);
        Gadget g12 = new LeftFlipper("c1",16,18,180);
        Gadget g13 = new LeftFlipper("c1",10,3,0);
        Gadget g14 = new LeftFlipper("c1",0,3,270);
        Gadget g15 = new Absorber("c1",0,16, 20, 2);
        
        board.addGadget(g1); board.addGadget(g2); board.addGadget(g3); board.addGadget(g4);
        board.addGadget(g5); board.addGadget(g6); board.addGadget(g7); board.addGadget(g8);
        board.addGadget(g9); board.addGadget(g10); board.addGadget(g11); board.addGadget(g12);
        board.addGadget(g13); board.addGadget(g14); board.addGadget(g15);
        
        assertEquals("......................\n./--                 .\n. #                  .\n.\\/                  .\n"
                + ".   \\      |         .\n.--        |         .\n.                    .\n.     --             .\n"
                + ".       |            .\n.       | |          .\n.         |          .\n.                    .\n"
                + ".                    .\n.                    .\n.                    .\n.                    .\n"
                + ".     O              .\n.====================.\n.====================.\n.                 |  .\n"
                + ".                 |--.\n......................\n", board.toString());
        
    }
    
    @Test
    public void testAbsorption(){
        Board board = new Board("board1", 0,0,0);
        Ball ball = new Ball("ball1",5,5,0,10);
        board.addBall(ball);
        Gadget g1 = new Absorber("abs1",0,16, 15, 2);
        board.addGadget(g1);
        board.update(2);
        
        assertEquals("......................\n.                    .\n.                    .\n.                    .\n"
                + ".                    .\n.                    .\n.                    .\n.                    .\n"
                + ".                    .\n.                    .\n.                    .\n.                    .\n"
                + ".                    .\n.                    .\n.                    .\n.                    .\n"
                + ".                    .\n.===============     .\n.===============     .\n.                    .\n"
                + ".                    .\n......................\n", board.toString());
        
        assertTrue(ball.getPosition().x() == 14.24);
        assertTrue(ball.getPosition().y() == 17.24);
    }

}
