package student;
import java.sql.*;
import java.util.Scanner;

public class MakeAppointment {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/university_db";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "Ecu@dor95";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Student ID: ");
        int studentId = scanner.nextInt();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            displayAvailableSlots(connection);

            System.out.println("\nEnter the Appointment Date (DD-MM-YYYY): ");
            String appointmentDate = scanner.next();

            System.out.println("Enter the Appointment Time (HH:MM): ");
            String appointmentTime = scanner.next();

            // Insert appointment
            if (makeAppointment(connection, studentId, appointmentDate, appointmentTime)) {
                System.out.println("Appointment successfully scheduled.");
            } else {
                System.out.println("The selected time slot is unavailable. Please try a different time.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private static void displayAvailableSlots(Connection connection) throws SQLException {
        String query = """
            SELECT DISTINCT a.appointment_date, a.appointment_time
            FROM Appointments a
            WHERE a.appointment_status != 'Confirmed';
        """;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            System.out.println("Available Appointment Slots:");
            System.out.println("-------------------------------------------------");
            while (resultSet.next()) {
                Date appointmentDate = resultSet.getDate("appointment_date");
                Time appointmentTime = resultSet.getTime("appointment_time");
                System.out.printf("Date: %s, Time: %s%n", appointmentDate, appointmentTime);
            }
        }
    }

    private static boolean makeAppointment(Connection connection, int studentId, String appointmentDate, String appointmentTime) throws SQLException {
        // Check if the time slot is available
        String checkQuery = """
            SELECT COUNT(*) AS count
            FROM Appointments
            WHERE appointment_date = ? AND appointment_time = ? AND appointment_status = 'Confirmed';
        """;

        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setString(1, appointmentDate);
            checkStmt.setString(2, appointmentTime);

            ResultSet resultSet = checkStmt.executeQuery();
            if (resultSet.next() && resultSet.getInt("count") > 0) {
                return false; // Slot is already taken
            }
        }

        // Insert the appointment
        String insertQuery = """
            INSERT INTO Appointments (appointment_id, professor_id, student_id, appointment_date, appointment_time, appointment_notes, appointment_status)
            VALUES (NULL, NULL, ?, ?, ?, '', 'Confirmed');
        """;

        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
            insertStmt.setInt(1, studentId);
            insertStmt.setString(2, appointmentDate);
            insertStmt.setString(3, appointmentTime);

            int rowsAffected = insertStmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}
