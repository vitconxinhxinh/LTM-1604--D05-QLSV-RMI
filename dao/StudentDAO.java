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
             "t.tentinh, l.tenlop, k.tenkhoa, l.makhoa " +
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
        // Kiểm tra trùng mã sinh viên
        if (s.getMasv() == null || s.getMasv().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sinh viên không được để trống!");
        }
        if (getStudentByMasv(s.getMasv()) != null) {
            throw new IllegalArgumentException("Mã sinh viên đã tồn tại!");
        }
        // Kiểm tra mã tỉnh, mã lớp
        String maTinh = getMaTinhByTen(s.getTenTinh());
        String maLop = getMaLopByTen(s.getTenLop());
        if (maTinh == null) throw new IllegalArgumentException("Tỉnh không hợp lệ!");
        if (maLop == null) throw new IllegalArgumentException("Lớp không hợp lệ!");
        String sql = "INSERT INTO sinhvien (masv, hoten, tuoi, gioitinh, email, sdt, matinh, malop) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getMasv());
            ps.setString(2, s.getHoten());
            ps.setInt(3, s.getTuoi());
            ps.setString(4, s.getGioitinh().equals("Nam") ? "M" : "F");
            ps.setString(5, s.getEmail());
            ps.setString(6, s.getSdt());
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
        // Kiểm tra tồn tại mã sinh viên
        if (s.getMasv() == null || s.getMasv().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sinh viên không được để trống!");
        }
        if (getStudentByMasv(s.getMasv()) == null) {
            throw new IllegalArgumentException("Không tìm thấy sinh viên để cập nhật!");
        }
        // Kiểm tra mã tỉnh, mã lớp
        String maTinh = getMaTinhByTen(s.getTenTinh());
        String maLop = getMaLopByTen(s.getTenLop());
        if (maTinh == null) throw new IllegalArgumentException("Tỉnh không hợp lệ!");
        if (maLop == null) throw new IllegalArgumentException("Lớp không hợp lệ!");
        String sql = "UPDATE sinhvien SET hoten=?, tuoi=?, gioitinh=?, email=?, sdt=?, matinh=?, malop=? WHERE masv=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getHoten());
            ps.setInt(2, s.getTuoi());
            ps.setString(3, s.getGioitinh().equals("Nam") ? "M" : "F");
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getSdt());
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
        // Kiểm tra tồn tại mã sinh viên
        if (masv == null || masv.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sinh viên không được để trống!");
        }
        if (getStudentByMasv(masv) == null) {
            throw new IllegalArgumentException("Không tìm thấy sinh viên để xóa!");
        }
        // Kiểm tra ràng buộc điểm danh
        String checkDD = "SELECT COUNT(*) FROM diemdanh WHERE masv = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkDD)) {
            checkPs.setString(1, masv);
            ResultSet rs = checkPs.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new IllegalArgumentException("Không thể xóa sinh viên đã có dữ liệu điểm danh!");
            }
            rs.close();
        } catch (Exception e) {
            System.out.println("Lỗi kiểm tra điểm danh khi xóa: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
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
             "t.tentinh, l.tenlop, k.tenkhoa, l.makhoa " +
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
        // ...existing code...
    }

    // Lưu điểm danh theo môn học
    public void saveAttendance(String masv, String mamh, String status, Date date) {
        if (masv == null || masv.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sinh viên không được để trống!");
        }
        if (mamh == null || mamh.trim().isEmpty()) {
            System.err.println("[ERROR] Mã môn học truyền vào bị null hoặc rỗng khi lưu điểm danh! masv=" + masv + ", status=" + status + ", date=" + date);
            throw new IllegalArgumentException("Mã môn học không được để trống!");
        }
        int soTiet;
        try {
            soTiet = Integer.parseInt(status);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Trạng thái điểm danh phải là số nguyên!");
        }
        if (soTiet < 0 || soTiet > 5) {
            throw new IllegalArgumentException("Số tiết vắng phải từ 0 đến 5!");
        }
        if (date == null) {
            throw new IllegalArgumentException("Ngày điểm danh không hợp lệ!");
        }
        System.out.println("[DEBUG] saveAttendance: masv=" + masv + ", mamh=" + mamh + ", soTiet=" + soTiet + ", date=" + date);
    String checkSql = "SELECT COUNT(*) FROM diemdanh WHERE masv = ? AND mamh = ? AND TRUNC(ngay) = TRUNC(?)";
    String insertSql = "INSERT INTO diemdanh (masv, mamh, ngay, trangthai) VALUES (?, ?, ?, ?)";
    String updateSql = "UPDATE diemdanh SET trangthai = ? WHERE masv = ? AND mamh = ? AND TRUNC(ngay) = TRUNC(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
            checkPs.setString(1, masv);
            checkPs.setString(2, mamh);
            checkPs.setDate(3, new java.sql.Date(date.getTime()));
            ResultSet rs = checkPs.executeQuery();
            boolean exists = false;
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
            rs.close();
            if (exists) {
                try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                    updatePs.setInt(1, soTiet);
                    updatePs.setString(2, masv);
                    updatePs.setString(3, mamh);
                    updatePs.setDate(4, new java.sql.Date(date.getTime())); // dùng cho TRUNC(ngay) = TRUNC(?)
                    int result = updatePs.executeUpdate();
                    System.out.println("[DEBUG] UPDATE diemdanh result: " + result);
                    if (result == 0) {
                        throw new SQLException("Cập nhật điểm danh thất bại");
                    }
                }
            } else {
                try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                    insertPs.setString(1, masv);
                    insertPs.setString(2, mamh);
                    insertPs.setDate(3, new java.sql.Date(date.getTime()));
                    insertPs.setInt(4, soTiet);
                    int result = insertPs.executeUpdate();
                    System.out.println("[DEBUG] INSERT diemdanh result: " + result);
                    if (result == 0) {
                        throw new SQLException("Thêm điểm danh thất bại");
                    }
                }
            }
            // Bổ sung log: In ra bản ghi vừa lưu, so sánh ngày chỉ theo ngày (TRUNC)
            String logSql = "SELECT masv, mamh, ngay, trangthai FROM diemdanh WHERE masv = ? AND mamh = ? AND TRUNC(ngay) = TRUNC(?)";
            try (PreparedStatement logPs = conn.prepareStatement(logSql)) {
                logPs.setString(1, masv);
                logPs.setString(2, mamh);
                logPs.setDate(3, new java.sql.Date(date.getTime()));
                ResultSet logRs = logPs.executeQuery();
                while (logRs.next()) {
                    System.out.println("[LOG] DIEMDANH: masv=" + logRs.getString("masv") + ", mamh=" + logRs.getString("mamh") + ", ngay=" + logRs.getDate("ngay") + ", trangthai=" + logRs.getInt("trangthai"));
                }
                logRs.close();
            }
        } catch (Exception e) {
            System.out.println("Lỗi saveAttendance (theo môn): " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // Lấy điểm danh theo lớp, môn học và ngày
    // Lấy điểm danh cho một sinh viên, trả về danh sách môn học và số tiết nghỉ
    public List<Object[]> getAttendanceByStudent(String masv) {
        List<Object[]> attendance = new ArrayList<>();
        String sql = "SELECT mh.tenmh, NVL(SUM(dd.trangthai), 0) AS soTietNghi " +
            "FROM monhoc mh " +
            "LEFT JOIN diemdanh dd ON mh.mamh = dd.mamh AND dd.masv = ? " +
            "GROUP BY mh.tenmh, mh.mamh " +
            "ORDER BY mh.mamh";
        System.out.println("getAttendanceByStudent SQL: " + sql);
        System.out.println("masv: " + masv);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, masv);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[2];
                row[0] = rs.getString("tenmh");
                row[1] = rs.getInt("soTietNghi");
                attendance.add(row);
                System.out.println("Attendance: " + row[0] + " - Số tiết nghỉ: " + row[1]);
            }
            rs.close();
        } catch (Exception e) {
            System.out.println("Lỗi getAttendanceByStudent: " + e.getMessage());
            e.printStackTrace();
        }
        return attendance;
    }

    // Lấy điểm danh theo lớp và ngày
    public List<Object[]> getAttendanceByClass(String tenLop, Date date) {
        List<Object[]> attendance = new ArrayList<>();
    String sql = "SELECT sv.masv, sv.hoten, mh.tenmh, dd.trangthai " +
        "FROM sinhvien sv " +
        "JOIN lop l ON sv.malop = l.malop " +
        "JOIN monhoc mh ON mh.makhoa = l.makhoa " +
        "LEFT JOIN diemdanh dd ON sv.masv = dd.masv AND mh.mamh = dd.mamh " +
        "WHERE l.tenlop = ? AND (dd.ngay IS NULL OR TRUNC(dd.ngay) = TRUNC(?)) " +
        "ORDER BY sv.masv, mh.mamh";
        System.out.println("getAttendanceByClass SQL: " + sql);
        System.out.println("tenLop: " + tenLop + ", date: " + date);
       try (Connection conn = DBConnection.getConnection();
           PreparedStatement ps = conn.prepareStatement(sql)) {
          ps.setString(1, tenLop);
          ps.setDate(2, new java.sql.Date(date.getTime()));
          ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[4];
                row[0] = rs.getString("masv");
                row[1] = rs.getString("hoten");
                row[2] = rs.getString("tenmh");
                Object trangthaiObj = rs.getObject("trangthai");
                row[3] = (trangthaiObj == null) ? null : trangthaiObj;
                attendance.add(row);
                System.out.println("Attendance: " + row[0] + " - " + row[1] + " - " + row[2] + " - " + (trangthaiObj == null ? "Chưa điểm danh" : trangthaiObj));
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
            try { s.setMakhoa(rs.getString("makhoa")); } catch (Exception ex) { s.setMakhoa(""); }
            return s;
    }

    // Các phương thức hỗ trợ
    private String getMaTinhByTen(String tenTinh) {
        if (tenTinh == null || tenTinh.isEmpty() || tenTinh.equals("-- Chọn tỉnh --")) {
            return null;
        }
        // Nếu chuỗi có dạng 'matinh - tentinh' thì chỉ lấy phần tên tỉnh
        String tenChiTiet = tenTinh;
        if (tenTinh.contains("-")) {
            String[] parts = tenTinh.split("-", 2);
            if (parts.length == 2) tenChiTiet = parts[1].trim();
        }
        TinhDAO tinhDAO = new TinhDAO();
        return tinhDAO.getMaTinhByTen(tenChiTiet);
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
    // Lấy điểm danh theo lớp, môn học và ngày
    public List<Object[]> getAttendanceByClassAndSubject(String tenLop, String mamh, Date date) {
        List<Object[]> attendance = new ArrayList<>();
        String sql = "SELECT sv.masv, sv.hoten, mh.tenmh, dd.trangthai " +
            "FROM sinhvien sv " +
            "JOIN lop l ON sv.malop = l.malop " +
            "JOIN monhoc mh ON mh.makhoa = l.makhoa AND mh.mamh = ? " +
            "LEFT JOIN diemdanh dd ON sv.masv = dd.masv AND mh.mamh = dd.mamh " +
            "WHERE l.tenlop = ? AND (dd.ngay IS NULL OR TRUNC(dd.ngay) = TRUNC(?)) " +
            "ORDER BY sv.masv, mh.mamh";
        System.out.println("getAttendanceByClassAndSubject SQL: " + sql);
        System.out.println("tenLop: " + tenLop + ", mamh: " + mamh + ", date: " + date);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mamh);
            ps.setString(2, tenLop);
            ps.setDate(3, new java.sql.Date(date.getTime()));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[4];
                row[0] = rs.getString("masv");
                row[1] = rs.getString("hoten");
                row[2] = rs.getString("tenmh");
                Object trangthaiObj = rs.getObject("trangthai");
                row[3] = (trangthaiObj == null) ? null : trangthaiObj;
                attendance.add(row);
                System.out.println("Attendance: " + row[0] + " - " + row[1] + " - " + row[2] + " - " + (trangthaiObj == null ? "Chưa điểm danh" : trangthaiObj));
            }
            rs.close();
        } catch (Exception e) {
            System.out.println("Lỗi getAttendanceByClassAndSubject: " + e.getMessage());
            e.printStackTrace();
        }
        return attendance;
    }
}