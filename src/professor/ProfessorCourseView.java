package professor;

import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.util.ArrayList;
import java.util.Collections;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import dbconnect.DBConnect;

public class ProfessorCourseView extends JPanel {
	private String username;
	private ArrayList<ArrayList<String>> courseDetails;
	private String currentSelectedCourse;
	private String currentSelectedCourseID;
	private static final long serialVersionUID = 1L;
	private JLabel totalStudentsLabel;
	private JLabel dayLabel;
	private JLabel semesterLabel;
	private JList<String> courseList;
	private JLabel courseLabel;
	private JLabel pendingStudentLabel;
	private JButton approveButton;
	private JButton rejectButton;
	private JList<String> pendingStudentList;
	private JLabel studentLabel;
	private JList<String> studentList;

	/**
	 * Create the panel.
	 */
	public ProfessorCourseView(String username) {
		this.username = username;
		initialize();
	}
	
	private void initialize() {
		semesterLabel = new JLabel("Semester: ");
		totalStudentsLabel = new JLabel("Students: ");
		dayLabel = new JLabel("Day: ");
		
		courseLabel = new JLabel("Courses");
		studentLabel = new JLabel("Students");
		pendingStudentLabel = new JLabel("Pending Students");
		
		DBConnect.connect();
		courseDetails = getCourses();
		ArrayList<String>courseNames = new ArrayList<String>();
		
		for (ArrayList<String> tmp : courseDetails) {
			courseNames.add(tmp.get(1));
		}
		
		String[] courseData = courseNames.toArray(new String[0]);
		ArrayList<String> students = getStudents(courseDetails.get(0).get(0));
		String[] studentData = students.toArray(new String[0]);
		studentList = new JList<String>(studentData);
		//ArrayList<String> pendingStudents = getPendingStudents(courseDetails.get(0).get(0));
		DefaultListModel<String> pendingStudents = getPendingStudents(courseDetails.get(0).get(0));
		pendingStudentList = new JList<String>(pendingStudents);
		
		//Add a listener so that if the course is changed, we select the correct students.
		courseList = new JList<String>(courseData);
		courseList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting()) {
					String selectedCourse = courseList.getSelectedValue();
					if(!selectedCourse.equals(currentSelectedCourse)) {
						for (ArrayList<String> course : courseDetails) {
							if(course.get(1).equals(selectedCourse)) {
								//Query students with the course_id and refresh student list
								refreshStudentList(studentList, course.get(0));
								
								
								//Query pending students as well
								pendingStudents.removeAllElements();
								DefaultListModel<String> tmpLM = getPendingStudents(course.get(0));
								ArrayList<String> tmpAL = Collections.list(tmpLM.elements());
								pendingStudents.addAll(tmpAL);
								
								//Set the new current course
								currentSelectedCourse = selectedCourse;
								currentSelectedCourseID = course.get(0);
								
								//update labels
								updateSummaryLabels(currentSelectedCourseID);
							}
						}
					}
					
				}
			}
		});
		
		
		
		courseList.setSelectedIndex(0);
		currentSelectedCourse = courseList.getSelectedValue();
		currentSelectedCourseID = getCourseID(courseList.getSelectedValue());		

		
		approveButton = new JButton("Approve");
		approveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Update selected student information
				String[] studentName = pendingStudentList.getSelectedValue().split(" ");
				String courseID = new String();
				for (ArrayList<String> course : courseDetails) {
					if(course.get(1).equals(currentSelectedCourse)) {
						courseID = course.get(0);
					}
				}
				if(!courseID.equals("")) {
					updatePendingStudent(studentName[0], studentName[1], "Approved", courseID, studentList);
					//Remove student from list
					pendingStudents.removeElementAt(pendingStudentList.getSelectedIndex());
				}
				
				
			}
		});
		
		rejectButton = new JButton("Reject");
		rejectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Get course ID
				String [] studentName = pendingStudentList.getSelectedValue().split(" ");
				String courseID = new String();
				for (ArrayList<String> course : courseDetails) {
					if(course.get(1).equals(currentSelectedCourse)) {
						courseID = course.get(0);
					}
				}
				if(!courseID.equals("")) {
					//Update selected student information
					updatePendingStudent(studentName[0], studentName[1], "Rejected", courseID, studentList);
					pendingStudents.removeElementAt(pendingStudentList.getSelectedIndex());
				}
			}
		});

		createGroupLayout(courseList, studentList, pendingStudentList, courseLabel, pendingStudentLabel, approveButton, rejectButton, studentLabel, semesterLabel, dayLabel, totalStudentsLabel);
		
		updateSummaryLabels(currentSelectedCourseID);
	}
	
	private String getCourseID(String className) {
		try {
			Connection connection = DBConnect.connection;
			String query = "SELECT * FROM courses WHERE course_name = '"+className+"'";
			Statement stm = connection.createStatement();
			ResultSet result = stm.executeQuery(query);
			result.first();
			return result.getString("course_id");
			
		}catch(Exception e) {
			System.out.println(e);
			throw new RuntimeException(e);
		}
	}
	
	private void updateSummaryLabels(String courseID) {
		for(ArrayList<String> course : courseDetails) {
			if(course.get(1).equals(currentSelectedCourse)) {
				ArrayList<String> students = getStudents(courseID);
				totalStudentsLabel.setText("Students: " + students.size() + "/" +course.get(6));
				semesterLabel.setText("Semester: " + course.get(2));
				dayLabel.setText("Day: " + course.get(3));
			}
		}
	}
	
	public void refreshStudentList(JList<String> studentList, String courseID) {
		ArrayList<String> tmp = getStudents(courseID);
		String[] tmpArray = tmp.toArray(new String[0]);
		studentList.setListData(tmpArray);
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
	
	private DefaultListModel<String> getPendingStudents(String courseID){
		try {
			DefaultListModel<String> pendingStudents = new DefaultListModel<String>();
			ArrayList<String> pendingStudentIDs = new ArrayList<String>();
			Connection connection = DBConnect.connection;
			String query = "SELECT * FROM course_requests WHERE course_id = " + courseID + " AND request_status = 'Pending'";
			Statement stm = connection.createStatement();
			ResultSet result = stm.executeQuery(query);
			
			while(result.next()) {
				pendingStudentIDs.add(result.getString("student_id"));
			}
			
			for (String studentID : pendingStudentIDs) {
				query = "SELECT * FROM students WHERE student_id = " + studentID;
				result = stm.executeQuery(query);
				result.first();
				String name = result.getString("first_name") + " " + result.getString("last_name");
				pendingStudents.addElement(name);
			}
			
			return pendingStudents;
		}catch (Exception e) {
			System.out.println(e);
			throw new RuntimeException(e);
		}
	}
	
	private void updatePendingStudent(String firstName, String lastName, String statusChange, String courseID, JList<String> studentList) {
		try {
		Connection connection = DBConnect.connection;
		//First we need to get the student ID
		String query = "SELECT student_id FROM students WHERE first_name = '"+firstName+"' AND last_name='"+lastName+"'";
		Statement stm = connection.createStatement();
		ResultSet result = stm.executeQuery(query);
		result.first();
		String studentID = result.getString("student_id");
		
		//Update there status on the course request table to approved
		if(statusChange.equals("Approved")) {
			query = "UPDATE course_requests SET request_status='Approved' WHERE student_id="+studentID;
			PreparedStatement stm1 = connection.prepareStatement(query);
			int rows = stm1.executeUpdate(query);
			
			//Assign them to the class
			query = String.format("INSERT INTO %s VALUES (%s, %s, %s)", "students_enrolled_in_courses", Integer.parseInt(courseID), Integer.parseInt(studentID), 0.00); 
			//stm.executeQuery(query);
			PreparedStatement stm2 = connection.prepareStatement(query);
			rows = stm2.executeUpdate(query);
			
			//Refresh studentList
			refreshStudentList(studentList, courseID);
			
		}else {
			query = "UPDATE course_requests SET request_status='Rejected' WHERE student_id = " + studentID;
			PreparedStatement stm3 = connection.prepareStatement(query);
			int rows = stm3.executeUpdate(query);
			
		}
		} catch(Exception e) {
			System.out.println(e);
			throw new RuntimeException(e);
		}
	}
	
	private void createGroupLayout(JList<String> courseList, JList<String> studentList, JList<String> pendingStudentList, JLabel courseLabel, JLabel pendingStudentLabel, JButton approveButton, JButton rejectButton, JLabel studentLabel, JLabel semesterLabel, JLabel dayLabel, JLabel totalStudentsLabel) {
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(25)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(dayLabel, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
							.addGap(148))
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
							.addGroup(groupLayout.createSequentialGroup()
								.addComponent(totalStudentsLabel, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
								.addGap(148))
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(semesterLabel, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
									.addGap(146))
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
									.addGroup(groupLayout.createSequentialGroup()
										.addComponent(courseList, GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
										.addGap(55))
									.addGroup(groupLayout.createSequentialGroup()
										.addComponent(courseLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addGap(160))))))
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
								.addComponent(pendingStudentList, GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(studentLabel, GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
									.addGap(132))
								.addComponent(studentList, GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE))
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
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(4)
							.addComponent(pendingStudentLabel, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(pendingStudentList, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(approveButton, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
								.addComponent(rejectButton, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(18)
							.addComponent(semesterLabel)
							.addGap(7)
							.addComponent(dayLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(totalStudentsLabel)))
					.addGap(31))
		);
		setLayout(groupLayout);
	}
}
