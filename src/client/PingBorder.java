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

import javax.swing.border.AbstractBorder;

import common.Constants;

/**
 * PingBorder is the GUI element that represents the walls of the Pingball
 * board.
 * 
 */
public class PingBorder extends AbstractBorder {

	private static final long serialVersionUID = 1L;
	private Color borderColour;
	private int gap;
	private String topString = null;
	private String bottomString = null;
	private String leftString = null;
	private String rightString = null;

	/**
	 * Create a new border
	 * 
	 * @param colour
	 *            the colour of the border
	 * @param g
	 *            the amount of gap in pixels
	 */
	public PingBorder(Color colour, int g) {
		borderColour = colour;
		gap = g;
	}

	/**
	 * Set the name of the wall on the given side
	 * 
	 * @param side
	 *            the side of the border
	 * @param name
	 *            the name to write on that side
	 */
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

	/**
	 * Clear the current side name, if there is one
	 * 
	 * @param side
	 *            the side of the border to clear the name of
	 */
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
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(borderColour);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		if (rightString != null) {
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
			g2d.drawString(
					rightString,
					(int) width
							- (theDerivedFont.getSize() + theDerivedFont
									.getSize() / 2), (int) y + height / 2);

			// put the original font back
			g2d.setFont(theFont);

		}
		// TOPRIGHT Border
		if (leftString != null) {
			AffineTransform fontAT = new AffineTransform();

			// get the current font
			Font theFont = g2d.getFont();

			// Derive a new font using a rotatation transform
			fontAT.rotate(270 * java.lang.Math.PI / 180);
			Font theDerivedFont = theFont.deriveFont(fontAT);

			// set the derived font in the Graphics2D context
			g2d.setFont(theDerivedFont);

			// Render a string using the derived font
			g2d.drawString(
					leftString,
					(int) (theDerivedFont.getSize() + theDerivedFont.getSize() / 2),
					(int) y + height / 2);

			// put the original font back
			g2d.setFont(theFont);
		}
		// Lower Left Border
		if (bottomString != null) {

			AffineTransform fontAT = new AffineTransform();

			// get the current font
			Font theFont = g2d.getFont();

			// Derive a new font using a rotatation transform
			Font theDerivedFont = theFont.deriveFont(fontAT);

			// set the derived font in the Graphics2D context
			g2d.setFont(theDerivedFont);

			// Render a string using the derived font
			g2d.drawString(bottomString, (int) x + width / 2, (int) height
					- (theDerivedFont.getSize() + theDerivedFont.getSize() / 2));

			// put the original font back
			g2d.setFont(theFont);
		}
		// Lower Right Border
		if (topString != null) {
			AffineTransform fontAT = new AffineTransform();

			// get the current font
			Font theFont = g2d.getFont();

			// Derive a new font using a rotatation transform
			Font theDerivedFont = theFont.deriveFont(fontAT);

			// set the derived font in the Graphics2D context
			g2d.setFont(theDerivedFont);

			// Render a string using the derived font
			g2d.drawString(
					topString,
					(int) x + width / 2,
					(int) (theDerivedFont.getSize() + theDerivedFont.getSize() / 2));

			// put the original font back
			g2d.setFont(theFont);
		}
	}

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