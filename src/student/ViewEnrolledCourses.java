package student;
import java.sql.*;
import java.util.Scanner;

public class ViewEnrolledCourses {
	private static final String DB_URL = "jdbc:mysql://localhost:3306/university_db";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "Ecu@dor95";

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Student ID: ");
        int studentId = scanner.nextInt();

        System.out.print("Enter Current Semester (e.g., 'Fall 2024'): ");
        scanner.nextLine(); // Consume newline
        String currentSemester = scanner.nextLine();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            // Display courses enrolled for the current semester
            System.out.println("Courses Enrolled in Current Semester:");
            displayCourses(connection, studentId, currentSemester);

            // Display past courses
            System.out.println("Courses Taken in Previous Semesters:");
            displayPastCourses(connection, studentId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        scanner.close();
    }

    private static void displayCourses(Connection connection, int studentId, String currentSemester) throws SQLException {
        String query = """
            SELECT c.course_name, c.start_time, c.end_time, c.course_day, c.course_description
            FROM Students_Enrolled_In_Courses sec
            JOIN Courses c ON sec.course_id = c.course_id
            WHERE sec.student_id = ? AND c.course_semester = ?;
        """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, studentId);
            statement.setString(2, currentSemester);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String courseName = resultSet.getString("course_name");
                String startTime = resultSet.getString("start_time");
                String endTime = resultSet.getString("end_time");
                String courseDay = resultSet.getString("course_day");
                String description = resultSet.getString("course_description");

                System.out.printf("Course Name: %s, Time: %s-%s, Day: %s, Description: %s%n",
                        courseName, startTime, endTime, courseDay, description);
            }
        }
    }

    private static void displayPastCourses(Connection connection, int studentId) throws SQLException {
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

                System.out.printf("Course Name: %s, Semester: %s, Final Grade: %s%n",
                        courseName, semester, finalGrade == null ? "N/A" : finalGrade);
            }
        }
    }
}