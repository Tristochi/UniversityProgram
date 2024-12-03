package admin.formlistener;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Statement;
import javax.swing.JFrame;
import javax.swing.JTextField;
import admin.ModifyCourseForm;
import custom.ConfirmDialog;
import custom.PopupDialog;
import dbconnect.DBConnect;

public class RemoveCourseButtonListener implements ActionListener {
	private ModifyCourseForm mainPane;
	private JTextField courseIdTextField;
	
	public RemoveCourseButtonListener(ModifyCourseForm mainPane, JTextField courseIdTextField) {
		this.mainPane = mainPane;
		this.courseIdTextField = courseIdTextField;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(courseIdTextField.getText().isBlank()) {
			showPopupMessage("No Course Selected.", "Error!");
			return;
		}
		boolean confirmation = getConfirmation("<HTML>Are You Sure You Want<br> To Delete This Course?</HTML>", "");
		if(confirmation) {
			boolean isQuerySuccessful = removeCourseFromDB();
			if(isQuerySuccessful) {
				showPopupMessage("Course Successfully Remove", "");
				mainPane.updateTableModel();
			}
		}
	}
	
	
	/*
	 * Helper Methods
	 */
	
	private boolean removeCourseFromDB() {
		String courseId = courseIdTextField.getText();
		try {
			Connection connection = DBConnect.connection;
			String query1 = String.format("DELETE FROM students_enrolled_in_courses WHERE course_id = '%s'", courseId);
			Statement stm = connection.createStatement();
			int rows = stm.executeUpdate(query1);
			
			String query2 = String.format("DELETE FROM course_requests WHERE course_id = '%s'", courseId);
			rows += stm.executeUpdate(query2);
			
			String query3 = String.format("DELETE FROM courses WHERE course_id = '%s'", courseId);
			rows += stm.executeUpdate(query3);
			
			if(rows > 0) {
				return true;
			}
			return false;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private void showPopupMessage(String message, String title) {
		JFrame frame = (JFrame) courseIdTextField.getTopLevelAncestor();
		Point location = new Point();
		int x = frame.getX() + (frame.getWidth() / 2);
		int y = frame.getY() + (frame.getHeight() / 2);
		location.setLocation(x, y);
		
		PopupDialog dialog = new PopupDialog(message);
		dialog.setLocation(location);
		dialog.pack();
		dialog.setTitle(title);
		dialog.setVisible(true);
	}
	
	private boolean getConfirmation(String message, String title) {
		JFrame frame = (JFrame) courseIdTextField.getTopLevelAncestor();
		Point location = new Point();
		int x = frame.getX() + (frame.getWidth() / 2);
		int y = frame.getY() + (frame.getHeight() / 2);
		location.setLocation(x, y);
		
		ConfirmDialog dialog = new ConfirmDialog(message);
		dialog.setLocation(location);
		dialog.pack();
		dialog.setTitle(title);
		dialog.setVisible(true);
		return dialog.getConfirmation();
	}
}
