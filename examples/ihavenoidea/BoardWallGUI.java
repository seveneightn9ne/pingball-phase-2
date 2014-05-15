package ihavenoidea;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;

import client.Board;
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
        setDoubleBuffered(true);


	}

	//
	// @Override
	// public void addNotify() {
	// super.addNotify();
	// animator = new Thread(this);
	// animator.start();
	// }

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		System.out.println("repainting");

		synchronized (board) {
			boardGUI.repaint();
			System.out.println("Board Painted");
			rightWall.repaint();
			System.out.println("Right Wall Painted");
			leftWall.repaint();
			System.out.println("left Wall Painted");
			topWall.repaint();
			System.out.println("Top Wall Painted");
			bottomWall.repaint();
			System.out.println("Bottom Wall Painted");

		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				revalidate();
				Thread.sleep((long) Constants.TIMESTEP);
			} catch (InterruptedException ex) {
				Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null,
						ex);
			}
		}
	}
}
