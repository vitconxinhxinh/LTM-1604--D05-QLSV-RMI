package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TinhDAO {
    private Connection conn;

    public TinhDAO() {
        conn = DBConnection.getConnection();
    }

    // Lấy toàn bộ danh sách tỉnh
    public List<String> getAllTinh() {
        List<String> list = new ArrayList<>();
        try {
            String sql = "SELECT matinh || ' - ' || tentinh AS tinh FROM tinh ORDER BY matinh";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                list.add(rs.getString("tinh"));
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy mã tỉnh từ tên tỉnh
    public String getMaTinhByTen(String tentinh) {
        try {
            String sql = "SELECT matinh FROM tinh WHERE tentinh = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, tentinh);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("matinh");
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
