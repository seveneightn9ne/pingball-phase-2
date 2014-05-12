package server;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;


public class ServerGUI extends JFrame {
	
	private final DefaultTableModel tableModel;
	private final JTable clientsTable;
//	private final JList<String> hConnected;
//	private final JList<String> vConnected;
	private final DefaultListModel<String> hConnectedModel;
	private final DefaultListModel<String> vConnectedModel;
//	private final DefaultTableModel hTableModel;
//	private final JTable hTable;
//	private final DefaultTableModel vTableModel;
//	private final JTable vTable;
	private final BlockingQueue<String> commandQueue;

	public ServerGUI(PingballServer server, BlockingQueue<String> commandQueue) {
		this.commandQueue = commandQueue;
		
		// table of connected clients
		String[] cols = {"Board Name", "IP Address"};
        tableModel = new DefaultTableModel(cols, 0);
        clientsTable = new JTable(tableModel){
            private static final long serialVersionUID = 1L;

            @Override public boolean isCellEditable(int arg0, int arg1) { 
                return false; 
            }
        }; 
        JScrollPane clientsPane = new JScrollPane(clientsTable);
        clientsPane.setPreferredSize(new Dimension((int) clientsTable.getSize().getWidth(), 150));
        
        // Server status
        JLabel serverStatus = new JLabel("Server running on host:port");
        
        // Action buttons
        JButton hButton = new JButton("Connect Clients Horizontally");
        JButton vButton = new JButton("Connect Clients Vertically");
        
        // Connected Boards:
        JLabel connectedBoards = new JLabel("Connected Boards:");
        
        // hConnect list
        hConnectedModel = new DefaultListModel<String>();
        vConnectedModel = new DefaultListModel<String>();
        final JList<String> hConnected = new JList<String>(hConnectedModel);
        final JList<String> vConnected = new JList<String>(vConnectedModel);
        
//        // hConnect table
// 		String[] hCols = {"left", "right"};
//        hTableModel = new DefaultTableModel(cols, 0);
//        hTable = new JTable(hTableModel){
//             private static final long serialVersionUID = 1L;
//
//             @Override public boolean isCellEditable(int arg0, int arg1) { 
//                 return false; 
//             }
//         }; 
//         JScrollPane hPane = new JScrollPane(hTable);
//         hTable.setPreferredSize(new Dimension((int) hTable.getSize().getWidth()/2, 70));
////         clientsPane.setPreferredSize(new Dimension((int) hTable.getSize().getWidth(), 70));
//
//         // vConnect table
//  		String[] vCols = {"top", "bottom"};
//         vTableModel = new DefaultTableModel(cols, 0);
//         vTable = new JTable(vTableModel){
//              private static final long serialVersionUID = 1L;
//
//              @Override public boolean isCellEditable(int arg0, int arg1) { 
//                  return false; 
//              }
//          }; 
//          JScrollPane vPane = new JScrollPane(vTable);
////          vTable.setPreferredSize(new Dimension((int) vTable.getSize().getWidth()/2, 70));
//          vTable.setSize((int) vTable.getSize().getWidth()/2, 70);
////          clientsPane.setPreferredSize(new Dimension((int) vTable.getSize().getWidth(), 70));
        
        // define layout
        GroupLayout layout = new GroupLayout(this.getContentPane());

        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);
        this.getContentPane().setLayout(layout);
        layout.setVerticalGroup(layout.createSequentialGroup()
        		.addComponent(serverStatus, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
        		.addComponent(clientsPane)
        		.addGroup(layout.createParallelGroup()
        				.addGroup(layout.createSequentialGroup()
        						.addComponent(hButton)
        						.addComponent(hConnected))
        				.addGroup(layout.createSequentialGroup()
        						.addComponent(vButton)
        						.addComponent(vConnected))
        ));
        layout.setHorizontalGroup(layout.createParallelGroup()
                .addComponent(serverStatus)
                .addComponent(clientsPane)
                .addGroup(layout.createSequentialGroup()
                		.addGroup(layout.createParallelGroup()
                				.addComponent(hButton)
                				.addComponent(hConnected))
                		.addGroup(layout.createParallelGroup()
                				.addComponent(vButton)
                				.addComponent(vConnected))
         ));
        pack();
        

        hButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
//            	int[] indices = hConnected.getSelectedIndices();
//            	if (indices.length == 2) {
//            		String b1 = hConnectedModel.getElementAt(indices[0]);
//            		String b2 = hConnectedModel.getElementAt(indices[1]);
//                    connectBoards("h", b1, b2);
//            	}
            	connectBoards("h");
            }
        });
        vButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
