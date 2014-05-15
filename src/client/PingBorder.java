package client;

//SAUCE: http://stackoverflow.com/questions/17834573/swing-custom-border

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

import javax.swing.border.AbstractBorder;

import common.Constants;

public class PingBorder extends AbstractBorder {
	private Color borderColour;
	private int gap;
	private String topString = null;
	private String bottomString = null;
	private String leftString = null;
	private String rightString = null;

	public PingBorder(Color colour, int g) {
		borderColour = colour;
		gap = g;
	}

	public void setString(Constants.BoardSide side, String name) {
		if (side == Constants.BoardSide.RIGHT) {
			rightString = name;
		} else if (side == Constants.BoardSide.LEFT) {
			leftString = name;
		} else if (side == Constants.BoardSide.TOP) {
			topString = name;
		} else if (side == Constants.BoardSide.BOTTOM) {
			bottomString = name;
		}
	}

	public void clearString(Constants.BoardSide side) {
		if (side == Constants.BoardSide.RIGHT) {
			rightString = null;
		} else if (side == Constants.BoardSide.LEFT) {
			leftString = null;
		} else if (side == Constants.BoardSide.TOP) {
			topString = null;
		} else if (side == Constants.BoardSide.BOTTOM) {
			bottomString = null;
		}
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width,
			int height) {
		super.paintBorder(c, g, x, y, width, height);
		Graphics2D g2d;
		if (g instanceof Graphics2D) {
			g2d = (Graphics2D) g;
			g2d.setColor(borderColour);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			AffineTransform orig = g2d.getTransform();

			// TOPLEFT Border
			if (leftString != null) {
				g2d.draw(new Line2D.Double((double) x + 10, (double) y + 10,
						(double) x + 10, (double) y + 20));
				g2d.draw(new Line2D.Double((double) x + 10, (double) y + 10,
						(double) x + 20, (double) y + 10));
				// Create a rotation transformation for the font.
				AffineTransform fontAT = new AffineTransform();

				// get the current font
				Font theFont = g2d.getFont();

				// Derive a new font using a rotatation transform
				fontAT.rotate(90 * java.lang.Math.PI / 180);
				Font theDerivedFont = theFont.deriveFont(fontAT);

				// set the derived font in the Graphics2D context
				g2d.setFont(theDerivedFont);

				// Render a string using the derived font
				g2d.drawString(leftString, (int) x, (int) y);

				// put the original font back
				g2d.setFont(theFont);

			}
			// TOPRIGHT Border
			if (rightString != null) {
				g2d.draw(new Line2D.Double((double) width - 10,
						(double) y + 10, (double) width - 10, (double) y + 20));
				g2d.draw(new Line2D.Double((double) width - 10,
						(double) y + 10, (double) width - 20, (double) y + 10));
				AffineTransform fontAT = new AffineTransform();

				// get the current font
				Font theFont = g2d.getFont();

				// Derive a new font using a rotatation transform
				fontAT.rotate(90 * java.lang.Math.PI / 180);
				Font theDerivedFont = theFont.deriveFont(fontAT);

				// set the derived font in the Graphics2D context
				g2d.setFont(theDerivedFont);

				// Render a string using the derived font
				g2d.drawString(rightString, (int) x, (int) y);

				// put the original font back
				g2d.setFont(theFont);
			}
			// Lower Left Border
			if (bottomString != null) {
				g2d.draw(new Line2D.Double((double) x + 10,
						(double) height - 10, (double) x + 20,
						(double) height - 10));
				g2d.draw(new Line2D.Double((double) x + 10,
						(double) height - 10, (double) x + 10,
						(double) height - 20));
				AffineTransform fontAT = new AffineTransform();

				// get the current font
				Font theFont = g2d.getFont();

				// Derive a new font using a rotatation transform
				fontAT.rotate(90 * java.lang.Math.PI / 180);
				Font theDerivedFont = theFont.deriveFont(fontAT);

				// set the derived font in the Graphics2D context
				g2d.setFont(theDerivedFont);

				// Render a string using the derived font
				g2d.drawString(bottomString, (int) x, (int) y);

				// put the original font back
				g2d.setFont(theFont);
			}
			// Lower Right Border
			if (topString != null) {
				g2d.draw(new Line2D.Double((double) width - 10,
						(double) height - 10, (double) width - 20,
						(double) height - 10));
				g2d.draw(new Line2D.Double((double) width - 10,
						(double) height - 10, (double) width - 10,
						(double) height - 20));
				AffineTransform fontAT = new AffineTransform();

				// get the current font
				Font theFont = g2d.getFont();

				// Derive a new font using a rotatation transform
				fontAT.rotate(90 * java.lang.Math.PI / 180);
				Font theDerivedFont = theFont.deriveFont(fontAT);

				// set the derived font in the Graphics2D context
				g2d.setFont(theDerivedFont);

				// Render a string using the derived font
				g2d.drawString(topString, (int) x, (int) y);

				// put the original font back
				g2d.setFont(theFont);
			}
		}
	}

	// g2d.setColor(borderColour);
	// AffineTransform orig = g2d.getTransform();
	// // Left Border
	// if (leftString != null) {
	// System.out.println("adding left border");
	// g2d.rotate(-Math.PI / 2);
	// g2d.setColor(Color.BLACK);
	// g2d.drawString(leftString, (float) width / 2, (float) height / 2);
	// g2d.setTransform(orig);
	//
	// }
	// // Right Border
	// if (rightString != null) {
	// System.out.println("adding right border");
	// g2d.rotate(Math.PI / 2);
	// g2d.setColor(Color.BLACK);
	// g2d.drawString(rightString, (float) width / 2, (float) height / 2);
	// g2d.setTransform(orig);
	// }
	// // Top Border
	// if (topString != null) {
	// System.out.println("adding top border");
	// g2d.setColor(Color.BLACK);
	// g2d.drawString(topString, (float) width / 2, (float) 0);
	// }
	// // Bottom Border
	// if (bottomString != null) {
	// System.out.println("adding bottom border");
	// g2d.setColor(Color.BLACK);
	// g2d.drawString(bottomString, (float) width / 2, (float) height);
	// }

	@Override
	public Insets getBorderInsets(Component c) {
		return (getBorderInsets(c, new Insets(gap, gap, gap, gap)));
	}

	@Override
	public Insets getBorderInsets(Component c, Insets insets) {
		insets.left = insets.top = insets.right = insets.bottom = gap;
		return insets;
	}

	@Override
	public boolean isBorderOpaque() {
		return true;
	}
}