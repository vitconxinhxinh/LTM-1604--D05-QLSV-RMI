package dao;

import model.BangDiem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BangDiemDAO {
    public List<BangDiem> getAllScores() {
        List<BangDiem> list = new ArrayList<>();
        String sql = "SELECT MASV, MAMH, DIEMCC, DIEMQT, DIEMCK, DIEMTK, MAGV FROM DIEM";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                BangDiem bd = new BangDiem(
                    rs.getString("MASV"),
                    rs.getString("MAMH"),
                    rs.getDouble("DIEMCC"),
                    rs.getDouble("DIEMQT"),
                    rs.getDouble("DIEMCK"),
                    rs.getDouble("DIEMTK"),
                    rs.getString("MAGV")
                );
                list.add(bd);
            }
        } catch (Exception e) {
            System.out.println("Lỗi getAllScores: " + e.getMessage());
        }
        return list;
    }

    public boolean addScore(BangDiem bd) {
        String sql = "INSERT INTO DIEM (MASV, MAMH, DIEMCC, DIEMQT, DIEMCK, DIEMTK, MAGV) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bd.getMasv());
            ps.setString(2, bd.getMamh());
            ps.setDouble(3, bd.getDiemCC());
            ps.setDouble(4, bd.getDiemQT());
            ps.setDouble(5, bd.getDiemCK());
            ps.setDouble(6, bd.getDiemTK());
            ps.setString(7, bd.getMagv());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Lỗi addScore: " + e.getMessage());
            return false;
        }
    }

    public boolean updateScore(BangDiem bd) {
        String sql = "UPDATE DIEM SET DIEMCC=?, DIEMQT=?, DIEMCK=?, DIEMTK=?, MAGV=? WHERE MASV=? AND MAMH=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, bd.getDiemCC());
            ps.setDouble(2, bd.getDiemQT());
            ps.setDouble(3, bd.getDiemCK());
            ps.setDouble(4, bd.getDiemTK());
            ps.setString(5, bd.getMagv());
            ps.setString(6, bd.getMasv());
            ps.setString(7, bd.getMamh());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Lỗi updateScore: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteScore(String masv, String mamh) {
        String sql = "DELETE FROM DIEM WHERE MASV=? AND MAMH=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, masv);
            ps.setString(2, mamh);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Lỗi deleteScore: " + e.getMessage());
            return false;
        }
    }
}
