package dao;

import model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StudentDAO {
    private Connection conn;

    public StudentDAO() {
        conn = DBConnection.getConnection();
    }

    // Thêm sinh viên mới
    public void addStudent(Student s) {
        try {
            String sql = "INSERT INTO student(mssv, name, birthdate, class_id, gpa, email, phone, address_id, status) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, s.getMssv());
            ps.setString(2, s.getName());
            ps.setDate(3, s.getBirthdate() != null ? new java.sql.Date(s.getBirthdate().getTime()) : null);
            ps.setInt(4, s.getClassId());
            ps.setDouble(5, s.getGpa());
            ps.setString(6, s.getEmail());
            ps.setString(7, s.getPhone());

            // addressId có thể null
            if (s.getAddressId() == 0) {
                ps.setNull(8, java.sql.Types.INTEGER);
            } else {
                ps.setInt(8, s.getAddressId());
            }

            ps.setString(9, s.getStatus());

            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lấy danh sách sinh viên (kèm city/district/ward)
    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        try {
            String sql = "SELECT s.id, s.mssv, s.name, s.birthdate, " +
                         "s.class_id, c.class_name, s.gpa, s.email, s.phone, " +
                         "s.address_id, a.city, a.district, a.ward, s.status " +
                         "FROM student s " +
                         "LEFT JOIN class c ON s.class_id = c.class_id " +
                         "LEFT JOIN address a ON s.address_id = a.address_id " +
                         "ORDER BY s.id ASC";

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                Student student = new Student(
                        rs.getInt("id"),
                        rs.getString("mssv"),
                        rs.getString("name"),
                        rs.getDate("birthdate"),
                        rs.getInt("class_id"),
                        rs.getDouble("gpa"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getInt("address_id")
                );

                // Tên lớp
                student.setClassName(rs.getString("class_name"));

                // Địa chỉ tách
                student.setCity(rs.getString("city"));
                student.setDistrict(rs.getString("district"));
                student.setWard(rs.getString("ward"));

                // Trạng thái
                student.setStatus(rs.getString("status"));

                list.add(student);
            }

            rs.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Cập nhật sinh viên
    public void updateStudent(Student s) {
        try {
            String sql = "UPDATE student SET mssv=?, name=?, birthdate=?, gpa=?, email=?, phone=?, class_id=?, address_id=?, status=? WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, s.getMssv());
            ps.setString(2, s.getName());
            ps.setDate(3, s.getBirthdate() != null ? new java.sql.Date(s.getBirthdate().getTime()) : null);
            ps.setDouble(4, s.getGpa());
            ps.setString(5, s.getEmail());
            ps.setString(6, s.getPhone());
            ps.setInt(7, s.getClassId());

            if (s.getAddressId() == 0) {
                ps.setNull(8, java.sql.Types.INTEGER);
            } else {
                ps.setInt(8, s.getAddressId());
            }

            ps.setString(9, s.getStatus());
            ps.setInt(10, s.getId());

            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lấy sinh viên theo ID (kèm address/class)
    public Student getStudentById(int id) {
        try {
            String sql = "SELECT s.id, s.mssv, s.name, s.birthdate, " +
                         "s.class_id, c.class_name, s.gpa, s.email, s.phone, " +
                         "s.address_id, a.city, a.district, a.ward, s.status " +
                         "FROM student s " +
                         "LEFT JOIN class c ON s.class_id = c.class_id " +
                         "LEFT JOIN address a ON s.address_id = a.address_id " +
                         "WHERE s.id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Student student = new Student(
                        rs.getInt("id"),
                        rs.getString("mssv"),
                        rs.getString("name"),
                        rs.getDate("birthdate"),
                        rs.getInt("class_id"),
                        rs.getDouble("gpa"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getInt("address_id")
                );
                student.setClassName(rs.getString("class_name"));
                student.setCity(rs.getString("city"));
                student.setDistrict(rs.getString("district"));
                student.setWard(rs.getString("ward"));
                student.setStatus(rs.getString("status"));
                return student;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Xóa sinh viên
    public void deleteStudent(int id) {
        try {
            String sql = "DELETE FROM student WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------------------------
    // Các phương thức hỗ trợ cho Address / Class
    // ---------------------------

    // Lấy danh sách city distinct từ table address
    public List<String> getAllCities() {
        List<String> list = new ArrayList<>();
        try {
            String sql = "SELECT DISTINCT city FROM address ORDER BY city";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(rs.getString("city"));
            rs.close();
            ps.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Lấy danh sách district theo city
    public List<String> getDistrictsByCity(String city) {
        List<String> list = new ArrayList<>();
        if (city == null) return list;
        try {
            String sql = "SELECT DISTINCT district FROM address WHERE city = ? ORDER BY district";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, city);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(rs.getString("district"));
            rs.close();
            ps.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Lấy danh sách ward theo city + district
    public List<String> getWardsByCityAndDistrict(String city, String district) {
        List<String> list = new ArrayList<>();
        if (city == null || district == null) return list;
        try {
            String sql = "SELECT DISTINCT ward FROM address WHERE city = ? AND district = ? ORDER BY ward";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, city);
            ps.setString(2, district);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(rs.getString("ward"));
            rs.close();
            ps.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Lấy address_id từ city/district/ward (nếu không tìm thấy trả về 0)
    public int getAddressId(String city, String district, String ward) {
        try {
            String sql = "SELECT address_id FROM address WHERE city = ? AND district = ? AND ward = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, city);
            ps.setString(2, district);
            ps.setString(3, ward);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("address_id");
                rs.close();
                ps.close();
                return id;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // Lấy danh sách class name từ bảng class
    public List<String> getAllClassNames() {
        List<String> list = new ArrayList<>();
        try {
            String sql = "SELECT DISTINCT class_name FROM class ORDER BY class_name";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(rs.getString("class_name"));
            rs.close();
            ps.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Lấy class_id theo tên class (nếu không tìm thấy trả về 0)
    public int getClassIdByName(String className) {
        if (className == null) return 0;
        try {
            String sql = "SELECT class_id FROM class WHERE class_name = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, className);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("class_id");
                rs.close();
                ps.close();
                return id;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
    
    
 // Lấy tất cả địa chỉ từ DB
    public List<String> getAllAddresses() {
        List<String> list = new ArrayList<>();
        try {
            String sql = "SELECT city, district, ward FROM address";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                String detail = rs.getString("ward") + " - " 
                              + rs.getString("district") + " - " 
                              + rs.getString("city");
                list.add(detail);
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy tất cả tên lớp từ DB
    public List<String> getAllClasses() {
        List<String> list = new ArrayList<>();
        try {
            String sql = "SELECT class_name FROM class";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                list.add(rs.getString("class_name"));
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


}
