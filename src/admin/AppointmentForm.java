package admin;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

import GUILook.GUILookAndFeel;
import admin.formlistener.AppointmentFormListener;
import custom.CustomTableModel;
import custom.RadioButtonEditor;
import custom.RadioButtonRenderer;
import dbconnect.DBConnect;

import java.awt.Color;
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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JButton;

public class AppointmentForm extends JPanel {

	private static final long serialVersionUID = 1L;
	private final int MAX_CHARACTERS = 254;
	private JPanel anchorPane;
	private JPanel contentPane;
	private JPanel formPane;
	private JScrollPane appointmentViewPane;
	private CustomTableModel tableModel;
	private JTable appointmentTable;
	private JLabel statusLabel;
	private JComboBox<String> statusComboBox;
	private JLabel notesLabel;
	private JTextArea notesTextArea;
	private JScrollPane scrollPane;
	private JButton submitButton;
	
	// for testing
	public static void main(String[] args) {
		DBConnect.connect();
		
		JFrame frame = new JFrame();
		frame.setContentPane(new AppointmentForm());
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
		GUILookAndFeel.setLookAndFeel();
	}
	
	/**
	 * Create the panel.
	 */
	public AppointmentForm() {
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
		gbl_formPane.columnWidths = new int[]{50, 200};
		gbl_formPane.rowHeights = new int[]{50, 80, 50};
		gbl_formPane.columnWeights = new double[]{0.0, 0.0};
		gbl_formPane.rowWeights = new double[]{0.0, 0.0, 0.0};
		formPane.setLayout(gbl_formPane);
		
		createLabels();
		createFormInputComponents();
		createAppointmentScrollPane();
		createButtonAndListener();
		
		addNotesTextAreaListener();
	}
	
	private void createLabels() {
		JLabel titleLabel = new JLabel("Student Appointments");
		GridBagConstraints gbc_titleLabel = new GridBagConstraints();
		titleLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		gbc_titleLabel.insets = new Insets(0, 0, 5, 0);
		gbc_titleLabel.gridx = 0;
		gbc_titleLabel.gridy = 0;
		add(titleLabel, gbc_titleLabel);
		
		statusLabel = new JLabel("Mark Appointment as:");
		statusLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
		GridBagConstraints gbc_statusLabel = new GridBagConstraints();
		gbc_statusLabel.insets = new Insets(0, 0, 5, 25);
		gbc_statusLabel.gridx = 0;
		gbc_statusLabel.gridy = 0;
		formPane.add(statusLabel, gbc_statusLabel);
		
		notesLabel = new JLabel("Notes:");
		notesLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
		GridBagConstraints gbc_notesLabel = new GridBagConstraints();
		gbc_notesLabel.insets = new Insets(0, 0, 5, 5);
		gbc_notesLabel.gridx = 0;
		gbc_notesLabel.gridy = 1;
		formPane.add(notesLabel, gbc_notesLabel);
	}
	
	private void createFormInputComponents() {
		statusComboBox = new JComboBox();
		statusComboBox.setFont(new Font("Tahoma", Font.PLAIN, 13));
		GridBagConstraints gbc_statusComboBox = new GridBagConstraints();
		gbc_statusComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_statusComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_statusComboBox.gridx = 1;
		gbc_statusComboBox.gridy = 0;
		formPane.add(statusComboBox, gbc_statusComboBox);
		
		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 1;
		formPane.add(scrollPane, gbc_scrollPane);
		
		notesTextArea = new JTextArea();
		scrollPane.setViewportView(notesTextArea);
		notesTextArea.setWrapStyleWord(true);
		notesTextArea.setLineWrap(true);
		notesTextArea.setFont(new Font("Tahoma", Font.PLAIN, 13));
		notesTextArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
	}
	
