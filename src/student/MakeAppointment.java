package student;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class MakeAppointment extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/university_db";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "password";

    private JTextField studentIdField;
    private JTable slotsTable;
    private JTextField dateField;
    private JTextField timeField;
    private DefaultTableModel slotsTableModel;

    public MakeAppointment() {
        setTitle("Make Appointment");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Input Panel
        JPanel inputPanel = new JPanel();
        
        JButton returnBtn = new JButton("Return");
        inputPanel.add(returnBtn);
        returnBtn.addActionListener(e -> returnHome());
        inputPanel.add(new JLabel("Student ID:"));
        studentIdField = new JTextField(10);
        inputPanel.add(studentIdField);

        JButton fetchSlotsButton = new JButton("View Available Slots");
        fetchSlotsButton.addActionListener(e -> fetchAvailableSlots());
        inputPanel.add(fetchSlotsButton);

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // Slots Table Panel
        slotsTableModel = new DefaultTableModel(new String[] { "Appointment Date", "Appointment Time" }, 0);
        slotsTable = new JTable(slotsTableModel);
        JScrollPane slotsScrollPane = new JScrollPane(slotsTable);

        JPanel slotsPanel = new JPanel(new BorderLayout());
        slotsPanel.add(new JLabel("Available Appointment Slots"), BorderLayout.NORTH);
        slotsPanel.add(slotsScrollPane, BorderLayout.CENTER);

        mainPanel.add(slotsPanel, BorderLayout.CENTER);

        // Appointment Panel
        JPanel appointmentPanel = new JPanel();
        appointmentPanel.add(new JLabel("Appointment Date (DD-MM-YYYY):"));
        dateField = new JTextField(10);
        appointmentPanel.add(dateField);

        appointmentPanel.add(new JLabel("Appointment Time (HH:MM):"));
        timeField = new JTextField(10);
        appointmentPanel.add(timeField);

        JButton makeAppointmentButton = new JButton("Schedule Appointment");
        makeAppointmentButton.addActionListener(e -> scheduleAppointment());
        appointmentPanel.add(makeAppointmentButton);

        mainPanel.add(appointmentPanel, BorderLayout.SOUTH);

        getContentPane().add(mainPanel);
    }
    private void returnHome() {
    	this.dispose();
    	CourseEnrollment screen = new CourseEnrollment();
    	screen.setVisible(true);
    }

    private void fetchAvailableSlots() {
        slotsTableModel.setRowCount(0); // Clear existing rows

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String query = """
                SELECT DISTINCT appointment_date, appointment_time
                FROM Appointments
                WHERE appointment_status != 'Confirmed';
            """;

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                while (resultSet.next()) {
                    Date date = resultSet.getDate("appointment_date");
                    Time time = resultSet.getTime("appointment_time");
                    slotsTableModel.addRow(new Object[] { date, time });
                }

                if (slotsTableModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "No available slots found.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching slots: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void scheduleAppointment() {
        String studentIdText = studentIdField.getText().trim();
        String appointmentDate = dateField.getText().trim();
        String appointmentTime = timeField.getText().trim();

        if (studentIdText.isEmpty() || appointmentDate.isEmpty() || appointmentTime.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int studentId;
        try {
            studentId = Integer.parseInt(studentIdText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Student ID. Please enter a numeric value.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            if (makeAppointment(connection, studentId, appointmentDate, appointmentTime)) {
                JOptionPane.showMessageDialog(this, "Appointment successfully scheduled.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "The selected time slot is unavailable.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error scheduling appointment: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean makeAppointment(Connection connection, int studentId, String appointmentDate, String appointmentTime) throws SQLException {
        String checkQuery = """
            SELECT COUNT(*) AS count
            FROM Appointments
            WHERE appointment_date = ? AND appointment_time = ? AND appointment_status = 'Confirmed';
        """;

        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setString(1, appointmentDate);
            checkStmt.setString(2, appointmentTime);

            ResultSet resultSet = checkStmt.executeQuery();
            if (resultSet.next() && resultSet.getInt("count") > 0) {
                return false; // Slot is already taken
            }
        }

        String insertQuery = """
            INSERT INTO Appointments (appointment_id, admin_id, student_id, appointment_date, appointment_time, appointment_notes, appointment_status)
            VALUES (NULL, NULL, ?, ?, ?, '', 'Confirmed');
        """;

        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
            insertStmt.setInt(1, studentId);
            insertStmt.setString(2, appointmentDate);
            insertStmt.setString(3, appointmentTime);

            int rowsAffected = insertStmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MakeAppointment frame = new MakeAppointment();
            frame.setVisible(true);
        });
    }
}
