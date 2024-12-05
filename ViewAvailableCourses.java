import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ViewAvailableCourses extends JFrame {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/university_db";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "password";

    private JTable table;
    private DefaultTableModel tableModel;

    public ViewAvailableCourses() {
        setTitle("Available Courses");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout setup
        JPanel panel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("List of Courses", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Table setup
        String[] columnNames = {
            "ID", "Course Name", "Semester", "Start Time", "End Time", 
            "Day", "Max Students", "Professor"
        };
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Load data
        loadData();

        add(panel);
    }

    private void loadData() {
        String query = """
            SELECT c.course_id, c.course_name, c.course_semester, c.start_time, c.end_time,
                   c.course_day, c.course_description, c.max_students, 
                   CONCAT(p.first_name, ' ', p.last_name) AS professor_name
            FROM Courses c
            JOIN Professors p ON c.professor_id = p.professor_id;
        """;

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int courseId = resultSet.getInt("course_id");
                String courseName = resultSet.getString("course_name");
                String courseSemester = resultSet.getString("course_semester");
                String startTime = resultSet.getString("start_time");
                String endTime = resultSet.getString("end_time");
                String courseDay = resultSet.getString("course_day");
                int maxStudents = resultSet.getInt("max_students");
                String professorName = resultSet.getString("professor_name");

                Object[] row = {
                    courseId, courseName, courseSemester, startTime, endTime,
                    courseDay, maxStudents, professorName
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading course data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ViewAvailableCourses frame = new ViewAvailableCourses();
            frame.setVisible(true);
        });
    }
}
