package ihavenoidea;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;

import common.Constants;
import common.Constants.BoardSide;
import client.*;
import client.gadgets.*;

public class WallGUI extends JPanel implements Runnable {

	private Thread animator;

	private Wall wall;
	private BoardSide side;
	private String name;
	private double rotation;
	private String str;
	private Color col;

	public WallGUI(Wall wall, int parentSize) {
		setDoubleBuffered(true);

		this.wall = wall;
		this.side = wall.getSide();

		if (wall.getSide() == Constants.BoardSide.RIGHT
				|| wall.getSide() == Constants.BoardSide.LEFT) {
			setSize(100, 100);
			if (wall.getSide() == Constants.BoardSide.RIGHT) {
				this.rotation = 90.0;
				this.str = "right";
				this.col = new Color(200, 50, 111);
			} else {
				this.rotation = 270.0;
				this.str = "left";
				this.col = new Color(60, 232, 44);

			}
		} else if (wall.getSide() == Constants.BoardSide.TOP
				|| wall.getSide() == Constants.BoardSide.BOTTOM) {
			setSize(100, 100);
			this.rotation = 0;
			if (wall.getSide() == Constants.BoardSide.TOP) {
				this.col = new Color(41, 179, 201);
				this.str = "top";
			} else {
				this.col = new Color(94, 183, 127);
				this.str = "bottom";
			}
		}

	}
	public WallGUI(Constants.BoardSide boardSide, int parentSize) {
		setDoubleBuffered(true);

		this.wall = wall;
//		this.side = wall.getSide();

		if (boardSide== Constants.BoardSide.RIGHT
				|| boardSide == Constants.BoardSide.LEFT) {
			setSize(100, 100);
			if (boardSide == Constants.BoardSide.RIGHT) {
				this.rotation = 90.0;
				this.str = "right";
				this.col = new Color(200, 50, 111);
			} else {
				this.rotation = 270.0;
				this.str = "left";
				this.col = new Color(60, 232, 44);

			}
		} else if (boardSide== Constants.BoardSide.TOP
				|| boardSide == Constants.BoardSide.BOTTOM) {
			setSize(100, 100);
			this.rotation = 0;
			if (boardSide == Constants.BoardSide.TOP) {
				this.col = new Color(41, 179, 201);
				this.str = "top";
			} else {
				this.col = new Color(94, 183, 127);
				this.str = "bottom";
			}
		}

	}

	@Override
	public void addNotify() {
		super.addNotify();
		animator = new Thread(this);
		animator.start();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		System.out.println("PAINTING WALL" + str);

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		AffineTransform orig = g2d.getTransform();
//		if (str.equalsIgnoreCase("right")) {
//			System.out.println("LAST ELSEBLOCK");
//			g.setColor(Color.RED);
////			g2d.rotate(0);
//			g.fillRect(40, 40, 50, 50);
//			g.drawString(this.str, 10, 10);
//		}
		if (wall.wallConnectedName() != null && this.name == null) {
			System.out
					.println("wall.wallConnectedName() != null && this.name == null");
			this.name = wall.wallConnectedName();
			g2d.setColor(Color.RED);
			g2d.rotate(this.rotation);
			g2d.drawString(this.name, 10, 10);

		} else {
			System.out.println("Chekcing else 1");
			if (wall.wallConnectedName() == null && this.name != null) {
				System.out
						.println("wall.wallConnectedName() == null && this.name != null");
				this.name = null;
				g2d.setColor(Color.YELLOW);
				g2d.rotate(this.rotation);
				g2d.drawRect(10, 10, 50, 50);
			} else {
				System.out.println("checking else 2");
				if (wall.wallConnectedName() != null && this.name != null) {
					System.out.println(wall.wallConnectedName() != null
							&& this.name != null);
					if (!wall.wallConnectedName().equals(this.name)) {
						g2d.setColor(Color.BLUE);
						this.name = wall.wallConnectedName();
						g2d.rotate(this.rotation);
						g2d.drawString(this.name, 10, 10);
					}
				} else {
					if (str.equalsIgnoreCase("left")) {
						System.out.println("LAST ELSEBLOCK");
						g2d.setColor(this.col);
						g2d.rotate(this.rotation);
						g2d.fillRect(40, 40, 50, 50);
						g2d.drawString(this.str, 10, 10);
					}

				}
			}
		}
		g2d.setTransform(orig);

		Toolkit.getDefaultToolkit().sync();
		g.dispose();
	}

	@Override
	public void run() {
		while (true) {
			try {
				repaint();

				Thread.sleep(10000);
			} catch (InterruptedException ex) {
				Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null,
						ex);
			}
		}
	}

}