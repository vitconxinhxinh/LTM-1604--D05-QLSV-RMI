package dao;

import model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class StudentDAO {
    
    // Lấy toàn bộ sinh viên
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT sv.masv, sv.hoten, sv.tuoi, sv.gioitinh, sv.email, sv.sdt, " +
                     "t.tentinh, l.tenlop, k.tenkhoa " +
                     "FROM sinhvien sv " +
                     "JOIN tinh t ON sv.matinh = t.matinh " +
                     "JOIN lop l ON sv.malop = l.malop " +
                     "JOIN khoa k ON l.makhoa = k.makhoa " +
                     "ORDER BY sv.masv";
        
        System.out.println("SQL getAllStudents: " + sql); // Debug
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Student student = mapStudent(rs);
                students.add(student);
                System.out.println("Loaded student: " + student.getMasv() + " - " + student.getHoten()); // Debug
            }
        } catch (Exception e) {
            System.out.println("Lỗi getAllStudents: " + e.getMessage());
            e.printStackTrace();
        }
        return students;
    }

    // Thêm sinh viên mới
    public void addStudent(Student s) {
        String sql = "INSERT INTO sinhvien (masv, hoten, tuoi, gioitinh, email, sdt, matinh, malop) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, s.getMasv());
            ps.setString(2, s.getHoten());
            ps.setInt(3, s.getTuoi());
            ps.setString(4, s.getGioitinh().equals("Nam") ? "M" : "F");
            ps.setString(5, s.getEmail());
            ps.setString(6, s.getSdt());
            
            // Lấy mã tỉnh và mã lớp
            String maTinh = getMaTinhByTen(s.getTenTinh());
            String maLop = getMaLopByTen(s.getTenLop());
            
            ps.setString(7, maTinh);
            ps.setString(8, maLop);
            
            int result = ps.executeUpdate();
            if (result == 0) {
                throw new SQLException("Thêm sinh viên thất bại, không có dòng nào được insert");
            }
        } catch (Exception e) {
            System.out.println("Lỗi addStudent: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // Cập nhật sinh viên
    public void updateStudent(Student s) {
        String sql = "UPDATE sinhvien SET hoten=?, tuoi=?, gioitinh=?, email=?, sdt=?, matinh=?, malop=? WHERE masv=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, s.getHoten());
            ps.setInt(2, s.getTuoi());
            ps.setString(3, s.getGioitinh().equals("Nam") ? "M" : "F");
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getSdt());
            
            // Lấy mã tỉnh và mã lớp
            String maTinh = getMaTinhByTen(s.getTenTinh());
            String maLop = getMaLopByTen(s.getTenLop());
            
            ps.setString(6, maTinh);
            ps.setString(7, maLop);
            ps.setString(8, s.getMasv());
            
            int result = ps.executeUpdate();
            if (result == 0) {
                throw new SQLException("Cập nhật sinh viên thất bại, không có dòng nào được update");
            }
        } catch (Exception e) {
            System.out.println("Lỗi updateStudent: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // Xóa sinh viên theo mã
    public void deleteStudent(String masv) {
        String sql = "DELETE FROM sinhvien WHERE masv = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, masv);
            int result = ps.executeUpdate();
            if (result == 0) {
                throw new SQLException("Xóa sinh viên thất bại, không có dòng nào được delete");
            }
        } catch (Exception e) {
            System.out.println("Lỗi deleteStudent: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // Lấy sinh viên theo mã
    public Student getStudentByMasv(String masv) {
        String sql = "SELECT sv.masv, sv.hoten, sv.tuoi, sv.gioitinh, sv.email, sv.sdt, " +
                     "t.tentinh, l.tenlop, k.tenkhoa " +
                     "FROM sinhvien sv " +
                     "JOIN tinh t ON sv.matinh = t.matinh " +
                     "JOIN lop l ON sv.malop = l.malop " +
                     "JOIN khoa k ON l.makhoa = k.makhoa " +
                     "WHERE sv.masv = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, masv);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return mapStudent(rs);
            }
        } catch (Exception e) {
            System.out.println("Lỗi getStudentByMasv: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Lấy sinh viên theo lớp
    public List<Student> getStudentsByClass(String tenLop) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT sv.masv, sv.hoten, sv.tuoi, sv.gioitinh, sv.email, sv.sdt, " +
                     "t.tentinh, l.tenlop, k.tenkhoa " +
                     "FROM sinhvien sv " +
                     "JOIN tinh t ON sv.matinh = t.matinh " +
                     "JOIN lop l ON sv.malop = l.malop " +
                     "JOIN khoa k ON l.makhoa = k.makhoa " +
                     "WHERE l.tenlop = ? " +
                     "ORDER BY sv.masv";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, tenLop);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                students.add(mapStudent(rs));
            }
        } catch (Exception e) {
            System.out.println("Lỗi getStudentsByClass: " + e.getMessage());
            e.printStackTrace();
        }
        return students;
    }

    // Lưu điểm danh
    public void saveAttendance(String masv, String status, Date date) {
        // Kiểm tra xem đã điểm danh chưa
        String checkSql = "SELECT COUNT(*) FROM diemdanh WHERE masv = ? AND ngay = ?";
        String insertSql = "INSERT INTO diemdanh (masv, ngay, trangthai) VALUES (?, ?, ?)";
        String updateSql = "UPDATE diemdanh SET trangthai = ? WHERE masv = ? AND ngay = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
            
            checkPs.setString(1, masv);
            checkPs.setDate(2, new java.sql.Date(date.getTime()));
            ResultSet rs = checkPs.executeQuery();
            
            boolean exists = false;
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
            rs.close();
            
            if (exists) {
                // Update nếu đã tồn tại
                try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                    updatePs.setString(1, status);
                    updatePs.setString(2, masv);
                    updatePs.setDate(3, new java.sql.Date(date.getTime()));
                    int result = updatePs.executeUpdate();
                    if (result == 0) {
                        throw new SQLException("Cập nhật điểm danh thất bại");
                    }
                }
            } else {
                // Insert mới
                try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                    insertPs.setString(1, masv);
                    insertPs.setDate(2, new java.sql.Date(date.getTime()));
                    insertPs.setString(3, status);
                    int result = insertPs.executeUpdate();
                    if (result == 0) {
                        throw new SQLException("Thêm điểm danh thất bại");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Lỗi saveAttendance: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // Lấy điểm danh theo lớp và ngày
    public List<Object[]> getAttendanceByClass(String tenLop, Date date) {
        List<Object[]> attendance = new ArrayList<>();
        String sql = "SELECT sv.masv, sv.hoten, NVL(dd.trangthai, 'Chưa điểm danh') as trangthai " +
                     "FROM sinhvien sv " +
                     "JOIN lop l ON sv.malop = l.malop " +
                     "LEFT JOIN diemdanh dd ON sv.masv = dd.masv AND TRUNC(dd.ngay) = TRUNC(?) " +
                     "WHERE l.tenlop = ? " +
                     "ORDER BY sv.masv";
        
        System.out.println("getAttendanceByClass SQL: " + sql);
        System.out.println("tenLop: " + tenLop + ", date: " + date);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDate(1, new java.sql.Date(date.getTime()));
            ps.setString(2, tenLop);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Object[] row = new Object[3];
                row[0] = rs.getString("masv");
                row[1] = rs.getString("hoten");
                row[2] = rs.getString("trangthai");
                attendance.add(row);
                
                System.out.println("Attendance: " + row[0] + " - " + row[1] + " - " + row[2]);
            }
            rs.close();
        } catch (Exception e) {
            System.out.println("Lỗi getAttendanceByClass: " + e.getMessage());
            e.printStackTrace();
        }
        return attendance;
    }

    // Tìm kiếm sinh viên
    public List<Student> searchStudents(String masv, String hoten, String gioitinh,
            String tentinh, String tenkhoa, String tenlop) {
        List<Student> students = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT sv.masv, sv.hoten, sv.tuoi, sv.gioitinh, sv.email, sv.sdt, " +
            "t.tentinh, l.tenlop, k.tenkhoa " +
            "FROM sinhvien sv " +
            "JOIN tinh t ON sv.matinh = t.matinh " +
            "JOIN lop l ON sv.malop = l.malop " +
            "JOIN khoa k ON l.makhoa = k.makhoa WHERE 1=1"
        );

        // Debug tham số
        System.out.println("Search params - masv: " + masv + ", hoten: " + hoten + ", gioitinh: " + gioitinh + 
                ", tentinh: " + tentinh + ", tenkhoa: " + tenkhoa + ", tenlop: " + tenlop);

        List<Object> params = new ArrayList<>();

        if (masv != null && !masv.isEmpty() && !masv.equals("-- Chọn MSSV --")) {
            sql.append(" AND sv.masv LIKE ?");
            params.add("%" + masv + "%");
        }
        if (hoten != null && !hoten.isEmpty()) {
            sql.append(" AND LOWER(sv.hoten) LIKE ?");
            params.add("%" + hoten.toLowerCase() + "%");
        }
        if (gioitinh != null && !gioitinh.isEmpty() && !gioitinh.equals("-- Chọn giới tính --")) {
            sql.append(" AND sv.gioitinh = ?");
            params.add(gioitinh.equals("Nam") ? "M" : "F");
        }
        if (tentinh != null && !tentinh.isEmpty() && !tentinh.equals("-- Chọn tỉnh --")) {
            sql.append(" AND t.tentinh = ?");
            params.add(tentinh);
        }
        if (tenkhoa != null && !tenkhoa.isEmpty() && !tenkhoa.equals("-- Chọn khoa --")) {
            sql.append(" AND k.tenkhoa = ?");
            params.add(tenkhoa);
        }
        if (tenlop != null && !tenlop.isEmpty() && !tenlop.equals("-- Chọn lớp --")) {
            sql.append(" AND l.tenlop = ?");
            params.add(tenlop);
        }

        sql.append(" ORDER BY sv.masv");

        System.out.println("Search SQL: " + sql.toString());
        System.out.println("Params: " + params);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    students.add(mapStudent(rs));
                }
            }
            System.out.println("Found " + students.size() + " students");
        } catch (Exception e) {
            System.out.println("Lỗi searchStudents: " + e.getMessage());
            e.printStackTrace();
        }
        return students;
    }

    // Map ResultSet sang Student
    private Student mapStudent(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setMasv(rs.getString("masv"));
        s.setHoten(rs.getString("hoten"));
        s.setTuoi(rs.getInt("tuoi"));
        
        // Xử lý giới tính
        String gt = rs.getString("gioitinh");
        if (gt != null) {
            s.setGioitinh(gt.equalsIgnoreCase("M") ? "Nam" : "Nữ");
        } else {
            s.setGioitinh("");
        }
        
        s.setEmail(rs.getString("email"));
        s.setSdt(rs.getString("sdt"));
        s.setTenTinh(rs.getString("tentinh"));
        s.setTenLop(rs.getString("tenlop"));
        s.setTenKhoa(rs.getString("tenkhoa"));
        return s;
    }

    // Các phương thức hỗ trợ
    private String getMaTinhByTen(String tenTinh) {
        if (tenTinh == null || tenTinh.isEmpty() || tenTinh.equals("-- Chọn tỉnh --")) {
            return null;
        }
        TinhDAO tinhDAO = new TinhDAO();
        return tinhDAO.getMaTinhByTen(tenTinh);
    }

    private String getMaLopByTen(String tenLop) {
        if (tenLop == null || tenLop.isEmpty() || tenLop.equals("-- Chọn lớp --")) {
            return null;
        }
        LopDAO lopDAO = new LopDAO();
        return lopDAO.getMaLopByTen(tenLop);
    }

    // Các phương thức không dùng đến (giữ cho interface RMI)
    public Student getStudentById(int id) { 
        throw new UnsupportedOperationException("Không hỗ trợ tìm sinh viên theo ID");
    }
    
    public void deleteStudent(int id) { 
        throw new UnsupportedOperationException("Không hỗ trợ xóa sinh viên theo ID");
    }
}