package admin;

import javax.management.RuntimeErrorException;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;

import GUILook.GUILookAndFeel;
import admin.formlistener.CreateCourseFormListener;
import custom.CustomComboBox;
import dbconnect.DBConnect;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.Component;
import java.awt.DefaultKeyboardFocusManager;

import javax.swing.Box;
import javax.swing.JFormattedTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.security.cert.PKIXCertPathChecker;
import javax.swing.SwingConstants;

public class CreateCourseForm extends JPanel {

	private static final long serialVersionUID = 1L;
	JLabel titleLabel;
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
	private final int MAX_CHARACTERS = 255;

	public static void main(String[] args) {
		DBConnect.connect();

		JFrame frame = new JFrame();
		frame.setContentPane(new CreateCourseForm());
		frame.setPreferredSize(new Dimension(700, 350));
		frame.pack();
		GUILookAndFeel.setLookAndFeel();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
	}

	/**
	 * Create the panel.
	 */
	public CreateCourseForm() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		gridBagLayout.columnWidths = new int[] { 678, 0 };
		gridBagLayout.rowHeights = new int[] { 17, 250, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		titleLabel = new JLabel("Create A Course");
		titleLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		GridBagConstraints gbc_titleLabel = new GridBagConstraints();
		gbc_titleLabel.insets = new Insets(0, 0, 5, 0);
		gbc_titleLabel.gridx = 0;
		gbc_titleLabel.gridy = 0;
		add(titleLabel, gbc_titleLabel);

		JPanel anchorPane = new JPanel();
		GridBagConstraints gbc_anchorPane = new GridBagConstraints();
		gbc_anchorPane.fill = GridBagConstraints.BOTH;
		gbc_anchorPane.gridx = 0;
		gbc_anchorPane.gridy = 1;
		add(anchorPane, gbc_anchorPane);

		formPane = new JPanel();
		anchorPane.add(formPane);
		GridBagLayout gbl_formPane = new GridBagLayout();
		gbl_formPane.columnWidths = new int[] { 100, 200, 100, 200 };
		gbl_formPane.rowHeights = new int[] { 52, 52, 52, 52, 52, 0 };
		gbl_formPane.columnWeights = new double[] { 0.1, 1.0, 0.1, 1.0 };
		gbl_formPane.rowWeights = new double[] { 1.0, 1.0, 1.0, 0.1, 0.1, 0.1 };
		formPane.setLayout(gbl_formPane);

		createTextFieldsAndComboBoxes();
		createLabels();
		createStartTimePane();
		createEndTimePane();

		addDescriptionTextAreaListener();
		
		// Add info to combo boxes
		setSemesterComboBox();
		setProfessorComboBox();
		setDaysComboBox();
		setMaxStudentsComboBox();
		
		createButtonAndListener();
	}

	private void createButtonAndListener() {
		JButton createCourseButton = new JButton("Create Course");
		GridBagConstraints gbc_createCourseButton = new GridBagConstraints();
		gbc_createCourseButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_createCourseButton.insets = new Insets(0, 0, 5, 0);
		gbc_createCourseButton.gridx = 3;
		gbc_createCourseButton.gridy = 4;
		formPane.add(createCourseButton, gbc_createCourseButton);
		
		CreateCourseFormListener listener = new CreateCourseFormListener(formPane, courseNameTextField, startTimeTextField, 
																			endTimeTextField, semesterComboBox, professorComboBox, 
																			dayComboBox, maxStudentsComboBox, startTimeComboBox, 
																			endTimeComboBox, descriptionTextArea);
		createCourseButton.addActionListener(listener);
	}

