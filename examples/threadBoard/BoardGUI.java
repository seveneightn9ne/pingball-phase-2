package threadBoard;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.SwingWorker;

import client.Board;
import client.gadgets.Gadget;


public class BoardGUI extends JPanel implements Runnable {


    private Board board;
    private Thread animator;
    private List<Gadget> gadgets;
    private final Color BACKGROUNDCOLOR = new Color(231,220,166);

    public BoardGUI(Board board) {
        setBackground(BACKGROUNDCOLOR);
        this.board = board;
        setDoubleBuffered(true);
        setSize(200,200);
        gadgets = board.returnGadgets();
//    	strategy = getBufferStrategy();

    }

    public void addNotify() {
        super.addNotify();
        animator = new Thread(this);
        animator.start();
    }

    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2d = (Graphics2D)g;

        for (Gadget s : this.gadgets) {
        	g2d.setColor(s.getColor());
        	g2d.fill(s.getShape());
        }

        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }

    public void run() {
		SwingWorker<String[], String[]> execution = new UpdateWorker(board);
		execution.execute();
//        while(true){
//            try {
//                for (Star s : this.items) {
//                    s.move();
//                }
//                repaint();
//
//                Thread.sleep(star.delay);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
    }
}