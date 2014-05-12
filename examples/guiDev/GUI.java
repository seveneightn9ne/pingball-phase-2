package guiDev;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import client.gadgets.*;

public class GUI extends Canvas {

	private boolean waitingForKeyPress = true;

	private boolean leftPressed = false;

	private boolean rightPressed = false;

	private boolean absorberAction = false;

	private List<Gadget> gadgets = new ArrayList<Gadget>();

	private JPanel panel;

	public static void main(String argv[]) {
		JFrame container = new JFrame("GUI");
		container.setVisible(true);
		GUI gui = new GUI(container);
		gui.addGadget(new CircleBumper("meow", 10, 10));
		// must create board first by injecting then running loop?

	}

	private BufferStrategy strategy;

	public GUI(JFrame container) {
		// create a frame to contain our game
		// JFrame container = new JFrame("GUI");

		// get hold the content of the frame and set up the
		// resolution of the game
		panel = (JPanel) container.getContentPane();
		panel.setPreferredSize(new Dimension(800, 600));
		panel.setLayout(null);

		// setup our canvas size and put it into the content of the frame
		setBounds(0, 0, 800, 600);
		panel.add(this);
		// Tell AWT not to bother repainting our canvas since we're
		// going to do that our self in accelerated mode
		setIgnoreRepaint(true);

		// finally make the window visible
		// container.pack();
		// container.setResizable(false);
		// container.setVisible(true);

		addKeyListener(new KeyInputHandler());

		createBufferStrategy(2);
		strategy = getBufferStrategy();
		SwingWorker<String[], String[]> execution = new UpdateWorkerGUI(this,
				strategy, leftPressed, rightPressed, absorberAction, gadgets);
		execution.execute();
		// gameLoop();

	}

	public JPanel returnJPANEL() {
		return panel;
	}

	// public void gameLoop() {
	// long lastLoopTime = System.currentTimeMillis();
	//
	// while (true) {
	// // work out how long its been since the last update, this
	// // will be used to calculate how far the entities should
	// // move this loop
	// long delta = System.currentTimeMillis() - lastLoopTime;
	// lastLoopTime = System.currentTimeMillis();
	//
	// // Get hold of a graphics context for the accelerated
	// // surface and blank it out
	// Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
	// g.setColor(Color.black);
	// g.fillRect(0, 0, 800, 600);
	//
	// for (Gadget gad : gadgets) {
	// gad.draw(g);
	// }
	//
	// // finally, we've completed drawing so clear up the graphics
	// // and flip the buffer over
	// g.dispose();
	// strategy.show();
	//
	// if ((leftPressed) && (!rightPressed)) {
	// // Flipper trigger for left
	// } else if ((rightPressed) && (!leftPressed)) {
	// // flipper trigger for right
	// }
	//
	// if (absorberAction) {
	// // incase we want to set absorbers
	// }
	// // finally pause for a bit. Note: this should run us at about
	// // 100 fps but on windows this might vary each loop due to
	// // a bad implementation of timer
	// try {
	// Thread.sleep(10);
	// } catch (Exception e) {
	// }
	// }
	// }

	public void addGadget(Gadget gadget) {
		synchronized (gadgets) {
			gadgets.add(gadget);
		}
	}

	private class KeyInputHandler extends KeyAdapter {

		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				leftPressed = true;
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				rightPressed = true;
			}
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				absorberAction = true;
			}
		}

		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				leftPressed = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				rightPressed = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				absorberAction = false;
			}
		}

		public void keyTyped(KeyEvent e) {
			// if we hit escape, then quit the game
			if (e.getKeyChar() == 27) {
				System.exit(0);
			}
		}
	}
}
