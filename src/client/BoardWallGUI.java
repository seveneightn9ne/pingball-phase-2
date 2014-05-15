package client;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;

import client.gadgets.Wall;
import common.Constants;

public class BoardWallGUI extends JPanel implements Runnable {

	private JPanel topWall;
	private JPanel bottomWall;
	private JPanel leftWall;
	private JPanel rightWall;
	private JPanel boardGUI;
	private Board board;
	private Thread animator;

	public BoardWallGUI(Board board, JPanel boardGUI, JPanel rightWall,
			JPanel leftWall, JPanel topWall, JPanel bottomWall) {
		this.boardGUI = boardGUI;
		this.rightWall = rightWall;
		this.leftWall = leftWall;
		this.topWall = topWall;
		this.bottomWall = bottomWall;
		this.board = board;
		setLayout(new BorderLayout());
		add(this.boardGUI, BorderLayout.CENTER);
		add(this.rightWall, BorderLayout.EAST);
		add(this.leftWall, BorderLayout.WEST);
		add(this.topWall, BorderLayout.NORTH);
		add(this.bottomWall, BorderLayout.SOUTH);

	}

	//
	// @Override
	// public void addNotify() {
	// super.addNotify();
	// animator = new Thread(this);
	// animator.start();
	// }

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		synchronized (board) {
			boardGUI.repaint();
			rightWall.repaint();
			leftWall.repaint();
			topWall.repaint();
			bottomWall.repaint();
		}
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
