package ihavenoidea;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Multithreaded {
	public static void main(String[] args) {

		JFrame frame = new JFrame();
		JPanel panel = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);

				// print something when the JPanel repaints
				// so that we know things are working
				System.out.println("repainting");
			}
		};

		frame.add(panel);
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);

		final JButton button = new JButton("Button");
		panel.add(button);
		JPanel jpan = new JPanel() {

			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setColor(Color.RED);
				g2d.draw(new Rectangle2D.Double(50,50, 0, 0));

			}
		};
		jpan.setSize(100, 100);
		panel.add(jpan);
		JPanel pannn = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				Random random = new Random();
				g2d.setColor(Color.RED);
				g2d.draw(new Line2D.Double(random.nextInt(10), random
						.nextInt(10), random.nextInt(10), random.nextInt(10)));
				// print something when the JPanel repaints
				// so that we know things are working
				System.out.println("repainting");
			}
		};
		panel.add(pannn);

		// create and start an instance of our custom
		// RepaintThread, defined below
		final RepaintThread thread = new RepaintThread(
				Collections.singletonList(panel));
		thread.start();

		// add an ActionListener to the JButton
		// which turns on and off the RepaintThread
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				thread.toggleRepaintMode();
			}
		});

		frame.setSize(300, 300);
		frame.setVisible(true);
	}

	public static class RepaintThread extends Thread {
		ReentrantLock lock;
		Condition modeChanged;
		boolean repaintMode;
		Collection<? extends Component> list;

		public RepaintThread(Collection<? extends Component> list) {
			this.lock = new ReentrantLock();
			this.modeChanged = this.lock.newCondition();

			this.repaintMode = false;
			this.list = list;
		}

		@Override
		public void run() {
			while (true) {
				lock.lock();
				try {
					// if repaintMode is false, wait until
					// Condition.signal( ) is called
					while (!repaintMode)
						try {
							modeChanged.await();
						} catch (InterruptedException e) {
						}
				} finally {
					lock.unlock();
				}

				// call repaint on all the Components
				// we're not on the event dispatch thread, but
				// repaint() is safe to call from any thread
				for (Component c : list)
					c.repaint();

				// wait a bit
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
				}
			}
		}

		public void toggleRepaintMode() {
			lock.lock();
			try {
				// update the repaint mode and notify anyone
				// awaiting on the Condition that repaintMode has changed
				this.repaintMode = !this.repaintMode;
				this.modeChanged.signalAll();
			} finally {
				lock.unlock();
			}
		}
	}
}
