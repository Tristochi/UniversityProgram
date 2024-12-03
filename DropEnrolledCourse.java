import java.sql.*;
import java.util.Scanner;

public class DropEnrolledCourse {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/university_db";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "password";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Student ID: ");
        int studentId = scanner.nextInt();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            // Display currently enrolled courses
            System.out.println("Currently Enrolled Courses:");
            displayEnrolledCourses(connection, studentId);

            // Prompt to drop a course
            System.out.print("Enter the Course ID to drop: ");
            int courseId = scanner.nextInt();

            // Attempt to drop the course
            if (dropCourse(connection, studentId, courseId)) {
                System.out.println("Successfully dropped the course.");
            } else {
                System.out.println("Failed to drop the course. Please check the course ID and try again.");
            }

            // Display updated enrolled courses
            System.out.println("\nUpdated Enrolled Courses:");
            displayEnrolledCourses(connection, studentId);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private static void displayEnrolledCourses(Connection connection, int studentId) throws SQLException {
        String query = """
            SELECT c.course_id, c.course_name, c.course_semester
            FROM Students_Enrolled_In_Courses sec
            JOIN Courses c ON sec.course_id = c.course_id
            WHERE sec.student_id = ?;
        """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, studentId);

            ResultSet resultSet = statement.executeQuery();
            boolean hasCourses = false;

            while (resultSet.next()) {
                hasCourses = true;
                int courseId = resultSet.getInt("course_id");
                String courseName = resultSet.getString("course_name");
                String courseSemester = resultSet.getString("course_semester");

                System.out.printf("Course ID: %d, Name: %s, Semester: %s%n", courseId, courseName, courseSemester);
            }

            if (!hasCourses) {
                System.out.println("No courses currently enrolled.");
            }
        }
    }

    private static boolean dropCourse(Connection connection, int studentId, int courseId) throws SQLException {
        String query = """
            DELETE FROM Students_Enrolled_In_Courses
            WHERE student_id = ? AND course_id = ?;
        """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, studentId);
            statement.setInt(2, courseId);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }
}

