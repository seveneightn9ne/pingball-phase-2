package kineticpingball;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Random;

import javax.swing.*;

import client.Ball;
import client.Board;
import client.gadgets.Gadget;

class DisplayPanel extends JPanel {

	private static final int ROWS = 20;
	private static final int COLS = ROWS;
	private static final Random random = new Random();
	private final Ensemble model;
	private Rectangle2D.Double r = new Rectangle2D.Double();
	private BufferedImage image;
	private TexturePaint paint;
	private long paintTime;
	private int[] iArray = { 0, 0, 0, 255 };

	/** Construct a display panel. */
	public DisplayPanel(final Ensemble model) {
		// model = board
		this.model = model;
		this.setPreferredSize(new Dimension(700, 600));
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				Component c = (Component) e.getSource();
			}
		});
	}

	/** Paint the display. */
	@Override
	protected void paintComponent(Graphics g) {
		if (image == null)
			initImage();
		long start = System.currentTimeMillis();
		WritableRaster raster = image.getRaster();
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				int v = random.nextInt(256);
				iArray[0] = 255;
				iArray[1] = v;
				iArray[2] = 0;
				raster.setPixel(col, row, iArray);
			}
		}
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());
		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setPaint(paint);
		r.setRect(0, 0, getWidth(), ROWS);
		g2D.fill(r);
		r.setRect(0, getHeight() - ROWS, getWidth(), ROWS);
		g2D.fill(r);
		r.setRect(0, 0, COLS, getHeight());
		g2D.fill(r);
		r.setRect(getWidth() - COLS, 0, COLS, getHeight());
		g2D.fill(r);
		for (Ball ball : model.getBalls()) {
			Shape shape = ball.getShape();
			g2D.setColor(ball.getColor());
			g2D.draw(shape);
		}
		for (Gadget gad : model.getGadgets())
		{
			Shape shape = gad.getShape();
			g2D.setColor(Color.RED);
			g2D.draw(shape);
		}
		paintTime = System.currentTimeMillis() - start;
	}

	/** Initialize offscreen buffer and paint. */
	private void initImage() {
		image = (BufferedImage) createImage(COLS, ROWS);
		r.setRect(0, 0, ROWS, COLS);
		paint = new TexturePaint(image, r);
	}

	/** Return time taken in paintComponent. */
	public long getPaintTime() {
		return paintTime;
	}

}
