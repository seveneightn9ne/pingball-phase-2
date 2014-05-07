package client;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Enumeration;

import client.gadgets.Absorber;
import client.gadgets.CircleBumper;
import client.gadgets.Gadget;
import client.gadgets.LeftFlipper;
import client.gadgets.RightFlipper;
import client.gadgets.SquareBumper;
import client.gadgets.TriangleBumper;
import physics.*;

/**
 *	Parses the input from the user as presented by the PingballServer class
 *	as presented by the server
 *
 *	Creates a board object that can be used by the PingballServer which can be given to the client
 *	Board object has 
 */
public class Parser {
	
	private static String[] validGadgets = {"squareBumper", "circleBumper", "triangleBumper", "leftFlipper", "rightFlipper", "absorber"};
	 
//	public Parser(){}
	
	
	/** Takes a file, makes a board out of it
	 * NOTE: the parser is case sensitive
	 * NOTE: the parser cannot currently handle tabs (only spaces count as whitespace)
	 * Calls cleanFile, isValidFile, and makeGadgets
	 * TODO: make it able to handle tabs
	 * @input file representing the board
	 * @output board instance
	 * @throws RuntimeException
	 */
	public static Board makeBoard(File file){
		
		List<String> cleanedFile = cleanFile(file);
		if (!isValidFile(cleanedFile)){
			throw new RuntimeException("This is not a valid file");
		}
		
		String name="";
		double gravity =25;
		double friction1=.025;
		double friction2=.025;
		ArrayList<Ball> ballList = new ArrayList<Ball>();
		ArrayList<Gadget> gadgetList = new ArrayList<Gadget>();
		Hashtable<String, String[]> triggerHash= new Hashtable<String, String[]>();
		Hashtable<Gadget, String> gadgToName = new Hashtable<Gadget, String>();
		Hashtable<String, Gadget> nameToGadg = new Hashtable<String, Gadget>();
		
		
		//this segment parses the first line of the cleaned file
		//it gives us the board name, the gravity, friction1, and friction2
		String[] line1 = cleanedFile.get(0).split(" ");
		for(String word: line1){
			word = word.toLowerCase();
			if (word.startsWith("name")){
				name = word.split("=")[1];
			}else if (word.startsWith("gravity")){
				gravity = Double.parseDouble(word.split("=")[1]);
			}else if (word.startsWith("friction1")){
				friction1 = Double.parseDouble(word.split("=")[1]);
			}else if (word.startsWith("friction2")){
				friction2 = Double.parseDouble(word.split("=")[1]);
			}
		}
		
		//this segment parses the rest of the cleaned file line by line
		//it gives us the information we need about each ball and gadget
		//and also check to see if each line is a trigger
		for (int i = 1; i < cleanedFile.size(); i++){
			String[] line = cleanedFile.get(i).split(" ");
			if (line[0].equals("ball")){
				Ball ball = makeBall(line);
				ballList.add(ball);
			}else if (Arrays.asList(validGadgets).contains(line[0])){
				Gadget gadget = makeGadget(line);
				String gadgetName = line[1].split("=")[1];
				gadgToName.put(gadget, gadgetName);
				nameToGadg.put(gadgetName, gadget);
				if (isValidGadget(gadget, gadgetList)){
					gadgetList.add(gadget);
				}else{
					throw new RuntimeException("gadget overlap error");
				}
			}else if (line[0].equals("fire")){
				if (line.length == 3 && line[1].startsWith("trigger")&& line[2].startsWith("action")){
					String trigger = line[1].split("=")[1];
					String action = line[2].split("=")[1];
					if (triggerHash.containsKey(action)){
						String[] val = triggerHash.get(action);
						String[] newVal = new String[val.length + 1];
						for (int j = 0; j<val.length; j++){
							newVal[j] = val[j];
						}
						newVal[val.length] = trigger;
						triggerHash.put(action, newVal);
					}else{
						String[] val = {trigger};
						triggerHash.put(action, val);
					}
				}else{
					throw new RuntimeException("bad trigger");
				}
			}else{
				throw new RuntimeException("this is not an acceptable gadget");
			}
		}
		
		//updates the actions on the gadgets
		Enumeration<String> enumKey = triggerHash.keys();
		while(enumKey.hasMoreElements()) {
		    String act = enumKey.nextElement();
		    for (String trig: triggerHash.get(act)){
		    	Gadget trigGadget = nameToGadg.get(trig);
		    	Gadget actGadget = nameToGadg.get(act);
		    	actGadget.addTrigger(trigGadget);
		    }
		}
		
		
		Board board = new Board(name, gravity, friction1, friction2);
		
		for (int i = 0; i<gadgetList.size(); i++){
		    board.addGadget(gadgetList.get(i));
        }
		
		for (int i = 0; i<ballList.size(); i++) {
		    board.addBall(ballList.get(i));
		}

		return board;
	}
	
