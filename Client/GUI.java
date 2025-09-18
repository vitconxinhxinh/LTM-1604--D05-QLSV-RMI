package Client;

import dao.StudentDAO;
import model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GUI extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private StudentDAO studentDAO = new StudentDAO();

    private JTextField tfSearch;
    private JButton btnSearch;

    public GUI() {
        setTitle("Quản lý sinh viên - RMI + Swing");
        setSize(1250, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ===== TABLE =====
        String[] columnNames = {"ID", "MSSV", "Tên", "Ngày sinh", "Lớp",
                "GPA", "Trạng thái", "Email", "Phone", "Thành phố", "Quận/Huyện", "Phường/Xã"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setFont(new Font("Arial", Font.PLAIN, 13));

        // Renderer để đổi màu theo trạng thái
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

                String status = (String) table.getModel().getValueAt(row, 6); // cột trạng thái
                if ("Bảo lưu".equalsIgnoreCase(status)) {
                    c.setBackground(Color.YELLOW);
                } else if ("Nghỉ học".equalsIgnoreCase(status)) {
                    c.setBackground(Color.PINK);
                } else {
                    c.setBackground(Color.WHITE);
                }

                if (isSelected) {
                    c.setBackground(c.getBackground().darker());
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // ===== BUTTON PANEL =====
        JPanel btnPanel = new JPanel();

        JButton btnLoad = new JButton("Tải lại");
        JButton btnAdd = new JButton("Thêm");
        JButton btnUpdate = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");

        tfSearch = new JTextField(20);
        btnSearch = new JButton("Tìm kiếm");

        btnPanel.add(new JLabel("Tìm (Tên/MSSV):"));
        btnPanel.add(tfSearch);
        btnPanel.add(btnSearch);

        btnPanel.add(btnLoad);
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);

        add(btnPanel, BorderLayout.SOUTH);

        // ===== BUTTON ACTIONS =====
        btnLoad.addActionListener(e -> loadData());
        btnSearch.addActionListener(e -> searchStudent());
        btnAdd.addActionListener(e -> openStudentDialog(null));
        btnUpdate.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = (int) model.getValueAt(row, 0);
                Student s = studentDAO.getStudentById(id);
                openStudentDialog(s);
            } else {
                JOptionPane.showMessageDialog(this, "Chọn 1 sinh viên để sửa!");
            }
        });
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = (int) model.getValueAt(row, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    studentDAO.deleteStudent(id);
                    loadData();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Chọn 1 sinh viên để xóa!");
            }
        });

        loadData();
    }

    // ===== LOAD DATA =====
    private void loadData() {
        model.setRowCount(0);
        List<Student> list = studentDAO.getAllStudents();

        list = list.stream()
                .sorted(Comparator.comparingInt(Student::getId))
                .collect(Collectors.toList());

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

        for (Student s : list) {
            Object[] row = {
                    s.getId(),
                    s.getMssv(),
                    s.getName(),
                    s.getBirthdate() != null ? df.format(s.getBirthdate()) : "",
                    s.getClassName(),
                    s.getGpa(),
                    s.getStatus(),
                    s.getEmail(),
                    s.getPhone(),
                    (s.getAddressDetail() != null && !s.getAddressDetail().isEmpty()) ? s.getAddressDetail().split(" - ")[2] : "",
                    (s.getAddressDetail() != null && !s.getAddressDetail().isEmpty()) ? s.getAddressDetail().split(" - ")[1] : "",
                    (s.getAddressDetail() != null && !s.getAddressDetail().isEmpty()) ? s.getAddressDetail().split(" - ")[0] : ""
            };
            model.addRow(row);
        }
    }

    // ===== SEARCH =====
    private void searchStudent() {
        String keyword = tfSearch.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            loadData();
            return;
        }

        model.setRowCount(0);
        List<Student> list = studentDAO.getAllStudents();

        List<Student> filtered = list.stream()
                .filter(s -> s.getMssv().toLowerCase().contains(keyword) ||
                        s.getName().toLowerCase().contains(keyword))
                .sorted(Comparator.comparingInt(Student::getId))
                .collect(Collectors.toList());

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

        for (Student s : filtered) {
            Object[] row = {
                    s.getId(),
                    s.getMssv(),
                    s.getName(),
                    s.getBirthdate() != null ? df.format(s.getBirthdate()) : "",
                    s.getClassName(),
                    s.getGpa(),
                    s.getStatus(),
                    s.getEmail(),
                    s.getPhone(),
                    (s.getAddressDetail() != null && !s.getAddressDetail().isEmpty()) ? s.getAddressDetail().split(" - ")[2] : "",
                    (s.getAddressDetail() != null && !s.getAddressDetail().isEmpty()) ? s.getAddressDetail().split(" - ")[1] : "",
                    (s.getAddressDetail() != null && !s.getAddressDetail().isEmpty()) ? s.getAddressDetail().split(" - ")[0] : ""
            };
            model.addRow(row);
        }
    }

    // ===== ADD/EDIT DIALOG =====
    private void openStudentDialog(Student student) {
        JDialog dialog = new JDialog(this, student == null ? "Thêm sinh viên" : "Sửa sinh viên", true);
        dialog.setSize(400, 600);
        dialog.setLayout(new GridLayout(13, 2, 5, 5));
        dialog.setLocationRelativeTo(this);

        JTextField tfMssv = new JTextField(student != null ? student.getMssv() : "");
        JTextField tfName = new JTextField(student != null ? student.getName() : "");

        // Ngày sinh tách thành 3 combo
        JComboBox<Integer> cbDay = new JComboBox<>();
        for (int i = 1; i <= 31; i++) cbDay.addItem(i);
        JComboBox<Integer> cbMonth = new JComboBox<>();
        for (int i = 1; i <= 12; i++) cbMonth.addItem(i);
        JComboBox<Integer> cbYear = new JComboBox<>();
        for (int i = 1980; i <= 2025; i++) cbYear.addItem(i);

        if (student != null && student.getBirthdate() != null) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(student.getBirthdate());
            cbDay.setSelectedItem(cal.get(java.util.Calendar.DAY_OF_MONTH));
            cbMonth.setSelectedItem(cal.get(java.util.Calendar.MONTH) + 1);
            cbYear.setSelectedItem(cal.get(java.util.Calendar.YEAR));
        }

        JTextField tfGpa = new JTextField(student != null ? String.valueOf(student.getGpa()) : "");
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Đang học", "Nghỉ học", "Bảo lưu"});
        if (student != null && student.getStatus() != null) cbStatus.setSelectedItem(student.getStatus());

        JTextField tfEmail = new JTextField(student != null ? student.getEmail() : "");
        JTextField tfPhone = new JTextField(student != null ? student.getPhone() : "");

        JComboBox<String> cbClass = new JComboBox<>(new String[]{"Công nghệ thông tin 1", "Công nghệ thông tin 2"});
        JComboBox<String> cbCity = new JComboBox<>(new String[]{"Hà Nội", "TP.HCM"});
        JComboBox<String> cbDistrict = new JComboBox<>(new String[]{"Cầu Giấy", "Quận 1"});
        JComboBox<String> cbWard = new JComboBox<>(new String[]{"Dịch Vọng", "Bến Nghé"});

        dialog.add(new JLabel("MSSV:")); dialog.add(tfMssv);
        dialog.add(new JLabel("Tên:")); dialog.add(tfName);
        dialog.add(new JLabel("Ngày sinh:"));
        JPanel birthPanel = new JPanel(new FlowLayout());
        birthPanel.add(cbDay);
        birthPanel.add(cbMonth);
        birthPanel.add(cbYear);
        dialog.add(birthPanel);
        dialog.add(new JLabel("Lớp:")); dialog.add(cbClass);
        dialog.add(new JLabel("GPA:")); dialog.add(tfGpa);
        dialog.add(new JLabel("Trạng thái:")); dialog.add(cbStatus);
        dialog.add(new JLabel("Email:")); dialog.add(tfEmail);
        dialog.add(new JLabel("Phone:")); dialog.add(tfPhone);
        dialog.add(new JLabel("Thành phố:")); dialog.add(cbCity);
        dialog.add(new JLabel("Quận/Huyện:")); dialog.add(cbDistrict);
        dialog.add(new JLabel("Phường/Xã:")); dialog.add(cbWard);

        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");

        dialog.add(btnSave);
        dialog.add(btnCancel);

        btnSave.addActionListener(e -> {
            try {
                int day = (int) cbDay.getSelectedItem();
                int month = (int) cbMonth.getSelectedItem();
                int year = (int) cbYear.getSelectedItem();
                java.sql.Date birthdate = java.sql.Date.valueOf(year + "-" + month + "-" + day);

                if (student == null) {
                    Student s = new Student();
                    s.setMssv(tfMssv.getText());
                    s.setName(tfName.getText());
                    s.setBirthdate(birthdate);
                    s.setGpa(Double.parseDouble(tfGpa.getText()));
                    s.setStatus(cbStatus.getSelectedItem().toString());
                    s.setEmail(tfEmail.getText());
                    s.setPhone(tfPhone.getText());
                    s.setClassId(cbClass.getSelectedIndex() + 1);
                    s.setAddressId(cbWard.getSelectedIndex() + 1);
                    studentDAO.addStudent(s);
                } else {
                    student.setMssv(tfMssv.getText());
                    student.setName(tfName.getText());
                    student.setBirthdate(birthdate);
                    student.setGpa(Double.parseDouble(tfGpa.getText()));
                    student.setStatus(cbStatus.getSelectedItem().toString());
                    student.setEmail(tfEmail.getText());
                    student.setPhone(tfPhone.getText());
                    student.setClassId(cbClass.getSelectedIndex() + 1);
                    student.setAddressId(cbWard.getSelectedIndex() + 1);
                    studentDAO.updateStudent(student);
                }
                loadData();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage());
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUI().setVisible(true));
    }
}
