package admin;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import custom.CustomTableModel;
import custom.RadioButtonEditor;
import custom.RadioButtonRenderer;
import dbconnect.DBConnect;

import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import GUILook.GUILookAndFeel;
import admin.formlistener.RemoveStudentCourseListener;
import admin.formlistener.StudentTableListener;
import javax.swing.JButton;

public class RemoveStudentCourseForm extends JPanel {

	private static final long serialVersionUID = 1L;
	private JPanel anchorPane;
	private JPanel studentPane;
	private JPanel coursePane;
	private CustomTableModel studentTableModel;
	private CustomTableModel courseTableModel;
	private JTable studentTable;
	private JTable courseTable;
	private JScrollPane studentViewPane;
	private JScrollPane courseViewPane;
	private ButtonGroup studentButtonGroup;
	private ButtonGroup courseButtonGroup;
	private JLabel studentLabel;
	private JButton removeStudentButton;

	// for testing
	public static void main(String[] args) {
		DBConnect.connect();
		JFrame frame = new JFrame();
		frame.setContentPane(new RemoveStudentCourseForm());
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
		GUILookAndFeel.setLookAndFeel();
	}
	
	/**
	 * Create the panel.
	 */
	public RemoveStudentCourseForm() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		gridBagLayout.columnWidths = new int[]{700, 0};
		gridBagLayout.rowHeights = new int[]{17, 250, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		anchorPane = new JPanel();
		GridBagConstraints gbc_anchorPane = new GridBagConstraints();
		gbc_anchorPane.fill = GridBagConstraints.BOTH;
		gbc_anchorPane.gridx = 0;
		gbc_anchorPane.gridy = 1;
		add(anchorPane, gbc_anchorPane);
		GridBagLayout gbl_anchorPane = new GridBagLayout();
		gbl_anchorPane.columnWidths = new int[]{700, 0};
		gbl_anchorPane.rowHeights = new int[]{200, 40, 200, 17, 0};
		gbl_anchorPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_anchorPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		anchorPane.setLayout(gbl_anchorPane);
		
		studentPane = new JPanel();
		GridBagConstraints gbc_studentPane = new GridBagConstraints();
		gbc_studentPane.insets = new Insets(0, 0, 5, 0);
		gbc_studentPane.fill = GridBagConstraints.BOTH;
		gbc_studentPane.gridx = 0;
		gbc_studentPane.gridy = 0;
		anchorPane.add(studentPane, gbc_studentPane);
		GridBagLayout gbl_studentPane = new GridBagLayout();
		gbl_studentPane.columnWidths = new int[]{421, 0};
		gbl_studentPane.rowHeights = new int[]{150, 0};
		gbl_studentPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_studentPane.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		studentPane.setLayout(gbl_studentPane);
		
		coursePane = new JPanel();
		GridBagConstraints gbc_coursePane = new GridBagConstraints();
		gbc_coursePane.insets = new Insets(0, 0, 5, 0);
		gbc_coursePane.fill = GridBagConstraints.BOTH;
		gbc_coursePane.gridx = 0;
		gbc_coursePane.gridy = 2;
		anchorPane.add(coursePane, gbc_coursePane);
		GridBagLayout gbl_coursePane = new GridBagLayout();
		gbl_coursePane.columnWidths = new int[]{421, 0};
		gbl_coursePane.rowHeights = new int[]{150, 0};
		gbl_coursePane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_coursePane.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		coursePane.setLayout(gbl_coursePane);
		
		
		
		createLabels();
		createStudentScrollPane();
		createCourseScrollPane();
		
		createRemoveButtonAndListener();
	}
	
