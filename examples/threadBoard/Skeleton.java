package threadBoard;

import java.awt.Color;

import javax.swing.JFrame;

import common.Constants;

import client.Board;
import client.BoardGUI;

public class Skeleton extends JFrame {

public Skeleton(Board board) {
        setTitle("Stars");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(20*Constants.SCALE, 21*Constants.SCALE);
        setBackground(new Color(201,204,183));
        add(new BoardGUI(board));
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }

}