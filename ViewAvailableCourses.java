import java.sql.*;

public class ViewAvailableCourses {
	 private static final String DB_URL = "jdbc:mysql://localhost:3306/university_db";
	    private static final String DB_USERNAME = "root";
	    private static final String DB_PASSWORD = "password";

	    public static void main(String[] args) {
	        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
	            displayCourses(connection);
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    private static void displayCourses(Connection connection) throws SQLException {
	        String query = """
	            SELECT c.course_id, c.course_name, c.course_semester, c.start_time, c.end_time,
	                   c.course_day, c.course_description, c.max_students, 
	                   CONCAT(p.first_name, ' ', p.last_name) AS professor_name
	            FROM Courses c
	            JOIN Professors p ON c.professor_id = p.professor_id;
	        """;

	        try (Statement statement = connection.createStatement();
	             ResultSet resultSet = statement.executeQuery(query)) {

	            System.out.println("List of Courses:");
	            System.out.println("--------------------------------------------------------------------------------");
	            System.out.printf("%-5s | %-30s | %-15s | %-10s | %-10s | %-10s | %-10s | %-30s%n", 
	                "ID", "Course Name", "Semester", "Start Time", "End Time", "Day", "Max Students", "Professor");
	            System.out.println("--------------------------------------------------------------------------------");

	            while (resultSet.next()) {
	                int courseId = resultSet.getInt("course_id");
	                String courseName = resultSet.getString("course_name");
	                String courseSemester = resultSet.getString("course_semester");
	                String startTime = resultSet.getString("start_time");
	                String endTime = resultSet.getString("end_time");
	                String courseDay = resultSet.getString("course_day");
	                int maxStudents = resultSet.getInt("max_students");
	                String professorName = resultSet.getString("professor_name");

	                System.out.printf("%-5d | %-30s | %-15s | %-10s | %-10s | %-10s | %-10d | %-30s%n",
	                    courseId, courseName, courseSemester, startTime, endTime, courseDay, maxStudents, professorName);
	            }
	        }
	    }
	}