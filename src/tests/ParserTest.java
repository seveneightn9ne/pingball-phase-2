package tests;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import client.Board;
import client.Parser;

/**
 * Parser Testing Strategy:
 *          
 *          Test that each class of object that parser must create is parsed correctly. 
 * 
 *          Ball parsing tests:         Check that ball initial locations and velocities are correct. Check that a parser can
 *                                      create a board with multiple balls.
 * 
 *          Gadget parsing tests:       Check that each gadget is initialized in the correct locations. Check that objects with variable
 *                                      orientations and sizes have correct orientations and sizes.
 * 
 *          Fire parsing tests:         Check that fire commands connect the correct gadgets.
 * 
 *          Keyup/down parsing tests:   Check that keyup and keydown commands are added to the board appropriately
 *          
 *          General tests:              Check that comments are ignored, exceptions are thrown when illegal arguments/formatting is given.
 *                                      Test staff boards.
 *          
 *
 */
public class ParserTest {
	
    private static File file1;
    private static File file2_1;
    private static File file2_2;
    private static File file3;
    private static File file4;
    
    @BeforeClass
    public static void setUpBeforeClass(){
        file1 = new File("boards/staffBoard1.pb");
        file2_1 = new File("boards/staffBoard2.pb");
        file2_2 = new File("boards/staffBoard2b.pb");
        file3 = new File("boards/staffBoard3.pb");
        file4 = new File("boards/staffBoard4.pb");
    }
    
    @Test
    public void staffFileBoards() {
        Board b1 = Parser.makeBoard(file1);
        Board b2_1 = Parser.makeBoard(file2_1);
        Board b2_2 = Parser.makeBoard(file2_2);
        Board b3 = Parser.makeBoard(file3);
        Board b4 = Parser.makeBoard(file4);
        
        assertEquals( "......................\n"
                    + ".                    .\n"
                    + ". *                  .\n"
                    + ".########|   |#######.\n"
                    + ".    O   |   |  O    .\n"
                    + ".     O        O     .\n"
                    + ".      O      O      .\n"
                    + ".       O    O       .\n"
                    + ".        |   |       .\n"
                    + ".        |   |       .\n"
                    + ".        \\  /        .\n"
                    + ".                    .\n"
                    + ".                    .\n"
                    + ".                    .\n"
                    + ".                    .\n"
                    + ".                    .\n"
                    + ".                    .\n"
                    + ".                    .\n"
                    + ".                    .\n"
                    + ".                    .\n"
                    + ".====================.\n"
                    + "......................\n", b1.toString());
        
        assertEquals("......................\n"
                + ".                    .\n"
                + ". *                  .\n"
                + ".################|   .\n"
                + ".          O     |   .\n"
                + ".           O        .\n"
                + ".            O       .\n"
                + ".             O      .\n"
                + ".              O     .\n"
                + ".               O    .\n"
                + ".                |   .\n"
                + ".                |   .\n"
                + ".                 \\  .\n"
                + ".                  \\ .\n"
                + ".                    .\n"
                + ".                    .\n"
                + ".                    .\n"
                + ".                    .\n"
                + ".                    .\n"
                + ".                    .\n"
                + ".====================.\n"
                + "......................\n", b2_1.toString());
        
        assertEquals("......................\n"
               + ".                    .\n"
               + ".                    .\n"
               + ".                    .\n"
               + ".    O              \\.\n"
               + ".                    .\n"
               + ".  *                 .\n"
               + ".                    .\n"
               + ".          |  |      .\n"
               + ".          |  |      .\n"
               + ".                    .\n"
               + ".########            .\n"
               + ".                    .\n"
               + ".                    .\n"
               + ".          *         .\n"
               + ".                    .\n"
               + ".                    .\n"
               + ".                    .\n"
               + ".          ==========.\n"
               + ".          ==========.\n"
               + ".                    .\n"
               + "......................\n", b3.toString());
        
        assertEquals("......................\n"
               + ".                    .\n"
               + ".                    .\n"
               + ".                    .\n"
               + ".    O              \\.\n"
               + ".                    .\n"
               + ".  *                 .\n"
               + ".                    .\n"
               + ".          |  |      .\n"
               + ".          |  |      .\n"
               + ".                    .\n"
               + ".####                .\n"
               + ".                    .\n"
               + ".                    .\n"
               + ".                    .\n"
               + ".                    .\n"
               + ".                    .\n"
               + ".                    .\n"
               + ".          ==========.\n"
               + ".          ==========.\n"
               + ".                    .\n"
               + "......................\n", b4.toString());
        
        assertEquals("......................\n"
                    +".                    .\n"
                    +".                    .\n"
                    +".    |###############.\n"
                    +".    |    O          .\n"
                    +".        O           .\n"
                    +".       O            .\n"
                    +".      O             .\n"
                    +".     O              .\n"
                    +".    O               .\n"
                    +".    |               .\n"
                    +".    |               .\n"
                    +".  /                 .\n"
                    +". /                  .\n"
                    +".                    .\n"
                    +".                    .\n"
                    +".                    .\n"
                    +".                    .\n"
                    +".                    .\n"
                    +".                    .\n"
                    +".====================.\n"
                    +"......................\n", b2_2.toString());
        
    }
    
