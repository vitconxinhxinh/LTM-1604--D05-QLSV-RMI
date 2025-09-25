package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:orcl";
    private static final String USER = "system";
    private static final String PASSWORD = "123456";
    
    static {
        try {
            // Nạp driver Oracle JDBC
            Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println(">> Oracle JDBC Driver loaded successfully!");
        } catch (ClassNotFoundException e) {
            System.out.println(">> Không tìm thấy Driver Oracle!");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println(">> Kết nối Oracle thành công!");
            return conn;
        } catch (SQLException e) {
            System.out.println(">> Lỗi kết nối CSDL: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    // Phương thức kiểm tra kết nối
    public static boolean isConnectionValid(Connection conn) {
        if (conn == null) return false;
        try {
            return conn.isValid(2); // Kiểm tra trong 2 giây
        } catch (SQLException e) {
            return false;
        }
    }
}