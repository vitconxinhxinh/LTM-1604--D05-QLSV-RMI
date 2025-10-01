package Client;

import model.Student;
import rmi.IStudentService;
import dao.KhoaDAO;
import dao.LopDAO;
import dao.TinhDAO;

import javax.swing.*;
import java.awt.*;


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

        // Đổi font mặc định cho toàn bộ giao diện form
        setFont(new javax.swing.plaf.FontUIResource("Segoe UI", Font.PLAIN, 15));
        setTitle(student == null ? "Thêm sinh viên" : "Sửa sinh viên");
        setSize(450, 450);
        setLocationRelativeTo(parent);

        initUI();
        if (student != null) {
            System.out.println("[DEBUG] Gọi fillForm từ constructor StudentForm");
            fillForm(student);
        }
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 250, 255));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200,220,255), 2),
            BorderFactory.createEmptyBorder(18, 24, 10, 24)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel lblMasv = new JLabel("MSSV:*");
        lblMasv.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtMasv = new JTextField();
        txtMasv.setToolTipText("Nhập mã số sinh viên");
        txtMasv.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180,200,230)),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        gbc.gridx = 0; gbc.gridy = 0; panel.add(lblMasv, gbc);
        gbc.gridx = 1; panel.add(txtMasv, gbc);

        JLabel lblHoten = new JLabel("Họ tên:*");
        lblHoten.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtHoten = new JTextField();
        txtHoten.setToolTipText("Nhập họ tên sinh viên");
        txtHoten.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180,200,230)),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        gbc.gridx = 0; gbc.gridy = 1; panel.add(lblHoten, gbc);
        gbc.gridx = 1; panel.add(txtHoten, gbc);

        JLabel lblTuoi = new JLabel("Tuổi:");
        lblTuoi.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtTuoi = new JTextField();
        txtTuoi.setToolTipText("Nhập tuổi sinh viên");
        txtTuoi.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180,200,230)),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        gbc.gridx = 0; gbc.gridy = 2; panel.add(lblTuoi, gbc);
        gbc.gridx = 1; panel.add(txtTuoi, gbc);

        JLabel lblGender = new JLabel("Giới tính:*");
        lblGender.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cbGender = new JComboBox<>(new String[]{"Nam", "Nữ"});
        cbGender.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 3; panel.add(lblGender, gbc);
        gbc.gridx = 1; panel.add(cbGender, gbc);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtEmail = new JTextField();
        txtEmail.setToolTipText("Nhập email sinh viên");
        txtEmail.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180,200,230)),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        gbc.gridx = 0; gbc.gridy = 4; panel.add(lblEmail, gbc);
        gbc.gridx = 1; panel.add(txtEmail, gbc);

        JLabel lblSdt = new JLabel("Số điện thoại:");
        lblSdt.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtSdt = new JTextField();
        txtSdt.setToolTipText("Nhập số điện thoại sinh viên");
        txtSdt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180,200,230)),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        gbc.gridx = 0; gbc.gridy = 5; panel.add(lblSdt, gbc);
        gbc.gridx = 1; panel.add(txtSdt, gbc);

        JLabel lblTinh = new JLabel("Tỉnh:");
        lblTinh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cbTinh = new JComboBox<>();
        cbTinh.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbTinh.setMaximumRowCount(8); // Hiện tối đa 8 dòng, có scroll
        cbTinh.addItem("-- Chọn tỉnh --");
        for (String t : tinhDAO.getAllTinh()) cbTinh.addItem(t);
        gbc.gridx = 0; gbc.gridy = 6; panel.add(lblTinh, gbc);
        gbc.gridx = 1; panel.add(cbTinh, gbc);

        JLabel lblKhoa = new JLabel("Khoa:*");
        lblKhoa.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cbKhoa = new JComboBox<>();
        cbKhoa.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbKhoa.addItem("-- Chọn khoa --");
        for (String k : khoaDAO.getAllTenKhoa()) cbKhoa.addItem(k);
        gbc.gridx = 0; gbc.gridy = 7; panel.add(lblKhoa, gbc);
        gbc.gridx = 1; panel.add(cbKhoa, gbc);

        JLabel lblLop = new JLabel("Lớp:*");
        lblLop.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cbLop = new JComboBox<>();
        cbLop.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbLop.addItem("-- Chọn lớp --");
        gbc.gridx = 0; gbc.gridy = 8; panel.add(lblLop, gbc);
        gbc.gridx = 1; panel.add(cbLop, gbc);

        // load lớp khi chọn khoa
        cbKhoa.addActionListener(e -> loadLopTheoKhoa());

        btnSave = new JButton("Lưu");
        btnSave.setBackground(new Color(76, 175, 80));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnSave.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(76, 175, 80)),
            BorderFactory.createEmptyBorder(6, 24, 6, 24)));
        btnSave.setToolTipText("Lưu thông tin sinh viên");

        btnCancel = new JButton("Hủy");
        btnCancel.setBackground(new Color(244, 67, 54));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnCancel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(244, 67, 54)),
            BorderFactory.createEmptyBorder(6, 24, 6, 24)));
        btnCancel.setToolTipText("Hủy bỏ và đóng cửa sổ");

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(245, 250, 255));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(16, 0, 10, 0));
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);

        JScrollPane scrollPanel = new JScrollPane(panel);
        scrollPanel.setBorder(null);
        scrollPanel.getVerticalScrollBar().setUnitIncrement(16);
        scrollPanel.setPreferredSize(new Dimension(420, 420));

        add(scrollPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        // sự kiện nút
        btnSave.addActionListener(e -> saveStudent());
        btnCancel.addActionListener(e -> dispose());
    }
    
    // Đổi font mặc định cho toàn bộ giao diện
    public static void setFont(javax.swing.plaf.FontUIResource f) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put(key, f);
        }
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
        
        System.out.println("[DEBUG] fillForm được gọi");
        if (s != null) {
            System.out.println("[DEBUG] Tên tỉnh của sinh viên: '" + s.getTenTinh() + "'");
        }
        System.out.println("[DEBUG] cbTinh item count: " + cbTinh.getItemCount());
        // Set tỉnh: luôn chọn đúng tỉnh hiện tại của sinh viên (so sánh loại bỏ dấu, khoảng trắng, không phân biệt hoa thường)
        if (s.getTenTinh() != null) {
            String tenTinh = s.getTenTinh().trim().toLowerCase();
            boolean found = false;
            for (int i = 0; i < cbTinh.getItemCount(); i++) {
                String item = cbTinh.getItemAt(i);
                if (item != null) {
                    String itemNorm = removeDiacritics(item.trim().toLowerCase().replaceAll("\\s+", ""));
                    String tenTinhNorm = removeDiacritics(tenTinh.replaceAll("\\s+", ""));
                    if (itemNorm.equals(tenTinhNorm)) {
                        cbTinh.setSelectedIndex(i);
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                // fallback: thử contains
                for (int i = 0; i < cbTinh.getItemCount(); i++) {
                    String item = cbTinh.getItemAt(i);
                    if (item != null && removeDiacritics(item.toLowerCase()).contains(removeDiacritics(tenTinh))) {
                        cbTinh.setSelectedIndex(i);
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                System.out.println("[DEBUG] Không tìm thấy tỉnh: '" + s.getTenTinh() + "' trong combobox! Danh sách:");
                for (int i = 0; i < cbTinh.getItemCount(); i++) System.out.println(cbTinh.getItemAt(i));
                cbTinh.setSelectedIndex(0); // chọn '-- Chọn tỉnh --'
            }
        } else {
            cbTinh.setSelectedIndex(0); // chọn '-- Chọn tỉnh --'
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

    // Loại bỏ dấu tiếng Việt để so sánh

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
            if (tinh != null && tinh.trim().equals("-- Chọn tỉnh --")) tinh = null;
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
            s.setTenTinh(tinh);
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

    // Loại bỏ dấu tiếng Việt để so sánh
    private static String removeDiacritics(String str) {
        if (str == null) return null;
        String nfdNormalizedString = java.text.Normalizer.normalize(str, java.text.Normalizer.Form.NFD);
        return nfdNormalizedString.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
}