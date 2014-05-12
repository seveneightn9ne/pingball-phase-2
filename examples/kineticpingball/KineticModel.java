package kineticpingball;


import java.awt.*;

import javax.swing.*;

import client.Board;

/**  
 * KineticModel: A program that models elastic collisions.
 *
 * @author John B. Matthews
 */
public class KineticModel extends JFrame {
	private static Board board;


    /** Construct a KineticModel frame. */
    public KineticModel(Board board) {
        this.setTitle("KineticModel");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(kineticModelPanel(this));
        this.pack();
        this.setVisible(true);
    }

    /**
     * Return a KineticModel panel.
     * The optional frame parameter is used for the enclosing
     * JFrame's default button. A null frame is ignored.
     * 
     * @param frame the enclosing JFrame
     */
    public static JPanel kineticModelPanel(JFrame frame) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        Ensemble model = new Ensemble();
        model.addBoard(board);
        DisplayPanel view = new DisplayPanel(model);
        panel.add(view, BorderLayout.CENTER);
        ControlPanel controls = new ControlPanel(view,model );
        panel.add(controls, BorderLayout.EAST);
        if (frame != null) {
            frame.getRootPane().setDefaultButton(
                controls.getDefaultButton());
        }
        return panel;
    }
}