	private void createLabels() {
		JLabel titleLabel = new JLabel("Remove Student From A Course");
		titleLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		GridBagConstraints gbc_titleLabel = new GridBagConstraints();
		gbc_titleLabel.insets = new Insets(0, 0, 5, 0);
		gbc_titleLabel.gridx = 0;
		gbc_titleLabel.gridy = 0;
		add(titleLabel, gbc_titleLabel);
		
		studentLabel = new JLabel("");
		studentLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		GridBagConstraints gbc_studentLabel = new GridBagConstraints();
		gbc_studentLabel.anchor = GridBagConstraints.CENTER;
		gbc_studentLabel.insets = new Insets(0, 0, 5, 0);
		gbc_studentLabel.gridx = 0;
		gbc_studentLabel.gridy = 1;
		anchorPane.add(studentLabel, gbc_studentLabel);
	}

	private void createStudentScrollPane() {
		String[] columnNames = {"Select Student", "Student ID", "First Name", "Last Name"};
		String[][] rowData = getStudentInfo();
		
		studentTableModel = new CustomTableModel(rowData, columnNames);
		StudentTableListener listener = new StudentTableListener(this, studentTableModel);
		studentTableModel.addTableModelListener(listener);
		
		studentButtonGroup = new ButtonGroup();
		
		// add radio buttons in the first column
		for(int i = 0; i < studentTableModel.getRowCount(); i++) {
			JRadioButton radioButton = new JRadioButton();
			studentButtonGroup.add(radioButton);
			studentTableModel.setValueAt(radioButton, i, 0);
		}
		
		
		studentTable = new JTable(studentTableModel);
		studentTable.setPreferredScrollableViewportSize(new Dimension(0, 0));
		studentTable.setFont(new Font("Tahoma", Font.PLAIN, 15));
		studentTable.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));
		studentTable.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		studentTable.setRowHeight(studentTable.getRowHeight() + 8);
		
		studentTable.getColumn("Select Student").setCellEditor(new RadioButtonEditor(new JCheckBox()));
		studentTable.getColumn("Select Student").setCellRenderer(new RadioButtonRenderer());
		studentTable.getColumn("Select Student").setMinWidth(140);
		studentTable.getColumn("Select Student").setMaxWidth(140);
		
		studentViewPane = new JScrollPane(studentTable);
		GridBagConstraints gbc_studentViewPane = new GridBagConstraints();
		gbc_studentViewPane.insets = new Insets(0, 0, 5, 0);
		gbc_studentViewPane.fill = GridBagConstraints.BOTH;
		gbc_studentViewPane.gridx = 0;
		gbc_studentViewPane.gridy = 0;
		studentPane.add(studentViewPane, gbc_studentViewPane);
	}
	
	private void createCourseScrollPane() {
		String[] columnNames = {"Select Course", "Course ID", "Course Name", "Course Semester", "Course Day", "Start Time", "End Time", "Professor"};
		
		courseTableModel = new CustomTableModel(new String[0][0], columnNames);
		// add table listener
		
		
		courseTable = new JTable(courseTableModel);
		courseTable.setEnabled(false); //set disabled since a student has not been selected yet.
		courseTable.setPreferredScrollableViewportSize(new Dimension(0, 0));
		courseTable.setFont(new Font("Tahoma", Font.PLAIN, 15));
		courseTable.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));
		courseTable.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		courseTable.setRowHeight(courseTable.getRowHeight() + 8);
		
		courseViewPane = new JScrollPane(courseTable);
		GridBagConstraints gbc_courseViewPane = new GridBagConstraints();
		gbc_courseViewPane.insets = new Insets(0, 0, 5, 0);
		gbc_courseViewPane.fill = GridBagConstraints.BOTH;
		gbc_courseViewPane.gridx = 0;
		gbc_courseViewPane.gridy = 0;
		coursePane.add(courseViewPane, gbc_courseViewPane);
	}
	
	private void createRemoveButtonAndListener() {
		removeStudentButton = new JButton("Remove Student From Course");
		removeStudentButton.setFont(new Font("Tahoma", Font.BOLD, 13));
		GridBagConstraints gbc_removeStudentButton = new GridBagConstraints();
		gbc_removeStudentButton.anchor = GridBagConstraints.EAST;
		gbc_removeStudentButton.insets = new Insets(0, 0, 5, 0);
		gbc_removeStudentButton.gridx = 0;
		gbc_removeStudentButton.gridy = 3;
		anchorPane.add(removeStudentButton, gbc_removeStudentButton);
		JPanel buttonPane = new JPanel();
		GridBagConstraints gbc_buttonPane = new GridBagConstraints();
		gbc_buttonPane.fill = GridBagConstraints.BOTH;
		gbc_buttonPane.gridx = 0;
		gbc_buttonPane.gridy = 4;
		anchorPane.add(buttonPane, gbc_buttonPane);
		
		// Add listener
		RemoveStudentCourseListener listener = new RemoveStudentCourseListener(this, studentTableModel, courseTableModel);
		removeStudentButton.addActionListener(listener);
	}
	
	
	
	/*
	 * Helper Methods
	 */
	
	private String[][] getStudentInfo() {
		List<String[]> studentInfo = new ArrayList<>();
		
		try {
			Connection connection = DBConnect.connection;
			String query = String.format("SELECT * FROM students");
			Statement stm = connection.createStatement();
			ResultSet resultSet = stm.executeQuery(query);
			
			while(resultSet.next()) {
				studentInfo.add(new String[]{"", resultSet.getString("student_id"), resultSet.getString("first_name"), resultSet.getString("last_name")});
			}
			stm.close();
			return studentInfo.toArray(new String[0][0]);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private String[][] getCourseInfo(String studentId) {
		List<String[]> courseInfo = new ArrayList<>();
		
		try {
			// Get the course info and the professors full name by joining 3 tables and searching by the student_id.
			Connection connection = DBConnect.connection;
			String query = String.format("SELECT courses.course_id, course_name, course_semester, course_day, start_time, end_time, first_name, last_name FROM courses " + 
											"JOIN students_enrolled_in_courses ON courses.course_id = students_enrolled_in_courses.course_id " + 
											"JOIN professors ON courses.professor_id = professors.professor_id " + 
											"WHERE student_id = '%s'", studentId);
			Statement stm = connection.createStatement();
			ResultSet resultSet = stm.executeQuery(query);
			
			while(resultSet.next()) {
				String courseId = resultSet.getString("courses.course_id");
				String courseName = resultSet.getString("course_name");
				String semester = resultSet.getString("course_semester");
				String startTime = resultSet.getString("start_time");
				String endTime = resultSet.getString("end_time");
				String professor = resultSet.getString("first_name") + " " + resultSet.getString("last_name");
				courseInfo.add(new String[]{"", courseId, courseName, semester, startTime, endTime, professor});
			}
			
			return courseInfo.toArray(new String[0][0]);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * Public Methods
	 */
	
	public void updateCourseInfoPane(String studentId, String studentName) {
		String[] columnNames = {"Select Course", "Course ID", "Course Name", "Course Semester", "Course Day", "Start Time", "End Time", "Professor"};
		String[][] rowData = getCourseInfo(studentId);
		
		courseTableModel.setDataVector(rowData, columnNames);
		courseButtonGroup = new ButtonGroup();
		
		// add radio buttons in the first column
		for(int i = 0; i < courseTableModel.getRowCount(); i++) {
			JRadioButton radioButton = new JRadioButton();
			courseButtonGroup.add(radioButton);
			courseTableModel.setValueAt(radioButton, i, 0);
		}
		
		String labelText = String.format("Showing Courses For: %s (ID: %s)", studentName, studentId);
		studentLabel.setText(labelText);
		
		courseTable.setEnabled(true);
		courseTable.getColumn("Select Course").setCellEditor(new RadioButtonEditor(new JCheckBox()));
		courseTable.getColumn("Select Course").setCellRenderer(new RadioButtonRenderer());
		
		courseTableModel.fireTableDataChanged();
	}
}










