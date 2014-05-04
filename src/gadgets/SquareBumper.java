package gadgets;

import java.util.HashSet;
import java.util.Set;

import client.Ball;
import physics.Geometry;
import physics.LineSegment;
import physics.Vect;

public class SquareBumper implements Gadget {
    /**
     * Rep invariant: 0 <= position.x() <= 19, 0 <= position.y() <= 19.
     * position.x() and position.y() are integer-valued doubles The LineSegments
     * in lines represent the boundaries of the square
     */
    private Vect position;
    private LineSegment[] lines;
    private LineSegment nextHit;
    private String name;
    private Set<Gadget> triggers = new HashSet<Gadget>();

    /**
     * Constructor for SquareBumper
     * 
     * @param position
     *            a Vect representing the coordinate position of the Square
     *            Bumper
     * @param name
     *            unique name representation of the bumper
     */
    public SquareBumper(String name, int x, int y) {
        this.name = name;
        this.position = new Vect(x, y);
        lines = new LineSegment[4];
        this.linesConstructor();
    }

    /**
     * Constructor for SquareBumper
     * 
     * @param position
     *            a Vect representing the coordinate position of the Square
     *            Bumper
     */
    public SquareBumper(int x, int y) {
        this.name = null;
        this.position = new Vect(x, y);
        lines = new LineSegment[4];
        this.linesConstructor();
    }
    
    private void linesConstructor() {

        lines[0] = new LineSegment(position.x() - 0.5, position.y() - 0.5,
                position.x() + 0.5, position.y() - 0.5);
        lines[1] = new LineSegment(position.x() - 0.5, position.y() - 0.5,
                position.x() - 0.5, position.y() + 0.5);
        lines[2] = new LineSegment(position.x() - 0.5, position.y() + 0.5,
                position.x() + 0.5, position.y() + 0.5);
        lines[3] = new LineSegment(position.x() + 0.5, position.y() + 0.5,
                position.x() + 0.5, position.y() - 0.5);
    }

    /**
     * Check the rep invariant. The lines are defined properly in the
     * constructor and never changed, the integer valued doubles are enforced in
     * the constructor arguments
     */
    public void checkRep() {
        assert (position.x() >= 0 && position.x() <= 19);
        assert (position.y() >= 0 && position.y() <= 19);
    }

    @Override
    public int hit(Ball ball, Board board) {
        Vect velocity = Geometry.reflectWall(nextHit, ball.getVelocity());
        ball.setVelocity(velocity);
        for (Gadget g : triggers) {
            g.action(board);
        }
        
        return -1;
    }

    @Override
    public void putInBoardRep(Board board, boolean remove) {
        char[][] boardRep = board.getBoardRep();
        boardRep[(int) Math.round(position.y() + 1)][(int) Math.round(position
                .x() + 1)] = '#';
        board.setBoardRep(boardRep);
    }

    /**
     * Accessor method
     * 
     * @return a Vect representing the position of the bumper
     */
    public Vect getPosition() {
        return new Vect(position.x(), position.y());
    }

    @Override
    public void action(Board board) {

    }

    @Override
    public String toString() {
        return "#";
    }

    @Override
    public void addTrigger(Gadget g) {
        triggers.add(g);
    }

    @Override
    public double timeUntilCollision(Ball ball) {
        double minTime = Double.POSITIVE_INFINITY;
        double time;
        for (LineSegment line : lines) {
            time = Geometry.timeUntilWallCollision(line, ball.getCircle(),
                    ball.getVelocity());
            if (time < minTime) {
                minTime = time;
                nextHit = line;
            }
        }
        return minTime;
    }

    @Override
    public String getName() {
        return this.name;
    }

}
