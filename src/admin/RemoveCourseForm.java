package org.example.admin;

import org.example.GUILookAndFeel;

import javax.swing.*;

// Remove course from the database. Remove all occurrences of this course in other places of the db first.
public class RemoveCourseForm extends JFrame {
    private JPanel contentPane;

    public RemoveCourseForm() {
        setTitle("Remove A Course");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(contentPane);
        pack();
        GUILookAndFeel.setLookAndFeel();

        // Add Listeners
    }
}
