package admin.formlistener;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import custom.CustomComboBox;
import custom.PopupDialog;
import dbconnect.DBConnect;

public class CreateCourseFormListener implements ActionListener{
	private JPanel formPane;
	private JTextField courseNameTextField;
	private JFormattedTextField startTimeTextField;
	private JFormattedTextField endTimeTextField;
	private JComboBox<String> semesterComboBox;
	private CustomComboBox professorComboBox;
	private JComboBox<String> dayComboBox;
	private JComboBox<Integer> maxStudentsComboBox;
	private JComboBox<String> startTimeComboBox;
	private JComboBox<String> endTimeComboBox;
	private JTextArea descriptionTextArea;
	
	public CreateCourseFormListener(JPanel formPane, JTextField courseNameTextField, JFormattedTextField startTimeTextField, 
										JFormattedTextField endTimeTextField, JComboBox<String> semesterComboBox, CustomComboBox professorComboBox,
											JComboBox<String> dayComboBox, JComboBox<Integer> maxStudentsComboBox, JComboBox<String> startTimeComboBox,
												JComboBox<String> endTimeComboBox, JTextArea descriptionTextArea) {
		this.formPane = formPane;
		this.courseNameTextField = courseNameTextField;
		this.startTimeTextField = startTimeTextField;
		this.endTimeTextField = endTimeTextField;
		this.semesterComboBox = semesterComboBox;
		this.professorComboBox = professorComboBox;
		this.dayComboBox = dayComboBox;
		this.maxStudentsComboBox = maxStudentsComboBox;
		this.semesterComboBox = semesterComboBox;
		this.startTimeComboBox = startTimeComboBox;
		this.endTimeComboBox = endTimeComboBox;
		this.descriptionTextArea = descriptionTextArea;
	}
	
	
	/*
	 * Handle Button Click: 
	 *  - Add account info to DB
	 */
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(isFieldEmpty()) {
			showPopupMessage("One Or More fields Are Empty.", "Error!");
		}	
		else {
			if(!isTimeInputValid(startTimeTextField.getText().toCharArray())) {
				showPopupMessage("Invalid Course Start Time", "Error!");
				return;
			}
			if(!isTimeInputValid(endTimeTextField.getText().toCharArray())) {
				showPopupMessage("Invalid Course End Time", "Error!");
				return;
			}
			if(doesCourseAlreadyExist()) {
				showPopupMessage("Course Already Exists in Time Slot", "Error!");
				return;
			}
			else {
				Boolean queryIsSuccessful = addCourseToDB();
				showPopupMessage("Course Successfully Added.", "");
			}
		}
	}
	
	private boolean addCourseToDB() {
		String courseName = courseNameTextField.getText();
		String semester = semesterComboBox.getSelectedItem().toString();
		int professorId = professorComboBox.getSelectedItemId();
		String courseDay = dayComboBox.getSelectedItem().toString();
		String startTime = startTimeTextField.getText() + " " + startTimeComboBox.getSelectedItem().toString();
		String endTime = endTimeTextField.getText() + " " + endTimeComboBox.getSelectedItem().toString();
		String description = descriptionTextArea.getText();
		int maxStudents = Integer.parseInt(maxStudentsComboBox.getSelectedItem().toString());
		
		try {
			Connection connection = DBConnect.connection;
			String query = String.format("INSERT INTO courses VALUES (null, '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')", 
											courseName, semester, startTime, endTime, courseDay, description, maxStudents, professorId);
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
	
	/*
	 * Helper Methods
	 */
	
	private boolean doesCourseAlreadyExist() {
		String semester = (String) semesterComboBox.getSelectedItem();
		String startTimeString = startTimeTextField.getText() + " " + startTimeComboBox.getSelectedItem();
		String endTimeString = endTimeTextField.getText() + " " + endTimeComboBox.getSelectedItem();
		String day = (String) dayComboBox.getSelectedItem();
		String professorId = professorComboBox.getSelectedItemId()+"";
		
		LocalTime newStartTime = getLocalTimeObject(startTimeString);
		LocalTime newEndTime = getLocalTimeObject(endTimeString);
		
		// courseList contains the start_time and end_time of each course with the same semester, day, and professor
		// as the course we are trying to modify.
		// If there are no courses with the same semester and day, we don't have to check if there's a time conflict.
		// Otherwise check if the start/end time conflict with other courses.
		List<String[]> courseList = getCoursesAtTime(semester, day, professorId);
		
		for(String[] course : courseList) {
			LocalTime startTime = getLocalTimeObject(course[0]);
			LocalTime endTime = getLocalTimeObject(course[1]);
			
			// If the time slot is before or after the existing course time slot, return false. Otherwise return true since there's a conflict.
			if(newStartTime.isBefore(startTime) && newEndTime.isBefore(startTime)) {
				return false;
			}
			if(newStartTime.isAfter(endTime) && newEndTime.isAfter(endTime)) {
				return false;
			}
			return true;
		}
		
		// Return false if no courses were returned from the Database.
		return false;
	}
	
	// Retrieves all courses with the same semester, day, and professor as the one selected to modify
		// This is then used to check if there will be a time conflict when modifying the time.
		private List<String[]> getCoursesAtTime(String semester, String day, String professorId) {
			List<String[]> courseList = new ArrayList<>();
			
			try {
				Connection connection = DBConnect.connection;
				String query = String.format("SELECT start_time, end_time FROM courses WHERE course_semester = '%s' AND course_day = '%s' AND professor_id = '%s'", 
												semester, day, professorId);
				Statement stm = connection.createStatement();
				ResultSet resultSet = stm.executeQuery(query);
				
				while(resultSet.next()) {
					courseList.add(new String[]{resultSet.getString("start_time"), resultSet.getString("end_time")});
				}
				stm.close();
				return courseList;
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	
	private boolean isFieldEmpty() {
		Component[] components = formPane.getComponents();
		
		for(Component component : components) {
			if(component instanceof JTextField textField) {
				if(textField.getText().isBlank()) {
					return true;
				}
			}
			
			if(component instanceof JComboBox comboBox) {
				if(comboBox.getSelectedItem() == null) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean isTimeInputValid(char[] timeCharArray) {
		for(char digit : timeCharArray) {
			if(digit == '-') {
				return false;
			}
		}
		
		String dateString = String.valueOf(timeCharArray);
	
		try {
			DateFormat sdf = new SimpleDateFormat("hh:mm");
			sdf.setLenient(false);
			sdf.parse(dateString);
		}
		catch (ParseException e) {
			return false;
		}
		return true;
	}
	
	// Convert string time to a LocalTime object
	private LocalTime getLocalTimeObject(String stringDate) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
		return LocalTime.parse(stringDate, formatter);
	}
	
	private void showPopupMessage(String message, String title) {
		JFrame frame = (JFrame) formPane.getTopLevelAncestor();
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
