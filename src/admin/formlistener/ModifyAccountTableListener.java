package admin.formlistener;

import java.util.Map;

import javax.swing.JComponent;
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

	public ModifyAccountTableListener(Map<String, JComponent> componentMap, CustomTableModel tableModel) {
		this.tableModel = tableModel;
		this.idTextField = (JTextField) componentMap.get("idTextField");
		this.usernameTextField = (JTextField) componentMap.get("usernameTextField");
		this.firstNameTextField = (JTextField) componentMap.get("firstNameTextField");
		this.lastNameTextField = (JTextField) componentMap.get("lastNameTextField");
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
