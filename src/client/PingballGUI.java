package client;

import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import javax.swing.JFrame;

/**
 * How to draw the gadgets on the board? 
 * * Option 1: 
 * 		A drawGadget method that uses instanceof and accesses data based on that
 * 		Downsides: Requires instanceof
 * * Option 2:
 * 		Gadgets have a drawSelf method
 * 		Downsides: Bad separation of model and view
 * * Option 3:
 * 		All gadgets have a corresponding GadgetGUI class
 * 		Downsides: Tons of new classes, 
 * 		Gadget and GadgetGUI don't relate to each other in a meaningful way,
 * 		The main GUI will have to know which GadgetGUI to use, might require instanceof,
 * 		Alternatively a Gadget can know its GUI but once again, bad separation of model and view
 * 	
 */
public class PingballGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	public PingballGUI() throws HeadlessException {
		// TODO Auto-generated constructor stub
	}

	public PingballGUI(GraphicsConfiguration gc) {
		super(gc);
		// TODO Auto-generated constructor stub
	}

	public PingballGUI(String title) throws HeadlessException {
		super(title);
		// TODO Auto-generated constructor stub
	}

	public PingballGUI(String title, GraphicsConfiguration gc) {
		super(title, gc);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
