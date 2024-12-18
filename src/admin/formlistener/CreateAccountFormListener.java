package admin.formlistener;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import admin.AccountTypes;
import custom.PopupDialog;
import dbconnect.DBConnect;
import encryption.EncryptionManager;

public class CreateAccountFormListener implements ActionListener{
	private JPanel formPane;
	private JTextField usernameTextField; 
	private JTextField firstNameTextField;
	private JTextField lastNameTextField;
	private JPasswordField tempPassTextField;
	private JComboBox<String> accountTypeComboBox;
	

	public CreateAccountFormListener(JPanel formPane, JTextField usernameTextField, 
										JTextField firstNameTextField, JTextField lastNameTextField, 
											JPasswordField tempPassTextField, JComboBox<String> accountTypeComboBox) {
		this.formPane = formPane;
		this.usernameTextField = usernameTextField;
		this.firstNameTextField = firstNameTextField;
		this.lastNameTextField = lastNameTextField;
		this.tempPassTextField = tempPassTextField;
		this.accountTypeComboBox = accountTypeComboBox;
	}
	
	/*
	 * Handle Button Click: 
	 *  - Add account info to DB
	 */
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(isFieldEmpty()) {
			showPopupMessage("One Or More Fields Are Empty.", "Error!");
		}
		else {
			if(!isUsernameUnique(usernameTextField.getText())) {
				showPopupMessage("Username Already Exists.", "Error!");
			}
			else {
				boolean queryIsSuccessful = addAccountToDB();
				if(queryIsSuccessful) {
					String message = accountTypeComboBox.getSelectedItem() + " Account Successfully Added.";
					showPopupMessage(message, "");
				}
			}
		}
	}
	
	private void handleButtonClick() {
		
	}
	
	private boolean addAccountToDB() {
		String username = usernameTextField.getText();
		String  firstName = firstNameTextField.getText();
		String lastName = lastNameTextField.getText();
		String tempPass = EncryptionManager.encrypt(String.valueOf(tempPassTextField.getPassword()));
		AccountTypes accountType = AccountTypes.valueOf(accountTypeComboBox.getSelectedItem().toString().toUpperCase());
		String table;
		if(accountType.getKey() == 1) {
			table = "students";
		}
		else {
			table = "professors";
		}
		
		try {
			Connection connection = DBConnect.connection;
			String query = String.format("INSERT INTO accounts (username, password, is_password_temporary, account_type_id) VALUES ('%s', '%s', '%s', '%s')", 
											username, tempPass, 1, accountType.getKey());
            PreparedStatement stm = connection.prepareStatement(query);
            int rows = stm.executeUpdate(query); //returns the number of rows affected
            
            // get user_id generated from DB
            query = String.format("SELECT user_id FROM accounts WHERE username = '%s'", username);
            Statement stm1 = connection.createStatement();
            ResultSet resultSet = stm1.executeQuery(query);
            
            int id = 0;
            while(resultSet.next()) {
                id = resultSet.getInt("user_id");
            }
            
            query = String.format("INSERT INTO %s VALUES (%s, '%s', '%s')", table, id, firstName, lastName);
            PreparedStatement stm2 = connection.prepareStatement(query);
            int rows1 = stm.executeUpdate(query);

            stm.close();
            stm1.close();
            stm2.close();
            if(rows > 0 && rows1 > 0) {
                return true;
            }
            return false;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	/*
	 * Helper Methods
	 */
	
	private boolean isFieldEmpty() {
		Component[] components = formPane.getComponents();
		
		for(Component component : components) {
			if(component instanceof JTextField textField) {
				if(textField.getText().isBlank()) {
					return true;
				}
			}
		}
		
		
		if(accountTypeComboBox.getSelectedItem() == null) {
			return true;
		}
		
		return false;
	}
	
	private boolean isUsernameUnique(String username) {
		try {
			Connection connection = DBConnect.connection;
			String query = String.format("SELECT username FROM accounts WHERE username = '%s'", username);
			Statement stm = connection.createStatement();
			ResultSet resultSet = stm.executeQuery(query);
			
			if(resultSet.next()) {
				return false;
			}
			return true;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private void showPopupMessage(String message, String title) {
		JFrame frame = (JFrame) formPane.getTopLevelAncestor();
		Point location = new Point();
		int x = frame.getX() + (frame.getWidth() / 2);
		int y = frame.getY() + (frame.getHeight() / 2);
		location.setLocation(x, y);
		
		PopupDialog dialog = new PopupDialog(message);
		dialog.setLocation(location);
		dialog.pack();
		dialog.setTitle(title);
		dialog.setVisible(true);
	}
}
