package org.example.admin;

import org.example.GUILookAndFeel;

import javax.swing.*;

// Remove a student from a course
public class RemoveStudentCourseForm extends JFrame {
    private JPanel contentPane;

    public RemoveStudentCourseForm() {
        setTitle("Remove Student From A Course");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(contentPane);
        pack();
        GUILookAndFeel.setLookAndFeel();

        // Add Listeners
    }
}
