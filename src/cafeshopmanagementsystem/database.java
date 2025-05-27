package cafeshopmanagementsystem;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class database {

    // Consider using a constant for the connection string
private static final String DB_URL = "jdbc:mysql://localhost:3306/cafe";
    private static final String USER = "root";
    private static final String PASSWORD = "";



    public static Connection connectDB() {
    Connection connect = null;
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connect = DriverManager.getConnection(DB_URL, USER, PASSWORD);
    } catch (SQLException e) {
        System.err.println("SQL Exception: " + e.getMessage());
        e.printStackTrace(); // Ajouté pour plus de détails
    } catch (ClassNotFoundException e) {
        System.err.println("JDBC Driver not found: " + e.getMessage());
    } catch (Exception e) {
        System.err.println("Exception: " + e.getMessage());
    }
    return connect;
}
}