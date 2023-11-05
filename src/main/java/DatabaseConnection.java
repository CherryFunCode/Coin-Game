import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/game_db?useSSL=false";
    private static final String USER = "root";
    private static final String PASS = "Cing";

    // JDBC variables for opening and managing connection
    private static Connection connection;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASS);
            }
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Error connecting to the database", ex);
        }
        return connection;
    }


}
