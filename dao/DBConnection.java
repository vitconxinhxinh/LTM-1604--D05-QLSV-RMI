package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static Connection conn = null;

    public static Connection getConnection() {
        if (conn == null) {
            try {
                // Nạp driver Oracle JDBC
                Class.forName("oracle.jdbc.driver.OracleDriver");

                String url = "jdbc:oracle:thin:@localhost:1521:orcl";
                
                // Tài khoản và mật khẩu Oracle
                String user = "system";
                String password = "123456";

                conn = DriverManager.getConnection(url, user, password);
                System.out.println(">> Kết nối Oracle thành công!");
            } catch (ClassNotFoundException e) {
                System.out.println(">> Không tìm thấy Driver Oracle!");
                e.printStackTrace();
            } catch (SQLException e) {
                System.out.println(">> Lỗi kết nối CSDL!");
                e.printStackTrace();
            }
        }
        return conn;
    }
}
