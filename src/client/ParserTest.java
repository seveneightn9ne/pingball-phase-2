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
		File file1 = new File("boards/staffboard1.pb");
		File file2 = new File("boards/staffboard2.pb");
		File file2b = new File("boards/staffboard2b.pb");
		File file3 = new File("boards/staffboard3.pb");
		File file4 = new File("boards/staffboard4.pb");
		Board board1 = Parser.makeBoard(file1);
		Board board2 = Parser.makeBoard(file2);
		Board board3 = Parser.makeBoard(file3);
		Board board4 = Parser.makeBoard(file4);
		//TODO: implement the rest of this test when Megan has getattribute methods implemented
	}
	
	//tests that exceptions are properly thrown when there is a board including an invalid character
	//invalidcharboard is identical to staffboard1 but there is an ! thrown in there
	//@Test
	public void invalidCharBoardTest(){
		File file = new File("boards/invalidcharboard.pb");
		Board board = Parser.makeBoard(file);
		//TODO: implement test
	}
	
	//this test checks that exceptions are properly thronw when there are invalid gadgets
	//badgadgetboard is identical to staffboard1 but circle3 and circle4 occupy the same location
	//@Test
	public void invalidGadgetBoardTest(){
		File file = new File("boards/badgadgetboard.pb");
		Board board = Parser.makeBoard(file);
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
//		assertFalse(parser.isValidLine(line2));
//		assertTrue(parser.isValidLine(line1));
	}
	
	@Test
	public void testMakeBoard(){
		File staffboard = new File("boards/staffboard1.pb");
		File staffboard2 = new File("boards/staffboard2.pb");
		List<String> clean = Parser.cleanFile(staffboard);
		
		Board board = Parser.makeBoard(staffboard);
		System.out.println(board.toString());
		Board board2 = Parser.makeBoard(staffboard2);
        System.out.println(board2.toString());
	}
	
}
