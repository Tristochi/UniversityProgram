package professor;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;

import custom.CustomTableModel;
import dbconnect.DBConnect;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.sql.*;
import java.util.ArrayList;

public class ProfessorCourseGrades extends JPanel {

	private static final long serialVersionUID = 1L;
	private String username;
	private JTable gradeTable;
	private JTextField textField;
	private GroupLayout groupLayout;
	private JButton submitBtn;
	private JLabel courseLabel;
	private JComboBox courseBox;
	private JScrollPane scrollPane;
	private JLabel gradeLabel;
	private String currentCourseID;
	private ArrayList<ArrayList<String>> courseList;
	
	
	/**
	 * Create the panel.
	 */
	public ProfessorCourseGrades(String username) {
		this.username=username;
		initialize();
	}
	
	private void initialize() {
		scrollPane = new JScrollPane();
		courseList = setCourseList();
		ArrayList<String> courseData = new ArrayList<>();
		for(ArrayList<String> course : courseList) {
			courseData.add(course.get(1));
		}
		
		courseBox = new JComboBox(courseData.toArray(new String[0]));
		courseLabel = new JLabel("Course Select");
		textField = new JTextField();
		textField.setColumns(10);
		gradeLabel = new JLabel("Final Grade");
		submitBtn = new JButton("Submit");
		groupLayout = new GroupLayout(this);
		currentCourseID = "1";
		createGroupLayout(scrollPane, courseBox, courseLabel, gradeLabel, submitBtn, groupLayout);
		
		String[] columnNames = {"Course ID", "Student Name", "Course Grade"};
		String[][] rowData = getStudentGrades(currentCourseID);
		CustomTableModel tableModel = new CustomTableModel(rowData, columnNames);
		gradeTable = new JTable(tableModel);
		scrollPane.setViewportView(gradeTable);
		setLayout(groupLayout);
	}
	
	
	private ArrayList<ArrayList<String>> setCourseList() {
		ArrayList<ArrayList<String>> courseList = new ArrayList<>();
		try {
			Connection connection = DBConnect.connection;
			
			//Retriever current UserID given Username
			String query = "SELECT * FROM accounts WHERE username='" + username + "'";
			Statement stm = connection.createStatement();
			ResultSet result = stm.executeQuery(query);
			result.first();
			String userID = result.getString("user_id");
			
			query = "SELECT * from Courses WHERE professor_id="+userID;
			stm = connection.createStatement();
			result = stm.executeQuery(query);
			
			while(result.next()) {
				String theCourseID = result.getString("course_id");
				String courseName = result.getString("course_name");
				
				ArrayList<String> tmp = new ArrayList<>();
				tmp.add(theCourseID);
				tmp.add(courseName);
				courseList.add(tmp);
			}
			
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
		return courseList;
		
	}
	
	private String[][] getStudentGrades(String courseID){
		ArrayList<String[]> studentGrades = new ArrayList<>();
		
		try {
			Connection connection = DBConnect.connection;
			String query = "SELECT sc.course_id, s.first_name, s.last_name, sc.grade FROM Students_Enrolled_In_Courses sc INNER JOIN Students s on sc.student_id = s.student_id WHERE course_id = " + courseID;
			Statement stm = connection.createStatement();
			ResultSet result = stm.executeQuery(query);
			
			while(result.next()) {
				String studentName = result.getString("first_name") + " " + result.getString("last_name");
				String theCourseID = result.getString("course_id");
				String grade = result.getString("grade");
				
				studentGrades.add(new String[] {theCourseID, studentName, grade});
				
			}
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
		return studentGrades.toArray(new String[0][0]);
	}
	
	private void createGroupLayout(JScrollPane scrollPane, JComboBox courseBox, JLabel courseLabe, JLabel gradeLabel, JButton submitBtn, GroupLayout groupLayout) {
		
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(30)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(courseBox, 0, 214, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(courseLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addGap(129))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(gradeLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addGap(144))
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(submitBtn, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
								.addComponent(textField))
							.addGap(84)))
					.addGap(43))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
					.addGap(43)
					.addComponent(courseLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(courseBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(34)
					.addComponent(gradeLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 128, Short.MAX_VALUE)
					.addComponent(submitBtn)
					.addGap(39))
				.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
					.addGap(45)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(25, Short.MAX_VALUE))
		);
	}
}
