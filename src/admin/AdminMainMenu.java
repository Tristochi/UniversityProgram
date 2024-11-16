package org.example.admin;

import org.example.GUILookAndFeel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Main Menu to navigate to other admin forms
public class AdminMainMenu extends JFrame{
    private JPanel contentPane;
    private JButton createAccountFormButton;
    private JButton createCourseFormButton;
    private JButton modifyAccountDetailsButton;
    private JButton modifyCoursesButton;
    private JButton removeCourseButton;
    private JButton removeStudentFromCourseButton;
    private JButton studentAppointmentsButton;

    public AdminMainMenu() {
        // Display frame
        setTitle("Admin View");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(contentPane);
        pack();
        GUILookAndFeel.setLookAndFeel();
        this.setMinimumSize(new Dimension(500, 300));

        // Add listeners
        createAccountFormButtonListener();
        createCourseFormButtonListener();
        modifyCoursesButtonListener();
        modifyAccountDetailsButtonListener();
        removeCourseButtonListener();
        removeStudentFromCourseButtonListener();
        studentAppointmentsButtonListener();
    }

    private void createAccountFormButtonListener() {
        createAccountFormButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // change pane when this button is clicked
                dispose();
                CreateAccountForm createAccountForm = new CreateAccountForm();
                createAccountForm.setVisible(true);
            }
        });
    }

    private void createCourseFormButtonListener() {
        createCourseFormButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                CreateCourseForm createCourseForm = new CreateCourseForm();
                createCourseForm.setVisible(true);
            }
        });
    }

    private void modifyAccountDetailsButtonListener() {
        modifyAccountDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                ModifyAccountDetailsForm modifyAccountDetailsForm = new ModifyAccountDetailsForm();
                modifyAccountDetailsForm.setVisible(true);
            }
        });
    }

    private void modifyCoursesButtonListener() {
        modifyCoursesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                ModifyCourseForm modifyCourseForm = new ModifyCourseForm();
                modifyCourseForm.setVisible(true);
            }
        });
    }

    private void removeCourseButtonListener() {
        removeCourseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                RemoveCourseForm removeCourseForm = new RemoveCourseForm();
                removeCourseForm.setVisible(true);
            }
        });
    }

    private void removeStudentFromCourseButtonListener() {
        removeStudentFromCourseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                RemoveStudentCourseForm removeStudentCourseForm = new RemoveStudentCourseForm();
                removeStudentCourseForm.setVisible(true);
            }
        });
    }

    private void studentAppointmentsButtonListener() {
        studentAppointmentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                StudentAppointmentForm studentAppointmentForm = new StudentAppointmentForm();
                studentAppointmentForm.setVisible(true);
            }
        });
    }
}
