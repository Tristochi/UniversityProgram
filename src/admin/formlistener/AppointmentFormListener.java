package admin.formlistener;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Statement;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import admin.AppointmentForm;
import custom.CustomTableModel;
import custom.PopupDialog;
import dbconnect.DBConnect;

/*
 * This is a listener class for both the tableModel and submitButton.
 */

public class AppointmentFormListener implements TableModelListener, ActionListener{
	private AppointmentForm mainPane;
	private CustomTableModel tableModel;
	private JComboBox<String> statusComboBox;
	private JTextArea notesTextArea;
	
	public AppointmentFormListener(AppointmentForm mainPane, CustomTableModel tableModel, JComboBox<String> statusComboBox, JTextArea notesTextArea) {
		this.mainPane = mainPane;
		this.tableModel = tableModel;
		this.statusComboBox = statusComboBox;
		this.notesTextArea = notesTextArea;
	}
	
	// Runs this when a row is selected
	@Override
	public void tableChanged(TableModelEvent e) {
		int rowIndex = tableModel.getSelectedRowIndex();
		if(rowIndex > -1) {
			String[] rowData = tableModel.getRowDataAtIndex(rowIndex);
			String status = rowData[6];
			String notes = rowData[5];
			
			statusComboBox.setSelectedItem(status);
			notesTextArea.setText(notes);
		}
	}
	
	// Runs this when submit button is pressed
	@Override
	public void actionPerformed(ActionEvent e) {
		int rowIndex = tableModel.getSelectedRowIndex();
		if(rowIndex < 0) {
			showPopupMessage("No Appointment Selected.", "Error!");
		}
		if(!isInformationNew()) {
			showPopupMessage("No Changes Made.", "Error!");
		}
		else {
			boolean isQuerySuccessful = updateDB();
			if(isQuerySuccessful) {
				showPopupMessage("Changes Made Successfully", "");
				mainPane.updateTableModel();
			}
		}
	}
	
	/*
	 * Helper Methods
	 */
	
	private boolean updateDB() {
		String status = (String) statusComboBox.getSelectedItem();
		String notes = notesTextArea.getText();
		String appointementId = tableModel.getRowDataAtIndex(tableModel.getSelectedRowIndex())[0];
		
		try {
			Connection connection = DBConnect.connection;
			String query = String.format("UPDATE appointments SET appointment_status = '%s', appointment_notes = '%s' WHERE appointment_id = '%s'", status, notes, appointementId);
			Statement stm = connection.createStatement();
			int rows = stm.executeUpdate(query);
			
			if(rows > 0) {
				return true;
			}
			return false;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private boolean isInformationNew() {
		String[] rowData = tableModel.getRowDataAtIndex(tableModel.getSelectedRowIndex());
		String newStatus = statusComboBox.getSelectedItem()+"";
		String newNotes = notesTextArea.getText();
		
		if(rowData[6].equals(newStatus) && rowData[5].equals(newNotes)) {
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
