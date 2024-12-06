package professor;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import custom.CustomTableModel;
import dbconnect.DBConnect;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
	private String currentSelectedCourse;
	private ArrayList<ArrayList<String>> courseList;
	private CustomTableModel tableModel;
	private String selectedGrade;
	
	
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
		courseBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    //Update current course/courseID
                	for(ArrayList<String> course : courseList) {
                		if(course.get(1).equals(e.getItem())) {
                			currentCourseID = course.get(0);
                			currentSelectedCourse = (String) e.getItem();
                			
                			//Get new table data
                			String [][] rowData = getStudentGrades(currentCourseID);
                			tableModel.setRowCount(0);
                			for(String[] row : rowData) {
                				tableModel.addRow(row);
                			}
                		}
                	}
                }
            }
		});
		
		currentSelectedCourse = (String) courseBox.getSelectedItem();
		courseLabel = new JLabel("Course Select");
		textField = new JTextField();
		textField.setColumns(10);
		gradeLabel = new JLabel("Final Grade");
		submitBtn = new JButton("Submit");
		groupLayout = new GroupLayout(this);
		currentCourseID = "1";
		createGroupLayout(scrollPane, courseBox, courseLabel, gradeLabel, submitBtn, groupLayout);
		
		String[] columnNames = {"Student ID", "Student Name", "Course Grade"};
		String[][] rowData = getStudentGrades(currentCourseID);
		
		tableModel = new CustomTableModel(rowData, columnNames);
		gradeTable = new JTable(tableModel);
		gradeTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = gradeTable.getSelectedRow();
                    if (selectedRow != -1) {
                        selectedGrade = (String) gradeTable.getValueAt(selectedRow, 2);
                        System.out.println("Grade: " + selectedGrade);
                        textField.setText(selectedGrade);   
                    }
                }
			}
		});
		
		scrollPane.setViewportView(gradeTable);
		setLayout(groupLayout);
		
		submitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Check if entered grade is different than final grade
				int selectedRow = gradeTable.getSelectedRow();
				if(selectedRow != -1) {
					selectedGrade = (String) gradeTable.getValueAt(selectedRow, 2);
					String selectedStudentID = (String) gradeTable.getValueAt(selectedRow, 0);
					String enteredGrade = textField.getText();
					
					if (!enteredGrade.equals(selectedGrade)) {
						//Update the database
						boolean result = updateFinalGrade(selectedStudentID, currentCourseID, enteredGrade);
						
						
						if(result) {
							//update the view and text field
                			//update the table
                			String [][] rowData = getStudentGrades(currentCourseID);
                			tableModel.setRowCount(0);
                			for(String[] row : rowData) {
                				tableModel.addRow(row);
                			}
                			
                			//Update the text field
                			textField.setText("");
                			
						}
					}
				}
			}
		});
	}
	
	private boolean updateFinalGrade(String studentID, String courseID, String grade) {
		String letterGrade = "";
		double gradeValue = Double.parseDouble(grade);
		
		if(gradeValue >= 97.00) {
			letterGrade = "A+";
			
		} else if(gradeValue >= 93.00 && gradeValue < 97.00) {
			letterGrade = "A";
			
		} else if(gradeValue >= 90.00 && gradeValue < 93.00) {
			letterGrade = "A-";
		
		} else if(gradeValue >= 87.00 && gradeValue < 90.00) {
			letterGrade = "B+";
		
		} else if(gradeValue >= 83.00 && gradeValue < 87.00) {
			letterGrade = "B";
		} else if(gradeValue >= 80.00 && gradeValue < 83.00) {
			letterGrade = "B-";
		} else if(gradeValue >= 77.00 && gradeValue < 80.00) {
			letterGrade = "C+";
		} else if(gradeValue >= 73.00 && gradeValue < 77.00) {
			letterGrade = "C";
		} else if(gradeValue >= 70.00 && gradeValue < 73.00) {
			letterGrade = "C-";
		} else if(gradeValue >= 67.00 && gradeValue < 70.00) {
			letterGrade = "D+";
		} else if(gradeValue >= 65.00 && gradeValue < 67.00) {
			letterGrade = "D";
		} else if(gradeValue < 65.00 ) {
			letterGrade = "F";
		}
		
		try {
			Connection connection = DBConnect.connection;
			String query = "UPDATE Students_Enrolled_In_Courses SET grade="+ grade + " WHERE course_id="+courseID+" AND student_id=" + studentID;
			Statement stm = connection.createStatement();
			int row = stm.executeUpdate(query);
			
			String query2 = "UPDATE past_final_grades SET final_grade= '" + letterGrade + "' WHERE course_id="+courseID+" AND student_id="  + studentID;
			Statement stm1 = connection.createStatement();
			int row1 = stm1.executeUpdate(query2);
			stm.close();
			stm1.close();
			if(row > 0) {
				return true;
			}
			return false;
			
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
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
			String query = "SELECT sc.course_id, s.student_id, s.first_name, s.last_name, sc.grade FROM Students_Enrolled_In_Courses sc INNER JOIN Students s on sc.student_id = s.student_id WHERE course_id = " + courseID;
			Statement stm = connection.createStatement();
			ResultSet result = stm.executeQuery(query);
			
			while(result.next()) {
				String studentName = result.getString("first_name") + " " + result.getString("last_name");
				String theCourseID = result.getString("student_id");
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
