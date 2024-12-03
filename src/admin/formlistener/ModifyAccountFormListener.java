package admin.formlistener;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import admin.AccountTypes;
import admin.ModifyAccountForm;
import custom.CustomTableModel;
import custom.PopupDialog;
import dbconnect.DBConnect;
import encryption.EncryptionManager;

public class ModifyAccountFormListener implements ActionListener {
	private ModifyAccountForm mainPane;
	private CustomTableModel tableModel;
	private JPanel formPane;
	private JTextField idTextField;
	private JTextField firstNameTextField;
	private JTextField lastNameTextField;
	private JTextField usernameTextField;
	private JPasswordField passwordTextField;
	

	public ModifyAccountFormListener(Map<String, JComponent> componentMap, CustomTableModel tableModel) {
		this.mainPane = (ModifyAccountForm) componentMap.get("mainPane");
		this.tableModel = tableModel;
		this.formPane = (JPanel) componentMap.get("formPane");
		this.idTextField = (JTextField) componentMap.get("idTextField");
		this.firstNameTextField = (JTextField) componentMap.get("firstNameTextField");
		this.lastNameTextField = (JTextField) componentMap.get("lastNameTextField");
		this.usernameTextField = (JTextField) componentMap.get("usernameTextField");
		this.passwordTextField = (JPasswordField) componentMap.get("passwordTextField");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(!isAccountSelected()) {
			showPopupMessage("No Account Selected.", "Error!");
			return;
		}
		if(!isUsernameUnique(usernameTextField.getText())) {
			showPopupMessage("Username Already Exists.", "Error!");
			return;
		}
		boolean queryIsSuccessful = updateAccountToDB();
		if(queryIsSuccessful) {
			showPopupMessage("Account Successfully Updated.", "");
			mainPane.updateTableModel();
		}
	}

	/*
	 * Helper Methods
	 */
	
	private boolean updateAccountToDB() {
		int id = Integer.parseInt(idTextField.getText());
		String username = usernameTextField.getText();
		String firstName = firstNameTextField.getText();
		String lastName = lastNameTextField.getText();
		String password = String.valueOf(passwordTextField.getPassword());
		String accountType = tableModel.getRowDataAtIndex(tableModel.getSelectedRowIndex())[4];
		String dbTable = accountType.toLowerCase() + "s";
		String dbColumn = accountType.toLowerCase() + "_id";
		String query1;
		String query2;
		
		// set the right queries based on the changes the admin made.
		if(!password.isBlank()) {
			password = EncryptionManager.encrypt(password);
			query1 = String.format("UPDATE accounts SET username = '%s', password = '%s' WHERE user_id = '%s'", username, password, id);
		} 
		else {
			query1 = String.format("UPDATE accounts SET username = '%s' WHERE user_id = '%s'", username, id);
		}
		query2 = String.format("UPDATE %s SET first_name = '%s', last_name = '%s' WHERE %s = '%s'", dbTable, firstName, lastName, dbColumn, id);
		
		try {
			Connection connection = DBConnect.connection;
			Statement stm = connection.createStatement();
			int rows1 = stm.executeUpdate(query1);
			int rows2 = stm.executeUpdate(query2);
			
			stm.close();
			if(rows1 > 0 && rows2 > 0) {
				return true;
			}
			return false;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private boolean isUsernameUnique(String newUsername) {
		String oldUsername = tableModel.getRowDataAtIndex(tableModel.getSelectedRowIndex())[1]; // usernames are at index 1;
		if(oldUsername.equals(newUsername)) {
			return true;
		}
		
		try {
			Connection connection = DBConnect.connection;
			String query = String.format("SELECT username FROM accounts WHERE username = '%s'", newUsername);
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
	
	private boolean isAccountSelected() {
		if(tableModel.getSelectedRowIndex() == -1) {
			return false;
		}
		return true;
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
