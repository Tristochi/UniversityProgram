import java.awt.EventQueue;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

public class LoginScreen {

	private JFrame frame;
	private JTextField usernameTextField;
	private JTextField passwordTextField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginScreen window = new LoginScreen();
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
	public LoginScreen() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		createUserLabel();
		createUserTextField();
		createPasswordLabel();
		createPasswordTextField();
		createSubmitBtn();

	}
	
	public void createUserLabel() {
		JLabel UsernameLabel = new JLabel("Username:");
		UsernameLabel.setBounds(113, 33, 78, 16);
		frame.getContentPane().add(UsernameLabel);
	}
	
	public void createUserTextField() {
		usernameTextField = new JTextField();
		usernameTextField.setBounds(113, 55, 202, 26);
		frame.getContentPane().add(usernameTextField);
		usernameTextField.setColumns(10);
	}
	
	public void createPasswordLabel() {
		JLabel PasswordLabel = new JLabel("Password:");
		PasswordLabel.setBounds(113, 114, 103, 16);
		frame.getContentPane().add(PasswordLabel);
		
	}
	
	public void createPasswordTextField() {
		passwordTextField = new JTextField();
		passwordTextField.setBounds(113, 139, 202, 26);
		frame.getContentPane().add(passwordTextField);
		passwordTextField.setColumns(10);
	}
	
	public void createSubmitBtn() {
		JButton submitBtn = new JButton("Submit");
		submitBtn.setBounds(160, 185, 117, 29);
		frame.getContentPane().add(submitBtn);
		submitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				goToMainMenu();
			}
			
		});
	}
	
	public void goToMainMenu() {
		frame.dispose();
		MainMenu screen = new MainMenu();
		screen.initialize();
		screen.frame.setVisible(true);
		System.out.println("Display Main Menu");
	}
}
