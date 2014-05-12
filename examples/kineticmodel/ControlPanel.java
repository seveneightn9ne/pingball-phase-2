package kineticmodel;


import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.event.*;

class ControlPanel extends JPanel
    implements ActionListener, ChangeListener {

    private static final int RATE = 25; // 25 Hz
    private static final int STRUT = 8;
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static final DecimalFormat pf = new DecimalFormat("0%");
    private DisplayPanel view;
    private Ensemble model;
    private Histogram histogram;
    private JButton runButton = new JButton();
    private JButton resetButton = new JButton();
    private JButton plusButton = new JButton();
    private JButton minusButton = new JButton();
    private JLabel paintLabel = new JLabel();
    private JLabel countLabel = new JLabel();
    private JLabel collisionLabel = new JLabel();
    private JSpinner spinner = new JSpinner();
    private JLabel histLabel = new JLabel();
    private Timer timer = new Timer(1000/RATE, this);

    /** Construct a control panel. */
    public ControlPanel(DisplayPanel view, Ensemble model) {
        this.view = view;
        this.model = model;
        histogram = new Histogram(model.getAtoms());
        timer.setInitialDelay(200);
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Catch a breath while resizing.
                if (timer.isRunning()) timer.restart();
            }
        });
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        runButton.setText("Run");
        runButton.setActionCommand("run");
        runButton.addActionListener(this);
        panel.add(runButton);

        resetButton.setText("Reset");
        resetButton.setActionCommand("reset");
        resetButton.addActionListener(this);
        panel.add(resetButton);

        plusButton.setText("Atoms +");
        plusButton.setActionCommand("plus");
        plusButton.addActionListener(this);
        panel.add(plusButton);

        minusButton.setText("Atoms -");
        minusButton.setActionCommand("minus");
        minusButton.addActionListener(this);
        panel.add(minusButton);

        panel.add(Box.createVerticalStrut(STRUT));
        JRadioButton colorButton = new JRadioButton("Color");
        colorButton.setMnemonic(KeyEvent.VK_C);
        colorButton.setActionCommand("color");
        colorButton.setSelected(false);
        panel.add(colorButton);

        JRadioButton grayButton = new JRadioButton("Gradient");
        grayButton.setMnemonic(KeyEvent.VK_G);
        grayButton.setActionCommand("gradient");
        grayButton.setSelected(true);
        panel.add(grayButton);

        ButtonGroup group = new ButtonGroup();
        group.add(colorButton);
        group.add(grayButton);
        colorButton.addActionListener(this);
        grayButton.addActionListener(this);
        
        panel.add(Box.createVerticalStrut(STRUT));
        panel.add(paintLabel);
        panel.add(countLabel);
        panel.add(collisionLabel);

        panel.add(Box.createVerticalStrut(STRUT));
        JLabel rateLabel = new JLabel("Update (Hz):");
        panel.add(rateLabel);
        spinner.setModel(new SpinnerNumberModel(RATE, 5, 50, 5));
        spinner.addChangeListener(this);
        spinner.setAlignmentX(JSpinner.LEFT_ALIGNMENT);
        panel.add(spinner);

        panel.add(Box.createVerticalStrut(STRUT));
        panel.add(histLabel);
        panel.add(histogram);

        this.add(panel);
        toggle();
    }

    /** Return the defualt button. */
    public JButton getDefaultButton() {
        return runButton;
    }

    private void toggle() {
        if (timer.isRunning()) {
            timer.stop();
            runButton.setText("Start");
        } else {
            timer.start();
            runButton.setText("Stop");
        }
    }

    /** Handle buttons and timer. */
    public void actionPerformed(ActionEvent e) {
    	
        Object source = e.getSource();
        String cmd = e.getActionCommand();
        if (source == timer && cmd == null) {
           view.repaint();
           long pt = view.getPaintTime();
           if (pt < 2) paintLabel.setText("Paint: ~1");
           else paintLabel.setText("Paint: "
               + view.getPaintTime());
           countLabel.setText("Atoms: "
               + model.getAtoms().size());
           collisionLabel.setText("Collide: "
               + pf.format(model.getCollisionRate()));
           histLabel.setText("Velocity: "
               + df.format(histogram.getAverage()));
           histogram.repaint();
        } else if ("run".equals(cmd)) {
           toggle();
        } else if ("reset".equals(cmd)) {
           model.initAtoms();
           timer.restart();
        } else if ("plus".equals(cmd)) {
           model.addAtoms();
        } else if ("minus".equals(cmd)) {
           model.removeAtoms();
        } else if ("color".equals(cmd)) {
           view.useGradient(false);
        } else if ("gradient".equals(cmd)) {
           view.useGradient(true);
        }
    }

    /** Handle spinners. */
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();
        if (source == spinner) {
            int rate = ((Number) spinner.getValue()).intValue();
            timer.setDelay(1000 / rate);
        }
    }
}
