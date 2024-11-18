package org.example.admin;
import org.example.GUILookAndFeel;

import javax.swing.*;

// Change the name, username, or password of a student or a professor
public class ModifyAccountDetailsForm extends JFrame {
    private JPanel contentPane;

    public ModifyAccountDetailsForm() {
        setTitle("Modify Account Details");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(contentPane);
        pack();
        GUILookAndFeel.setLookAndFeel();

        // Add Listeners

    }
}
