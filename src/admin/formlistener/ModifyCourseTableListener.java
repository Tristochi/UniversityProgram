package admin.formlistener;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import custom.CustomComboBox;
import custom.CustomTableModel;
import dbconnect.DBConnect;

public class ModifyCourseTableListener implements TableModelListener {
	private CustomTableModel tableModel;
	private JTextField courseIdTextField;
	private JTextField courseNameTextField;
	private CustomComboBox professorComboBox;
	private JComboBox<String> semesterComboBox;
	private JComboBox<String> dayComboBox;
	private JTextField startTimeTextField;
	private JTextField endTimeTextField;
	private JComboBox<String> startTimeComboBox;
	private JComboBox<String> endTimeComboBox;
	private JTextArea descriptionTextArea;
	private JComboBox<Integer> maxStudentsComboBox;
	
	public ModifyCourseTableListener(Map<String, JComponent> componentMap, CustomTableModel tableModel) {
		this.tableModel = tableModel;
		this.courseIdTextField = (JTextField) componentMap.get("courseIdTextField");
		this.courseNameTextField = (JTextField) componentMap.get("courseNameTextField");
		this.professorComboBox = (CustomComboBox) componentMap.get("professorComboBox");
		this.semesterComboBox = (JComboBox<String>) componentMap.get("semesterComboBox");
		this.dayComboBox = (JComboBox<String>) componentMap.get("dayComboBox");
		this.startTimeTextField = (JTextField) componentMap.get("startTimeTextField");
		this.endTimeTextField = (JTextField) componentMap.get("endTimeTextField");
		this.startTimeComboBox = (JComboBox<String>) componentMap.get("startTimeComboBox");
		this.endTimeComboBox = (JComboBox<String>) componentMap.get("endTimeComboBox");
		this.descriptionTextArea = (JTextArea) componentMap.get("descriptionTextArea");
		this.maxStudentsComboBox = (JComboBox<Integer>) componentMap.get("maxStudentsComboBox");
	}
	
	@Override
	public void tableChanged(TableModelEvent e) {
		int rowIndex = tableModel.getSelectedRowIndex();
		if(rowIndex > -1) {
			String[] rowData = tableModel.getRowDataAtIndex(rowIndex);
			fillTextFields(rowData);
			fillComboBoxes();
			matchComboBoxWithSelection(rowData);
		}
	}
	
	private void fillTextFields(String[] rowData) {
		String id = rowData[0];
		String courseName = rowData[1];
		String startTime = rowData[3].split(" ")[0];
		String endTime = rowData[4].split(" ")[0];
		String description = rowData[6];
		
		
		courseIdTextField.setText(id);
		courseNameTextField.setText(courseName);
		startTimeTextField.setText(startTime);
		endTimeTextField.setText(endTime);
		descriptionTextArea.setText(description);
	}
	
	// fill combo boxes with data from database
	private void fillComboBoxes() {
		// clear combo boxes beforehand
		clearAllComboBoxes();
		
		// add professors
		List<String[]> professorList = getProfessors();
		for(String[] array : professorList) {
			professorComboBox.addCustomItem(Integer.parseInt(array[0]), array[1]);
		}
		
		// add semesters
		List<String> semesterList = getSemesters();
		for(String semester : semesterList) {
			semesterComboBox.addItem(semester);
		}
		
		// add AM/PM to time combo boxes
		startTimeComboBox.addItem("AM");
		startTimeComboBox.addItem("PM");
		endTimeComboBox.addItem("AM");
		endTimeComboBox.addItem("PM");
		
		// add days
		String[] days = {"Monday", "Tuesday", "Wenesday", "Thursday", "Friday", "Saturday", "Sunday"};
		for(String day : days) {
			dayComboBox.addItem(day);
		}
		
		// add max students
		for(int i = 0; i < 41; i++) {
			maxStudentsComboBox.addItem(i);
		}
	}
	
	// set the current value of each combo with the 
	// course information currently selected from the table.
	private void matchComboBoxWithSelection(String[] rowData) {
		String professorName = rowData[8];
		int professorId = getProfessorIDFromCourseID(courseIdTextField.getText());
		String semester = rowData[2];
		String startTimeAMPM = rowData[3].split(" ")[1];
		String endTimeAMPM = rowData[4].split(" ")[1];
		String day = rowData[5];
		int maxStudents = Integer.parseInt(rowData[7]);
		
		professorComboBox.setSelectedItem(professorId, professorName);
		semesterComboBox.setSelectedItem(semester);
		startTimeComboBox.setSelectedItem(startTimeAMPM);
		endTimeComboBox.setSelectedItem(endTimeAMPM);
		dayComboBox.setSelectedItem(day);
		maxStudentsComboBox.setSelectedItem(maxStudents);
	}
	
	/*
	 * Helper Methods
	 */
	
	private List<String[]> getProfessors() {
		List<String[]> professorList = new ArrayList();
		
		try {
			Connection connection = DBConnect.connection;
			String query = String.format("SELECT user_id, first_name, last_name FROM accounts, professors "
											+"WHERE accounts.user_id = professors.professor_id");
			Statement stm = connection.createStatement();
			ResultSet resultSet = stm.executeQuery(query);
			
			while(resultSet.next()) {
				String id = resultSet.getInt("user_id") + "";
				String fullName = resultSet.getString("first_name") + " " + resultSet.getString("last_name");
				professorList.add(new String[] {id, fullName});
			}
			stm.close();
			return professorList;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private int getProfessorIDFromCourseID(String courseId) {	
		try {
			Connection connection = DBConnect.connection;
			String query = String.format("SELECT professor_id FROM Courses WHERE course_id = '%s'", courseId);
			Statement stm = connection.createStatement();
			ResultSet resultSet = stm.executeQuery(query);
			
			while(resultSet.next()) {
				 return resultSet.getInt("professor_id");
			}
			return -1;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private List<String> getSemesters() {
		List<String> semesterList = new ArrayList<>();
		
		try {
			Connection connection = DBConnect.connection;
			String query = String.format("SELECT * FROM semesters");
			Statement stm = connection.createStatement();
			ResultSet resultSet = stm.executeQuery(query);
			
			while(resultSet.next()) {
				semesterList.add(resultSet.getString("semester"));
			}
			stm.close();
			return semesterList;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private void clearAllComboBoxes() {
		professorComboBox.removeAllItems();
		semesterComboBox.removeAllItems();
		startTimeComboBox.removeAllItems();
		endTimeComboBox.removeAllItems();
		dayComboBox.removeAllItems();
		maxStudentsComboBox.removeAllItems();
	}
	
	private boolean isComboBoxEmpty() {
		JComboBox[] comboBoxes = {semesterComboBox, professorComboBox, startTimeComboBox, endTimeComboBox, dayComboBox, maxStudentsComboBox};
		for(JComboBox comboBox : comboBoxes) {
			if(comboBox.getItemCount() < 1) {
				return true;
			}
		}
		return false;
	}
}
