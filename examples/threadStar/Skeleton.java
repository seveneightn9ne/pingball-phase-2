package threadStar;

import javax.swing.JFrame;

public class Skeleton extends JFrame {

public Skeleton() {
        add(new BoardStar());
        setTitle("Stars");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(200, 200);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
        pack();
    }
    public static void main(String[] args) {
        new Skeleton();
    }
}