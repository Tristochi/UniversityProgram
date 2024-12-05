import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class DropEnrolledCourse extends JFrame {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/university_db";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "password";

    private JTextField studentIdField;
    private JTable enrolledCoursesTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    public DropEnrolledCourse() {
        setTitle("Drop Enrolled Courses");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout setup
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Student ID:"));
        studentIdField = new JTextField(10);
        inputPanel.add(studentIdField);

        JButton fetchButton = new JButton("Fetch Enrolled Courses");
        fetchButton.addActionListener(e -> fetchEnrolledCourses());
        inputPanel.add(fetchButton);

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // Table for enrolled courses
        String[] columnNames = { "Course ID", "Course Name", "Semester" };
        tableModel = new DefaultTableModel(columnNames, 0);
        enrolledCoursesTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(enrolledCoursesTable);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Drop Button and Status
        JPanel actionPanel = new JPanel();
        JButton dropButton = new JButton("Drop Selected Course");
        dropButton.addActionListener(e -> dropSelectedCourse());
        actionPanel.add(dropButton);

        statusLabel = new JLabel();
        actionPanel.add(statusLabel);

        mainPanel.add(actionPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void fetchEnrolledCourses() {
        tableModel.setRowCount(0); // Clear existing rows

        String studentIdText = studentIdField.getText().trim();
        if (studentIdText.isEmpty()) {
            statusLabel.setText("Please enter a valid Student ID.");
            return;
        }

        int studentId;
        try {
            studentId = Integer.parseInt(studentIdText);
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid Student ID. Please enter a number.");
            return;
        }

        String query = """
            SELECT c.course_id, c.course_name, c.course_semester
            FROM Students_Enrolled_In_Courses sec
            JOIN Courses c ON sec.course_id = c.course_id
            WHERE sec.student_id = ?;
        """;

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, studentId);
            ResultSet resultSet = statement.executeQuery();
            boolean hasCourses = false;

            while (resultSet.next()) {
                hasCourses = true;
                int courseId = resultSet.getInt("course_id");
                String courseName = resultSet.getString("course_name");
                String courseSemester = resultSet.getString("course_semester");

                tableModel.addRow(new Object[]{ courseId, courseName, courseSemester });
            }

            if (!hasCourses) {
                statusLabel.setText("No courses currently enrolled.");
            } else {
                statusLabel.setText("Enrolled courses fetched successfully.");
            }
        } catch (SQLException e) {
            statusLabel.setText("Error fetching enrolled courses: " + e.getMessage());
        }
    }

    private void dropSelectedCourse() {
        int selectedRow = enrolledCoursesTable.getSelectedRow();
        if (selectedRow == -1) {
            statusLabel.setText("Please select a course to drop.");
            return;
        }

        String studentIdText = studentIdField.getText().trim();
        if (studentIdText.isEmpty()) {
            statusLabel.setText("Please enter a valid Student ID.");
            return;
        }

        int studentId;
        try {
            studentId = Integer.parseInt(studentIdText);
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid Student ID. Please enter a number.");
            return;
        }

        int courseId = (int) tableModel.getValueAt(selectedRow, 0);

        String query = """
            DELETE FROM Students_Enrolled_In_Courses
            WHERE student_id = ? AND course_id = ?;
        """;

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, studentId);
            statement.setInt(2, courseId);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                statusLabel.setText("Successfully dropped the course.");
                fetchEnrolledCourses(); // Refresh the table
            } else {
                statusLabel.setText("Failed to drop the course. Please try again.");
            }
        } catch (SQLException e) {
            statusLabel.setText("Error dropping course: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DropEnrolledCourse frame = new DropEnrolledCourse();
            frame.setVisible(true);
        });
    }
}

