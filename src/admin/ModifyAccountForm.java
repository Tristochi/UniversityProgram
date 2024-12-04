package admin;

import javax.swing.BorderFactory;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import java.awt.GridBagLayout;
import javax.swing.JLabel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;

import dbconnect.DBConnect;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import GUILook.GUILookAndFeel;
import admin.formlistener.ModifyAccountFormListener;
import admin.formlistener.ModifyAccountTableListener;
import custom.CustomTableModel;
import custom.RadioButtonEditor;
import custom.RadioButtonRenderer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPasswordField;

public class ModifyAccountForm extends JPanel {

	private static final long serialVersionUID = 1L;
	private JPanel anchorPane;
	private JPanel contentPane;
	private JPanel formPane;
	
	private JScrollPane accountViewPane;
	private JLabel idLabel;
	private JLabel firstNameLabel;
	private JTextField idTextField;
	private JTextField firstNameTextField;
	private JLabel usernameLabel;
	private JLabel passwordLabel;
	private JTextField usernameTextField;
	private JLabel lastNameLabel;
	private JButton submitButton;
	private JTextField lastNameTextField;
	private JTable accountTable;
	CustomTableModel tableModel;
	ButtonGroup buttonGroup;
	private JPasswordField passwordTextField;
	Map<String, JComponent> componentMap;
	
	
	// for testing
	public static void main(String[] args) {
		DBConnect.connect();
		
		JFrame frame = new JFrame();
		frame.setContentPane(new ModifyAccountForm());
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
		GUILookAndFeel.setLookAndFeel();
	}

	/**
	 * Create the panel.
	 */
	
	public ModifyAccountForm() {
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
		gbl_formPane.columnWidths = new int[]{50, 150, 50, 150};
		gbl_formPane.rowHeights = new int[]{50, 50, 50, 50};
		gbl_formPane.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0};
		gbl_formPane.rowWeights = new double[]{0.0, 0.0, 0.0};
		formPane.setLayout(gbl_formPane);
		
		createLabels();
		createTextFields();
		
		createComponentHashMap();
		
