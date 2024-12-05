import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ViewEnrolledCourses extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/university_db";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "password";

    private JTextField studentIdField;
    private JTextField semesterField;
    private JTable currentSemesterTable;
    private JTable pastCoursesTable;
    private DefaultTableModel currentTableModel;
    private DefaultTableModel pastTableModel;
    private JLabel statusLabel;

    public ViewEnrolledCourses() {
        setTitle("View Enrolled Courses");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Student ID:"));
        studentIdField = new JTextField(10);
        inputPanel.add(studentIdField);

        inputPanel.add(new JLabel("Current Semester (e.g., 'Fall 2024'):"));
        semesterField = new JTextField(15);
        inputPanel.add(semesterField);

        JButton fetchButton = new JButton("Fetch Courses");
        fetchButton.addActionListener(e -> fetchCourses());
        inputPanel.add(fetchButton);

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // Tables Panel
        JPanel tablesPanel = new JPanel(new GridLayout(2, 1));

        // Current Semester Table
        currentTableModel = new DefaultTableModel(new String[] { "Course Name", "Start Time", "End Time", "Day", "Description" }, 0);
        currentSemesterTable = new JTable(currentTableModel);
        JScrollPane currentScrollPane = new JScrollPane(currentSemesterTable);
        JPanel currentPanel = new JPanel(new BorderLayout());
        currentPanel.add(new JLabel("Current Semester Courses"), BorderLayout.NORTH);
        currentPanel.add(currentScrollPane, BorderLayout.CENTER);
        tablesPanel.add(currentPanel);

        // Past Courses Table
        pastTableModel = new DefaultTableModel(new String[] { "Course Name", "Semester", "Final Grade" }, 0);
        pastCoursesTable = new JTable(pastTableModel);
        JScrollPane pastScrollPane = new JScrollPane(pastCoursesTable);
        JPanel pastPanel = new JPanel(new BorderLayout());
        pastPanel.add(new JLabel("Past Courses"), BorderLayout.NORTH);
        pastPanel.add(pastScrollPane, BorderLayout.CENTER);
        tablesPanel.add(pastPanel);

        mainPanel.add(tablesPanel, BorderLayout.CENTER);

        // Status Panel
        JPanel statusPanel = new JPanel();
        statusLabel = new JLabel(" ");
        statusPanel.add(statusLabel);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void fetchCourses() {
        currentTableModel.setRowCount(0); // Clear current semester table
        pastTableModel.setRowCount(0);    // Clear past courses table

        String studentIdText = studentIdField.getText().trim();
        String semesterText = semesterField.getText().trim();

        if (studentIdText.isEmpty() || semesterText.isEmpty()) {
            statusLabel.setText("Please enter both Student ID and Current Semester.");
            return;
        }

        int studentId;
        try {
            studentId = Integer.parseInt(studentIdText);
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid Student ID. Please enter a numeric value.");
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            fetchCurrentSemesterCourses(connection, studentId, semesterText);
            fetchPastCourses(connection, studentId);
            statusLabel.setText("Courses fetched successfully.");
        } catch (SQLException e) {
            statusLabel.setText("Error fetching courses: " + e.getMessage());
        }
    }

    private void fetchCurrentSemesterCourses(Connection connection, int studentId, String semester) throws SQLException {
        String query = """
            SELECT c.course_name, c.start_time, c.end_time, c.course_day, c.course_description
            FROM Students_Enrolled_In_Courses sec
            JOIN Courses c ON sec.course_id = c.course_id
            WHERE sec.student_id = ? AND c.course_semester = ?;
        """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, studentId);
            statement.setString(2, semester);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String courseName = resultSet.getString("course_name");
                String startTime = resultSet.getString("start_time");
                String endTime = resultSet.getString("end_time");
                String day = resultSet.getString("course_day");
                String description = resultSet.getString("course_description");

                currentTableModel.addRow(new Object[] { courseName, startTime, endTime, day, description });
            }
        }
    }

    private void fetchPastCourses(Connection connection, int studentId) throws SQLException {
        String query = """
            SELECT pc.course_name, pc.semester, pfg.final_grade
            FROM past_courses_for_student pc
            LEFT JOIN past_final_grades pfg ON pc.course_id = pfg.course_id AND pc.student_id = pfg.student_id
            WHERE pc.student_id = ?;
        """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, studentId);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String courseName = resultSet.getString("course_name");
                String semester = resultSet.getString("semester");
                String finalGrade = resultSet.getString("final_grade");

                pastTableModel.addRow(new Object[] { courseName, semester, finalGrade == null ? "N/A" : finalGrade });
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ViewEnrolledCourses frame = new ViewEnrolledCourses();
            frame.setVisible(true);
        });
    }
}
