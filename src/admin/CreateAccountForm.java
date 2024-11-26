package admin;

import javax.swing.JPanel;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTextField;

import admin.formlistener.CreateAccountFormListener;
import dbconnect.DBConnect;

import javax.swing.JPasswordField;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JButton;

public class CreateAccountForm extends JPanel {

	private static final long serialVersionUID = 1L;
	private JPanel formPane;
	private JTextField usernameTextField;
	private JTextField firstNameTextField;
	private JTextField lastNameTextField;
	private JPasswordField tempPassTextField;
	private JComboBox<String> accountTypeComboBox;
	private JButton submitButton;

	/**
	 * Create the panel.
	 */
	public CreateAccountForm() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		gridBagLayout.columnWidths = new int[]{678, 0};
		gridBagLayout.rowHeights = new int[]{17, 250, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		// Anchor pane prevents child components from separating when expanding the window
		JPanel anchorPane = new JPanel();
		GridBagConstraints gbc_anchorPane = new GridBagConstraints();
		gbc_anchorPane.fill = GridBagConstraints.BOTH;
		gbc_anchorPane.gridx = 0;
		gbc_anchorPane.gridy = 1;
		add(anchorPane, gbc_anchorPane);
		
		formPane = new JPanel();
		anchorPane.add(formPane);
		GridBagLayout gbl_formPane = new GridBagLayout();
		gbl_formPane.columnWidths = new int[]{100, 200};
		gbl_formPane.rowHeights = new int[]{52, 52, 52, 52, 52, 52};
		gbl_formPane.columnWeights = new double[]{0.1, 1.0};
		gbl_formPane.rowWeights = new double[]{0.1, 0.1, 0.1, 0.1, 0.1, 0.1};
		formPane.setLayout(gbl_formPane);
		
		accountTypeComboBox = new JComboBox();
		GridBagConstraints gbc_accountTypeComboBox = new GridBagConstraints();
		gbc_accountTypeComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_accountTypeComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_accountTypeComboBox.gridx = 1;
		gbc_accountTypeComboBox.gridy = 4;
		formPane.add(accountTypeComboBox, gbc_accountTypeComboBox);
		accountTypeComboBox.addItem(AccountTypes.STUDENT.getValue());
		accountTypeComboBox.addItem(AccountTypes.PROFESSOR.getValue());
		
		createLabels();
		createTextFields();
		createButtonAndListener();
	}
	
	private void createLabels() {
		JLabel titleLabel = new JLabel("Create An Account");
		titleLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		GridBagConstraints gbc_titleLabel = new GridBagConstraints();
		gbc_titleLabel.insets = new Insets(0, 0, 5, 0);
		gbc_titleLabel.gridx = 0;
		gbc_titleLabel.gridy = 0;
		add(titleLabel, gbc_titleLabel);
		
		JLabel usernameLabel = new JLabel("Username");
		usernameLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_usernameLabel = new GridBagConstraints();
		gbc_usernameLabel.insets = new Insets(0, 0, 5, 5);
		gbc_usernameLabel.gridx = 0;
		gbc_usernameLabel.gridy = 0;
		formPane.add(usernameLabel, gbc_usernameLabel);
		
		JLabel firstNameLabel = new JLabel("First Name");
		firstNameLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_firstNameLabel = new GridBagConstraints();
		gbc_firstNameLabel.insets = new Insets(0, 0, 5, 5);
		gbc_firstNameLabel.gridx = 0;
		gbc_firstNameLabel.gridy = 1;
		formPane.add(firstNameLabel, gbc_firstNameLabel);
		
		JLabel lastNameLabel = new JLabel("Last Name");
		lastNameLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_lastNameLabel = new GridBagConstraints();
		gbc_lastNameLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lastNameLabel.gridx = 0;
		gbc_lastNameLabel.gridy = 2;
		formPane.add(lastNameLabel, gbc_lastNameLabel);
		
		JLabel temporaryPasswordLabel = new JLabel("<HTML>Temporary<br>Password</HTML>");
		temporaryPasswordLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_temporaryPasswordLabel = new GridBagConstraints();
		gbc_temporaryPasswordLabel.insets = new Insets(0, 0, 5, 5);
		gbc_temporaryPasswordLabel.gridx = 0;
		gbc_temporaryPasswordLabel.gridy = 3;
		formPane.add(temporaryPasswordLabel, gbc_temporaryPasswordLabel);
		
		JLabel accountTypeLabel = new JLabel("Account Type");
		accountTypeLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_accountTypeLabel = new GridBagConstraints();
		gbc_accountTypeLabel.insets = new Insets(0, 0, 5, 5);
		gbc_accountTypeLabel.gridx = 0;
		gbc_accountTypeLabel.gridy = 4;
		formPane.add(accountTypeLabel, gbc_accountTypeLabel);
	}
	
	private void createTextFields() {
		usernameTextField = new JTextField();
		GridBagConstraints gbc_usernameTextField = new GridBagConstraints();
		gbc_usernameTextField.insets = new Insets(0, 0, 5, 0);
		gbc_usernameTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_usernameTextField.gridx = 1;
		gbc_usernameTextField.gridy = 0;
		formPane.add(usernameTextField, gbc_usernameTextField);
		usernameTextField.setColumns(10);
		
		firstNameTextField = new JTextField();
		GridBagConstraints gbc_firstNameTextField = new GridBagConstraints();
		gbc_firstNameTextField.insets = new Insets(0, 0, 5, 0);
		gbc_firstNameTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_firstNameTextField.gridx = 1;
		gbc_firstNameTextField.gridy = 1;
		formPane.add(firstNameTextField, gbc_firstNameTextField);
		firstNameTextField.setColumns(10);
		
		lastNameTextField = new JTextField();
		GridBagConstraints gbc_lastNameTextField = new GridBagConstraints();
		gbc_lastNameTextField.insets = new Insets(0, 0, 5, 0);
		gbc_lastNameTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_lastNameTextField.gridx = 1;
		gbc_lastNameTextField.gridy = 2;
		formPane.add(lastNameTextField, gbc_lastNameTextField);
		lastNameTextField.setColumns(10);
		
		tempPassTextField = new JPasswordField();
		GridBagConstraints gbc_tempPassTextField = new GridBagConstraints();
		gbc_tempPassTextField.insets = new Insets(0, 0, 5, 0);
		gbc_tempPassTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_tempPassTextField.gridx = 1;
		gbc_tempPassTextField.gridy = 3;
		formPane.add(tempPassTextField, gbc_tempPassTextField);
	}
	
	private void createButtonAndListener() {
		submitButton = new JButton("Create Account");
		GridBagConstraints gbc_createAccountButton = new GridBagConstraints();
		gbc_createAccountButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_createAccountButton.gridx = 1;
		gbc_createAccountButton.gridy = 5;
		formPane.add(submitButton, gbc_createAccountButton);
		
		submitButton.addActionListener(new CreateAccountFormListener(formPane, usernameTextField, 
																		firstNameTextField, lastNameTextField, 
																			tempPassTextField, accountTypeComboBox));
	}
	
	
}
