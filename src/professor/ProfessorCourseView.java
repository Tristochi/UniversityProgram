package professor;

import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.util.ArrayList;
import java.sql.*;
import dbconnect.DBConnect;

public class ProfessorCourseView extends JPanel {
	private String username;
	private ArrayList<ArrayList<String>> courseDetails;
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public ProfessorCourseView(String username) {
		this.username = username;
		initialize();
	}
	
	private void initialize() {
		DBConnect.connect();
		courseDetails = getCourses();
		ArrayList<String>courseNames = new ArrayList<String>();
		
		for (ArrayList<String> tmp : courseDetails) {
			courseNames.add(tmp.get(1));
		}
		
		String[] courseData = courseNames.toArray(new String[0]);
		ArrayList<String> students = getStudents(courseDetails.get(0).get(0));
		String[] studentData = students.toArray(new String[0]);
		//ArrayList<String> pendingStudents = getPendingStudents();
		
		JList<String> courseList = new JList<String>(courseData);
		courseList.setSelectedIndex(0);
		
		JLabel courseLabel = new JLabel("Courses");
		JList<String> studentList = new JList<String>(studentData);
		JLabel studentLabel = new JLabel("Students");
		JList<String> pendingStudentList = new JList<String>();
		JLabel pendingStudentLabel = new JLabel("Pending Students");
		JButton approveButton = new JButton("Approve");
		JButton rejectButton = new JButton("Reject");

		createGroupLayout(courseList, studentList, pendingStudentList, courseLabel, pendingStudentLabel, approveButton, rejectButton, studentLabel);
		
	}
	
	public String getUsername() {
		return username;
	}
	
	private ArrayList<ArrayList<String>> getCourses(){
		try {
			Connection connection = DBConnect.connection;
			
			//Retriever current UserID given Username
			String query = "SELECT * FROM accounts WHERE username='" + this.getUsername() + "'";
			Statement stm = connection.createStatement();
			ResultSet result = stm.executeQuery(query);
			result.first();
			String userID = result.getString("user_id");
			
			//Professor ID is the same as the UserID so get courses with the UserID. 
			
			query = "SELECT * FROM courses WHERE professor_id = " + userID;
			result = stm.executeQuery(query);
			
			ArrayList<ArrayList<String>> courses = new ArrayList<ArrayList<String>>();
			
			while(result.next()) {
				ArrayList<String> sublist = new ArrayList<String>();
				sublist.add(result.getString("course_id"));
				sublist.add(result.getString("course_name"));
				sublist.add(result.getString("course_semester"));
				sublist.add(result.getString("course_day"));
				sublist.add(result.getString("start_time"));
				sublist.add(result.getString("end_time"));
				sublist.add(result.getString("max_students"));
				courses.add(sublist);
			}
			
			return courses;
		} catch(Exception e) {
			System.out.println(e);
			ArrayList<String> tmp = new ArrayList<>();
			tmp.add(e.getMessage());
			ArrayList<ArrayList<String>> error = new ArrayList<ArrayList<String>>();
			return error;
		}
		
	}
	
	private ArrayList<String> getStudents(String courseID){
		try {
				Connection connection = DBConnect.connection;
				//Get all student IDs for the currently selected/provided course ID.
				String query = "SELECT * FROM students_enrolled_in_courses WHERE course_id = " + courseID;
				Statement stm = connection.createStatement();
				ResultSet result = stm.executeQuery(query);
				
				ArrayList<String> studentIDs = new ArrayList<>();
				
				while(result.next()) {
					studentIDs.add(result.getString("student_id"));
				}
				
				//Get all of the student names associated with the course.
				ArrayList<String> studentNames = new ArrayList<String>();
				
				for (String ID : studentIDs) {
					query = "SELECT * FROM students WHERE student_id=" + ID;
					result = stm.executeQuery(query);
					result.first();
					String name = result.getString("first_name") + " " + result.getString("last_name");
					studentNames.add(name);
				}
			
			return studentNames;
		}catch (Exception e) {
			System.out.println(e);
			throw new RuntimeException(e);
		}

	}
	
	private void createGroupLayout(JList<String> courseList, JList<String> studentList, JList<String> pendingStudentList, JLabel courseLabel, JLabel pendingStudentLabel, JButton approveButton, JButton rejectButton, JLabel studentLabel) {
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(25)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(courseList, GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
							.addGap(55))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(courseLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addGap(160)))
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(pendingStudentLabel, GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
							.addGap(90))
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(approveButton, GroupLayout.PREFERRED_SIZE, 81, Short.MAX_VALUE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(rejectButton, GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
									.addGap(20))
								.addComponent(pendingStudentList, GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(studentLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addGap(132))
								.addComponent(studentList, GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE))
							.addGap(10))))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(11)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(courseLabel)
						.addComponent(studentLabel))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(courseList, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE)
						.addComponent(studentList, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE))
					.addGap(4)
					.addComponent(pendingStudentLabel, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(pendingStudentList, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(approveButton, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addComponent(rejectButton, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
					.addGap(31))
		);
		setLayout(groupLayout);
	}
}
