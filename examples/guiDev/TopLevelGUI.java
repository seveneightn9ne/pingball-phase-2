package guiDev;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import client.gadgets.CircleBumper;

public class TopLevelGUI extends JFrame {
	public static void main(String argv[]) {
		TopLevelGUI gui = new TopLevelGUI();
//		SwingUtilities.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					TopLevelGUI gui = new TopLevelGUI();
//					gui.setVisible(true);
//
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//
//			}
//		});
	}

	public TopLevelGUI() {
		setTitle("myFrame Example");

		getContentPane().setLayout(new BorderLayout());

		setSize(500, 250);
		setVisible(true);
        
        
        GroupLayout layout = new GroupLayout(getContentPane());
        Container headerContainer = this.getContentPane();
        headerContainer.setLayout(layout);
		GUI boardGUI = new GUI(this);
		boardGUI.addGadget(new CircleBumper("meow", 10, 10));
		
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(boardGUI.returnJPANEL(), BorderLayout.CENTER);


		setContentPane(contentPane);
        pack();        		



	}
}
