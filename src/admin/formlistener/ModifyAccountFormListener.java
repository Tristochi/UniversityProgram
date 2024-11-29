package admin.formlistener;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import admin.ModifyAccountForm;
import custom.CustomTableModel;
import custom.PopupDialog;
import dbconnect.DBConnect;
import encryption.EncryptionManager;

public class ModifyAccountFormListener implements ActionListener {
	private CustomTableModel tableModel;
	private ModifyAccountForm mainPane;
	private JTextField idTextField;
	private JTextField firstNameTextField;
	private JTextField lastNameTextField;
	private JTextField usernameTextField;
	private JPasswordField passwordTextField;
	private JButton submitButton;
	

	public ModifyAccountFormListener(CustomTableModel tableModel, ModifyAccountForm mainPane, JTextField idTextField,
										JTextField firstNameTextField, JTextField lastNameTextField, JTextField usernameTextField,
											JPasswordField passwordTextField, JButton submitButton) {
		this.tableModel = tableModel;
		this.mainPane = mainPane;
		this.idTextField = idTextField;
		this.firstNameTextField = firstNameTextField;
		this.lastNameTextField = lastNameTextField;
		this.usernameTextField = usernameTextField;
		this.passwordTextField = passwordTextField;
		this.submitButton = submitButton;
		
		addTableListener();
	}
	
	// retrieve item from selected row and add them to text fields so they can be edited.
	public void addTableListener() {
		tableModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				int rowIndex = tableModel.getSelectedRowIndex();
				if(rowIndex > -1) {
					String[] rowData = tableModel.getRowDataAtIndex(rowIndex);
					fillTextFields(rowData);
				}
			}
		});
	}
	
	public void fillTextFields(String[] rowData) {
		String id = rowData[0];
		String username = rowData[1];
		String firstName = rowData[2];
		String lastName = rowData[3];
		
		idTextField.setText(id);
		usernameTextField.setText(username);
		firstNameTextField.setText(firstName);
		lastNameTextField.setText(lastName);
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
		JFrame frame = (JFrame) mainPane.getTopLevelAncestor();
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
