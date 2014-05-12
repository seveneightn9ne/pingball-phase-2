package kineticpingball;


import java.awt.Color;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import physics.Vect;
import client.Ball;
import client.Board;
import client.gadgets.Gadget;

/**
 * An Ensemble of spherical Particles that undergo elastic
 * collision between Particles and the walls of a container.
 * 
 * @author John B. Matthews
 */
public class Ensemble {

    private List<Ball> balls;
    private Ellipse2D ellipse = new Ellipse2D.Double();
    private Vect p1; // position
	private List<Gadget> gadgets;
	private Board board;


    /** Construct a container of atoms. */
    public Ensemble() {

    }

    public void addBoard(Board board)
    {
        balls = board.getBalls();
        gadgets = board.returnGadgets();
        this.board = board;
    }
    /** Get an atom's shape. */
    public Shape getBallShape(Ball ball) {
        double radius = 0.25;
        double diameter = 0.5;
        p1 = ball.getPosition();
        ellipse.setFrame(p1.x() - radius, p1.y() - radius , diameter, diameter);
        return ellipse;
    }

    public List<Gadget> getGadgets()
    {
    	return gadgets;
    }
    public Shape getGadgetShape(Gadget gadget)
    {
    	return gadget.getShape();
    }
    
    /** Get the list of atoms. */
    public List<Ball> getBalls() { return balls; }
}

