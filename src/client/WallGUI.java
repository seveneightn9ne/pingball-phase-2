package client;

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
	private final Color BACKGROUNDCOLOR = new Color(231, 220, 166);
	private Wall wall;
	private BoardSide side;
	private String name;
	private double rotation;

	public WallGUI(Wall wall, int parentSize) {
		setBackground(BACKGROUNDCOLOR);
		setDoubleBuffered(true);

		this.wall = wall;
		this.side = wall.getSide();

		if (wall.getSide() == Constants.BoardSide.RIGHT
				|| wall.getSide() == Constants.BoardSide.LEFT) {
			setSize(20, 20);
			if (wall.getSide() == Constants.BoardSide.RIGHT)
				this.rotation = 90.0;
			else
				this.rotation = 270.0;
		} else if (wall.getSide() == Constants.BoardSide.TOP
				|| wall.getSide() == Constants.BoardSide.BOTTOM) {
			setSize(20, 20);
			this.rotation = 0;
		}

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

		AffineTransform orig = g2d.getTransform();
		if (wall.wallConnectedName() != null && this.name == null) {
			System.out
					.println("wall.wallConnectedName() != null && this.name == null");
			this.name = wall.wallConnectedName();
			g2d.setColor(Color.RED);
			g2d.rotate(this.rotation);
			g2d.drawString(this.name, 10, 10);

		} else if (wall.wallConnectedName() == null && this.name != null) {
			System.out
					.println("wall.wallConnectedName() == null && this.name != null");
			g2d.setColor(Color.YELLOW);
			g2d.rotate(this.rotation);
			g2d.drawString(this.name, 10, 10);

		} else if (wall.wallConnectedName() != null && this.name != null) {
			System.out.println(wall.wallConnectedName() != null
					&& this.name != null);
			if (!wall.wallConnectedName().equals(this.name)) {
				g2d.setColor(Color.BLUE);
				this.name = wall.wallConnectedName();
				g2d.rotate(this.rotation);
				g2d.drawString(this.name, 10, 10);

			} else {
				g2d.setColor(Color.BLACK);
				g2d.rotate(this.rotation);
				g2d.drawString(this.name, 10, 10);
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

				Thread.sleep((long) Constants.TIMESTEP);
			} catch (InterruptedException ex) {
				Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null,
						ex);
			}
		}
	}

}