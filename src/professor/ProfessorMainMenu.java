package professor;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

public class ProfessorMainMenu {
	private String username;
	public JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ProfessorMainMenu window = new ProfessorMainMenu("");
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
	public ProfessorMainMenu(String username) {
		this.username = username;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	public void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 700, 450);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		ProfessorCourseView courseView = new ProfessorCourseView(this.getCurrentUser());
		tabbedPane.addTab("Course Overview", null, courseView, null);
		
		ProfessorCourseGrades gradeView = new ProfessorCourseGrades(this.getCurrentUser());
		tabbedPane.addTab("Grades", null, gradeView, null);
	}
	
	public String getCurrentUser() {
		return this.username;
	}

}