	/**
	 * checks whether a gadget overlaps with any previously existing gadgets
	 * @param gadget
	 * @param gadgetList of gadgets that we're checking
	 * @return true if the gadget is valid
	 * false otherwise
	 */
	public static boolean isValidGadget(Gadget gadget, List<Gadget> gadgetList){
		boolean[][] occupied = new boolean[20][20];
		
		for (int i = 0; i<20; i++){
			for (int j = 0; j<20; j++){
				occupied[i][j] = false;
			}
		}
		
		for (Gadget g: gadgetList){
			String type = g.getClass().toString();
			//flippers need a 2x2 bounding box
			if (type.equals("LeftFlipper")||type.equals("RightFlipper")){
				Vect origin = g.getOrigin();
				int x = (int) origin.x();
				int y = (int) origin.y();
				if (x > 18||x<0||y>18||y<0){
					throw new RuntimeException("invalid dimensions for flipper");
				}
				occupied[x][y] = true;
				occupied[x+1][y] = true;
				occupied[x][y+1] = true;
				occupied[x+1][y+1] = true;
			}
			//absorbers are width x height
			//where width and height are properties of the absorber
			else if (type.equals("Absorber")){
				Vect origin = g.getOrigin();
				int x = (int) origin.x();
				int y = (int) origin.y();
				int[] size = g.getSize();
				int width = size[0];
				int height = size[1];
				if (x > 20-width || x<0|| y>20-height || y<0){
					throw new RuntimeException("invalid dimensions for absorber");
				}
				for (int i = x; i<x+width; i++){
					for (int j = y; j<y+height; j++){
						occupied[i][j] = true;
					}
				}
			}
			//all other gadget types only occupy one square
			else{
				Vect origin = g.getOrigin();
				int x = (int) origin.x();
				int y = (int) origin.y();
				occupied[x][y] = true;
			}
		}
		
		//now we'll check whether any of the spaces our gadget needs are occupied
		return true;
	}
	
