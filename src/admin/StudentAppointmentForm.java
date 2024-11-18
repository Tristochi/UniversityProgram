package org.example.admin;

import org.example.GUILookAndFeel;

import javax.swing.*;

// Mark a student appointment as completed, and describe what was accomplished
public class StudentAppointmentForm extends JFrame {
    private JPanel contentPane;

    public StudentAppointmentForm() {
        setTitle("Student Appointment Forms");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(contentPane);
        pack();
        GUILookAndFeel.setLookAndFeel();

        // Add Listeners
    }
}
