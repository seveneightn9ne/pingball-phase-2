package client;

import ihavenoidea.RepaintThread;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import common.Constants;
import client.gadgets.Wall;

/**
 * How to draw the gadgets on the board? * Option 1: A drawGadget method that
 * uses instanceof and accesses data based on that Downsides: Requires
 * instanceof * Option 2: Gadgets have a drawSelf method Downsides: Bad
 * separation of model and view * Option 3: All gadgets have a corresponding
 * GadgetGUI class Downsides: Tons of new classes, Gadget and GadgetGUI don't
 * relate to each other in a meaningful way, The main GUI will have to know
 * which GadgetGUI to use, might require instanceof, Alternatively a Gadget can
 * know its GUI but once again, bad separation of model and view
 * 
 * 
 * Manual Testing Strategy: * New board from file * Try invalid filepath
 * (possible with open dialog?) * Try file that is not a board * Make sure
 * connecting to server is enabled * Maybe game should not start immediately. *
 * Connect to server * Some notification that it worked (in bottom area?) *
 * Disconnect becomes enabled, connect becomes disabled
 * 
 */
public class PingballGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final String CONNECTED_TEXT = "Connected to ";
	private static final String DISCONNECTED_TEXT = "Not connected to server.";
	private static final String WINDOW_TITLE = "Pingball";
	private static final Color DARK_GREEN = new Color(0, 150, 0);

	private final PingballClient client;
	private final JMenuItem pauseMI;
	private final JMenuItem resumeMI;
	private final JLabel serverStatus;
	private final JMenuItem connectMI;
	private final JMenuItem disconnectMI;
	private final BoardGUI boardPanel;

	/**
	 * Create a PingballGUI
	 * 
	 * @param client
	 *            the client which is running the Pingball game
	 * @param hostname
	 *            the name of the connected server, or null if there is no
	 *            server connection
	 * @param board
	 *            the connected board, or null if there is no board
	 */

	public PingballGUI(final PingballClient client, String hostname, final Board board) {

		this.client = client;

		// make the menu bar
		JMenuBar menuBar = new JMenuBar();
		JMenu gameMenu = new JMenu("Game");
		menuBar.add(gameMenu);

		JMenuItem newGameMI = new JMenuItem("New Game...");
		gameMenu.add(newGameMI);
		pauseMI = new JMenuItem("Pause");
		gameMenu.add(pauseMI);
		resumeMI = new JMenuItem("Resume");
		gameMenu.add(resumeMI);
		resumeMI.setEnabled(false);
		JMenuItem restartMI = new JMenuItem("Restart");
		gameMenu.add(restartMI);

		JMenu serverMenu = new JMenu("Server");
		menuBar.add(serverMenu);

		connectMI = new JMenuItem("Connect to Server...");
		serverMenu.add(connectMI);
		disconnectMI = new JMenuItem("Disconnect from Server");
		serverMenu.add(disconnectMI);

		if (board == null) {
			pauseMI.setEnabled(false);
			restartMI.setEnabled(false);
			connectMI.setEnabled(false);
			connectMI
					.setToolTipText("You must start a game before you can connect.");
		}
		boardPanel = new BoardGUI(board);
		

		// set JFrame details
		setJMenuBar(menuBar);
		setTitle(WINDOW_TITLE);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// The bottom status bar
		JPanel statusBar = new JPanel();
		serverStatus = new JLabel(DISCONNECTED_TEXT, SwingConstants.RIGHT);
		serverStatus.setFont(serverStatus.getFont().deriveFont(Font.PLAIN));
		serverStatus.setForeground(Color.darkGray);
		statusBar.add(serverStatus);
		statusBar.setAlignmentX(RIGHT_ALIGNMENT);
		statusBar.setLayout(new GridLayout(0, 1));

		if (hostname == null) {
			disconnectMI.setEnabled(false);
		} else {
			connectMI.setEnabled(false);
			serverStatus.setText(CONNECTED_TEXT + hostname);
			serverStatus.setForeground(DARK_GREEN);
		}
		// define layout
		GroupLayout layout = new GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(layout);
		// layout.setAutoCreateContainerGaps(true);
		// layout.setAutoCreateGaps(true);

		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addComponent(boardPanel, GroupLayout.PREFERRED_SIZE, 400+2*Constants.SCALE,
						GroupLayout.PREFERRED_SIZE)
				.addComponent(statusBar, GroupLayout.PREFERRED_SIZE, 20,
						GroupLayout.PREFERRED_SIZE));
		layout.setHorizontalGroup(layout
				.createParallelGroup(Alignment.CENTER)
				.addComponent(boardPanel, GroupLayout.PREFERRED_SIZE, 400+2*Constants.SCALE,
						GroupLayout.PREFERRED_SIZE).addComponent(statusBar));
		pack();
		// TODO: keyboard listener sends keys to board

		// menu event listeners
		newGameMI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				newGame();
			}
		});
		pauseMI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				pause();
			}
		});
		resumeMI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				resume();
			}
		});
		restartMI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				restart();
			}
		});
		connectMI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				connect();
			}
		});
		disconnectMI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				disconnect();
			}
		});

		KeyListener listener = new KeyAdapter() {
			public void keyPressed(final KeyEvent e) {
				client.invokeLater(new Runnable() {
					public void run() {
						board.notifyKeydown(KeyEvent.getKeyText(e.getKeyCode()));
						System.out.println(KeyEvent.getKeyText(e.getKeyCode()));
					}
				});
			}

			public void keyReleased(final KeyEvent e) {
				client.invokeLater(new Runnable() {
					public void run() {
						board.notifyKeyup(KeyEvent.getKeyText(e.getKeyCode()));
						System.out.println(KeyEvent.getKeyText(e.getKeyCode()));
					}
				});
			}
		};

		KeyListener magical = new MagicKeyListener(listener);

		this.addKeyListener(magical);
	}

	private void newGame() {
		pause();
		String boardsdir = System.getProperty("user.dir") + "/boards" /*
																	 * + System.
																	 * getProperty
																	 * (
																	 * "line.separator"
																	 * +)
																	 * "boards"
																	 */;
		// JFileChooser fileChooser = new JFileChooser();
		final JFileChooser fc = new JFileChooser(new File(boardsdir));
		int returnVal = fc.showOpenDialog(PingballGUI.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			final String filename = fc.getCurrentDirectory().toString() + "/"
					+ fc.getSelectedFile().getName();
			setTitle(WINDOW_TITLE + " - " + filename);
			client.invokeLater(new Runnable() {
				public void run() {
					client.setBoard(filename);
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							boardPanel.setBoard(client.getBoard());
							resume();
						}
					});
				}
			});
		}
	}

	private void pause() {
		client.invokeLater(new Runnable() {
			public void run() {
				client.pause();
			}
		});
		pauseMI.setEnabled(false);
		resumeMI.setEnabled(true);
	}

	private void resume() {
		client.invokeLater(new Runnable() {
			public void run() {
				client.resume();
			}
		});
		pauseMI.setEnabled(true);
		resumeMI.setEnabled(false);
	}

	private void restart() {
		client.invokeLater(new Runnable() {
			public void run() {
				client.restart();
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						boardPanel.setBoard(client.getBoard());
					}
				});
			}
		});
		// restart disconnects from server
		serverIsNotConnected("");
	}

	private void connect() {
		JTextField hostField = new JTextField(10);
		hostField.setText("localhost"); // default hostname
		JTextField portField = new JTextField(5);
		portField.setText("10987"); // default port

		JPanel myPanel = new JPanel();
		myPanel.add(new JLabel("Host name:"));
		myPanel.add(hostField);
		myPanel.add(Box.createHorizontalStrut(15)); // a spacer
		myPanel.add(new JLabel("Port:"));
		myPanel.add(portField);

		int result = JOptionPane.showConfirmDialog(null, myPanel,
				"Connect to Server...", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			final String hostname = hostField.getText();
			final int port = Integer.parseInt(portField.getText());

			serverStatus.setText("Connecting...");
			client.invokeLater(new Runnable() {
				public void run() {
					try {
						client.connectToServer(hostname, port);
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								serverIsConnected(hostname, port);
							}
						});
					} catch (IOException e) {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								serverIsNotConnected("Connection failed.");
							}
						});
					}
				}
			});
		}
	}

	private void disconnect() {
		client.invokeLater(new Runnable() {
			public void run() {
				client.disconnectFromServer();
			}
		});
		serverIsNotConnected("");
	}

	private void serverIsNotConnected(String details) { // TODO: show details on
														// status bar
		serverStatus.setText(DISCONNECTED_TEXT);
		serverStatus.setForeground(Color.darkGray);
		connectMI.setEnabled(true);
		disconnectMI.setEnabled(false);
	}

	private void serverIsConnected(String hostname, int port) {
		serverStatus.setText(CONNECTED_TEXT + hostname + ":" + port);
		serverStatus.setForeground(DARK_GREEN);
		connectMI.setEnabled(false);
		disconnectMI.setEnabled(true);
	}

}
