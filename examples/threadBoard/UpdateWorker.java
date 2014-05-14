package threadBoard;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingWorker;

import client.Ball;
import client.Board;
import client.gadgets.Gadget;

public class UpdateWorker extends SwingWorker<String[], String[]> {

	private boolean leftPressed;
	private BufferStrategy strategy;
	private boolean rightPressed;
	private boolean absorberAction;
	private List<Gadget> gadgets;
	private Board board;

	public UpdateWorker(Board board) {
		this.board = board;
	}

	@Override
	protected String[] doInBackground() throws Exception {
		long lastLoopTime = System.currentTimeMillis();

		while (true) {
			// work out how long its been since the last update, this
			// will be used to calculate how far the entities should
			// move this loop
			long delta = System.currentTimeMillis() - lastLoopTime;
			lastLoopTime = System.currentTimeMillis();

			// Get hold of a graphics context for the accelerated
			// surface and blank it out
			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
			g.setColor(Color.black);
			g.fillRect(0, 0, 800, 600);



			// finally, we've completed drawing so clear up the graphics
			// and flip the buffer over
			g.dispose();
			strategy.show();

			if ((leftPressed) && (!leftPressed)) {
				// Flipper trigger for left
			} else if ((leftPressed) && (!leftPressed)) {
				// flipper trigger for right
			}

			if (absorberAction) {
				// incase we want to set absorbers
			}
			// finally pause for a bit. Note: this should run us at about
			// 100 fps but on windows this might vary each loop due to
			// a bad implementation of timer
			synchronized (gadgets) {
				for (Gadget gad : board.returnGadgets()) {
					g.setColor(gad.getColor());
					g.fill(gad.getShape());
				}
				for (Ball ball : board.getBalls())
				{
					g.setColor(ball.getColor());
					g.fill(ball.getShape());
				}
			}
			try {
				Thread.sleep(10);
			} catch (Exception e) {
				Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null,
						e);
			}
		}

	}
}
