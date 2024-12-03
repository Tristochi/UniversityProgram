package admin.formlistener;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import admin.RemoveStudentCourseForm;
import custom.CustomTableModel;
import dbconnect.DBConnect;

public class StudentTableListener implements TableModelListener{
	private RemoveStudentCourseForm mainPane;
	private CustomTableModel studentTableModel;
	
	public StudentTableListener(RemoveStudentCourseForm mainPane, CustomTableModel studentTableModel) {
		this.mainPane = mainPane;
		this.studentTableModel = studentTableModel;
	}
	
	@Override
	public void tableChanged(TableModelEvent e) {
		int rowIndex = studentTableModel.getSelectedRowIndex();
		if(rowIndex > -1) {
			String[] studentInfo = studentTableModel.getRowDataAtIndex(rowIndex);
			String studentId = studentInfo[0];
			String studentName = studentInfo[1] + " " + studentInfo[2];
			mainPane.updateCourseInfoPane(studentId, studentName);
		}
	}
}