    @Test 
    public void absorberParserTest() {
        File file = new File("boards/absorberTest.pb");
        Board board = Parser.makeBoard(file);
        
        assertEquals("......................\n"
                +    ".==                  .\n"
                +    ".==                  .\n"
                +    ".==                  .\n"
                +    ".==                  .\n"
                +    ".==                  .\n"
                +    ".==   =====     =    .\n"
                +    ".==   =====          .\n"
                +    ".==   =====          .\n"
                +    ".==   =====          .\n"
                +    ".==   =====          .\n"
                +    ".==                  .\n"
                +    ".==                  .\n"
                +    ".==                  .\n"
                +    ".==                  .\n"
                +    ".==                  .\n"
                +    ".                    .\n"
                +    ".                    .\n"
                +    ".                    .\n"
                +    ".====================.\n"
                +    ".====================.\n"
                +    "......................\n", board.toString());
        
    }
    
    @Test 
    public void flipperParserTest() {
        File file = new File("boards/flipland.pb");
        Board board = Parser.makeBoard(file);
        System.out.println(board);
        assertEquals("......................\n"
                +    ".                    .\n"
                +    ".                    .\n"
                +    ".      *             .\n"
                +    ".                    .\n"
                +    ".               *    .\n"
                +    ".  *                 .\n"
                +    ".                    .\n"
                +    ".          *         .\n"
                +    ".                    .\n"
                +    ".                    .\n"
                +    ".                   \\.\n"
                +    ".                    .\n"
                +    ".                    .\n"
                +    ".                    .\n"
                +    ".                    .\n"
                +    ".                    .\n"
                +    ".  -- -- --  -- --   .\n"
                +    ".                    .\n"
                +    ".====================.\n"
                +    ".====================.\n"
                +    "......................\n", board.toString());
        
    }
    
    @Test 
    public void triangleParserTest() {
        File file = new File("boards/triangleBumperHYPE.pb");
        Board board = Parser.makeBoard(file);
        
        assertEquals("......................\n"
                +    ".                   \\.\n"
                +    ".                    .\n"
                +    ".               \\    .\n"
                +    ".                   \\.\n"
                +    ". \\    \\           / .\n"
                +    ".  \\    \\         /  .\n"
                +    ".                    .\n"
                +    ".          *         .\n"
                +    ".                    .\n"
                +    ".        \\  /        .\n"
                +    ".                    .\n"
                +    ".  /              \\  .\n"
                +    ". /                \\ .\n"
                +    ".                    .\n"
                +    ".                    .\n"
                +    ".                    .\n"
                +    ".                    .\n"
                +    ".                    .\n"
                +    ".                    .\n"
                +    ".====================.\n"
                +    "......................\n", board.toString());
        
    }
    
    @Test 
    public void circleParserTest() {
        File file = new File("boards/mesh.pb");
        Board board = Parser.makeBoard(file);     
        
        assertEquals("......................\n"
                +    ".                    .\n"
                +    ".                    .\n"
                +    ".      *        \\    .\n"
                +    ".                    .\n"
                +    ".               *    .\n"
                +    ".  *                 .\n"
                +    ".                    .\n"
                +    ".          *         .\n"
                +    ". O    O     O      O.\n"
                +    ".          O         .\n"
                +    ".  O     O           .\n"
                +    ".                    .\n"
                +    ".     O              .\n"
                +    ".   O              O .\n"
                +    ".                   O.\n"
                +    ".       O            .\n"
                +    ".                    .\n"
                +    ".    ============    .\n"
                +    ".    ============    .\n"
                +    ".                    .\n"
                +    "......................\n", board.toString());
        
    }
    
    @Test 
    public void squareParserTest() {
        File file = new File("boards/squareTest.pb");
        Board board = Parser.makeBoard(file);
        
        System.out.println(board);
        
        assertEquals("......................\n"
                +    ".                   #.\n"
                +    ".                    .\n"
                +    ".               #    .\n"
                +    ".                   #.\n"
                +    ". #    #           # .\n"
                +    ".  #    #         #  .\n"
                +    ".                    .\n"
                +    ".                    .\n"
                +    ".                    .\n"
                +    ".        #  #        .\n"
                +    ".                    .\n"
                +    ".  #              #  .\n"
                +    ". #                # .\n"
                +    ".                    .\n"
                +    ".                    .\n"
                +    ".                    .\n"
                +    ".                    .\n"
                +    ".                    .\n"
                +    ".                    .\n"
                +    ".                    .\n"
                +    "......................\n", board.toString());
        
    }
    
    @Test 
    public void portalParserTest() {
        File file = new File("boards/funwithportals.pb");
        Board board = Parser.makeBoard(file); 
        
        assertEquals("......................\n"
                +    ".                    .\n"
                +    ". *                  .\n"
                +    ".                    .\n"
                +    ".                    .\n"
                +    ".    @               .\n"
                +    ".                   @.\n"
                +    ".      @             .\n"
                +    ".                    .\n"
                +    ".    @               .\n"
                +    ".                    .\n"
                +    ".                    .\n"
                +    ".                    .\n"
                +    ".     @              .\n"
                +    ".          @         .\n"
                +    ".                    .\n"
                +    ".                    .\n"
                +    ".                    .\n"
                +    ".====================.\n"
                +    ".                    .\n"
                +    ".                    .\n"
                +    "......................\n", board.toString());
        
    }
	
	@Test (expected=RuntimeException.class)
	public void invalidCharParserTest() {
		File file = new File("boards/invalidcharboard.pb");
		Parser.makeBoard(file);
	}
	
	
}
