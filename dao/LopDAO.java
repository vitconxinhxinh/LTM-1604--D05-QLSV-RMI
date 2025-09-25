package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LopDAO {
    private Connection conn;

    public LopDAO() {
        conn = DBConnection.getConnection();
    }

    // Lấy danh sách tên lớp theo tên khoa
    public List<String> getTenLopByKhoa(String tenKhoa) {
        List<String> list = new ArrayList<>();
        try {
            String sql = "SELECT l.tenlop " +
                         "FROM lop l JOIN khoa k ON l.makhoa = k.makhoa " +
                         "WHERE k.tenkhoa = ? " +
                         "ORDER BY l.tenlop";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, tenKhoa);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("tenlop"));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy mã lớp theo tên lớp
    public String getMaLopByTen(String tenLop) {
        try {
            String sql = "SELECT malop FROM lop WHERE tenlop = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, tenLop);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("malop");
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}