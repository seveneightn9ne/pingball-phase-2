package server;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
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
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * ServerGUI is a Swing GUI for a Pingball server. 
 * It displays the connected users, the server's IP, 
 * and information about connected boards.
 * It also allows the user to connect boards. 
 * 
 * Thread Safety: The GUI is confined to the Swing thread. 
 * 
 * Manual Testing Strategy:
 * * Multiple boards connect to the server
 * * Try connecting them in different configurations
 * * Make sure a new connection overwrites an old one if there is a shared wall
 * * When the client disconnects, the GUI updates accordingly and doesn't crash
 * * Connecting boards via the CLI updates the GUI too
 *
 */
public class ServerGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private final DefaultTableModel tableModel;
	private final JTable clientsTable;
	private final DefaultListModel<String> hConnectedModel;
	private final DefaultListModel<String> vConnectedModel;
	private final BlockingQueue<String> commandQueue;
	private final JLabel serverStatus;

	/**
	 * Create a new Server GUI with the given server and its command queue
	 * @param server the server
	 * @param commandQueue the queue to which the GUI should send commands from the user
	 */
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
        
        // Server status. Get the public IP from checkip.amazonaws.com :)
        final int port = server.getPort();
        Thread backgroundThread = new Thread(new Runnable() {
        	public void run() {
        		try {
        	        URL ipFetcher = new URL("http://checkip.amazonaws.com");
        	        BufferedReader in = new BufferedReader(new InputStreamReader(ipFetcher.openStream()));
        			final String ip = in.readLine();
        			SwingUtilities.invokeLater(new Runnable() {
        				public void run() {
        					setIP(ip, port);
        				}
        			});
        		} catch (MalformedURLException e) {
        			e.printStackTrace();
        		} catch (IOException e) {
        			e.printStackTrace();
        		}
        	}
        });
        backgroundThread.start();
        
		
        serverStatus = new JLabel("");
        
        // Action buttons
        JButton hButton = new JButton("Connect Clients Horizontally");
        JButton vButton = new JButton("Connect Clients Vertically");
        
        // hConnect list
        hConnectedModel = new DefaultListModel<String>();
        vConnectedModel = new DefaultListModel<String>();
        final JList<String> hConnected = new JList<String>(hConnectedModel);
        final JList<String> vConnected = new JList<String>(vConnectedModel);
        
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
            	connectBoards("h");
            }
        });
        vButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
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
	
	/**
	 * Add a connection to the GUI so the user can see that two boards have been connected
	 * @param dir the direction of the connection. must be "h" or "v"
	 * @param b1 the name of the board on the top/left side of the connection
	 * @param b2 the name of the board on the bottom/right side of the connection
	 */
	public void addConnection(String dir, String b1, String b2) {
		if (dir.equals("h")) hConnectedModel.addElement(b1 + " :: " + b2);
		if (dir.equals("v")) vConnectedModel.addElement(b1 + " :: " + b2);
	}
	
	/**
	 * Remove a connection from the GUI, for example if a board disconnects
	 * from the server, then its connections have been broken and must be removed.
	 * @param dir the direction of the connection. must be "h" or "v"
	 * @param b1 the name of the board on the top/left side of the connection
	 * @param b2 the name of the board on the bottom/right side of the connection
	 */
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
	
	/**
	 * connectBoards asks the user to select two boards from the list of clients
	 * and then asks the server to connect those boards in the direction specified
	 * @param dir must be "h" or "v" to mean horizontal or vertical connection.
	 */
	private void connectBoards(String dir) {
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
	
	private void setIP(String ip, int port) {
		serverStatus.setText("Server running on " + ip + ":" + port);
	}
	

}
