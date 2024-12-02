package admin.formlistener;

import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import custom.CustomTableModel;

public class ModifyAccountTableListener implements TableModelListener {
	private CustomTableModel tableModel;
	private JTextField idTextField;
	private JTextField usernameTextField;
	private JTextField firstNameTextField;
	private JTextField lastNameTextField;

	public ModifyAccountTableListener(CustomTableModel tableModel, JTextField idTextField, JTextField usernameTextField,
										JTextField firstNameTextField, JTextField lastNameTextField) {
		this.tableModel = tableModel;
		this.idTextField = idTextField;
		this.usernameTextField = usernameTextField;
		this.firstNameTextField = firstNameTextField;
		this.lastNameTextField = lastNameTextField;
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		int rowIndex = tableModel.getSelectedRowIndex();
		if(rowIndex > -1) {
			String[] rowData = tableModel.getRowDataAtIndex(rowIndex);
			fillTextFields(rowData);
		}
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

}
