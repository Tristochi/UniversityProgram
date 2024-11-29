package professor;

import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.GridLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class ProfessorCourseView extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public ProfessorCourseView() {
		
		JList courseList = new JList();
		
		JLabel courseLabel = new JLabel("Courses");
		
		JList studentList = new JList();
		
		JLabel studentLabel = new JLabel("Students");
		
		JList pendingStudentList = new JList();
		
		JLabel pendingStudentLabel = new JLabel("Pending Students");
		
		JButton approveButton = new JButton("Approve");
		
		JButton rejectButton = new JButton("Reject");
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(25)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(courseList, GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
							.addGap(55))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(courseLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addGap(160)))
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(pendingStudentLabel, GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
							.addGap(90))
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(approveButton, GroupLayout.PREFERRED_SIZE, 81, Short.MAX_VALUE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(rejectButton, GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
									.addGap(20))
								.addComponent(pendingStudentList, GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(studentLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addGap(132))
								.addComponent(studentList, GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE))
							.addGap(10))))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(11)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(courseLabel)
						.addComponent(studentLabel))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(courseList, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE)
						.addComponent(studentList, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE))
					.addGap(4)
					.addComponent(pendingStudentLabel, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(pendingStudentList, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(approveButton, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addComponent(rejectButton, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE))
					.addGap(31))
		);
		setLayout(groupLayout);

	}
}
