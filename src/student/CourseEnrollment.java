package student;
import java.sql.*;
import java.util.Scanner;

public class CourseEnrollment {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/university_db";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "Ecu@dor95";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Student ID: ");
        int studentId = scanner.nextInt();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            // Check total credits enrolled
            int totalCredits = getTotalCredits(connection, studentId);
            System.out.printf("You are currently enrolled in %d credits.%n", totalCredits);

            if (totalCredits >= 18) {
                System.out.println("You have reached the maximum credit limit of 18 credits. You cannot enroll in more courses.");
                return;
            }

            // Display available courses
            System.out.println("\nAvailable Courses:");
            displayAvailableCourses(connection, studentId);

            // Enroll in a course
            System.out.print("Enter the Course ID to enroll: ");
            int courseId = scanner.nextInt();

            // Check if the course is full
            if (isCourseFull(connection, courseId)) {
                System.out.println("The course is full. Sending a request to the professor...");
                if (sendCourseRequest(connection, studentId, courseId)) {
                    System.out.println("Request sent successfully. Wait for the professor's response.");
                } else {
                    System.out.println("Failed to send the request. Please try again.");
                }
            } else {
                System.out.println("The course has available slots. Enrolling...");
                if (enrollInCourse(connection, studentId, courseId)) {
                    System.out.println("Successfully enrolled in the course.");
                } else {
                    System.out.println("Failed to enroll in the course. Please try again.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private static int getTotalCredits(Connection connection, int studentId) throws SQLException {
        String query = """
            SELECT SUM(c.max_students) AS total_credits
            FROM Students_Enrolled_In_Courses sec
            JOIN Courses c ON sec.course_id = c.course_id
            WHERE sec.student_id = ?;
        """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, studentId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("total_credits");
            }
        }
        return 0;
    }

    private static void displayAvailableCourses(Connection connection, int studentId) throws SQLException {
        String query = """
            SELECT c.course_id, c.course_name, c.course_semester, c.max_students
            FROM Courses c
            WHERE c.course_id NOT IN (
                SELECT course_id FROM Students_Enrolled_In_Courses WHERE student_id = ?
            );
        """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, studentId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int courseId = resultSet.getInt("course_id");
                String courseName = resultSet.getString("course_name");
                String courseSemester = resultSet.getString("course_semester");
                int maxStudents = resultSet.getInt("max_students");

                System.out.printf("Course ID: %d, Name: %s, Semester: %s, Max Students: %d%n",
                        courseId, courseName, courseSemester, maxStudents);
            }
        }
    }

    private static boolean isCourseFull(Connection connection, int courseId) throws SQLException {
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

    private static boolean enrollInCourse(Connection connection, int studentId, int courseId) throws SQLException {
        String query = """
            INSERT INTO Students_Enrolled_In_Courses (course_id, student_id, grade)
            VALUES (?, ?, NULL);
        """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, courseId);
            statement.setInt(2, studentId);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    private static boolean sendCourseRequest(Connection connection, int studentId, int courseId) throws SQLException {
        String query = """
            INSERT INTO Course_Requests (course_id, student_id, request_date, request_time, request_status)
            VALUES (?, ?, CURDATE(), CURTIME(), 'Pending');
        """;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, courseId);
            statement.setInt(2, studentId);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }
}