	private void createTextFieldsAndComboBoxes() {
		courseNameTextField = new JTextField();
		GridBagConstraints gbc_courseNameTextField = new GridBagConstraints();
		gbc_courseNameTextField.insets = new Insets(0, 0, 5, 5);
		gbc_courseNameTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_courseNameTextField.gridx = 1;
		gbc_courseNameTextField.gridy = 0;
		formPane.add(courseNameTextField, gbc_courseNameTextField);
		courseNameTextField.setColumns(10);

		semesterComboBox = new JComboBox();
		GridBagConstraints gbc_semesterComboBox = new GridBagConstraints();
		gbc_semesterComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_semesterComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_semesterComboBox.gridx = 1;
		gbc_semesterComboBox.gridy = 1;
		formPane.add(semesterComboBox, gbc_semesterComboBox);

		professorComboBox = new CustomComboBox();
		GridBagConstraints gbc_professorComboBox = new GridBagConstraints();
		gbc_professorComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_professorComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_professorComboBox.gridx = 1;
		gbc_professorComboBox.gridy = 2;
		formPane.add(professorComboBox, gbc_professorComboBox);

		dayComboBox = new JComboBox();
		GridBagConstraints gbc_dayComboBox = new GridBagConstraints();
		gbc_dayComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_dayComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_dayComboBox.gridx = 1;
		gbc_dayComboBox.gridy = 3;
		formPane.add(dayComboBox, gbc_dayComboBox);

		descriptionTextArea = new JTextArea();
		descriptionTextArea.setLineWrap(true);
		GridBagConstraints gbc_descriptionTextArea = new GridBagConstraints();
		gbc_descriptionTextArea.insets = new Insets(0, 0, 5, 0);
		gbc_descriptionTextArea.fill = GridBagConstraints.HORIZONTAL;
		gbc_descriptionTextArea.gridx = 3;
		gbc_descriptionTextArea.gridy = 2;
		descriptionTextArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		formPane.add(descriptionTextArea, gbc_descriptionTextArea);

		maxStudentsComboBox = new JComboBox();
		GridBagConstraints gbc_maxStudentsComboBox = new GridBagConstraints();
		gbc_maxStudentsComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_maxStudentsComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_maxStudentsComboBox.gridx = 3;
		gbc_maxStudentsComboBox.gridy = 3;
		formPane.add(maxStudentsComboBox, gbc_maxStudentsComboBox);
	}

	private void createLabels() {
		JLabel courseNameLabel = new JLabel("Course Name");
		courseNameLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_courseNameLabel = new GridBagConstraints();
		gbc_courseNameLabel.insets = new Insets(0, 0, 5, 5);
		gbc_courseNameLabel.gridx = 0;
		gbc_courseNameLabel.gridy = 0;
		formPane.add(courseNameLabel, gbc_courseNameLabel);

		JLabel semesterLabel = new JLabel("Semester");
		semesterLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_semesterLabel = new GridBagConstraints();
		gbc_semesterLabel.insets = new Insets(0, 0, 5, 5);
		gbc_semesterLabel.gridx = 0;
		gbc_semesterLabel.gridy = 1;
		formPane.add(semesterLabel, gbc_semesterLabel);

		JLabel startTimeLabel = new JLabel("Start Time");
		startTimeLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_startTimeLabel = new GridBagConstraints();
		gbc_startTimeLabel.insets = new Insets(0, 0, 5, 5);
		gbc_startTimeLabel.gridx = 2;
		gbc_startTimeLabel.gridy = 0;
		formPane.add(startTimeLabel, gbc_startTimeLabel);

		JLabel endTimeLabel = new JLabel("End Time");
		endTimeLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_endTimeLabel = new GridBagConstraints();
		gbc_endTimeLabel.insets = new Insets(0, 0, 5, 5);
		gbc_endTimeLabel.gridx = 2;
		gbc_endTimeLabel.gridy = 1;
		formPane.add(endTimeLabel, gbc_endTimeLabel);

		JLabel professorLabel = new JLabel("Professor");
		professorLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_professorLabel = new GridBagConstraints();
		gbc_professorLabel.insets = new Insets(0, 0, 5, 5);
		gbc_professorLabel.gridx = 0;
		gbc_professorLabel.gridy = 2;
		formPane.add(professorLabel, gbc_professorLabel);

		JLabel descriptionLabel = new JLabel("<HTML>Description<br>(optional)</HTML>");
		descriptionLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_descriptionLabel = new GridBagConstraints();
		gbc_descriptionLabel.insets = new Insets(0, 0, 5, 5);
		gbc_descriptionLabel.gridx = 2;
		gbc_descriptionLabel.gridy = 2;
		formPane.add(descriptionLabel, gbc_descriptionLabel);

		JLabel dayLabel = new JLabel("Day");
		dayLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_dayLabel = new GridBagConstraints();
		gbc_dayLabel.insets = new Insets(0, 0, 5, 5);
		gbc_dayLabel.gridx = 0;
		gbc_dayLabel.gridy = 3;
		formPane.add(dayLabel, gbc_dayLabel);

		JLabel maxStudentsLabel = new JLabel("<HTML>Max Number<br>of Students</HTML>");
		maxStudentsLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_maxStudentsLabel = new GridBagConstraints();
		gbc_maxStudentsLabel.insets = new Insets(0, 0, 5, 5);
		gbc_maxStudentsLabel.gridx = 2;
		gbc_maxStudentsLabel.gridy = 3;
		formPane.add(maxStudentsLabel, gbc_maxStudentsLabel);
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
		gbl_startTimePane.columnWeights = new double[] { 0.3, 0.1, 0.6 };
		gbl_startTimePane.rowWeights = new double[] { 0.0 };
		startTimePane.setLayout(gbl_startTimePane);

		startTimeTextField = new JFormattedTextField(getTimeFormat());
		startTimeTextField.setHorizontalAlignment(SwingConstants.CENTER);
		startTimeTextField.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_startTimeTextField = new GridBagConstraints();
		gbc_startTimeTextField.insets = new Insets(0, 0, 0, 5);
		gbc_startTimeTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_startTimeTextField.gridx = 0;
		gbc_startTimeTextField.gridy = 0;
		startTimePane.add(startTimeTextField, gbc_startTimeTextField);

		startTimeComboBox = new JComboBox();
		startTimeComboBox.setFont(new Font("Tahoma", Font.PLAIN, 11));
		GridBagConstraints gbc_startTimeComboBox = new GridBagConstraints();
		gbc_startTimeComboBox.insets = new Insets(0, 0, 0, 5);
		gbc_startTimeComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_startTimeComboBox.gridx = 1;
		gbc_startTimeComboBox.gridy = 0;
		startTimePane.add(startTimeComboBox, gbc_startTimeComboBox);
		startTimeComboBox.addItem("AM");
		startTimeComboBox.addItem("PM");
		
		Component horizontalStrut = Box.createHorizontalStrut(50);
		GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
		gbc_horizontalStrut.gridx = 2;
		gbc_horizontalStrut.gridy = 0;
		startTimePane.add(horizontalStrut, gbc_horizontalStrut);
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
		gbl_endTimePane.columnWeights = new double[] { 0.3, 0.1, 0.6 };
		gbl_endTimePane.rowWeights = new double[] { 0.0 };
		endTimePane.setLayout(gbl_endTimePane);

		endTimeTextField = new JFormattedTextField(getTimeFormat());
		endTimeTextField.setHorizontalAlignment(SwingConstants.CENTER);
		endTimeTextField.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_endTimeTextField = new GridBagConstraints();
		gbc_endTimeTextField.insets = new Insets(0, 0, 0, 5);
		gbc_endTimeTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_endTimeTextField.gridx = 0;
		gbc_endTimeTextField.gridy = 0;
		endTimePane.add(endTimeTextField, gbc_endTimeTextField);

		endTimeComboBox = new JComboBox();
		endTimeComboBox.setFont(new Font("Tahoma", Font.PLAIN, 11));
		GridBagConstraints gbc_endTimeComboBox = new GridBagConstraints();
		gbc_endTimeComboBox.insets = new Insets(0, 0, 0, 5);
		gbc_endTimeComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_endTimeComboBox.gridx = 1;
		gbc_endTimeComboBox.gridy = 0;
		endTimePane.add(endTimeComboBox, gbc_endTimeComboBox);
		endTimeComboBox.addItem("AM");
		endTimeComboBox.addItem("PM");
		
		Component horizontalStrut = Box.createHorizontalStrut(50);
		GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
		gbc_horizontalStrut.gridx = 2;
		gbc_horizontalStrut.gridy = 0;
		endTimePane.add(horizontalStrut, gbc_horizontalStrut);
	}

	
	/**
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
	 * Add values to Combo Boxes
	 */

