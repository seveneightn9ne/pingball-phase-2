package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

public class WhyAmIUpSoLate {

	private JFrame frame = new JFrame();
	private JPanel leftPanel = new JPanel();
	private JSeparator sep = new JSeparator();
	private JLabel label = new JLabel("<html> L<br>a<br>b<br>e<br>l<br></html>");

	public WhyAmIUpSoLate() {
		label.setOpaque(true);
		sep.setOrientation(JSeparator.VERTICAL);
		sep.setLayout(new GridLayout(3, 1));
		sep.add(new JLabel());
		sep.add(label);
		sep.add(new JLabel());
		leftPanel.setLayout(new BorderLayout());
		leftPanel.setBorder(BorderFactory.createEmptyBorder(10, // top
				10, // left
				10, // bottom
				10)); // right
		leftPanel.add(sep, BorderLayout.CENTER);
		leftPanel.setPreferredSize(new Dimension(40, 220));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(leftPanel, BorderLayout.WEST);
		// frame.add(label);
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				WhyAmIUpSoLate nestedLayout = new WhyAmIUpSoLate();
			}
		});
	}
}