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
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
//import admin.ProfessorData;
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
	
	@Override
	public void actionPerformed(ActionEvent e) {
		handleButtonClick();
	}
	
	/*
	 * Handle Button Click: 
	 *  - Add account info to DB
	 */
	
	private void handleButtonClick() {
		if(isFieldEmpty()) {
			showPopupMessage("One or more fields are empty.");
		}	
		else {
			if(!isTimeInputValid(startTimeTextField.getText().toCharArray())) {
				showPopupMessage("Invalid course start time");
				return;
			}
			if(!isTimeInputValid(endTimeTextField.getText().toCharArray())) {
				showPopupMessage("Invalid course end time");
				return;
			}
			
			// TODO finish methods to check if there's a conflicting course or it already exists.
			/*
			 * if(doesCourseExistInTimeSlot()) {
			 * 
			 * } if(!isCourseUnique()) { showPopupMessage("Course already exists!"); }
			 */
			else {
				Boolean queryIsSuccessful = addCourseToDB();
				showPopupMessage("Course successfully added.");
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
		
		// TODO change start_time and end_time in database (only using start time right now).
		
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
	
	private boolean doesCourseExistInTimeSlot() {
		String semester = semesterComboBox.getSelectedItem().toString();
		String courseDay = dayComboBox.getSelectedItem().toString();
		String startTime = startTimeTextField.getText() + " " + startTimeComboBox.getSelectedItem().toString();
		String endTime = endTimeTextField.getText() + " " + endTimeComboBox.getSelectedItem().toString();
		
		try {
			Connection connection = DBConnect.connection;
			String query = String.format("SELECT * FROM courses");
			Statement stm = connection.createStatement();
			ResultSet resultSet = stm.executeQuery(query);
			
			while(resultSet.next()) {
				String semesterFromDB = resultSet.getString("course_semester");
				String courseDayFromDB = resultSet.getString("course_day");
				String startTimeFromDB = resultSet.getString("start_time");
				String endTimeFromDB = resultSet.getString("end_time");	
				
				if(semester.equals(semesterFromDB) && courseDay.equals(courseDayFromDB)) {
					
				}
			}
			return false;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private boolean isCourseUnique() {
		String courseName = courseNameTextField.getText().toLowerCase();
		String professor = professorComboBox.getSelectedItem().toString().toLowerCase();
		
		try {
			Connection connection = DBConnect.connection;
			String query = String.format("SELECT * FROM courses");
			Statement stm = connection.createStatement();
			ResultSet resultSet = stm.executeQuery(query);
			
			while(resultSet.next()) {
				String courseNameFromDB = resultSet.getString("course_name").toLowerCase();
				String professorFromDB = getProfessorFullName(resultSet.getInt("professor_id")).toLowerCase();
				
				if(courseName.equals(courseNameFromDB) && professor.equals(professorFromDB)) {
					return false;
				}
			}
			return true;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private String getProfessorFullName(int professorID) {
		try {
			Connection connection = DBConnect.connection;
			String query = String.format("SELECT * FROM professors WHERE professor_id = '&s'", professorID);
			Statement stm = connection.createStatement();
			ResultSet resultSet = stm.executeQuery(query);
			
			while(resultSet.next()) {
				return resultSet.getString("first_name") + " " + resultSet.getString("last_name");
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
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
	
	private void showPopupMessage(String message) {
		JFrame frame = (JFrame) formPane.getTopLevelAncestor();
		Point location = new Point();
		int x = frame.getX() + (frame.getWidth() / 2);
		int y = frame.getY() + (frame.getHeight() / 2);
		location.setLocation(x, y);
		
		PopupDialog dialog = new PopupDialog(message);
		dialog.setLocation(location);
		dialog.pack();
		dialog.setTitle("Error!");
		dialog.setVisible(true);
	}
	
}