	/** Takes a string, makes a board out of it
	 * NOTE: the parser is case sensitive
	 * NOTE: the parser cannot currently handle tabs (only spaces count as whitespace)
	 * Calls cleanFile, isValidFile, and makeGadgets
	 * TODO: Make it call isValidGadget on each gadget
	 * TODO: make it able to handle tabs
	 * @input file representing the board
	 * @output board instance
	 * @throws RuntimeException
	 */
	public static Board makeBoardFromString(String fileString){
		
		List<String> cleanedFile = Arrays.asList(fileString.split("\n"));
		if (!isValidFile(cleanedFile)){
			throw new RuntimeException("This is not a valid file");
		}
		
		String name="";
		double gravity =0;
		double friction1=0;
		double friction2=0;
		ArrayList<Ball> ballList = new ArrayList<Ball>();
		ArrayList<Gadget> gadgetList = new ArrayList<Gadget>();
		Hashtable<String, String[]> triggerHash= new Hashtable<String, String[]>();
		Hashtable<Gadget, String> gadgToName = new Hashtable<Gadget, String>();
		Hashtable<String, Gadget> nameToGadg = new Hashtable<String, Gadget>();
		
		
		//this segement parses the first line of the cleaned file
		//it gives us the board name, the gravity, friction1, and friction2
		String[] line1 = cleanedFile.get(0).split(" ");
		for(String word: line1){
			word = word.toLowerCase();
			if (word.startsWith("name")){
				name = word.split("=")[1];
			}else if (word.startsWith("gravity")){
				gravity = Double.parseDouble(word.split("=")[1]);
			}else if (word.startsWith("friction1")){
				friction1 = Double.parseDouble(word.split("=")[1]);
			}else if (word.startsWith("friction2")){
				friction2 = Double.parseDouble(word.split("=")[1]);
			}
		}
		
		//this segment parses the rest of the cleaned file line by line
		//it gives us the information we need about each ball and gadget
		//and also check to see if each line is a trigger
		for (int i = 1; i < cleanedFile.size(); i++){
			String[] line = cleanedFile.get(i).split(" ");
			if (line[0].equals("ball")){
				Ball ball = makeBall(line);
				ballList.add(ball);
			}else if (Arrays.asList(validGadgets).contains(line[0])){
				Gadget gadget = makeGadget(line);
				String gadgetName = line[1].split("=")[1];
				gadgToName.put(gadget, gadgetName);
				nameToGadg.put(gadgetName, gadget);
				gadgetList.add(gadget);
			}else if (line[0].equals("fire")){
				if (line.length == 3 && line[1].startsWith("trigger")&& line[2].startsWith("action")){
					String trigger = line[1].split("=")[1];
					String action = line[2].split("=")[1];
					if (triggerHash.containsKey(action)){
						String[] val = triggerHash.get(action);
						String[] newVal = new String[val.length + 1];
						for (int j = 0; j<val.length; j++){
							newVal[j] = val[j];
						}
						newVal[val.length] = trigger;
						triggerHash.put(action, newVal);
					}else{
						String[] val = {trigger};
						triggerHash.put(action, val);
					}
				}else{
					throw new RuntimeException("bad trigger");
				}
			}else{
				throw new RuntimeException("this is not an acceptable gadget");
			}
		}
		
		//updates the actions on the gadgets
		Enumeration<String> enumKey = triggerHash.keys();
		while(enumKey.hasMoreElements()) {
		    String act = enumKey.nextElement();
		    for (String trig: triggerHash.get(act)){
		    	Gadget trigGadget = nameToGadg.get(trig);
		    	Gadget actGadget = nameToGadg.get(act);
		    	actGadget.addTrigger(trigGadget);
		    }
		}
		Board board = new Board(name, gravity, friction1, friction2);
        
        for (int i = 0; i<gadgetList.size(); i++){
            board.addGadget(gadgetList.get(i));
        }
        
        for (int i = 0; i<ballList.size(); i++) {
            board.addBall(ballList.get(i));
        }

        return board;
	}
	
	
	/**
	 * Makes a gadget from a given line in the file
	 * @param line
	 * @return Gadget
	 * @throws runtimeException if it's a bad line
	 * or if it's a bad gadget
	 */
	public static Gadget makeGadget(String[] line){
		int x;
		int y;
		if (line[0].equals("squareBumper")){
			if (line[2].startsWith("x")&&line[3].startsWith("y")&&line.length == 4){
			    String name = line[1];
				String[] xsplit = line[2].split("=");
				String[] ysplit = line[3].split("=");
				x = Integer.parseInt(xsplit[1]);
				y = Integer.parseInt(ysplit[1]);
				return new SquareBumper(name, x, y);
			}else{
				throw new RuntimeException("invalid squareBumper");
			}
		}else if (line[0].equals("circleBumper")){
			if (line[2].startsWith("x")&&line[3].startsWith("y")&&line.length==4){
			    String name = line [1];
				String[] xsplit = line[2].split("=");
				String[] ysplit = line[3].split("=");
				x = Integer.parseInt(xsplit[1]);
				y = Integer.parseInt(ysplit[1]);
				return new CircleBumper(name, x, y);
			}else{
				throw new RuntimeException("invalid CircleBumper");
			}
		}else if (line[0].equals("triangleBumper")){
			if (line[2].startsWith("x")&&line[3].startsWith("y")&&line[4].startsWith("orientation")&&line.length==5){
			    String name = line[1];
				String[] xsplit = line[2].split("=");
				String[] ysplit = line[3].split("=");
				x = Integer.parseInt(xsplit[1]);
				y = Integer.parseInt(ysplit[1]); 
				String[] orientationsplit = line[4].split("=");
				int orientation = Integer.parseInt(orientationsplit[1]);
				return new TriangleBumper(name, x, y, orientation);
			}else{
				throw new RuntimeException("invalid triangularBumper");
			}
		}else if (line[0].equals("leftFlipper")){
			if (line[2].startsWith("x")&&line[3].startsWith("y")&&line[4].startsWith("orientation")&&line.length==5){
			    String name = line[1];
				String[] xsplit = line[2].split("=");
				String[] ysplit = line[3].split("=");
				x = Integer.parseInt(xsplit[1]);
				y = Integer.parseInt(ysplit[1]); 
				String[] orientationsplit = line[4].split("=");
				int orientation = Integer.parseInt(orientationsplit[1]);
				return new LeftFlipper(name, x, y, orientation);
			}else{
				throw new RuntimeException("invalid leftFlipper");
			}
		}else if (line[0].equals("rightFlipper")){
			if (line[2].startsWith("x")&&line[3].startsWith("y")&&line[4].startsWith("orientation")&&line.length==5){
			    String name = line[1];
				String[] xsplit = line[2].split("=");
				String[] ysplit = line[3].split("=");
				x = Integer.parseInt(xsplit[1]);
				y = Integer.parseInt(ysplit[1]); 
				String[] orientationsplit = line[4].split("=");
				int orientation = Integer.parseInt(orientationsplit[1]);
				return new RightFlipper(name, x, y, orientation);
			}else{
				throw new RuntimeException("invalid rightFlipper");
			}
		}else if (line[0].equals("absorber")){
			if (line[2].startsWith("x")&&line[3].startsWith("y")&&line[4].startsWith("width")&&line[5].startsWith("height")&&line.length==6){
			    String name = line[1];
				String[] xsplit = line[2].split("=");
				String[] ysplit = line[3].split("=");
				x = Integer.parseInt(xsplit[1]);
				y = Integer.parseInt(ysplit[1]); 
				String[] widthsplit = line[4].split("=");
				String[] heightsplit = line[5].split("=");
				int width = Integer.parseInt(widthsplit[1]);
				int height = Integer.parseInt(heightsplit[1]);
				return new Absorber(name, x, y, width, height);
			}else{
				throw new RuntimeException("invalid absorber");
			}
		}else{
			throw new RuntimeException("this is not a valid gadget");
		}
	}
	
