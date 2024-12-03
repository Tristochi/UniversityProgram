package admin.formlistener;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JFrame;

import admin.RemoveStudentCourseForm;
import custom.CustomTableModel;
import custom.PopupDialog;
import dbconnect.DBConnect;

public class RemoveStudentCourseListener implements ActionListener {
	private RemoveStudentCourseForm mainPane;
	private CustomTableModel studentTableModel;
	private CustomTableModel courseTableModel;
	
	public RemoveStudentCourseListener(RemoveStudentCourseForm mainPane, CustomTableModel studentTableModel, CustomTableModel courseTableModel) {
		this.mainPane = mainPane;
		this.studentTableModel = studentTableModel;
		this.courseTableModel = courseTableModel;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		int courseTableIndex = courseTableModel.getSelectedRowIndex();
		int studentTableIndex = studentTableModel.getSelectedRowIndex();
		if(studentTableIndex < 0) {
			showPopupMessage("No Student Selected", "Error!");
		}
		if(courseTableIndex < 0) {
			showPopupMessage("No Course Selected", "Error!");
		}
		if(courseTableIndex > -1 && studentTableIndex > -1) {
			String courseId = courseTableModel.getRowDataAtIndex(courseTableIndex)[0];
			String[] studentInfo = studentTableModel.getRowDataAtIndex(studentTableIndex);
			String studentId = studentInfo[0];
			String studentName = studentInfo[1] + " " + studentInfo[2];
			removeStudentFromCourse(courseId, studentId);
			mainPane.updateCourseInfoPane(studentId, studentName);
		}
	}
	
	/*
	 * Helper Methods
	 */
	
	private boolean removeStudentFromCourse(String courseId, String studentId) {
		try {
			Connection connection = DBConnect.connection;
			String query = String.format("DELETE FROM students_enrolled_in_courses WHERE course_id = '%s' AND student_id = '%s'", courseId, studentId);
			Statement stm = connection.createStatement();
			int rows = stm.executeUpdate(query);
			
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
		JFrame frame = (JFrame) mainPane.getTopLevelAncestor();
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
}
