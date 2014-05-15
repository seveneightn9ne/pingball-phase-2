package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.border.AbstractBorder;

import com.sun.xml.internal.bind.v2.model.core.WildcardMode;

import common.Constants;
import client.gadgets.Gadget;
import client.gadgets.Wall;

public class BoardGUI extends JPanel implements Runnable {

	private Board board;
	private Thread animator;
	private final Color BACKGROUNDCOLOR = new Color(231, 220, 166);
	private PingBorder border = new PingBorder(Color.BLACK, 0);
	private boolean firstPrintTop = false;
	private boolean firstPrintBottom = false;
	private boolean firstPrintLeft = false;
	private boolean firstPrintRight = false;

	public BoardGUI(Board board) {
		setBackground(BACKGROUNDCOLOR);
		this.board = board;
		setDoubleBuffered(true);
		setSize(200, 200);
		setBorder(border);
		setPreferredSize(new Dimension(200, 200));

	}

	@Override
	public void addNotify() {
		super.addNotify();
		animator = new Thread(this);
		animator.start();
	}

	@Override
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
			for (Wall w : board.getWallList()) {
				if (w.wallConnectedName() != null) {
					if (w.getSide() == Constants.BoardSide.RIGHT
							&& !firstPrintRight) {
						System.out.println("adding name to RIGHTSIDE");
						firstPrintRight = true;
						border.setString(w.getSide(), w.wallConnectedName());
						setBorder(border);
					} else if (w.getSide() == Constants.BoardSide.LEFT
							&& !firstPrintLeft) {
						System.out.println("adding name to LEFTSIDE");
						firstPrintLeft = true;
						border.setString(w.getSide(), w.wallConnectedName());
						setBorder(border);
					} else if (w.getSide() == Constants.BoardSide.TOP
							&& !firstPrintTop) {
						System.out.println("adding name to TOPSIDE");
						firstPrintTop = true;
						border.setString(w.getSide(), w.wallConnectedName());
						setBorder(border);
					} else if (w.getSide() == Constants.BoardSide.BOTTOM
							&& !firstPrintBottom) {
						System.out.println("adding name to BOTTOMSIDE");
						firstPrintBottom = true;
						border.setString(w.getSide(), w.wallConnectedName());
						setBorder(border);
					}

				} else {
					border.clearString(w.getSide());
					setBorder(border);
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