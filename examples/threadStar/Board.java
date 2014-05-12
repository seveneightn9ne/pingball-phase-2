package threadStar;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;


public class Board extends JPanel implements Runnable {


    private Star star;
    private Thread animator;
    ArrayList<Star> items=new ArrayList<Star>();


    public Board() {
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        star=new Star(25,0,0);
        Star star2=new Star(50,20,25);
        items.add(star2);
        items.add(star);
    }

    public void addNotify() {
        super.addNotify();
        animator = new Thread(this);
        animator.start();
    }

    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2d = (Graphics2D)g;



        for (Star s : this.items) {
            g2d.drawImage(s.starImage, s.x, s.y, this);
        }


        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }

    public void run() {

        while(true){
            try {
                for (Star s : this.items) {
                    s.move();
                }
                repaint();

                Thread.sleep(star.delay);
            } catch (InterruptedException ex) {
                Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


}