//            	int[] indices = vConnected.getSelectedIndices();
//            	if (indices.length == 2) {
//            		String b1 = vConnectedModel.getElementAt(indices[0]);
//            		String b2 = vConnectedModel.getElementAt(indices[1]);
//                    connectBoards("v", b1, b2);
//            	}
            	connectBoards("v");
            }
        });
        

		// set JFrame details
		setTitle("Pingball Server");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // tell server about gui
        server.notifyGUI(this);

	}
	
	/**
	 * add a Client to the GUI view. 
	 * @param name the client's board name
	 * @param ip the client's IP address
	 */
	public void addClient(String name, String ip) {
		tableModel.addRow(new String[] {name, ip});
        clientsTable.revalidate();
        pack();
	}
	
	/**
	 * Remove the client with the given board name from the GUI view
	 * @param name the board name of the client to remove
	 */
	public void removeClient(String name) {
		int rowToRemove = -1;
		for (int i=0; i<tableModel.getRowCount(); i++) {
			if (tableModel.getValueAt(i, 0).equals(name)) {
				rowToRemove = i;
				break;
			}
		}
		if (rowToRemove != -1) { // row found in client table
			tableModel.removeRow(rowToRemove);
		}
		clientsTable.revalidate();
		pack();
	}
	
	public void addConnection(String dir, String b1, String b2) {
		if (dir.equals("h")) hConnectedModel.addElement(b1 + " :: " + b2);
		if (dir.equals("v")) vConnectedModel.addElement(b1 + " :: " + b2);
	}
	
	public void removeConnection(String dir, String b1, String b2) {
		DefaultListModel<String> m;
		if (dir.equals("h")) m = hConnectedModel;
		else m = vConnectedModel;
		
		for (int i=0; i<m.getSize(); i++) {
			if (m.get(i).equals(b1 + " :: " + b2)) {
				m.remove(i);
				return;
			}
		}
	}
	
	public void connectBoards(String dir) {
		String[] boards = new String[clientsTable.getRowCount()];
		for (int i=0; i<clientsTable.getRowCount(); i++) {
			boards[i] = (String) clientsTable.getValueAt(i, 0);
		}
		JComboBox<String> boardsBox1 = new JComboBox<String>(boards);
		JComboBox<String> boardsBox2 = new JComboBox<String>(boards);
		

	    JPanel myPanel = new JPanel();
	    myPanel.add(new JLabel("First board:"));
	    myPanel.add(boardsBox1);
	    myPanel.add(Box.createHorizontalStrut(15)); // a spacer
	    myPanel.add(new JLabel("Second board:"));
	    myPanel.add(boardsBox2);
	
	    int result = JOptionPane.showConfirmDialog(null, myPanel, 
	           "Connect Boards...", JOptionPane.OK_CANCEL_OPTION);
	    if (result == JOptionPane.OK_OPTION) {
	    	String b1 = (String) boardsBox1.getSelectedItem();
	    	String b2 = (String) boardsBox2.getSelectedItem();
	    	commandQueue.add(dir + " " + b1 + " " + b2);
	    }
	}
	
//	public void removeBoardConnect(String b1, String b2) {
//		List<DefaultListModel<String>> models = Arrays.asList(hConnectedModel, vConnectedModel);
//		for (DefaultListModel<String> model : models) {
//			List<Integer> rowsToRemove = new ArrayList<Integer>();
//			for (int i=0; i<model.getSize(); i++) {
//				if (model.get(i).equals(b1 + " :: " + b2)){
//					rowsToRemove.add(i);
//				}
//			}
//			for (int i : rowsToRemove) {
//				model.remove(i);
//			}
//		}
//	}

}
