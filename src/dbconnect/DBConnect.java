package dbconnect;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnect {
	public static Connection connection;

    public static void connect() {
        try {
            //Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/university_db";
            String user = "root";
            String pass = "Ecu@dor95";
            connection = DriverManager.getConnection(url, user, pass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
