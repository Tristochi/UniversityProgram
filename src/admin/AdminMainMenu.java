package admin;

import java.awt.EventQueue;

import javax.swing.JFrame;
import GUILook.GUILookAndFeel;
import dbconnect.DBConnect;
import javax.swing.JTabbedPane;
import javax.swing.BoxLayout;

public class AdminMainMenu{

	public JFrame frmAdminView;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		DBConnect.connect();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AdminMainMenu window = new AdminMainMenu();
					window.frmAdminView.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public AdminMainMenu() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	public void initialize() {
		frmAdminView = new JFrame();
		frmAdminView.setTitle("Admin View");
		frmAdminView.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmAdminView.getContentPane().setLayout(new BoxLayout(frmAdminView.getContentPane(), BoxLayout.X_AXIS));
		
		JTabbedPane jTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frmAdminView.getContentPane().add(jTabbedPane);
		
		CreateAccountForm createAccountForm = new CreateAccountForm();
		jTabbedPane.addTab("Create Account", null, createAccountForm, null);
		
		CreateCourseForm createCourseForm = new CreateCourseForm();
		jTabbedPane.addTab("Create Course", null, createCourseForm, null);
		
		ModifyAccountForm modifyAccountForm = new ModifyAccountForm();
		jTabbedPane.addTab("Modify Account", null, modifyAccountForm, null);
		
		ModifyCourseForm modifyCourseForm = new ModifyCourseForm();
		jTabbedPane.addTab("Modify/Remove Course", null, modifyCourseForm, null);
		
		RemoveStudentCourseForm removeStudentCourseForm = new RemoveStudentCourseForm();
		jTabbedPane.addTab("Remove Student From A Course", null, removeStudentCourseForm, null);
		
		frmAdminView.pack();
		GUILookAndFeel.setLookAndFeel();
	}

}