	/**
	 * Makes a ball from a given line in the file
	 * @param line
	 * @return Ball
	 * @throws runtime exception if it's a bad line
	 * TODO: make the thrown errors less hacky
	 */
	public static Ball makeBall(String[] line){
		double x;
		double y;
		double xVelocity;
		double yVelocity;
		
		if (line.length != 6){
			throw new RuntimeException("invalid ball");
		}
		
		//check that the line contains everything we expect
		//in the order that we expect
		if (!(line[1].startsWith("name") && line[2].startsWith("x") && line[3].startsWith("y") && line[4].startsWith("xVelocity") && line[5].startsWith("yVelocity"))){
			throw new RuntimeException("invalid ball");
		}
		
		String[] xWord = line[2].split("=");
		if (xWord.length == 2){
			x = Double.parseDouble(xWord[1]);
		}else{
			throw new RuntimeException("invalid ball");
		}
		
		String[] yWord = line[3].split("=");
		if (yWord.length == 2){
			y = Double.parseDouble(yWord[1]);
		}else{
			throw new RuntimeException("invalid ball");
		}
		
		String[] xVelWord = line[4].split("=");
		if (xVelWord.length == 2){
			xVelocity = Double.parseDouble(xVelWord[1]);
		}else{
			throw new RuntimeException("invalid ball");
		}
		
		String[] yVelWord = line[4].split("=");
		if (yVelWord.length == 2){
			yVelocity = Double.parseDouble(yVelWord[1]);
		}else{
			throw new RuntimeException("invalid ball");
		}
		
		Ball ball = new Ball(x, y, xVelocity, yVelocity);
		return ball;		
	}
	
	
	/**Takes a board file and cleans it by removing all comments and formatting it regularly
	 * comments are lines that begin with a # or are blank
	 * 
	 * calls makeFileList(file) and then removes all the comment lines
	 * 
	 * @param file
	 * @return List<String> of all the uncommented files
	 * each String in the array represents a single line in the file 
	 */
	public static List<String> cleanFile(File file){
		List<String> lineList = makeFileList(file);
		List<String> cleanList = new ArrayList<String>();
		for (String line: lineList){
			//check the individual string for cleanliness (ie. comments)
			String clean = cleanLine(line);
			if (!clean.isEmpty()){
				cleanList.add(clean);
			}
		}
		return cleanList;
	}
	
