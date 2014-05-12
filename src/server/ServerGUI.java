package server;

import java.util.concurrent.BlockingQueue;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


public class ServerGUI extends JFrame {

	public ServerGUI(PingballServer server, BlockingQueue<String> commandQueue) {
		
		// table of connected clients
		String[] cols = {"Board Name", "IP Address"};
        DefaultTableModel tableModel = new DefaultTableModel(cols, 0);
        JTable clientsTable = new JTable(tableModel){
            private static final long serialVersionUID = 1L;

            @Override public boolean isCellEditable(int arg0, int arg1) { 
                return false; 
            }
        }; 
        
        // Server status
        JLabel serverStatus = new JLabel("Server running on host:port");
        
        // Action buttons
        JButton hButton = new JButton("Connect Clients Horizontally");
        JButton vButton = new JButton("Connect Clients Vertically");
        
     // define layout
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setVerticalGroup(layout.createSequentialGroup()
        		.addComponent(serverStatus, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
        		.addComponent(clientsTable)
        		.addGroup(layout.createParallelGroup()
        				.addComponent(hButton)
        				.addComponent(vButton))
        );
        layout.setHorizontalGroup(layout.createParallelGroup()
                .addComponent(serverStatus)
                .addComponent(clientsTable)
                .addGroup(layout.createSequentialGroup()
                		.addComponent(hButton)
                		.addComponent(vButton))
         );
        pack();
        

		// set JFrame details
		setTitle("Pingball Server");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // tell server about gui
        server.notifyGUI(this);

	}
	

}
