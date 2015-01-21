package zeromq;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTextArea;
import javax.swing.AbstractAction;
import javax.swing.Action;


public class ClientForm {

	private JFrame frame;
	private JTextField textField_1;
	public static JTextArea textArea;
	private final Action action = new SwingAction();
	public static String str;
	public static Client client;
	public static Boolean isConect = false;
	public static JButton btnNewButton;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientForm window = new ClientForm();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ClientForm() {
		initialize();
	}

	public void printHelp(){
		textArea.setText(
				"use:\n"
				+ "For see this msg, type @help\n"
				+ "For connetion to server, enter your name between 4 and 10 characters\n"
				+ "For exit from the server, enter @exit\n"
				+ "For clean screen, enter @clean\n"				
				);
	}
	
	public void handler() {
		str = textField_1.getText();

		if (str.equals("@clean")) {
			textArea.setText("");
			textField_1.setText("");
			return;
		} else if (str.equals("@help")) {
			printHelp();
		}

		if (!isConect) {
			client = new Client();
			if ((textField_1.getText().length() < 4)
					|| (textField_1.getText().length() > 10)) {
				textField_1.setText("Enter your name between 4 and 10 characters");
				textField_1.setForeground(Color.red);
				return;
			}
			client.start();
			isConect = true;
			btnNewButton.setText("Send");
		} else {
			if (textField_1.getText().toString().equals("@exit")) {
				client.sender.sendExit();
				client.die();
				client.interrupt();
				textField_1.setText("");
				isConect = false;
				return;
			} else {
				toString();
				if(String.valueOf(str.charAt(0)).equals("@")){
					textField_1.setText("");
				}
			}
			client.sender.send();
		}
		textField_1.setText("");
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		textField_1 = new JTextField();
		textField_1.setBounds(10, 223, 316, 39);
		textField_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				handler();
			}
		});

		textField_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				textField_1.setText("");
				textField_1.setForeground(Color.black);
			}
		});

		frame.getContentPane().add(textField_1);
		textField_1.setColumns(10);
		textField_1.setText("Enter your name between 4 and 10 characters");
		textField_1.setForeground(Color.red);
		
		btnNewButton = new JButton("");
		btnNewButton.setAction(action);
		btnNewButton.setText("Connect");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handler();
			}
		});
		btnNewButton.setBounds(338, 223, 96, 39);
		frame.getContentPane().add(btnNewButton);

		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setBounds(10, 12, 424, 202);
		frame.getContentPane().add(textArea);
		printHelp();
	}

	@SuppressWarnings("serial")
	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "SwingAction");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}

		public void actionPerformed(ActionEvent e) {
		}
	}
}
