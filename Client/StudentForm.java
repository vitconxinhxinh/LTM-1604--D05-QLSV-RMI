package Client;

import model.Student;
import rmi.IStudentService;
import dao.KhoaDAO;
import dao.LopDAO;
import dao.TinhDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Form nhập thông tin Sinh viên (dùng cho cả Thêm và Sửa)
 */
public class StudentForm extends JDialog {
    private JTextField txtMasv, txtHoten, txtTuoi, txtEmail, txtSdt;
    private JComboBox<String> cbGender, cbTinh, cbKhoa, cbLop;

    private JButton btnSave, btnCancel;

    private IStudentService service;
    private Student student; // nếu != null => đang sửa

    private KhoaDAO khoaDAO = new KhoaDAO();
    private LopDAO lopDAO = new LopDAO();
    private TinhDAO tinhDAO = new TinhDAO();

    public StudentForm(JFrame parent, IStudentService service, Student student) {
        super(parent, true);
        this.service = service;
        this.student = student;

        setTitle(student == null ? "Thêm sinh viên" : "Sửa sinh viên");
        setSize(450, 450);
        setLocationRelativeTo(parent);

        initUI();
        if (student != null) {
            fillForm(student);
        }
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(10, 2, 5, 5));

        panel.add(new JLabel("MSSV:*"));
        txtMasv = new JTextField();
        panel.add(txtMasv);

        panel.add(new JLabel("Họ tên:*"));
        txtHoten = new JTextField();
        panel.add(txtHoten);

        panel.add(new JLabel("Tuổi:"));
        txtTuoi = new JTextField();
        panel.add(txtTuoi);

        panel.add(new JLabel("Giới tính:*"));
        cbGender = new JComboBox<>(new String[]{"Nam", "Nữ"});
        panel.add(cbGender);

        panel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        panel.add(txtEmail);

        panel.add(new JLabel("Số điện thoại:"));
        txtSdt = new JTextField();
        panel.add(txtSdt);

        panel.add(new JLabel("Tỉnh:"));
        cbTinh = new JComboBox<>();
        cbTinh.addItem("-- Chọn tỉnh --");
        for (String t : tinhDAO.getAllTinh()) cbTinh.addItem(t);
        panel.add(cbTinh);

        panel.add(new JLabel("Khoa:*"));
        cbKhoa = new JComboBox<>();
        cbKhoa.addItem("-- Chọn khoa --");
        for (String k : khoaDAO.getAllTenKhoa()) cbKhoa.addItem(k);
        panel.add(cbKhoa);

        panel.add(new JLabel("Lớp:*"));
        cbLop = new JComboBox<>();
        cbLop.addItem("-- Chọn lớp --");
        panel.add(cbLop);

        // load lớp khi chọn khoa
        cbKhoa.addActionListener(e -> loadLopTheoKhoa());

        btnSave = new JButton("Lưu");
        btnCancel = new JButton("Hủy");

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);

        add(panel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        // sự kiện nút
        btnSave.addActionListener(e -> saveStudent());
        btnCancel.addActionListener(e -> dispose());
    }

    private void loadLopTheoKhoa() {
        cbLop.removeAllItems();
        cbLop.addItem("-- Chọn lớp --");
        
        String khoa = (String) cbKhoa.getSelectedItem();
        if (khoa != null && !khoa.equals("-- Chọn khoa --")) {
            for (String l : lopDAO.getTenLopByKhoa(khoa)) {
                cbLop.addItem(l);
            }
        }
    }

    private void fillForm(Student s) {
        txtMasv.setText(s.getMasv());
        if (student != null) {
            txtMasv.setEnabled(false); // MSSV không cho sửa khi đang chỉnh sửa
        }

        txtHoten.setText(s.getHoten());
        txtTuoi.setText(s.getTuoi() > 0 ? String.valueOf(s.getTuoi()) : "");
        
        // Set giới tính
        if (s.getGioitinh() != null) {
            if (s.getGioitinh().equals("Nam") || s.getGioitinh().equals("M")) {
                cbGender.setSelectedItem("Nam");
            } else {
                cbGender.setSelectedItem("Nữ");
            }
        }
        
        txtEmail.setText(s.getEmail() != null ? s.getEmail() : "");
        txtSdt.setText(s.getSdt() != null ? s.getSdt() : "");
        
        // Set tỉnh
        if (s.getTenTinh() != null) {
            cbTinh.setSelectedItem(s.getTenTinh());
        } else {
            cbTinh.setSelectedItem("-- Chọn tỉnh --");
        }
        
        // Set khoa và lớp
        if (s.getTenKhoa() != null) {
            cbKhoa.setSelectedItem(s.getTenKhoa());
            // Load lớp của khoa này
            loadLopTheoKhoa();
            if (s.getTenLop() != null) {
                cbLop.setSelectedItem(s.getTenLop());
            }
        } else {
            cbKhoa.setSelectedItem("-- Chọn khoa --");
            cbLop.setSelectedItem("-- Chọn lớp --");
        }
    }

    private void saveStudent() {
        try {
            // Lấy dữ liệu từ form
            String masv = txtMasv.getText().trim();
            String hoten = txtHoten.getText().trim();
            String tuoiStr = txtTuoi.getText().trim();
            String gioitinh = (String) cbGender.getSelectedItem();
            String email = txtEmail.getText().trim();
            String sdt = txtSdt.getText().trim();
            String tinh = (String) cbTinh.getSelectedItem();
            String khoa = (String) cbKhoa.getSelectedItem();
            String lop = (String) cbLop.getSelectedItem();

            // Validate dữ liệu
            if (masv.isEmpty() || hoten.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mã SV và Họ tên không được bỏ trống!");
                return;
            }

            if (khoa.equals("-- Chọn khoa --") || lop.equals("-- Chọn lớp --")) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn khoa và lớp!");
                return;
            }

            // Xử lý tuổi
            int tuoi = 0;
            if (!tuoiStr.isEmpty()) {
                try {
                    tuoi = Integer.parseInt(tuoiStr);
                    if (tuoi < 0 || tuoi > 100) {
                        JOptionPane.showMessageDialog(this, "Tuổi phải từ 0 đến 100!");
                        return;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Tuổi phải là số!");
                    return;
                }
            }

            // Tạo đối tượng Student
            Student s = new Student();
            s.setMasv(masv);
            s.setHoten(hoten);
            s.setTuoi(tuoi);
            s.setGioitinh(gioitinh);
            s.setEmail(email);
            s.setSdt(sdt);
            s.setTenTinh(tinh.equals("-- Chọn tỉnh --") ? null : tinh);
            s.setTenKhoa(khoa);
            s.setTenLop(lop);

            // Lưu sinh viên
            if (student == null) { // thêm mới
                service.addStudent(s);
                JOptionPane.showMessageDialog(this, "Thêm sinh viên thành công!");
            } else { // cập nhật
                service.updateStudent(s);
                JOptionPane.showMessageDialog(this, "Cập nhật sinh viên thành công!");
            }

            dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu sinh viên: " + e.getMessage());
        }
    }
}