package Client;

import dao.StudentDAO;
import model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class StudentManagementGUI extends JFrame {
    private StudentDAO studentDAO = new StudentDAO();

    private LinkedHashMap<Integer, String> addressMap = new LinkedHashMap<>();

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField tfId;
    private JTextField tfMssv;
    private JTextField tfName;
    private JTextField tfBirth;
    private JTextField tfGpa;
    private JComboBox<String> cbClass;
    private JComboBox<String> cbAddress;
    private JComboBox<String> cbStatus;
    private JTextField tfEmail;
    private JTextField tfPhone;
    private JTextField tfSearch;
    private JLabel lblSummary;

    public StudentManagementGUI() {
        setTitle("Quản lý sinh viên");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        initComponents();
        loadAllData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(8, 8));

        // Top: search
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Tìm (Tên / MSSV):"));
        tfSearch = new JTextField(30);
        top.add(tfSearch);
        JButton btnSearch = new JButton("Tìm");
        JButton btnReload = new JButton("Tải lại");
        top.add(btnSearch);
        top.add(btnReload);
        add(top, BorderLayout.NORTH);

        // Left: form
        JPanel left = new JPanel(new BorderLayout());
        left.setPreferredSize(new Dimension(380, 0));
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        int row = 0;

        tfId = new JTextField();
        tfId.setEditable(false);
        tfMssv = new JTextField("Auto");
        tfMssv.setEditable(false);
        tfName = new JTextField();
        tfBirth = new JTextField();
        tfGpa = new JTextField();
        cbClass = new JComboBox<>();
        cbAddress = new JComboBox<>();
        cbStatus = new JComboBox<>(new String[]{"Đang học", "Bảo lưu", "Nghỉ học", "Đã tốt nghiệp"});
        tfEmail = new JTextField();
        tfPhone = new JTextField();

        addFormRow(form, g, row++, "ID:", tfId);
        addFormRow(form, g, row++, "MSSV:", tfMssv);
        addFormRow(form, g, row++, "Họ và tên:", tfName);
        addFormRow(form, g, row++, "Ngày sinh (yyyy-MM-dd):", tfBirth);
        addFormRow(form, g, row++, "Lớp:", cbClass);
        addFormRow(form, g, row++, "GPA:", tfGpa);
        addFormRow(form, g, row++, "Trạng thái:", cbStatus);
        addFormRow(form, g, row++, "Email:", tfEmail);
        addFormRow(form, g, row++, "SĐT:", tfPhone);
        addFormRow(form, g, row++, "Địa chỉ:", cbAddress);

        left.add(form, BorderLayout.NORTH);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnAdd = new JButton("Thêm");
        JButton btnUpdate = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnReset = new JButton("Reset");
        btns.add(btnAdd);
        btns.add(btnUpdate);
        btns.add(btnDelete);
        btns.add(btnReset);
        left.add(btns, BorderLayout.SOUTH);

        add(left, BorderLayout.WEST);

        // Right: table
        String[] cols = {"STT", "MSSV", "Họ tên", "Ngày sinh", "Lớp", "GPA", "Trạng thái", "Email", "SĐT", "Thành phố", "Quận/Huyện", "Phường/Xã"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                String status = (String) getValueAt(row, 6);
                if ("Bảo lưu".equalsIgnoreCase(status)) {
                    c.setBackground(new Color(255, 255, 200));
                } else if ("Nghỉ học".equalsIgnoreCase(status)) {
                    c.setBackground(new Color(255, 200, 200));
                } else {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        };
        table.setRowHeight(26);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottom: summary
        lblSummary = new JLabel("Tổng: 0 | Đang học:0 | Bảo lưu:0 | Nghỉ học:0");
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(lblSummary);
        add(bottom, BorderLayout.SOUTH);

        // Events
        btnReload.addActionListener(e -> {
            tfSearch.setText("");
            loadAllData();
            clearForm();
        });

        btnSearch.addActionListener(e -> searchAction());

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                if (r >= 0) populateFormFromTable(r);
            }
        });

        btnAdd.addActionListener(e -> {
            try {
                Student s = buildStudentFromForm(false);
                studentDAO.addStudent(s);
                loadAllData();
                clearForm();
                JOptionPane.showMessageDialog(this, "Thêm thành công.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        btnUpdate.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) {
                JOptionPane.showMessageDialog(this, "Chọn 1 sinh viên để sửa.");
                return;
            }
            try {
                Student s = buildStudentFromForm(true);
                studentDAO.updateStudent(s);
                loadAllData();
                JOptionPane.showMessageDialog(this, "Cập nhật thành công.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        btnDelete.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) {
                JOptionPane.showMessageDialog(this, "Chọn 1 sinh viên để xóa.");
                return;
            }
            String mssv = (String) tableModel.getValueAt(r, 1);
            int confirm = JOptionPane.showConfirmDialog(this, "Xóa sinh viên MSSV=" + mssv + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int id = Integer.parseInt(tfId.getText());
                studentDAO.deleteStudent(id);
                loadAllData();
                clearForm();
            }
        });

        btnReset.addActionListener(e -> clearForm());
    }

    private void addFormRow(JPanel panel, GridBagConstraints g, int row, String label, Component comp) {
        g.gridx = 0;
        g.gridy = row;
        g.weightx = 0.0;
        panel.add(new JLabel(label), g);
        g.gridx = 1;
        g.gridy = row;
        g.weightx = 1.0;
        panel.add(comp, g);
    }

    private void loadAllData() {
        tableModel.setRowCount(0);
        addressMap.clear();

        List<Student> list = studentDAO.getAllStudents();

        for (Student s : list) {
            int aid = s.getAddressId();
            if (aid > 0) {
                String detail = s.getAddressDetail();
                if (detail == null) detail = "";
                if (!addressMap.containsKey(aid) && !detail.isEmpty()) {
                    addressMap.put(aid, detail);
                }
            }
        }

        populateClassAndAddressCombos(list);

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        int stt = 1;
        for (Student s : list) {
            String birth = s.getBirthdate() != null ? df.format(s.getBirthdate()) : "";
            tableModel.addRow(new Object[]{
                    stt++, s.getMssv(), s.getName(), birth,
                    s.getClassName(), s.getGpa(), s.getStatus(),
                    s.getEmail(), s.getPhone(),
                    s.getCity(), s.getDistrict(), s.getWard()
            });
        }

        updateSummary();
    }

    private void populateClassAndAddressCombos(List<Student> students) {
        Set<String> classes = new LinkedHashSet<>();
        for (Student s : students) if (s.getClassName() != null) classes.add(s.getClassName());
        cbClass.removeAllItems();
        if (classes.isEmpty()) {
            cbClass.addItem("CNTT1");
            cbClass.addItem("CNTT2");
        } else {
            for (String c : classes) cbClass.addItem(c);
        }

        cbAddress.removeAllItems();
        if (!addressMap.isEmpty()) {
            for (String d : addressMap.values()) cbAddress.addItem(d);
        } else {
            Set<String> addrs = new LinkedHashSet<>();
            for (Student s : students) {
                String det = s.getAddressDetail();
                if (det != null && !det.isEmpty()) addrs.add(det);
            }
            if (addrs.isEmpty()) {
                cbAddress.addItem("Dịch Vọng - Cầu Giấy - Hà Nội");
                cbAddress.addItem("Bến Nghé - Quận 1 - TP.HCM");
            } else {
                for (String d : addrs) cbAddress.addItem(d);
            }
        }
    }

    private Student buildStudentFromForm(boolean forUpdate) throws Exception {
        Student s = new Student();

        if (forUpdate) {
            int sel = table.getSelectedRow();
            if (sel < 0) throw new Exception("Chưa chọn sinh viên.");
            s.setId(Integer.parseInt(tfId.getText()));
            Object mssvObj = tableModel.getValueAt(sel, 1);
            if (mssvObj != null) s.setMssv(mssvObj.toString());
        } else {
            s.setMssv(null);
        }

        String name = tfName.getText().trim();
        if (name.isEmpty()) throw new Exception("Tên không được để trống.");
        s.setName(name);

        String birth = tfBirth.getText().trim();
        if (!birth.isEmpty()) {
            try {
                s.setBirthdate(java.sql.Date.valueOf(birth));
            } catch (Exception ex) {
                throw new Exception("Ngày sinh phải ở dạng yyyy-MM-dd");
            }
        }

        try {
            s.setGpa(tfGpa.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(tfGpa.getText().trim()));
        } catch (NumberFormatException ex) {
            throw new Exception("GPA phải là số.");
        }

        s.setEmail(tfEmail.getText().trim());
        s.setPhone(tfPhone.getText().trim());

        s.setClassId(cbClass.getSelectedIndex() + 1);
        s.setClassName(cbClass.getSelectedItem() != null ? cbClass.getSelectedItem().toString() : null);

        String selectedAddr = cbAddress.getSelectedItem() != null ? cbAddress.getSelectedItem().toString() : null;
        int addrId = 0;
        String city = null, district = null, ward = null;
        if (selectedAddr != null) {
            for (Map.Entry<Integer, String> en : addressMap.entrySet()) {
                if (Objects.equals(en.getValue(), selectedAddr)) {
                    addrId = en.getKey();
                    break;
                }
            }
            String[] parts = selectedAddr.split("\\s*-\\s*");
            if (parts.length >= 3) {
                ward = parts[0].trim();
                district = parts[1].trim();
                city = parts[2].trim();
            } else if (parts.length == 2) {
                ward = parts[0].trim();
                district = parts[1].trim();
            } else if (parts.length == 1) {
                ward = parts[0].trim();
            }
        }
        s.setAddressId(addrId);
        s.setCity(city);
        s.setDistrict(district);
        s.setWard(ward);

        s.setStatus(cbStatus.getSelectedItem() != null ? cbStatus.getSelectedItem().toString() : null);

        return s;
    }

    private void populateFormFromTable(int row) {
        tfId.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        tfMssv.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        tfName.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        tfBirth.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        cbClass.setSelectedItem(tableModel.getValueAt(row, 4));
        tfGpa.setText(String.valueOf(tableModel.getValueAt(row, 5)));
        cbStatus.setSelectedItem(tableModel.getValueAt(row, 6));
        tfEmail.setText(String.valueOf(tableModel.getValueAt(row, 7)));
        tfPhone.setText(String.valueOf(tableModel.getValueAt(row, 8)));

        Object cityObj = tableModel.getValueAt(row, 9);
        Object districtObj = tableModel.getValueAt(row, 10);
        Object wardObj = tableModel.getValueAt(row, 11);
        String detail = "";
        if (wardObj != null) detail += wardObj.toString();
        if (districtObj != null) detail += (detail.isEmpty() ? "" : " - ") + districtObj.toString();
        if (cityObj != null) detail += (detail.isEmpty() ? "" : " - ") + cityObj.toString();
        if (!detail.isEmpty()) cbAddress.setSelectedItem(detail);
    }

    private void clearForm() {
        tfId.setText("");
        tfMssv.setText("Auto");
        tfName.setText("");
        tfBirth.setText("");
        tfGpa.setText("");
        tfEmail.setText("");
        tfPhone.setText("");
        if (cbClass.getItemCount() > 0) cbClass.setSelectedIndex(0);
        if (cbAddress.getItemCount() > 0) cbAddress.setSelectedIndex(0);
        cbStatus.setSelectedIndex(0);
    }

    private void searchAction() {
        String key = tfSearch.getText().trim().toLowerCase();
        if (key.isEmpty()) {
            loadAllData();
            return;
        }
        tableModel.setRowCount(0);
        List<Student> list = studentDAO.getAllStudents();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        int stt = 1;
        for (Student s : list) {
            if ((s.getMssv() != null && s.getMssv().toLowerCase().contains(key))
                    || (s.getName() != null && s.getName().toLowerCase().contains(key))
                    || (s.getClassName() != null && s.getClassName().toLowerCase().contains(key))
                    || (s.getStatus() != null && s.getStatus().toLowerCase().contains(key))) {
                String birth = s.getBirthdate() != null ? df.format(s.getBirthdate()) : "";
                tableModel.addRow(new Object[]{
                        stt++, s.getMssv(), s.getName(), birth,
                        s.getClassName(), s.getGpa(), s.getStatus(),
                        s.getEmail(), s.getPhone(),
                        s.getCity(), s.getDistrict(), s.getWard()
                });
            }
        }
        updateSummary();
    }

    private void updateSummary() {
        int total = tableModel.getRowCount();
        int dang = 0, baoluu = 0, nghi = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object st = tableModel.getValueAt(i, 6);
            if (st == null) continue;
            String s = st.toString();
            if ("Đang học".equalsIgnoreCase(s)) dang++;
            else if ("Bảo lưu".equalsIgnoreCase(s)) baoluu++;
            else if ("Nghỉ học".equalsIgnoreCase(s)) nghi++;
        }
        lblSummary.setText(String.format("Tổng: %d | Đang học: %d | Bảo lưu: %d | Nghỉ học: %d",
                total, dang, baoluu, nghi));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StudentManagementGUI ui = new StudentManagementGUI();
            ui.setVisible(true);
        });
    }
}
