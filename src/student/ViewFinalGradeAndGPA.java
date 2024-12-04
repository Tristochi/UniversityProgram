package student;
import java.sql.*;
public class ViewFinalGradeAndGPA {
	private static final String DB_URL = "jdbc:mysql://localhost:3306/university_db";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "Ecu@dor95";

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		int studentId = 2; 

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            // Display final grades per course by semester
            System.out.println("Final Grades Per Course by Semester:");
            displayFinalGradesBySemester(connection, studentId);

            // Display current overall GPA
            System.out.println("\nCurrent Overall GPA:");
            displayCurrentGPA(connection, studentId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void displayFinalGradesBySemester(Connection connection, int studentId) throws SQLException {
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
            String currentSemester = null;

            while (resultSet.next()) {
                String semester = resultSet.getString("semester");
                String courseName = resultSet.getString("course_name");
                String finalGrade = resultSet.getString("final_grade");

                if (!semester.equals(currentSemester)) {
                    currentSemester = semester;
                    System.out.printf("\nSemester: %s%n", currentSemester);
                }
                System.out.printf("  Course: %s, Final Grade: %s%n", courseName, finalGrade);
            }
        }
    }

    private static void displayCurrentGPA(Connection connection, int studentId) throws SQLException {
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

                System.out.printf("GPA: %.2f, Total Credits: %d%n", gpa, totalCredits);
            } else {
                System.out.println("No GPA data was found for the student.");
            }
        }
    }
}