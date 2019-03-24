import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

public class GameFrame extends JFrame implements GameUI {

	private final int WINDOW_WIDTH = 1200;
	private final int WINDOW_HEIGHT = 1000;
	private final int CHAT_HEIGHT = 200;
	
	private ConnectionManager cm;
	private Thread waitingThread;
	private boolean tempDisable;
	
	private Game game;
	private GamePlayer me;
	
	private final String dir_images = "images/";
	
	private JMenuBar menuBar = new JMenuBar();
	private JMenu menu_test = new JMenu("Test");
	private JMenuItem menu_test_test1 = new JMenuItem("Test 1");
	
	private JButton button_host = new JButton("Host");
	private JLabel label_host = new JLabel("localhost");
	private JButton button_cancel = new JButton("Cancel");
	private JButton button_join = new JButton("Join");
	private JTextField textField_hostname = new JTextField("Chris-PC", 20);
	
	private GamePanel gamePanel = new GamePanel();
	private ChatPanel chatPanel = new ChatPanel();
	
	public GameFrame(String title) {
		//Preliminary Stuff
		super(title);
		setSize(WINDOW_WIDTH,WINDOW_HEIGHT);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
	    
	    //Set up network stuff
		cm = new ConnectionManager(this, ConnectionManager.DEFAULT_PORT);
		waitingThread = new Thread(cm);
		
		//Construct menu bar
		
		menuBar.add(menu_test);
		
		menu_test.add(menu_test_test1);
	    menu_test_test1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.CTRL_MASK));
	    
	    this.setJMenuBar(menuBar);
	    
	    //Set up listeners
	    
	    MenuListener ml = new MenuListener();
	    menu_test_test1.addActionListener(ml);
	    
	    ButtonListener bl = new ButtonListener();
		button_host.addActionListener(bl);
		button_join.addActionListener(bl);
	    
	    //Initiate components
	    label_host.setLabelFor(button_host);
	    button_cancel.setEnabled(false);
	    textField_hostname.setText(""+cm.localhost().getHostName());
		textField_hostname.setEditable(true);
		label_host.setText(cm.localhost().getHostName()+"/"+cm.localhost().getHostAddress());
		
		JPanel vertPanel = new JPanel();
	    vertPanel.setLayout(new BoxLayout(vertPanel,BoxLayout.Y_AXIS));
	    JPanel hostPanel = new JPanel();
	    hostPanel.add(button_host);
	    hostPanel.add(label_host);
	    JPanel joinPanel = new JPanel();
	    joinPanel.add(button_join);
	    joinPanel.add(textField_hostname);
	    
	    gamePanel.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT-CHAT_HEIGHT));
	    chatPanel.setPreferredSize(new Dimension(WINDOW_WIDTH, CHAT_HEIGHT));
	    
	    vertPanel.add(hostPanel);
	    vertPanel.add(joinPanel);
	    vertPanel.add(gamePanel);
	    vertPanel.add(chatPanel);
	    
	    this.add(vertPanel);
	    
	    this.setLocationRelativeTo(null);
		this.setVisible(true);
		
	}
	
	private class MenuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source==menu_test_test1) {

			}
			//updateAll();
		}
	}
	
	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e){
			Object source = e.getSource();
			if (source == button_host) {
				log("Hosting at "+cm.localhost());
				try {
					game = cm.host(new Player("Host"));
					startGame();
				} catch (IOException exception) {
					ConnectionManager.networkError("IOException while attempting to host: "+exception.getMessage());
				} catch (ClassNotFoundException exception) {
					ConnectionManager.networkError("ClassNotFoundException while attempting to host");
				}
			} else if (source == button_join) {
				log("Try to join hostname "+textField_hostname.getText());
				try {
					Player client = new Player("Client");
					game = cm.connectTo(textField_hostname.getText(), client);
					startGame();
				} catch (IOException exception) {
					ConnectionManager.networkError("IOException while attempting to join "+textField_hostname.getText()+": "+exception.getMessage());
				} catch (ClassNotFoundException exception) {
					ConnectionManager.networkError("ClassNotFoundException while attempting to join"+textField_hostname.getText());
				}
			}
			updateAll();
		}
	}
	
	private class ChatPanel extends JPanel {
		
		private JTextArea chat = new JTextArea();
		private JScrollPane chatScroll = new JScrollPane(chat);
		private JTextField textField_chat = new JTextField(15);
		private JButton button_send = new JButton("Send");
		
		public ChatPanel() {
			
			ChatListener listener = new ChatListener();
			
			chat.setEditable(false);
			chat.setLineWrap(true);
			button_send.addActionListener(listener);
			
			JPanel sendBar = new JPanel();
			sendBar.add(textField_chat);
			sendBar.add(button_send);
			
			this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
			this.add(chatScroll);
			this.add(sendBar);
			
			
		}
		
		public void append(String m) {
			chat.append(m+"\n");
		}
		
		private class ChatListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				Object source = e.getSource();
				if (source==button_send && !textField_chat.getText().equals("")) {
					if (game!=null) {
						try {
							cm.sendChatMessage(textField_chat.getText());
							textField_chat.setText("");
						} catch (IOException exception) {
							ConnectionManager.networkError("IOException while sending message");
						}
					}
				}
			}
		}
	}
	
	private class GamePanel extends JPanel {
		
		private Color BG_COLOR = Color.BLACK;
		
		public GamePanel() {
			super();
			setBackground(BG_COLOR);
			setLayout(null);
			
			this.addMouseListener(new MyMouseListener());	
			
		}
		
		private class MyMouseListener implements MouseListener {
			@Override
			public void mouseReleased(MouseEvent e) {
				
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				
			}
			@Override
			public void mouseEntered(MouseEvent e) {

				
			}
			@Override
			public void mouseExited(MouseEvent e) {

				
			}
			@Override
			public void mousePressed(MouseEvent e) {
				
			}
		}
		
	}
	
	public void updateAll() {
		
		repaint();
	}
	
	@Override
	public void processing() {
		tempDisable = true;
	}
	
	@Override
	public void doneProcessing() {
		tempDisable = false;
	}
	
	@Override
	public void chatMsg(int sourcePlayer, String m) {
		chatPanel.append(game.getPlayer(sourcePlayer).getName()+": "+m);
	}
	
	@Override
	public void actionMsg(int sourcePlayer, int par, String str) {
		game.getPlayer(sourcePlayer).doAction(par, str);
		game.update();
		updateAll();
	}
	
	private void startGame() {
		button_host.setEnabled(false);
		button_join.setEnabled(false);
		me = game.getPlayer(cm.getIndex());
		game.begin();
		waitingThread.start();
	}
	
	public static void log(String m) {
		System.out.println(m);
	}
	
}
