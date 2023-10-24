package Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Test {
    private static final String url = "jdbc:mysql://localhost:3306/mydb?serverTimezone=UTC";
    private static final String user = "root";
    private static final String password = "Gjwldnd!1";
    
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("연결");
            runSQL(conn);
        } catch (SQLException e) {
            System.err.println("연결 x");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public static String runSQL(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            String sql = "SHOW TABLES";
            ResultSet resultSet = stmt.executeQuery(sql);

            StringBuilder resultText = new StringBuilder();

            while (resultSet.next()) {
                String tableName = resultSet.getString(1);
                resultText.append(tableName).append("\n");
            }

            return resultText.toString();
        }
    }




    
    
}
