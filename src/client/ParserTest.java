package client;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class ParserTest {
	
	Parser parser = new Parser();
	
	//the this test checks the staff board implementations
	//comments included
	//staffboard1: sample board designed for single-player play
	//staffboard2: sample board designed for two players
	//is very similar to the right half of board 1, scaled and
	//broken into two halves
	//staffboard3: defines events for gadgets
	//staffboard4: also is a board
	//@Test
	public void staffBoardTest(){
		File file1 = new File("staffboard1.txt");
		File file2 = new File("staffboard2.txt");
		File file2b = new File("staffboard2b.txt");
		File file3 = new File("staffboard3.txt");
		File file4 = new File("staffboard4.txt");
		Board board1 = parser.makeBoard(file1);
		Board board2 = parser.makeBoard(file2);
		Board board3 = parser.makeBoard(file3);
		Board board4 = parser.makeBoard(file4);
		//TODO: implement the rest of this test when Megan has getattribute methods implemented
	}
	
	//tests that exceptions are properly thrown when there is a board including an invalid character
	//invalidcharboard is identical to staffboard1 but there is an ! thrown in there
	//@Test
	public void invalidCharBoardTest(){
		File file = new File("invalidcharboard.txt");
		Board board = parser.makeBoard(file);
		//TODO: implement test
	}
	
	//this test checks that exceptions are properly thronw when there are invalid gadgets
	//badgadgetboard is identical to staffboard1 but circle3 and circle4 occupy the same location
	//@Test
	public void invalidGadgetBoardTest(){
		File file = new File("badgadgetboard.txt");
		Board board = parser.makeBoard(file);
		//TODO: implement test
	}
	
	//checks the method "cleanLine" in parser
	//doesn't exist anymore because I made cleanLine() private
	//@Test
	public void testCleanLine(){
		String line1 = "this is a partially commented line #and here is the comment";
		String line2 = "this has      many        spaces";
		String cleaned1 = "this is a partially commented line";
		String cleaned2 = "this has many spaces";
		//System.out.println(parser.cleanLine(line1) + "printing in test");
		//assertEquals(parser.cleanLine(line1), cleaned1);
		//assertEquals(parser.cleanLine(line2), cleaned2);
	}
	
	//checks the method isValidLine() in parser
	//which is now a private method
	//@Test
	public void testisValidLine(){
		String line1 = "this is a valid . . .. 312 line";
		String line2 = "!! not valid^";
		//assertFalse(parser.isValidLine(line2));
		//assertTrue(parser.isValidLine(line1));
	}
	
	@Test
	public void testMakeBoard(){
		File staffboard = new File("staffboard1.txt");
		File staffboard2 = new File("staffboard2.txt");
		List<String> clean = parser.cleanFile(staffboard);
		
		Board board = parser.makeBoard(staffboard);
		System.out.println(board.toString());
	}
	
}
