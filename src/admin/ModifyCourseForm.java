package admin;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableModel;
import javax.swing.text.MaskFormatter;

import GUILook.GUILookAndFeel;
import admin.formlistener.ModifyCourseFormListener;
import admin.formlistener.ModifyCourseTableListener;
import admin.formlistener.RemoveCourseButtonListener;
import custom.CustomComboBox;
import custom.CustomTableModel;
import custom.FormRefresh;
import custom.RadioButtonEditor;
import custom.RadioButtonRenderer;
import dbconnect.DBConnect;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JTextArea;

/*
 * GUI to Modify Or Remove Selected Course.
 */

public class ModifyCourseForm extends JPanel implements FormRefresh {

	private static final long serialVersionUID = 1L;
	private final int MAX_CHARACTERS = 254;
	private JPanel anchorPane;
	private JPanel contentPane;
	private JPanel formPane;
	private JScrollPane courseViewPane;
	private JTextField courseIdTextField;
	private JTextField courseNameTextField;
	private JButton submitButton;
	private CustomTableModel tableModel;
	private ButtonGroup buttonGroup;
	private JTable courseTable;
	private JComboBox<String> semesterComboBox;
	private JComboBox<String> dayComboBox;
	private JComboBox<Integer> maxStudentsComboBox;
	private JTextArea descriptionTextArea;
	private CustomComboBox professorComboBox;
	private JTextField startTimeTextField;
	private JComboBox<String> startTimeComboBox;
	private JTextField endTimeTextField;
	private JComboBox<String> endTimeComboBox;
	private JButton removeCourseButton;
	private Map<String, JComponent> componentMap;
	
	// for testing
	public static void main(String[] args) {
		DBConnect.connect();
		JFrame frame = new JFrame();
		frame.setContentPane(new ModifyCourseForm());
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
		GUILookAndFeel.setLookAndFeel();
	}
	

	/**
	 * Create the panel.
	 */
	public ModifyCourseForm() {
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
		gbl_anchorPane.columnWidths = new int[] {0};
		gbl_anchorPane.rowHeights = new int[] {0};
		gbl_anchorPane.columnWeights = new double[]{1.0};
		gbl_anchorPane.rowWeights = new double[]{0};
		anchorPane.setLayout(gbl_anchorPane);
		
		contentPane = new JPanel();
		GridBagConstraints gbc_contentPane = new GridBagConstraints();
		gbc_contentPane.fill = GridBagConstraints.BOTH;
		gbc_contentPane.gridx = 0;
		gbc_contentPane.gridy = 0;
		anchorPane.add(contentPane, gbc_contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] {700};
		gbl_contentPane.rowHeights = new int[] {200, 50};
		gbl_contentPane.columnWeights = new double[]{1.0};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0};
		contentPane.setLayout(gbl_contentPane);
		
