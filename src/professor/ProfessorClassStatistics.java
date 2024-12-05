package professor;

import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import custom.CustomTableModel;
import dbconnect.DBConnect;

import javax.swing.JTable;
import javax.swing.JScrollPane;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

public class ProfessorClassStatistics extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTable gradeSummaryTable;
	private JTable studentTable;
	private JComboBox courseSelect;
	private JScrollPane gradeSummaryPane;
	private JScrollPane studentPane;
	private String currentSelectedClass;
	private String currentCourseID;
	private ArrayList<ArrayList<String>> courseList;
	private String username;
	private CustomTableModel gradeModel;
	private CustomTableModel studentModel;
	private ArrayList<String[]> currentStudents;
	private String currentlySelectedGrade;
	private JLabel classAvgLabel;

	/**
	 * Create the panel.
	 */
	
	public ProfessorClassStatistics(String username) {
		this.username = username;
		currentStudents = new ArrayList<>();
		initialize();
	}
	
	public void initialize() {
		classAvgLabel = new JLabel("");
		courseList = setCourseList();
		ArrayList<String> courseData = new ArrayList<>();
		for(ArrayList<String>course : courseList) {
			courseData.add(course.get(1));
		}
		
		courseSelect = new JComboBox(courseData.toArray(new String[0]));
		currentSelectedClass = (String) courseSelect.getSelectedItem();
		courseSelect.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    //Update current course/courseID
                	for(ArrayList<String> course : courseList) {
                		if(course.get(1).equals(e.getItem())){
                			currentCourseID = course.get(0);
                			currentSelectedClass = (String) e.getItem();
                			refreshTables();
                			refreshStudentTable();
                			updateClassAverage();
                		}
                	}
                	
                	}
                }
            }
		);
		for(ArrayList<String> course : courseList) {
			if (course.get(1).equals(currentSelectedClass)) {
				currentCourseID = course.get(0);
				
			}
		}
		gradeSummaryPane = new JScrollPane();
		studentPane = new JScrollPane();
		String[] gradeColumns = {"Grade", "Total Students"};
		String[][] gradeRows = getStudentsGrades(currentCourseID);
		updateClassAverage();
		gradeModel = new CustomTableModel(gradeRows, gradeColumns);
		gradeSummaryTable = new JTable(gradeModel);
		
		gradeSummaryTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					int selectedRow = gradeSummaryTable.getSelectedRow();
					System.out.println(selectedRow);
					if(selectedRow != -1) {
						currentlySelectedGrade = (String) gradeSummaryTable.getValueAt(selectedRow, 0);
						//System.out.println("Current selected grade: " + currentlySelectedGrade);
						refreshStudentTable();
					}
				}
			}
		});
		currentlySelectedGrade = "A+";
		String[] studentColumns = {"Student ID", "Student Name", "Course Grade", "Letter Grade"};
		String[][] studentRows = createStudentList(currentlySelectedGrade, currentStudents);
		studentModel = new CustomTableModel(studentRows, studentColumns);
		studentTable = new JTable(studentModel);
		
		createGroupLayout();
	}
	
	private void updateClassAverage() {
		double totalScore = 0.00;
		double count = 0.00;
		for(String[] student : currentStudents) {
			totalScore += Double.parseDouble(student[2]);
			count += 1;
		}
		
		double classAverage = totalScore/count;
		classAvgLabel.setText("Class Average: " + classAverage);
	}
	
	private void refreshTables() {
		
		String[][] gradeRowData = getStudentsGrades(currentCourseID);
		gradeModel.setRowCount(0);
		for(String[] row : gradeRowData) {
			gradeModel.addRow(row);
		}
		

		
	}
	private void refreshStudentTable() {
		String[][] studentRowData = createStudentList(currentlySelectedGrade, currentStudents);
		studentModel.setRowCount(0);
		for(String[] row : studentRowData) {
			studentModel.addRow(row);
		}
	}
	
	private String[][] createStudentList(String currentlySelectedGrade, ArrayList<String[]> currentStudents) {
		ArrayList<String[]> newList = new ArrayList<>();
		for(String[] student : currentStudents) {
			if(student[3].equals(currentlySelectedGrade)) {
				newList.add(student);
				
			}
		}
		
		return newList.toArray(new String[0][0]);
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
			e.printStackTrace();
		}
		return courseList;
		
	}
	
	private String[][] getStudentsGrades(String courseID){
		
		/*
		 * Here we are not only getting all of the students and their final grades,
		 * but creating a a structure to cache of those students and their grades that
		 * looks like 
		 * {
		 *     [ "A+", "1"],
		 *     [ "A", "5"],
		 *     ...
		 *     
		 * }
		 */
		
		ArrayList<String[]> students = new ArrayList<>();
		ArrayList<String[]> studentGrades = new ArrayList<>();
		studentGrades.add(new String[] {"A+", "0"});
		studentGrades.add(new String[] {"A","0"});
		studentGrades.add(new String[] {"A-","0"});
		studentGrades.add(new String[] {"B+","0"});
		studentGrades.add(new String[] {"B","0"});
		studentGrades.add(new String[] {"B-","0"});
		studentGrades.add(new String[] {"C+","0"});
		studentGrades.add(new String[] {"C","0"});
		studentGrades.add(new String[] {"C-","0"});
		studentGrades.add(new String[] {"D+","0"});
		studentGrades.add(new String[] {"D","0"});
		studentGrades.add(new String[] {"F","0"});
		
		try {
			Connection connection = DBConnect.connection;
			String query = "SELECT sc.course_id, s.student_id, s.first_name, s.last_name, sc.grade FROM Students_Enrolled_In_Courses sc INNER JOIN Students s on sc.student_id = s.student_id WHERE course_id = " + courseID;
			Statement stm = connection.createStatement();
			ResultSet result = stm.executeQuery(query);
			
			while(result.next()) {
				String studentName = result.getString("first_name") + " " + result.getString("last_name");
				String theCourseID = result.getString("student_id");
				String grade = result.getString("grade");
				String letterGrade = new String();
				double gradeValue = result.getDouble("grade");
				
				if(gradeValue >= 97.00) {
					letterGrade = "A+";
					int total = Integer.parseInt(studentGrades.get(0)[1]);
					total += 1;
					studentGrades.get(0)[1] = String.valueOf(total);
					
				} else if(gradeValue >= 93.00 && gradeValue < 97.00) {
					letterGrade = "A";
					int total = Integer.parseInt(studentGrades.get(1)[1]);
					total += 1;
					studentGrades.get(1)[1] = String.valueOf(total);
					
				} else if(gradeValue >= 90.00 && gradeValue < 93.00) {
					letterGrade = "A-";
					int total = Integer.parseInt(studentGrades.get(2)[1]);
					total += 1;
					studentGrades.get(2)[1] = String.valueOf(total);
				
				} else if(gradeValue >= 87.00 && gradeValue < 90.00) {
					letterGrade = "B+";
					int total = Integer.parseInt(studentGrades.get(3)[1]);
					total += 1;
					studentGrades.get(3)[1] = String.valueOf(total);
				
				} else if(gradeValue >= 83.00 && gradeValue < 87.00) {
					letterGrade = "B";
					int total = Integer.parseInt(studentGrades.get(4)[1]);
					total += 1;
					studentGrades.get(4)[1] = String.valueOf(total);
				
				} else if(gradeValue >= 80.00 && gradeValue < 83.00) {
					letterGrade = "B-";
					int total = Integer.parseInt(studentGrades.get(5)[1]);
					total += 1;
					studentGrades.get(5)[1] = String.valueOf(total);
				
				} else if(gradeValue >= 77.00 && gradeValue < 80.00) {
					letterGrade = "C+";
					int total = Integer.parseInt(studentGrades.get(6)[1]);
					total += 1;
					studentGrades.get(6)[1] = String.valueOf(total);
				
				} else if(gradeValue >= 73.00 && gradeValue < 77.00) {
					letterGrade = "C";
					int total = Integer.parseInt(studentGrades.get(7)[1]);
					total += 1;
					studentGrades.get(7)[1] = String.valueOf(total);
				
				} else if(gradeValue >= 70.00 && gradeValue < 73.00) {
					letterGrade = "C-";
					int total = Integer.parseInt(studentGrades.get(8)[1]);
					total += 1;
					studentGrades.get(8)[1] = String.valueOf(total);
				
				} else if(gradeValue >= 67.00 && gradeValue < 70.00) {
					letterGrade = "D+";
					int total = Integer.parseInt(studentGrades.get(9)[1]);
					total += 1;
					studentGrades.get(9)[1] = String.valueOf(total);
				
				} else if(gradeValue >= 65.00 && gradeValue < 67.00) {
					letterGrade = "D";
					int total = Integer.parseInt(studentGrades.get(10)[1]);
					total += 1;
					studentGrades.get(10)[1] = String.valueOf(total);
				
				} else if(gradeValue < 65.00 ) {
					letterGrade = "F";
					int total = Integer.parseInt(studentGrades.get(11)[1]);
					total += 1;
					studentGrades.get(11)[1] = String.valueOf(total);
				
				}
				
				students.add(new String[] {theCourseID, studentName, grade, letterGrade});
				//System.out.println(theCourseID + " " + studentName + " " + grade + " " + letterGrade);
				
			}
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		currentStudents.clear();
		currentStudents.addAll(students);
		return studentGrades.toArray(new String[0][0]);
	}
	
	public void createGroupLayout() {
		
		
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(16)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(courseSelect, 0, 546, Short.MAX_VALUE)
							.addGap(18))
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(gradeSummaryPane, GroupLayout.PREFERRED_SIZE, 279, GroupLayout.PREFERRED_SIZE)
								.addComponent(classAvgLabel))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(studentPane, GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)))
					.addGap(6))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(17)
					.addComponent(courseSelect, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(gradeSummaryPane, GroupLayout.PREFERRED_SIZE, 218, GroupLayout.PREFERRED_SIZE)
							.addGap(28)
							.addComponent(classAvgLabel))
						.addComponent(studentPane, GroupLayout.PREFERRED_SIZE, 321, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(25, Short.MAX_VALUE))
		);
		
		
		//studentPane.setColumnHeaderView(studentTable);
		studentPane.setViewportView(studentTable);
		
		gradeSummaryPane.setViewportView(gradeSummaryTable);
		setLayout(groupLayout);
	}
}
