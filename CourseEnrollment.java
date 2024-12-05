import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CourseEnrollment extends JFrame {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/university_db";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "password";

    private JTextField studentIdField;
    private JTable coursesTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    public CourseEnrollment() {
        setTitle("Course Enrollment System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout setup
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Input panel
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Student ID:"));
        studentIdField = new JTextField(10);
        inputPanel.add(studentIdField);

        JButton fetchCoursesButton = new JButton("Fetch Courses");
        fetchCoursesButton.addActionListener(e -> fetchCourses());
        inputPanel.add(fetchCoursesButton);

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // Courses table
        String[] columnNames = { "Course ID", "Name", "Semester", "Max Students" };
        tableModel = new DefaultTableModel(columnNames, 0);
        coursesTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(coursesTable);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Action panel
        JPanel actionPanel = new JPanel();
        JButton enrollButton = new JButton("Enroll in Course");
        enrollButton.addActionListener(e -> enrollInCourse());
        actionPanel.add(enrollButton);

        statusLabel = new JLabel();
        actionPanel.add(statusLabel);

        mainPanel.add(actionPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void fetchCourses() {
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
            SELECT c.course_id, c.course_name, c.course_semester, c.max_students
            FROM Courses c
            WHERE c.course_id NOT IN (
                SELECT course_id FROM Students_Enrolled_In_Courses WHERE student_id = ?
            );
        """;

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, studentId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int courseId = resultSet.getInt("course_id");
                String courseName = resultSet.getString("course_name");
                String courseSemester = resultSet.getString("course_semester");
                int maxStudents = resultSet.getInt("max_students");

                tableModel.addRow(new Object[]{ courseId, courseName, courseSemester, maxStudents });
            }

            if (tableModel.getRowCount() == 0) {
                statusLabel.setText("No available courses for this student.");
            } else {
                statusLabel.setText("Courses fetched successfully.");
            }
        } catch (SQLException e) {
            statusLabel.setText("Error fetching courses: " + e.getMessage());
        }
    }

    private void enrollInCourse() {
        int selectedRow = coursesTable.getSelectedRow();
        if (selectedRow == -1) {
            statusLabel.setText("Please select a course to enroll.");
            return;
        }

        int courseId = (int) tableModel.getValueAt(selectedRow, 0);
        String studentIdText = studentIdField.getText().trim();

        int studentId;
        try {
            studentId = Integer.parseInt(studentIdText);
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid Student ID. Please enter a number.");
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            if (isCourseFull(connection, courseId)) {
                if (sendCourseRequest(connection, studentId, courseId)) {
                    statusLabel.setText("Course is full. Request sent to professor.");
                } else {
                    statusLabel.setText("Failed to send request. Please try again.");
                }
            } else {
                if (enrollStudentInCourse(connection, studentId, courseId)) {
                    statusLabel.setText("Successfully enrolled in the course.");
                    fetchCourses(); // Refresh courses list
                } else {
                    statusLabel.setText("Failed to enroll. Please try again.");
                }
            }
        } catch (SQLException e) {
            statusLabel.setText("Error during enrollment: " + e.getMessage());
        }
    }

    private boolean isCourseFull(Connection connection, int courseId) throws SQLException {
        String query = """
            SELECT COUNT(*) AS enrolled_count, c.max_students
            FROM Students_Enrolled_In_Courses sec
            JOIN Courses c ON sec.course_id = c.course_id
            WHERE c.course_id = ?
            GROUP BY c.max_students;
        """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, courseId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int enrolledCount = resultSet.getInt("enrolled_count");
                int maxStudents = resultSet.getInt("max_students");
                return enrolledCount >= maxStudents;
            }
        }
        return false;
    }

    private boolean enrollStudentInCourse(Connection connection, int studentId, int courseId) throws SQLException {
        String query = """
            INSERT INTO Students_Enrolled_In_Courses (course_id, student_id, grade)
            VALUES (?, ?, NULL);
        """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, courseId);
            statement.setInt(2, studentId);
            return statement.executeUpdate() > 0;
        }
    }

    private boolean sendCourseRequest(Connection connection, int studentId, int courseId) throws SQLException {
        String query = """
            INSERT INTO Course_Requests (course_id, student_id, request_date, request_time, request_status)
            VALUES (?, ?, CURDATE(), CURTIME(), 'Pending');
        """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, courseId);
            statement.setInt(2, studentId);
            return statement.executeUpdate() > 0;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CourseEnrollment frame = new CourseEnrollment();
            frame.setVisible(true);
        });
    }
}

