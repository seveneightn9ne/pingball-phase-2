package threadBoard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;

import common.Constants;
import client.Ball;
import client.Board;
import client.gadgets.Gadget;
import client.gadgets.Wall;

public class WallBoardGUI extends JPanel implements Runnable {

	private Board board;
	private Thread animator;
	private final Color BACKGROUNDCOLOR = new Color(231, 220, 166);
	private BoardGUI gui;

	public WallBoardGUI(Board board) {
		setBackground(BACKGROUNDCOLOR);
		this.board = board;
		setDoubleBuffered(true);
		setSize(Constants.SCALE * 20 + Constants.SCALE, Constants.SCALE * 20
				+ Constants.SCALE);
		this.gui = new BoardGUI(board);
		
//		setLayout(new FlowLayout(FlowLayout.LEADING));
		setLayout(new GridBagLayout());
		add(gui);


	}

	public void addNotify() {
		super.addNotify();
		animator = new Thread(this);
		animator.start();
	}

	public void paint(Graphics g) {
		super.paint(g);
		gui.paint(g);

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		synchronized (board) {
			for (Wall w : board.getWallList()) {
				if (w.wallConnectedName() != null) {
					AffineTransform orig = g2d.getTransform();
					if (w.getSide() == Constants.BoardSide.BOTTOM) {
						g2d.drawString(w.wallConnectedName(),
								(Constants.SCALE * 20 + Constants.SCALE) / 2,
								Constants.SCALE * 20 + Constants.SCALE / 2);

					} else if (w.getSide() == Constants.BoardSide.RIGHT) {
						g2d.rotate(90.0);
						g2d.drawString(w.wallConnectedName(),
								(Constants.SCALE * 20 + Constants.SCALE) / 2,
								Constants.SCALE * 20 + Constants.SCALE / 2);

					} else if (w.getSide() == Constants.BoardSide.TOP) {
						g2d.drawString(w.wallConnectedName(),
								(Constants.SCALE * 20 + Constants.SCALE) / 2, 0);

					} else if (w.getSide() == Constants.BoardSide.LEFT) {
						g2d.rotate(270.0);
						g2d.drawString(w.wallConnectedName(),
								(Constants.SCALE * 20 + Constants.SCALE) / 2,
								Constants.SCALE * 20 + Constants.SCALE / 2);
					}
					g2d.setTransform(orig);
				}
			}
		}

		Toolkit.getDefaultToolkit().sync();
		g.dispose();
	}

	@Override
	public void run() {
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

}
