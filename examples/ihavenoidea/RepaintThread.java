package ihavenoidea;

import java.awt.Component;
import java.util.Collection;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import common.Constants;

public class RepaintThread extends Thread {
	ReentrantLock lock;
	Condition modeChanged;
	boolean repaintMode;
	Collection<? extends Component> list;

	public RepaintThread(Collection<? extends Component> list) {
		this.lock = new ReentrantLock();
		this.modeChanged = this.lock.newCondition();

		this.list = list;
	}

	@Override
	public void run() {
		while (true) {
			// call repaint on all the Components
			// we're not on the event dispatch thread, but
			// repaint() is safe to call from any thread
			for (Component c : list)
				c.repaint();

			// wait a bit
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
			}
		}
	}
}
