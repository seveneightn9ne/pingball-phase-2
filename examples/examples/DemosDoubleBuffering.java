package examples;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.image.BufferedImage;

/** @see http://stackoverflow.com/questions/4430356 */
public class DemosDoubleBuffering extends JPanel implements ActionListener {

    private static final int W = 600;
    private static final int H = 400;
    private static final int r = 80;
    private int xs = 3;
    private int ys = xs;
    private int x = 0;
    private int y = 0;
    private final BufferedImage bi;
    private final JLabel jl = new JLabel();
    private final Timer t  = new Timer(10, this);

    public static void main(final String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(new DemosDoubleBuffering());
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    public DemosDoubleBuffering() {
        super(true);
        this.setLayout(new GridLayout());
        this.setPreferredSize(new Dimension(W, H));
        bi = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        jl.setIcon(new ImageIcon(bi));
        this.add(jl);
        t.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        drawSquare(bi);
        jl.repaint();
    }

    private void drawSquare(final BufferedImage bi) {
        Graphics2D g = bi.createGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, W, H);
        g.setColor(Color.blue);
        g.fillRect(x, y, r, r);
        g.dispose();
    }

    private void move() {
        if (!(x + xs >= 0 && x + xs + r < bi.getWidth())) {
            xs = -xs;
        }
        if (!(y + ys >= 0 && y + ys + r < bi.getHeight())) {
            ys = -ys;
        }
        x += xs;
        y += ys;
    }
}