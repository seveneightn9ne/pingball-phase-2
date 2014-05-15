package kineticmodel;


import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Random;
import javax.swing.*;


class DisplayPanel extends JPanel {

    private static final int ROWS = 20;
    private static final int COLS = ROWS;
    private static final Random random = new Random();
    private final Ensemble model;
    private Rectangle2D.Double r = new Rectangle2D.Double();
    private BufferedImage image;
    private TexturePaint paint;
    private boolean useGradient = true;
    private long paintTime;
    private int[] iArray = { 0, 0, 0, 255 };

    /** Construct a display panel. */
    public DisplayPanel(final Ensemble model) {
    	//model = board
        this.model = model;
        this.setPreferredSize(new Dimension(700, 600));
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Component c = (Component) e.getSource();
                model.setWalls(COLS, ROWS,
                    c.getWidth() - COLS, c.getHeight() - ROWS);
            }
        });
    }

    /** Paint the display. */
    @Override
    protected void paintComponent(Graphics g) {
        if (image == null) initImage();
        if (model.getAtoms().isEmpty()) model.initAtoms();
        long start = System.currentTimeMillis();
        WritableRaster raster = image.getRaster();
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0 ; col < COLS; col++) {
                int v = random.nextInt(256);
                iArray[0] = 255; iArray[1] = v; iArray[2] = 0;
                raster.setPixel(col, row, iArray);
            }
        }
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());
        System.out.println("get width " +getWidth());
        System.out.println("get width " +getHeight());

        Graphics2D g2D = (Graphics2D) g;
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        g2D.setPaint(paint);
        //THESES ARE BOARDERS
        r. setRect(0, 0, getWidth(), ROWS);
        g2D.fill(r);
        r. setRect(0, getHeight() - ROWS, getWidth(), ROWS);
        g2D.fill(r);
        r. setRect(0, 0, COLS, getHeight());
        g2D.fill(r);
        r. setRect(getWidth() - COLS, 0, COLS, getHeight());
        g2D.fill(r);
        //ATOMS
        for (Particle atom : model.getAtoms()) {
            model.iterate(atom);
            Shape shape = model.getShape(atom);
            if (useGradient) {
                Image atomImage = atom.getImage();
                int x = (int) shape.getBounds2D().getX();
                int y = (int) shape.getBounds2D().getY();
                g2D.drawImage(atomImage, x, y, null);
            }
            else {
                g2D.setPaint(atom.getColor());
                g2D.fill(shape);
            }
        }
        paintTime = System.currentTimeMillis() - start;
    }

    /** Initialize offscreen buffer and paint. */
    private void initImage() {
        image = (BufferedImage) createImage(COLS, ROWS);
        r.setRect(0, 0, ROWS, COLS);
        paint = new TexturePaint(image, r);
    }

    /** Return time taken in paintComponent. */
    public long getPaintTime() { return paintTime; }

    /** Specify color (true) or gray (false). */
    public void useGradient(boolean state) { useGradient = state; }
}
