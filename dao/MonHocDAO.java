package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MonHocDAO {
    public List<String[]> getAllMonHoc() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT MAMH, TENMH FROM MONHOC ORDER BY TENMH";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{rs.getString("MAMH"), rs.getString("TENMH")});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy danh sách môn học theo mã khoa
    public List<String[]> getMonHocByMaKhoa(String maKhoa) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT MAMH, TENMH, MAKHOA FROM MONHOC WHERE TRIM(UPPER(MAKHOA)) = TRIM(UPPER(?)) ORDER BY TENMH";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            System.out.println("[DEBUG] (THAMSO) Truy vấn lấy môn học với MAKHOA = '" + maKhoa + "'");
            ps.setString(1, maKhoa);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String mamh = rs.getString("MAMH");
                    String tenmh = rs.getString("TENMH");
                    String makhoa = rs.getString("MAKHOA");
                    System.out.println("[DEBUG] getMonHocByMaKhoa: " + mamh + " - " + tenmh + " - " + makhoa);
                    list.add(new String[]{mamh, tenmh});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
