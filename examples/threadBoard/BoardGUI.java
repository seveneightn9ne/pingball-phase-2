package threadBoard;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;

import common.Constants;
import client.*;
import client.gadgets.*;

public class BoardGUI extends JPanel implements Runnable {

	private Board board;
	private Thread animator;
	private final Color BACKGROUNDCOLOR = new Color(231, 220, 166);

	public BoardGUI(Board board) {
		setBackground(BACKGROUNDCOLOR);
		this.board = board;
		setDoubleBuffered(true);
//		setSize(Constants.SCALE * 20 + Constants.SCALE, Constants.SCALE * 20
//				+ Constants.SCALE);
		setSize(400,400);

	}

	public void addNotify() {
		super.addNotify();
		animator = new Thread(this);
		animator.start();
	}

	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		synchronized (board) {
			for (Gadget s : board.returnGadgets()) {
				g2d.setColor(s.getColor());
				g2d.fill(s.getShape());
			}
			for (Ball b : board.getBalls()) {
				g2d.setColor(b.getColor());
				g2d.fill(b.getShape());
			}
			// for (Wall w : board.getWallList()) {
			// if (w.wallConnectedName() != null) {
			// if (w.getSide() == Constants.BoardSide.BOTTOM) {
			// g2d.drawString(w.wallConnectedName(),
			// (Constants.SCALE * 20 + Constants.SCALE) / 2,
			// Constants.SCALE * 20 + Constants.SCALE / 2);
			//
			// } else if (w.getSide() == Constants.BoardSide.RIGHT) {
			// g2d.rotate(90.0);
			// g2d.drawString(w.wallConnectedName(),
			// (Constants.SCALE * 20 + Constants.SCALE) / 2,
			// Constants.SCALE * 20 + Constants.SCALE / 2);
			//
			// } else if (w.getSide() == Constants.BoardSide.TOP) {
			// g2d.drawString(w.wallConnectedName(),
			// (Constants.SCALE * 20 + Constants.SCALE) / 2, 0);
			//
			// } else if (w.getSide() == Constants.BoardSide.LEFT) {
			// g2d.rotate(270.0);
			// g2d.drawString(w.wallConnectedName(),
			// (Constants.SCALE * 20 + Constants.SCALE) / 2,
			// Constants.SCALE * 20 + Constants.SCALE / 2);
			// }
			// }
			// }
		}
		Toolkit.getDefaultToolkit().sync();
		g.dispose();
	}

	@Override
	public void run() {
		// SwingWorker<String[], String[]> execution = new UpdateWorker(board);
		// execution.execute();
		// while(true){
		// try {
		// }
		// repaint();
		//
		// Thread.sleep((long) Constants.TIMESTEP);
		// } catch (InterruptedException ex) {
		// Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
		// }
		while (true) {
			try {
				repaint();

				Thread.sleep((long) Constants.TIMESTEP);
			} catch (InterruptedException ex) {
				Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null,
						ex);
			}
		}
	}

	/**
	 * Set the board to be this board. Used, for example, when resetting the
	 * game.
	 * 
	 * @param b
	 *            the new board
	 */
	public void setBoard(Board b) {
		this.board = b;
	}
}