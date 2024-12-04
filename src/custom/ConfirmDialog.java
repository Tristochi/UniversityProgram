package custom;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class ConfirmDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JButton okButton;
	private JButton cancelButton;
	private boolean confirmation;
	
	public ConfirmDialog(String message) {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 15, 5, 15));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		JLabel popupTextLabel = new JLabel(message);
		popupTextLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
		popupTextLabel.setHorizontalAlignment(SwingConstants.CENTER);
		contentPanel.add(popupTextLabel);
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		okButton = new JButton("OK");
		okButton.setBorder(new EmptyBorder(5, 15, 5, 15));
		okButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
		buttonPane.add(okButton);
		
		cancelButton = new JButton("Cancel");
		cancelButton.setBorder(new EmptyBorder(5, 15, 5, 15));
		cancelButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
		buttonPane.add(cancelButton);
		getRootPane().setDefaultButton(cancelButton);
		
		addOkButtonListener();
		addCancelButtonListener();
		setModal(true);
	}
	
	private void addOkButtonListener() {
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				confirmation = true;
				dispose();
			}
		});
	}
	
	private void addCancelButtonListener() {
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				confirmation = false;
				dispose();
			}
		});
	}
	
	public boolean getConfirmation() {
		return confirmation;
	}

}
