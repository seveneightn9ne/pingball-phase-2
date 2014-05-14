package tests;
import static org.junit.Assert.*;


import org.junit.BeforeClass;
import org.junit.Test;

import client.*;
import client.gadgets.*;
import server.*;


/**
 * Testing strategies for Parser:
 * 
 * > test staff boards
 * > test our three new board files
 * > test optional name function
 *
 *
 *
 * Client Server Tests:
 * 
 * We ran theses tests manually and they worked, however code of these tests
 * were not executed.
 * 
 * testing strategy: - test connection to server
 * 
 * - test adding two boards to the server
 * 
 * - test passed balls between boards
 * 
 * - differences in gravity
 * 
 * - adding boards to each side of a board
 * 
 * - attaching a board to itself
 * 
 */
public class ParserAndClientServerTest {
    
    private static String file1;
    private static String file2_1;
    private static String file2_2;
    private static String file3;
    private static String file4;
    
    @BeforeClass
    public static void setUpBeforeClass(){
        file1 = "src/server/sampleBoard1.pb";
        file2_1 = "src/server/sampleBoard2-1.pb";
        file2_2 = "src/server/sampleBoard2-2.pb";
        file3 = "src/server/sampleBoard3.pb";
        file4 = "src/server/sampleBoard4.pb";
    }
    
    @Test
    public void staffFileBoards() {
        Board b1 = PingballServer.createBoard(file1);
        Board b2_1 = PingballServer.createBoard(file2_1);
        Board b2_2 = PingballServer.createBoard(file2_2);g
        Board b3 = PingballServer.createBoard(file3);
        Board b4 = PingballServer.createBoard(file4);
        
        assertEquals("......................\n.                    .\n. *                  .\n.########|   |#######.\n"
                    +".    O   |   |  O    .\n.     O        O     .\n.      O      O      .\n.       O    O       .\n"
                    +".        |   |       .\n.        |   |       .\n.        \\  /        .\n.                    .\n"
                    +".                    .\n.                    .\n.                    .\n.                    .\n"
                    +".                    .\n.                    .\n.                    .\n.                    .\n"
                    +".====================.\n......................\n", b1.toString());
        
        assertEquals("......................\n.                    .\n. *                  .\n.################|   .\n"
                +".          O     |   .\n.           O        .\n.            O       .\n.             O      .\n"
                +".              O     .\n.               O    .\n.                |   .\n.                |   .\n"
                +".                 \\  .\n.                  \\ .\n.                    .\n.                    .\n"
                +".                    .\n.                    .\n.                    .\n.                    .\n"
                +".====================.\n......................\n", b2_1.toString());
        
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
                 + ".                    .\n"
                 + ".                    .\n"
                 + ".   |################.\n"
                 + ".   |     O          .\n"
                 + ".        O           .\n"
                 + ".       O            .\n"
                 + ".      O             .\n"
                 + ".     O              .\n"
                 + ".    O               .\n"
                 + ".   |                .\n"
                 + ".   |                .\n"
                 + ".  /                 .\n"
                 + ". /                  .\n"
                 + ".                    .\n"
                 + ".                    .\n"
                 + ".                    .\n"
                 + ".                    .\n"
                 + ".                    .\n"
                 + ".                    .\n"
                 + ".====================.\n"
                 + "......................\n", b2_2.toString());
        
    }
    
}