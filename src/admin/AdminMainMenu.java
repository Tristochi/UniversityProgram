package admin;

import java.awt.EventQueue;

import javax.swing.JFrame;
import GUILook.GUILookAndFeel;
import custom.ContextMenuListener;
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
					GUILookAndFeel.setLookAndFeel();
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
		createAccountForm.addMouseListener(new ContextMenuListener());
		jTabbedPane.addTab("Create Account", null, createAccountForm, null);
		
		CreateCourseForm createCourseForm = new CreateCourseForm();
		createCourseForm.addMouseListener(new ContextMenuListener());
		jTabbedPane.addTab("Create Course", null, createCourseForm, null);
		
		ModifyAccountForm modifyAccountForm = new ModifyAccountForm();
		modifyAccountForm.addMouseListener(new ContextMenuListener());
		jTabbedPane.addTab("Modify Account", null, modifyAccountForm, null);
		
		ModifyCourseForm modifyCourseForm = new ModifyCourseForm();
		modifyCourseForm.addMouseListener(new ContextMenuListener());
		jTabbedPane.addTab("Modify/Remove Course", null, modifyCourseForm, null);
		
		RemoveStudentCourseForm removeStudentCourseForm = new RemoveStudentCourseForm();
		removeStudentCourseForm.addMouseListener(new ContextMenuListener());
		jTabbedPane.addTab("Remove Student From A Course", null, removeStudentCourseForm, null);
		
		AppointmentForm appointmentForm = new AppointmentForm();
		appointmentForm.addMouseListener(new ContextMenuListener());
		jTabbedPane.addTab("Manage Appointements", null, appointmentForm, null);
		
		frmAdminView.pack();
	}
}
