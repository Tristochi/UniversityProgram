package student;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ViewFinalGradeAndGPA extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/university_db";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "Ecu@dor95";

    private JTextField studentIdField;
    private JTable gradesTable;
    private JLabel gpaLabel;
    private DefaultTableModel gradesTableModel;

    public ViewFinalGradeAndGPA() {
        setTitle("View Final Grades and GPA");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Input Panel
        JPanel inputPanel = new JPanel();
        
        JButton returnBtn = new JButton("Return");
        inputPanel.add(returnBtn);
        returnBtn.addActionListener(e->returnHome());
        inputPanel.add(new JLabel("Student ID:"));
        studentIdField = new JTextField(10);
        inputPanel.add(studentIdField);

        JButton fetchButton = new JButton("Fetch Grades and GPA");
        fetchButton.addActionListener(e -> fetchGradesAndGPA());
        inputPanel.add(fetchButton);

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // Grades Table Panel
        gradesTableModel = new DefaultTableModel(new String[] { "Semester", "Course Name", "Final Grade" }, 0);
        gradesTable = new JTable(gradesTableModel);
        JScrollPane gradesScrollPane = new JScrollPane(gradesTable);

        JPanel gradesPanel = new JPanel(new BorderLayout());
        gradesPanel.add(new JLabel("Final Grades Per Course by Semester"), BorderLayout.NORTH);
        gradesPanel.add(gradesScrollPane, BorderLayout.CENTER);

        mainPanel.add(gradesPanel, BorderLayout.CENTER);

        // GPA Panel
        JPanel gpaPanel = new JPanel();
        gpaLabel = new JLabel(" ");
        gpaPanel.add(new JLabel("Current Overall GPA:"));
        gpaPanel.add(gpaLabel);

        mainPanel.add(gpaPanel, BorderLayout.SOUTH);

        getContentPane().add(mainPanel);
    }

    private void returnHome() {
    	this.dispose();
    	CourseEnrollment screen = new CourseEnrollment();
    	screen.setVisible(true);
    }
    
    private void fetchGradesAndGPA() {
        gradesTableModel.setRowCount(0); // Clear grades table
        gpaLabel.setText(" ");          // Clear GPA label

        String studentIdText = studentIdField.getText().trim();

        if (studentIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Student ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
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
            fetchFinalGradesBySemester(connection, studentId);
            fetchGPA(connection, studentId);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fetchFinalGradesBySemester(Connection connection, int studentId) throws SQLException {
        String query = """
            SELECT pc.semester, pc.course_name, pfg.final_grade
            FROM past_courses_for_student pc
            JOIN past_final_grades pfg ON pc.course_id = pfg.course_id AND pc.student_id = pfg.student_id
            WHERE pc.student_id = ?
            ORDER BY pc.semester, pc.course_name;
        """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, studentId);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String semester = resultSet.getString("semester");
                String courseName = resultSet.getString("course_name");
                String finalGrade = resultSet.getString("final_grade");

                gradesTableModel.addRow(new Object[] { semester, courseName, finalGrade });
            }
        }
    }

    private void fetchGPA(Connection connection, int studentId) throws SQLException {
        String query = """
            SELECT gpa, total_credits
            FROM student_gpa
            WHERE student_id = ?;
        """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, studentId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                double gpa = resultSet.getDouble("gpa");
                int totalCredits = resultSet.getInt("total_credits");

                gpaLabel.setText(String.format("GPA: %.2f, Total Credits: %d", gpa, totalCredits));
            } else {
                gpaLabel.setText("No GPA data found for this student.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ViewFinalGradeAndGPA frame = new ViewFinalGradeAndGPA();
            frame.setVisible(true);
        });
    }
}