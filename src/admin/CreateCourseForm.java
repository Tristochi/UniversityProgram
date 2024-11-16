package org.example.admin;

import org.example.GUILookAndFeel;

import javax.swing.*;

// Create new courses
public class CreateCourseForm extends JFrame {
    private JPanel contentPane;

    public CreateCourseForm() {
        setTitle("Create A Course");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(contentPane);
        pack();
        GUILookAndFeel.setLookAndFeel();

        // Add Listeners

    }
}
