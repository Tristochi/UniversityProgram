package org.example.admin;

import org.example.GUILookAndFeel;

import javax.swing.*;

public class ModifyCourseForm extends JFrame {
    private JPanel contentPane;

    public ModifyCourseForm() {
        setTitle("Modify Course Information");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(contentPane);
        pack();
        GUILookAndFeel.setLookAndFeel();

        // Add Listeners
    }
}
