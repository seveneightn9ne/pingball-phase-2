package threadBoard;

import java.awt.Color;

import javax.swing.JFrame;

import client.Board;

public class Skeleton extends JFrame {

public Skeleton(Board board) {
        setTitle("Stars");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(300, 280);
        setBackground(new Color(201,204,183));
        add(new BoardGUI(board));
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }

}