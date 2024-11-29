package custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JRadioButton;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

public class CustomTableModel extends DefaultTableModel {
	public CustomTableModel(Object[][] rowData, Object[] columnNames) {
		super(rowData, columnNames);
	}
	
	public int getSelectedRowIndex() {
		for(int i = 0; i < this.getRowCount(); i++) {
			if(this.getValueAt(i, 0) instanceof JRadioButton button) {
				if(button.isSelected()) {
					button.setSelected(true);
					return i;
				}
			}
		}
		return -1; // no row was selected
	}
	
	public String[] getRowDataAtIndex(int rowIndex) {
		String[] dataArray = new String[this.getColumnCount()-1];
		
		// column 0 with radio buttons is ignored
		for(int i = 0; i < this.getColumnCount()-1; i++) {
			dataArray[i] = (String) this.getValueAt(rowIndex, i+1);
		}
		
		return dataArray;
	}
	
	@Override
	public void setValueAt(Object aValue, int row, int column) {
		fireTableDataChanged(); // update the table to re-render the radio buttons when clicked.
		super.setValueAt(aValue, row, column);
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return column == 0; // can only edit the first table;
	}
	
	public void updateData(String[][] rowData) {
		for(int i = 0; i < getRowCount(); i++) {
			for(int j = 1; j < getColumnCount(); j++) {
				setValueAt(rowData[i][j], i, j);
			}
		}
	}
	
	public void outputData() {
		for(int i = 0; i < getRowCount(); i++) {
			for(int j = 1; j < getColumnCount(); j++) {
				System.out.printf(getValueAt(i, j) + "");
			}
			System.out.println(" ");
		}
	}
}