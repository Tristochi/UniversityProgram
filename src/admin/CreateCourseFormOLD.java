package org.example.admin;

import org.example.DBConnect;
import org.example.GUILookAndFeel;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Create student and professor accounts
public class CreateAccountForm extends JFrame {
    private JPanel contentPane;
    private JButton returnButton;
    private JButton button1;

    public CreateAccountForm() {
        setTitle("Create An Account");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(contentPane);
        pack();
        //GUILookAndFeel.setLookAndFeel();

        // Add Listeners
        returnButtonListener();
    }

    private void returnButtonListener() {
        // Return to admin menu
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                AdminMainMenu adminMainMenu = new AdminMainMenu();
                adminMainMenu.setVisible(true);
            }
        });
    }

    private void addStudentToDB() {
    }

    private void testDB() {
        try {
            Connection connection = DBConnect.connection;
            String query = "SELECT * FROM accounts WHERE username = 'harry'";
            PreparedStatement stm = connection.prepareStatement(query);
            ResultSet resultSet = stm.executeQuery(query);

            while(resultSet.next()) {
                System.out.println(resultSet.getString("first_time_login"));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
