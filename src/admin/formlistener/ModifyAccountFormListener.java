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

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import admin.AccountTypes;
import custom.CustomTableModel;
import custom.PopupDialog;
import dbconnect.DBConnect;
import encryption.EncryptionManager;

public class ModifyAccountFormListener implements ActionListener {
	private CustomTableModel tableModel;
	private JPanel formPane;
	private JTextField idTextField;
	private JTextField firstNameTextField;
	private JTextField lastNameTextField;
	private JTextField usernameTextField;
	private JPasswordField passwordTextField;
	

	public ModifyAccountFormListener(CustomTableModel tableModel, JPanel formPane, JTextField idTextField,
										JTextField firstNameTextField, JTextField lastNameTextField, JTextField usernameTextField,
											JPasswordField passwordTextField) {
		this.tableModel = tableModel;
		this.formPane = formPane;
		this.idTextField = idTextField;
		this.firstNameTextField = firstNameTextField;
		this.lastNameTextField = lastNameTextField;
		this.usernameTextField = usernameTextField;
		this.passwordTextField = passwordTextField;
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
			updateTableModel();
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
	
	private void updateTableModel() {
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
		
		tableModel.updateData(accountInfo.toArray(new String[0][0]));
		ButtonGroup buttonGroup = new ButtonGroup();
		
		// add radio buttons in the first column
		for(int i = 0; i < tableModel.getRowCount(); i++) {
			JRadioButton radioButton = new JRadioButton();
			buttonGroup.add(radioButton);
			tableModel.setValueAt(radioButton, i, 0);
		}
		
		tableModel.fireTableDataChanged();
		clearTextFields();
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
	
	private void clearTextFields() {
		for(Component component : formPane.getComponents()) {
			if(component instanceof JTextField textField) {
				textField.setText("");
			}
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