		formPane = new JPanel();
		formPane.setBorder(new EmptyBorder(0, 75, 0, 75));
		GridBagConstraints gbc_formPane = new GridBagConstraints();
		gbc_formPane.fill = GridBagConstraints.BOTH;
		gbc_formPane.gridx = 0;
		gbc_formPane.gridy = 1;
		contentPane.add(formPane, gbc_formPane);
		GridBagLayout gbl_formPane = new GridBagLayout();
		gbl_formPane.columnWidths = new int[]{50, 180, 50, 180};
		gbl_formPane.rowHeights = new int[]{50, 50, 50, 50, 50, 50};
		gbl_formPane.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0};
		gbl_formPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		formPane.setLayout(gbl_formPane);
		
		createLabels();
		createTextFields();
		createComboBoxes();
		
		addDescriptionTextAreaListener();
		
		createStartTimePane();
		createEndTimePane();
		
		createComponentHashMap();
		
		createCourseInfoScrollPane();
		createModifyButtonAndListener();
		createRemoveButtonAndListener();
		
		
	}
	
	private void createComponentHashMap() {
		componentMap = new HashMap<>();
		componentMap.put("mainPane", this);
		componentMap.put("formPane", formPane);
		componentMap.put("courseIdTextField", courseIdTextField);
		componentMap.put("courseNameTextField", courseNameTextField);
		componentMap.put("professorComboBox", professorComboBox);
		componentMap.put("semesterComboBox", semesterComboBox);
		componentMap.put("dayComboBox", dayComboBox);
		componentMap.put("startTimeComboBox", startTimeComboBox);
		componentMap.put("endTimeComboBox", endTimeComboBox);
		componentMap.put("startTimeTextField", startTimeTextField);
		componentMap.put("endTimeTextField", endTimeTextField);
		componentMap.put("descriptionTextArea", descriptionTextArea);
		componentMap.put("maxStudentsComboBox", maxStudentsComboBox);
	}
	
	private void createLabels(){
		JLabel titleLabel = new JLabel("Modify/Remove A Course");
		GridBagConstraints gbc_titleLabel = new GridBagConstraints();
		titleLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		gbc_titleLabel.insets = new Insets(0, 0, 5, 0);
		gbc_titleLabel.gridx = 0;
		gbc_titleLabel.gridy = 0;
		add(titleLabel, gbc_titleLabel);
		
		JLabel courseIdLabel = new JLabel("Course ID");
		courseIdLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
		GridBagConstraints gbc_courseIdLabel = new GridBagConstraints();
		gbc_courseIdLabel.insets = new Insets(0, 0, 5, 5);
		gbc_courseIdLabel.gridx = 0;
		gbc_courseIdLabel.gridy = 0;
		formPane.add(courseIdLabel, gbc_courseIdLabel);
		
		JLabel courseNameLabel = new JLabel("Course Name");
		courseNameLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
		GridBagConstraints gbc_courseNameLabel = new GridBagConstraints();
		gbc_courseNameLabel.insets = new Insets(0, 0, 5, 5);
		gbc_courseNameLabel.gridx = 0;
		gbc_courseNameLabel.gridy = 1;
		formPane.add(courseNameLabel, gbc_courseNameLabel);
		
		JLabel endTimeLabel = new JLabel("End Time");
		endTimeLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
		GridBagConstraints gbc_endTimeLabel = new GridBagConstraints();
		gbc_endTimeLabel.insets = new Insets(0, 20, 5, 10);
		gbc_endTimeLabel.gridx = 2;
		gbc_endTimeLabel.gridy = 1;
		formPane.add(endTimeLabel, gbc_endTimeLabel);
		
		JLabel professorLabel = new JLabel("Professor");
		professorLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
		GridBagConstraints gbc_professorLabel = new GridBagConstraints();
		gbc_professorLabel.insets = new Insets(0, 0, 5, 5);
		gbc_professorLabel.gridx = 0;
		gbc_professorLabel.gridy = 2;
		formPane.add(professorLabel, gbc_professorLabel);
		
		JLabel semesterLabel = new JLabel("Semester");
		semesterLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
		GridBagConstraints gbc_semesterLabel = new GridBagConstraints();
		gbc_semesterLabel.insets = new Insets(0, 0, 5, 5);
		gbc_semesterLabel.gridx = 0;
		gbc_semesterLabel.gridy = 3;
		formPane.add(semesterLabel, gbc_semesterLabel);
		
		JLabel startTimeLabel = new JLabel("Start Time");
		startTimeLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
		GridBagConstraints gbc_startTimeLabel = new GridBagConstraints();
		gbc_startTimeLabel.insets = new Insets(0, 20, 5, 10);
		gbc_startTimeLabel.gridx = 2;
		gbc_startTimeLabel.gridy = 0;
		formPane.add(startTimeLabel, gbc_startTimeLabel);
		
		JLabel descriptionLabel = new JLabel("Description");
		descriptionLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
		GridBagConstraints gbc_descriptionLabel = new GridBagConstraints();
		gbc_descriptionLabel.insets = new Insets(0, 20, 5, 10);
		gbc_descriptionLabel.gridx = 2;
		gbc_descriptionLabel.gridy = 2;
		formPane.add(descriptionLabel, gbc_descriptionLabel);
		
		JLabel dayLabel = new JLabel("Day");
		dayLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
		GridBagConstraints gbc_dayLabel = new GridBagConstraints();
		gbc_dayLabel.insets = new Insets(0, 0, 5, 5);
		gbc_dayLabel.gridx = 0;
		gbc_dayLabel.gridy = 4;
		formPane.add(dayLabel, gbc_dayLabel);
		
		JLabel maxStudentsLabel = new JLabel("<HTML>Max Number<br>of Students</HTML>");
		maxStudentsLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
		GridBagConstraints gbc_maxStudentsLabel = new GridBagConstraints();
		gbc_maxStudentsLabel.anchor = GridBagConstraints.EAST;
		gbc_maxStudentsLabel.insets = new Insets(0, 20, 5, 10);
		gbc_maxStudentsLabel.gridx = 2;
		gbc_maxStudentsLabel.gridy = 3;
		formPane.add(maxStudentsLabel, gbc_maxStudentsLabel);
	}
	
	private void createTextFields() {
		courseIdTextField = new JTextField();
		courseIdTextField.setFont(new Font("Tahoma", Font.PLAIN, 13));
		courseIdTextField.setEditable(false);
		GridBagConstraints gbc_courseIdTextField = new GridBagConstraints();
		gbc_courseIdTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_courseIdTextField.insets = new Insets(0, 0, 5, 5);
		gbc_courseIdTextField.gridx = 1;
		gbc_courseIdTextField.gridy = 0;
		formPane.add(courseIdTextField, gbc_courseIdTextField);
		courseIdTextField.setColumns(10);
		
		courseNameTextField = new JTextField();
		courseNameTextField.setFont(new Font("Tahoma", Font.PLAIN, 13));
		GridBagConstraints gbc_courseNameTextField = new GridBagConstraints();
		gbc_courseNameTextField.insets = new Insets(0, 0, 5, 5);
		gbc_courseNameTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_courseNameTextField.gridx = 1;
		gbc_courseNameTextField.gridy = 1;
		formPane.add(courseNameTextField, gbc_courseNameTextField);
		courseNameTextField.setColumns(10);
		
		// make description text area scrollable
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.gridx = 3;
		gbc_scrollPane.gridy = 2;
		formPane.add(scrollPane, gbc_scrollPane);
		
		descriptionTextArea = new JTextArea();
		scrollPane.setViewportView(descriptionTextArea);
		descriptionTextArea.setWrapStyleWord(true);
		descriptionTextArea.setLineWrap(true);
		descriptionTextArea.setFont(new Font("Tahoma", Font.PLAIN, 13));
		descriptionTextArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
	}
	
	private void createComboBoxes() {
		semesterComboBox = new JComboBox();
		semesterComboBox.setFont(new Font("Tahoma", Font.PLAIN, 13));
		GridBagConstraints gbc_semesterComboBox = new GridBagConstraints();
		gbc_semesterComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_semesterComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_semesterComboBox.gridx = 1;
		gbc_semesterComboBox.gridy = 3;
		formPane.add(semesterComboBox, gbc_semesterComboBox);
		
		professorComboBox = new CustomComboBox();
		professorComboBox.setFont(new Font("Tahoma", Font.PLAIN, 13));
		GridBagConstraints gbc_professorComboBox = new GridBagConstraints();
		gbc_professorComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_professorComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_professorComboBox.gridx = 1;
		gbc_professorComboBox.gridy = 2;
		formPane.add(professorComboBox, gbc_professorComboBox);
		
		dayComboBox = new JComboBox();
		dayComboBox.setFont(new Font("Tahoma", Font.PLAIN, 13));
		GridBagConstraints gbc_dayComboBox = new GridBagConstraints();
		gbc_dayComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_dayComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_dayComboBox.gridx = 1;
		gbc_dayComboBox.gridy = 4;
		formPane.add(dayComboBox, gbc_dayComboBox);
		
		maxStudentsComboBox = new JComboBox();
		maxStudentsComboBox.setFont(new Font("Tahoma", Font.PLAIN, 13));
		GridBagConstraints gbc_maxStudentsComboBox = new GridBagConstraints();
		gbc_maxStudentsComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_maxStudentsComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_maxStudentsComboBox.gridx = 3;
		gbc_maxStudentsComboBox.gridy = 3;
		formPane.add(maxStudentsComboBox, gbc_maxStudentsComboBox);
	}
	
	private void createStartTimePane() {
		JPanel startTimePane = new JPanel();
		GridBagConstraints gbc_startTimePane = new GridBagConstraints();
		gbc_startTimePane.insets = new Insets(0, 0, 5, 0);
		gbc_startTimePane.fill = GridBagConstraints.BOTH;
		gbc_startTimePane.gridx = 3;
		gbc_startTimePane.gridy = 0;
		formPane.add(startTimePane, gbc_startTimePane);
		GridBagLayout gbl_startTimePane = new GridBagLayout();
		gbl_startTimePane.columnWidths = new int[] { 10, 10, 10 };
		gbl_startTimePane.rowHeights = new int[] { 10 };
		gbl_startTimePane.columnWeights = new double[] { 0.5, 0.1, 0.4 };
		gbl_startTimePane.rowWeights = new double[] { 0.0 };
		startTimePane.setLayout(gbl_startTimePane);

		startTimeTextField = new JFormattedTextField(getTimeFormat());
		startTimeTextField.setHorizontalAlignment(SwingConstants.CENTER);
		startTimeTextField.setFont(new Font("Tahoma", Font.PLAIN, 13));
		GridBagConstraints gbc_startTimeTextField = new GridBagConstraints();
		gbc_startTimeTextField.insets = new Insets(0, 0, 0, 5);
		gbc_startTimeTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_startTimeTextField.gridx = 0;
		gbc_startTimeTextField.gridy = 0;
		startTimePane.add(startTimeTextField, gbc_startTimeTextField);

		startTimeComboBox = new JComboBox();
		startTimeComboBox.setFont(new Font("Tahoma", Font.PLAIN, 13));
		GridBagConstraints gbc_startTimeComboBox = new GridBagConstraints();
		gbc_startTimeComboBox.insets = new Insets(0, 0, 0, 5);
		gbc_startTimeComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_startTimeComboBox.gridx = 1;
		gbc_startTimeComboBox.gridy = 0;
		startTimePane.add(startTimeComboBox, gbc_startTimeComboBox);
	}
	
	private void createEndTimePane() {
		JPanel endTimePane = new JPanel();
		GridBagConstraints gbc_endTimePane = new GridBagConstraints();
		gbc_endTimePane.insets = new Insets(0, 0, 5, 0);
		gbc_endTimePane.fill = GridBagConstraints.BOTH;
		gbc_endTimePane.gridx = 3;
		gbc_endTimePane.gridy = 1;
		formPane.add(endTimePane, gbc_endTimePane);
		GridBagLayout gbl_endTimePane = new GridBagLayout();
		gbl_endTimePane.columnWidths = new int[] { 10, 10, 10 };
		gbl_endTimePane.rowHeights = new int[] { 10 };
		gbl_endTimePane.columnWeights = new double[] { 0.5, 0.1, 0.4 };
		gbl_endTimePane.rowWeights = new double[] { 0.0 };
		endTimePane.setLayout(gbl_endTimePane);

		endTimeTextField = new JFormattedTextField(getTimeFormat());
		endTimeTextField.setHorizontalAlignment(SwingConstants.CENTER);
		endTimeTextField.setFont(new Font("Tahoma", Font.PLAIN, 13));
		GridBagConstraints gbc_endTimeTextField = new GridBagConstraints();
		gbc_endTimeTextField.insets = new Insets(0, 0, 0, 5);
		gbc_endTimeTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_endTimeTextField.gridx = 0;
		gbc_endTimeTextField.gridy = 0;
		endTimePane.add(endTimeTextField, gbc_endTimeTextField);

		endTimeComboBox = new JComboBox();
		endTimeComboBox.setFont(new Font("Tahoma", Font.PLAIN, 13));
		GridBagConstraints gbc_endTimeComboBox = new GridBagConstraints();
		gbc_endTimeComboBox.insets = new Insets(0, 0, 0, 5);
		gbc_endTimeComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_endTimeComboBox.gridx = 1;
		gbc_endTimeComboBox.gridy = 0;
		endTimePane.add(endTimeComboBox, gbc_endTimeComboBox);
	}
	
	private void createModifyButtonAndListener() {
		submitButton = new JButton("Submit Changes");
		submitButton.setFont(new Font("Tahoma", Font.BOLD, 13));
		GridBagConstraints gbc_submitButton = new GridBagConstraints();
		gbc_submitButton.insets = new Insets(0, 0, 5, 0);
		gbc_submitButton.gridx = 3;
		gbc_submitButton.gridy = 4;
		formPane.add(submitButton, gbc_submitButton);
		
		ModifyCourseFormListener listener = new ModifyCourseFormListener(componentMap, tableModel);
		
		submitButton.addActionListener(listener);
	}
	
	private void createRemoveButtonAndListener() {
		removeCourseButton = new JButton("Remove Course");
		removeCourseButton.setFont(new Font("Tahoma", Font.BOLD, 13));
		GridBagConstraints gbc_removeCourseButton = new GridBagConstraints();
		gbc_removeCourseButton.gridx = 3;
		gbc_removeCourseButton.gridy = 5;
		formPane.add(removeCourseButton, gbc_removeCourseButton);
		
		RemoveCourseButtonListener listener = new RemoveCourseButtonListener(this, courseIdTextField);
		removeCourseButton.addActionListener(listener);
	}
	
	private void createCourseInfoScrollPane() {
		String[] columnNames = {"Select Course", "Course ID", "Course Name", "Semester", "Start Time", "End Time", "Day", "Description", "Max Students", "Professor"};
		String rowData[][] = getCourseInfoFromDB();
		
		tableModel = new CustomTableModel(rowData, columnNames);
		ModifyCourseTableListener tableListener = new ModifyCourseTableListener(componentMap, tableModel);
		tableModel.addTableModelListener(tableListener);
		buttonGroup = new ButtonGroup();
		
		// add radio buttons in the first column
		for(int i = 0; i < tableModel.getRowCount(); i++) {
			JRadioButton radioButton = new JRadioButton();
			buttonGroup.add(radioButton);
			tableModel.setValueAt(radioButton, i, 0);
		}
		
		courseTable = new JTable(tableModel);
		courseTable.setPreferredScrollableViewportSize(new Dimension(0, 0));
		courseTable.setFont(new Font("Tahoma", Font.PLAIN, 15));
		courseTable.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));
		courseTable.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		courseTable.setRowHeight(courseTable.getRowHeight() + 8);
		
		courseTable.getColumn("Select Course").setCellEditor(new RadioButtonEditor(new JCheckBox()));
		courseTable.getColumn("Select Course").setCellRenderer(new RadioButtonRenderer());
		
		courseViewPane =  new JScrollPane(courseTable);
		GridBagConstraints gbc_accountViewPane = new GridBagConstraints();
		gbc_accountViewPane.insets = new Insets(0, 0, 5, 0);
		gbc_accountViewPane.fill = GridBagConstraints.BOTH;
		gbc_accountViewPane.gridx = 0;
		gbc_accountViewPane.gridy = 0;
		contentPane.add(courseViewPane, gbc_accountViewPane);
	}
	
	/*
	 * Action/Key Listeners
	 */
	
	// Sets character limit of text area to 255
	private void addDescriptionTextAreaListener() {
		descriptionTextArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if(descriptionTextArea.getText().length() > MAX_CHARACTERS+1) {
					e.consume();
					String shortenedString = descriptionTextArea.getText().substring(0, MAX_CHARACTERS);
					descriptionTextArea.setText(shortenedString);
				}
				else if(descriptionTextArea.getText().length() > MAX_CHARACTERS) {
					e.consume();
				}
			}
		});
	}
	
	
	/*
	 * Helper Methods
	 */	
		
	private String[][] getCourseInfoFromDB() {
		List<String[]> courseInfo = new ArrayList<>();
		
		try {
			Connection connection = DBConnect.connection;
			String query = String.format("SELECT *, professors.first_name, professors.last_name FROM courses, professors "
											+ "WHERE professors.professor_id = courses.professor_id ORDER BY course_id");
			Statement stm = connection.createStatement();
			ResultSet resultSet = stm.executeQuery(query);
			
			while(resultSet.next()) {
				String id = ""+resultSet.getInt("course_id");
				String courseName = resultSet.getString("course_name");
				String semester = resultSet.getString("course_semester");
				String startTime = resultSet.getString("start_time");
				String endTime = resultSet.getString("end_time");
				String day = resultSet.getString("course_day");
				String description = resultSet.getString("course_description");
				String maxStudents = ""+resultSet.getInt("max_students");
				String professorName = resultSet.getString("first_name") + " " + resultSet.getString("last_name");
				
				courseInfo.add(new String[]{"", id, courseName, semester, startTime, endTime, day, description, maxStudents, professorName});
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return courseInfo.toArray(new String[0][0]);
	}
	
	private MaskFormatter getTimeFormat() {
		MaskFormatter format = null;
		try {
			format = new MaskFormatter("##:##");
			format.setPlaceholderCharacter('-');
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

		return format;
	}
	
	
	/*
	 * Public methods
	 */
	
	// Update the table on the admin view
	public void updateTableModel() {
		String[] columnNames = {"Select Course", "Course ID", "Course Name", "Semester", "Start Time", "End Time", "Day", "Description", "Max Students", "Professor"};
		String[][] courseInfo = getCourseInfoFromDB();
		
		tableModel.setDataVector(courseInfo, columnNames);
		ButtonGroup buttonGroup = new ButtonGroup();
		
		for(int i = 0; i < tableModel.getRowCount(); i++) {
			JRadioButton radioButton = new JRadioButton();
			buttonGroup.add(radioButton);
			tableModel.setValueAt(radioButton, i, 0);
		}
		
		courseTable.getColumn("Select Course").setCellEditor(new RadioButtonEditor(new JCheckBox()));
		courseTable.getColumn("Select Course").setCellRenderer(new RadioButtonRenderer());
		
		tableModel.fireTableDataChanged();
		clearTextFields();
	}
	
	public void clearTextFields() {
		for(Component component : formPane.getComponents()) {
			if(component instanceof JTextField textField) {
				textField.setText("");
			}
			if(component instanceof JComboBox comboBox) {
				comboBox.removeAllItems();
			}
		}
		
		// startTime/endTime text fields and combo boxes are in another JPanel
		// so they have to be removed manually
		descriptionTextArea.setText("");
		startTimeTextField.setText("");
		endTimeTextField.setText("");
		startTimeComboBox.removeAllItems();
		endTimeComboBox.removeAllItems();
	}
	
	@Override
	public void refreshComponents() {
		updateTableModel();
	}
}