	public void setSemesterComboBox() {
		ArrayList<String> semesterList = new ArrayList<>();

		try {
			Connection connection = DBConnect.connection;
			String query = String.format("SELECT * FROM semesters");
			PreparedStatement stm = connection.prepareStatement(query);
			ResultSet resultSet = stm.executeQuery();

			while (resultSet.next()) {
				semesterList.add(resultSet.getString("semester"));
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		for (String semester : semesterList) {
			semesterComboBox.addItem(semester);
		}
	}

	public void setProfessorComboBox() {
		try {
			Connection connection = DBConnect.connection;
			String query = String.format("SELECT professor_id, first_name, last_name FROM professors");
			Statement stm = connection.createStatement();
			ResultSet resultSet = stm.executeQuery(query);

			while (resultSet.next()) {
				int id = resultSet.getInt("professor_id");
				String firstName = resultSet.getString("first_name");
				String lastName = resultSet.getString("last_name");
				
				professorComboBox.addCustomItem(id, firstName + " " + lastName);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setDaysComboBox() {
		String[] days = new String[] { "Monday", "Tuesday", "Wenesday", "Thursday", "Friday", "Saturday", "Sunday" };

		for (String day : days) {
			dayComboBox.addItem(day);
		}
	}

	public void setMaxStudentsComboBox() {
		for (int i = 1; i < 41; i++) {
			maxStudentsComboBox.addItem(i);
		}
	}
	
	
	/*
	 * Helper Methods
	 */

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
}