		createAccountInfoScrollPane();
		createButtonAndListener();
	}
	
	private void createComponentHashMap() {
		componentMap = new HashMap();
		componentMap.put("mainPane", this);
		componentMap.put("formPane", formPane);
		componentMap.put("idTextField", idTextField);
		componentMap.put("firstNameTextField", firstNameTextField);
		componentMap.put("lastNameTextField", lastNameTextField);
		componentMap.put("usernameTextField", usernameTextField);
		componentMap.put("passwordTextField", passwordTextField);
	}
	
	private void createLabels(){
		JLabel titleLabel = new JLabel("Modify An Account");
		GridBagConstraints gbc_titleLabel = new GridBagConstraints();
		titleLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		gbc_titleLabel.insets = new Insets(0, 0, 5, 0);
		gbc_titleLabel.gridx = 0;
		gbc_titleLabel.gridy = 0;
		add(titleLabel, gbc_titleLabel);
		
		idLabel = new JLabel("ID");
		idLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
		GridBagConstraints gbc_idLabel = new GridBagConstraints();
		gbc_idLabel.insets = new Insets(0, 0, 5, 5);
		gbc_idLabel.gridx = 0;
		gbc_idLabel.gridy = 0;
		formPane.add(idLabel, gbc_idLabel);
		
		firstNameLabel = new JLabel("First Name");
		firstNameLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
		GridBagConstraints gbc_firstNameLabel = new GridBagConstraints();
		gbc_firstNameLabel.insets = new Insets(0, 0, 5, 5);
		gbc_firstNameLabel.gridx = 0;
		gbc_firstNameLabel.gridy = 1;
		formPane.add(firstNameLabel, gbc_firstNameLabel);
		
		lastNameLabel = new JLabel("Last Name");
		lastNameLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
		GridBagConstraints gbc_lastNameLabel = new GridBagConstraints();
		gbc_lastNameLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lastNameLabel.gridx = 0;
		gbc_lastNameLabel.gridy = 2;
		formPane.add(lastNameLabel, gbc_lastNameLabel);
		
		usernameLabel = new JLabel("Username");
		usernameLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
		GridBagConstraints gbc_usernameLabel = new GridBagConstraints();
		gbc_usernameLabel.insets = new Insets(0, 20, 5, 5);
		gbc_usernameLabel.gridx = 2;
		gbc_usernameLabel.gridy = 0;
		formPane.add(usernameLabel, gbc_usernameLabel);
		
		passwordLabel = new JLabel("New Password (i)");
		passwordLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
		passwordLabel.setToolTipText("Leave this field blank if password doesn't need to be changed.");
		GridBagConstraints gbc_passwordLabel = new GridBagConstraints();
		gbc_passwordLabel.anchor = GridBagConstraints.EAST;
		gbc_passwordLabel.insets = new Insets(0, 20, 5, 5);
		gbc_passwordLabel.gridx = 2;
		gbc_passwordLabel.gridy = 1;
		formPane.add(passwordLabel, gbc_passwordLabel);
	}
	
	private void createTextFields() {
		idTextField = new JTextField();
		idTextField.setFont(new Font("Tahoma", Font.PLAIN, 13));
		idTextField.setEditable(false);
		GridBagConstraints gbc_idTextField = new GridBagConstraints();
		gbc_idTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_idTextField.insets = new Insets(0, 0, 5, 5);
		gbc_idTextField.gridx = 1;
		gbc_idTextField.gridy = 0;
		formPane.add(idTextField, gbc_idTextField);
		idTextField.setColumns(10);
		
		firstNameTextField = new JTextField();
		firstNameTextField.setFont(new Font("Tahoma", Font.PLAIN, 13));
		GridBagConstraints gbc_firstNameTextField = new GridBagConstraints();
		gbc_firstNameTextField.insets = new Insets(0, 0, 5, 5);
		gbc_firstNameTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_firstNameTextField.gridx = 1;
		gbc_firstNameTextField.gridy = 1;
		formPane.add(firstNameTextField, gbc_firstNameTextField);
		firstNameTextField.setColumns(10);
		
		lastNameTextField = new JTextField();
		lastNameTextField.setFont(new Font("Tahoma", Font.PLAIN, 13));
		GridBagConstraints gbc_lastNameTextField = new GridBagConstraints();
		gbc_lastNameTextField.insets = new Insets(0, 0, 5, 5);
		gbc_lastNameTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_lastNameTextField.gridx = 1;
		gbc_lastNameTextField.gridy = 2;
		formPane.add(lastNameTextField, gbc_lastNameTextField);
		lastNameTextField.setColumns(10);
		
		usernameTextField = new JTextField();
		usernameTextField.setFont(new Font("Tahoma", Font.PLAIN, 13));
		GridBagConstraints gbc_usernameTextField = new GridBagConstraints();
		gbc_usernameTextField.insets = new Insets(0, 0, 5, 0);
		gbc_usernameTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_usernameTextField.gridx = 3;
		gbc_usernameTextField.gridy = 0;
		formPane.add(usernameTextField, gbc_usernameTextField);
		usernameTextField.setColumns(10);
		
		passwordTextField = new JPasswordField();
		passwordTextField.setFont(new Font("Tahoma", Font.PLAIN, 13));
		passwordTextField.setToolTipText("Leave this field blank if password doesn't need to be changed.");
		GridBagConstraints gbc_passwordTextField = new GridBagConstraints();
		gbc_passwordTextField.insets = new Insets(0, 0, 5, 0);
		gbc_passwordTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordTextField.gridx = 3;
		gbc_passwordTextField.gridy = 1;
		formPane.add(passwordTextField, gbc_passwordTextField);
	}
	
	private void createButtonAndListener() {
		submitButton = new JButton("Submit Changes");
		submitButton.setFont(new Font("Tahoma", Font.BOLD, 13));
		GridBagConstraints gbc_submitButton = new GridBagConstraints();
		gbc_submitButton.insets = new Insets(0, 0, 5, 0);
		gbc_submitButton.gridx = 3;
		gbc_submitButton.gridy = 2;
		formPane.add(submitButton, gbc_submitButton);
		
		ModifyAccountFormListener listener = new ModifyAccountFormListener(componentMap, tableModel);
		submitButton.addActionListener(listener);
	}
	
	public void createAccountInfoScrollPane() {
		String[] columnNames = {"Select Account", "Id", "Username", "First Name", "Last Name", "Account Type"};
		String[][] rowData = getAccountInfoFromDB();
		
		tableModel = new CustomTableModel(rowData, columnNames);
		ModifyAccountTableListener tableListener = new ModifyAccountTableListener(componentMap, tableModel);
		tableModel.addTableModelListener(tableListener);
		buttonGroup = new ButtonGroup();
		
		// add radio buttons in the first column
		for(int i = 0; i < tableModel.getRowCount(); i++) {
			JRadioButton radioButton = new JRadioButton();
			buttonGroup.add(radioButton);
			tableModel.setValueAt(radioButton, i, 0);
		}
		
		accountTable = new JTable(tableModel);
		accountTable.setPreferredScrollableViewportSize(new Dimension(0, 0));
		accountTable.setFont(new Font("Tahoma", Font.PLAIN, 15));
		accountTable.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));
		accountTable.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		accountTable.setRowHeight(accountTable.getRowHeight() + 8);
		
		accountTable.getColumn("Select Account").setCellEditor(new RadioButtonEditor(new JCheckBox()));
		accountTable.getColumn("Select Account").setCellRenderer(new RadioButtonRenderer());
		
		accountViewPane = new JScrollPane(accountTable);
		GridBagConstraints gbc_accountViewPane = new GridBagConstraints();
		gbc_accountViewPane.insets = new Insets(0, 0, 5, 0);
		gbc_accountViewPane.fill = GridBagConstraints.BOTH;
		gbc_accountViewPane.gridx = 0;
		gbc_accountViewPane.gridy = 0;
		contentPane.add(accountViewPane, gbc_accountViewPane);
	}
	
	/**
	 * Helper Methods
	 */
	
	private String[][] getAccountInfoFromDB() {
		List<String[]> accountInfo = new ArrayList<>();
		
		try {
			Connection connection = DBConnect.connection;
			String query = String.format("SELECT user_id, username, account_type_id FROM accounts WHERE account_type_id != 3 ORDER BY user_id");
			Statement stm = connection.createStatement();
			ResultSet resultSet = stm.executeQuery(query);
			
			while(resultSet.next()) {
				int id = resultSet.getInt("user_id");
				String username = resultSet.getString("username");
				int accountType = resultSet.getInt("account_type_id");
				
				// string is "Student" or "Professor" if accountType is 1 or 2 respectively
				String accountTypeString = AccountTypes.valueOf(accountType).getValue();
				
				String fullName[] = getUserFullName(id, accountType);
				String firstName = fullName[0]; 
				String lastName = fullName[1];
				
				accountInfo.add(new String[]{"", ""+id, username, firstName, lastName, accountTypeString});
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return accountInfo.toArray(new String[0][0]);
	}
	
	private String[] getUserFullName(int id, int accountType) {
		String table;
		String idString;
		String fullName[] = null;
		if(accountType == 1) {
			table = "students";
			idString = "student_id";
		}
		else {
			table = "professors";
			idString = "professor_id";
		}
		
		try {
			Connection connection = DBConnect.connection;
			String query = String.format("SELECT * FROM %s WHERE %s = '%s'", table, idString, id);
			Statement stm = connection.createStatement();
			ResultSet resultSet = stm.executeQuery(query);
			
			while(resultSet.next()) {
				fullName = new String[]{resultSet.getString("first_name"), resultSet.getString("last_name")};
				break;
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		return fullName;
	}
	
	/*
	 * Public Methods
	 */
	
	public void updateTableModel() {
		String[] columnNames = {"Select Account", "Id", "Username", "First Name", "Last Name", "Account Type"};
		String[][] rowData = getAccountInfoFromDB();
		
		tableModel.setDataVector(rowData, columnNames);
		ButtonGroup buttonGroup = new ButtonGroup();
		
		// add radio buttons in the first column
		for(int i = 0; i < tableModel.getRowCount(); i++) {
			JRadioButton radioButton = new JRadioButton();
			buttonGroup.add(radioButton);
			tableModel.setValueAt(radioButton, i, 0);
		}
		
		accountTable.getColumn("Select Account").setCellEditor(new RadioButtonEditor(new JCheckBox()));
		accountTable.getColumn("Select Account").setCellRenderer(new RadioButtonRenderer());
		
		tableModel.fireTableDataChanged();
		clearTextFields();
	}
	
	public void clearTextFields() {
		for(Component component : formPane.getComponents()) {
			if(component instanceof JTextField textField) {
				textField.setText("");
			}
		}
	}
}