	/**
	 * takes an individual line and cleans it
	 * by removing comments and extra whitespace
	 * @param line
	 * @return cleaned line with comments removed and extra whitespace removed
	 * 
	 * NOTE: can handle spaces, can't currently handle tab characters
	 */
	public static String cleanLine(String line){
	    line = line.replaceAll("\\s+"," ");
	    line = line.replaceAll(" = ", "=");
		StringBuilder str = new StringBuilder();
		boolean lastCharSpace = true;
		for (int i = 0; i < line.length(); i++){
			//exit the loop if we're at a comment
			if (line.charAt(i) == '#'){
				break;
			}else if (line.charAt(i) == ' ' && !lastCharSpace){
				lastCharSpace = true;
				str.append(line.charAt(i));
			}else if (line.charAt(i) == ' '){
				lastCharSpace = true;
			}else if (!lastCharSpace){
				str.append(line.charAt(i));
			}else{
				lastCharSpace = false;
				str.append(line.charAt(i));
			}
		}
		//now we're going to loop through again and just delete all the
		//extra spaces that might exist at the end of the string
		for (int i = str.length() - 1; i>=0; --i){
			if (str.charAt(str.length() - 1) == ' '){
				str.deleteCharAt(str.length() - 1);
			}else{
				break;
			}
		}
		return str.toString();
	}
	
	/**Called by cleanFile
	 * Simply reads the file line by line and makes a list of all the strings (lines) in the file
	 * 
	 * @param file
	 * @return List<String> of all the lines in the file
	 */
	public static List<String> makeFileList(File file){
		List<String> lineList = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            try {
                while((line = reader.readLine()) != null) {
                    lineList.add(line);
                }
                reader.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return lineList;
	}
	
	
	/**
	 * this is called by the pingballServer
	 * @input a file
	 * @return a string representation of the file
	 */
	public static String fileToString(File file){
		StringBuilder str = new StringBuilder();
		try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            try {
                while((line = reader.readLine()) != null) {
                    str.append(line);
                    str.append("\n");
                }
                reader.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return str.toString();
	}
	
	/**
	 * checks a board file for validity
	 * throws a badFileException if the board is not valid
	 * @input List<String> -- THIS MUST BE THE CLEANED LINE
	 * because we don't care about what characters are in the comments
	 * @output true if the file is valid (contains only letters, numbers, and periods)
	 */
	private static boolean isValidFile(List<String> fileList){
		boolean valid = true;
		for (String line: fileList){
			if (!isValidLine(line)){
			    System.out.println(line);
				valid = false;
				break;
			}
		}
		return valid;
	}
	
	/**
	 * checks a line for validity
	 * called by isValidFile
	 * is checked on a cleaned line (all characters are valid in comments)
	 * Note: doesn't 
	 * @input String line to check for validity
	 *NOTE: THIS LINE MUST BE PRE-CLEANED (contain no comments or excess whitespace)
	 *@return true if the line is valid characters only
	 *return false if the line contains any invalid characters
	 */
	private static boolean isValidLine(String line){
		String[] splitLine = line.split(" ");
		for (String word: splitLine){
			if (!word.matches("[a-zA-Z0-9.,=_-]+")){
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 * checks each gadget to make sure it's valid
	 * (ie. isn't overlapping, has valid dimensions for a gadget of that type)
	 * Called by makeGadget
	 * @param Gadget to check
	 * @return true if we have a valid gadget
	 * false otherwise
	 * @throw badGadgetException
	 */
	private static boolean checkGadget(Gadget gadget){
		//TODO: implement
		throw new UnsupportedOperationException();
	}
	
}
