package dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseHelper {
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String USER = "system";       // user bạn tạo trong Oracle
    private static final String PASS = "123456";     // mật khẩu

    public static Connection getConnection() throws Exception {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
