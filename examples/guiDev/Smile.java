package guiDev;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Smile draws a smiley face and turns it yellow whenever a key is pressed, 
 * to demonstrate some Java2D drawing commands.
 */
public class Smile extends JPanel {
    Color backgroundColor = Color.white;
    
    /**
     * Make a smile.
     * @param width width in pixels
     * @param height height in pixels
     */
    public Smile(int width, int height) {
        this.setPreferredSize(new Dimension(width, height));
        
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                changeColor(Color.yellow);
            }

            public void keyReleased(KeyEvent e) {
                changeColor(Color.white);
            }
        });

    }
    
    private void changeColor(final Color color) {
        backgroundColor = color;
        this.repaint(); // IMPORTANT!  this is what triggers Swing to call paintComponent() below
    }
    
    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        fillWindow(g2);
        drawSmile(g2);
    }
    
    /*
     * Make the drawing buffer entirely white.
     */
    private void fillWindow(final Graphics2D g) {
        g.setColor(backgroundColor);
        g.fillRect(0,  0,  getWidth(), getHeight());
    }
    
    /*
     * Draw a happy smile on the drawing buffer.
     */
    private void drawSmile(final Graphics2D g) {
        // all positions and sizes below are in pixels
        final Rectangle smileBox = new Rectangle(20, 20, 100, 100); // x, y, width, height
        final Point smileCenter = new Point(smileBox.x + smileBox.width/2, smileBox.y + smileBox.height/2);
        final int smileStrokeWidth = 3;
        final Dimension eyeSize = new Dimension(9, 9);
        final Dimension eyeOffset = new Dimension(smileBox.width/6, smileBox.height/6);
        
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(smileStrokeWidth));
        
        // draw the smile -- an arc inscribed in smileBox, starting at -30 degrees (southeast)
        // and covering 120 degrees
        g.drawArc(smileBox.x, smileBox.y, smileBox.width, smileBox.height, -30, -120);
        
        // draw some eyes to make it look like a smile rather than an arc
        for (int side: new int[] { -1, 1 }) {
            g.fillOval(smileCenter.x + side * eyeOffset.width - eyeSize.width/2,
                       smileCenter.y - eyeOffset.height - eyeSize.width/2,
                       eyeSize.width,
                       eyeSize.height);
        }
    }
    
    
    /*
     * Main program. Make a window containing a Canvas.
     */
    public static void main(String[] args) {
        // set up the UI (on the event-handling thread)
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame window = new JFrame("Smile");
                window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                window.setLayout(new BorderLayout());
                Smile canvas = new Smile(400, 400);
                window.add(canvas, BorderLayout.CENTER);
                window.pack();
                window.setVisible(true);
            }
        });
    }
}