	private void createAppointmentScrollPane() {
		String[] columnNames = {"Select Appointment", "Appointment ID", "Student ID", "Student Name", "Date", "Time", "Notes", "Status"};
		String[][] rowData = getAppointmentsFromDB();
		
		tableModel = new CustomTableModel(rowData, columnNames);
		AppointmentFormListener listener = new AppointmentFormListener(this, tableModel, statusComboBox, notesTextArea);
		tableModel.addTableModelListener(listener);
		
		ButtonGroup buttonGroup = new ButtonGroup();
		
		// add radio buttons in the first column
		for(int i = 0; i < tableModel.getRowCount(); i++) {
			JRadioButton radioButton = new JRadioButton();
			buttonGroup.add(radioButton);
			tableModel.setValueAt(radioButton, i, 0);
		}
		
		appointmentTable = new JTable(tableModel);
		appointmentTable.setPreferredScrollableViewportSize(new Dimension(0, 0));
		appointmentTable.setFont(new Font("Tahoma", Font.BOLD, 14));
		appointmentTable.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));
		appointmentTable.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		appointmentTable.setRowHeight(appointmentTable.getRowHeight() + 8);
		
		appointmentTable.getColumn("Select Appointment").setCellEditor(new RadioButtonEditor(new JCheckBox()));
		appointmentTable.getColumn("Select Appointment").setCellRenderer(new RadioButtonRenderer());
		
		appointmentViewPane = new JScrollPane(appointmentTable);
		GridBagConstraints gbc_appointmentViewPane = new GridBagConstraints();
		gbc_appointmentViewPane.insets = new Insets(0, 0, 5, 0);
		gbc_appointmentViewPane.fill = GridBagConstraints.BOTH;
		gbc_appointmentViewPane.gridx = 0;
		gbc_appointmentViewPane.gridy = 0;
		contentPane.add(appointmentViewPane, gbc_appointmentViewPane);
	}
	
	private void createButtonAndListener() {
		submitButton = new JButton("Submit");
		submitButton.setFont(new Font("Tahoma", Font.BOLD, 13));
		GridBagConstraints gbc_submitButton = new GridBagConstraints();
		gbc_submitButton.gridx = 1;
		gbc_submitButton.gridy = 2;
		formPane.add(submitButton, gbc_submitButton);
		statusComboBox.addItem("Pending");
		statusComboBox.addItem("Completed");
		
		AppointmentFormListener listener = new AppointmentFormListener(this, tableModel, statusComboBox, notesTextArea);
		submitButton.addActionListener(listener);
	}
	
	/*
	 * Action/Key Listeners
	 */
	
	// Sets character limit of text area to 255
	private void addNotesTextAreaListener() {
		notesTextArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if(notesTextArea.getText().length() > MAX_CHARACTERS+1) {
					e.consume();
					String shortenedString = notesTextArea.getText().substring(0, MAX_CHARACTERS);
					notesTextArea.setText(shortenedString);
				}
				else if(notesTextArea.getText().length() > MAX_CHARACTERS) {
					e.consume();
				}
			}
		});
	}
	
	/*
	 * Helper Methods
	 */
	
	private String[][] getAppointmentsFromDB() {
		List<String[]> appointmentList = new ArrayList();
		
		try {
			Connection connection = DBConnect.connection;
			String query = String.format("SELECT * FROM appointments JOIN students ON appointments.student_id = students.student_id");
			Statement stm = connection.createStatement();
			ResultSet resultSet = stm.executeQuery(query);
			
			while(resultSet.next()) {
				String appointmentId = resultSet.getInt("appointment_id")+"";
				String studentId = resultSet.getInt("students.student_id")+"";
				String studentName = resultSet.getString("first_name") + " " + resultSet.getString("last_name");
				String date = resultSet.getString("appointment_date");
				String time = resultSet.getString("appointment_time");
				String notes = resultSet.getString("appointment_notes");
				String status = resultSet.getString("appointment_status");
				appointmentList.add(new String[]{"", appointmentId, studentId, studentName, date, time, notes, status});
			}
			
			stm.close();
			return appointmentList.toArray(new String[0][0]);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private void clearTextFields() {
		notesTextArea.setText("");
	}
	
	/*
	 * Public methods
	 */
	
	public void updateTableModel() {
		String[] columnNames = {"Select Appointment", "Appointment ID", "Student ID", "Student Name", "Date", "Time", "Notes", "Status"};
		String[][] rowData = getAppointmentsFromDB();
		
		tableModel.setDataVector(rowData, columnNames);
		ButtonGroup buttonGroup = new ButtonGroup();
		
		for(int i = 0; i < tableModel.getRowCount(); i++) {
			JRadioButton radioButton = new JRadioButton();
			buttonGroup.add(radioButton);
			tableModel.setValueAt(radioButton, i, 0);
		}
		
		appointmentTable.getColumn("Select Appointment").setCellEditor(new RadioButtonEditor(new JCheckBox()));
		appointmentTable.getColumn("Select Appointment").setCellRenderer(new RadioButtonRenderer());
		
		tableModel.fireTableDataChanged();
		clearTextFields();
	}
}
