package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhoaDAO {
    private Connection conn;

    public KhoaDAO() {
        conn = DBConnection.getConnection();
    }

    // Lấy danh sách tên khoa
    public List<String> getAllTenKhoa() {
        List<String> list = new ArrayList<>();
        try {
            String sql = "SELECT tenkhoa FROM khoa ORDER BY tenkhoa";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                list.add(rs.getString("tenkhoa"));
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy mã khoa theo tên khoa (phục vụ LopDAO)
    public String getMaKhoaByTen(String tenKhoa) {
        try {
            String sql = "SELECT makhoa FROM khoa WHERE tenkhoa = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, tenKhoa);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("makhoa");
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
