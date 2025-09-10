        import java.sql.Connection;
        import java.sql.DriverManager;
        import java.sql.SQLException;

        public class DatabaseManager {
            private static final String URL = "jdbc:mysql://localhost:3306/roms?serverTimezone=UTC";
            private static final String USER = "root"; // Default for XAMPP
            private static final String PASSWORD = ""; // Default is empty for XAMPP

            public static Connection getConnection() {
                Connection conn = null;
                try {
                    // Load MySQL JDBC Driver
                    Class.forName("com.mysql.cj.jdbc.Driver");

                    // Establish connection
                    conn = DriverManager.getConnection(URL, USER, PASSWORD);
                    System.out.println("Connected to database!");
                } catch (ClassNotFoundException e) {
                    System.out.println("MySQL JDBC Driver not found. Add the library!");
                    e.printStackTrace();
                } catch (SQLException e) {
                    System.out.println("Failed to connect to the database. Check XAMPP MySQL.");
                    e.printStackTrace();
                }
                return conn;
            }

            public static void main(String[] args) {
                // Test connection
                Connection testConnection = getConnection();
                if (testConnection != null) {
                    System.out.println("Connection successful!");
                } else {
                    System.out.println("Connection failed.");
                }
            }
        }
