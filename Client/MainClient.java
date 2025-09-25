package Client;

import dao.KhoaDAO;
import dao.LopDAO;
import dao.TinhDAO;
import model.Student;
import rmi.IStudentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.rmi.Naming;
import java.text.SimpleDateFormat;
import java.util.List;

public class MainClient extends JFrame {
    private IStudentService service;
    private JTable tblStudents;
    private DefaultTableModel studentModel;

    // bộ lọc
    private JTextField txtSearchMasv, txtSearchName;
    private JComboBox<String> cbGender, cbTinh, cbKhoa, cbLop;

    private KhoaDAO khoaDAO = new KhoaDAO();
    private LopDAO lopDAO = new LopDAO();
    private TinhDAO tinhDAO = new TinhDAO();

    public MainClient() {
        setTitle("Quản lý sinh viên - RMI");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        try {
            service = (IStudentService) Naming.lookup("rmi://localhost:1099/StudentService");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Không kết nối được RMI Server!");
            e.printStackTrace();
            return;
        }

        initUI();
        loadAllStudents();
    }

    private void initUI() {
        JTabbedPane tabPane = new JTabbedPane();

        // ===== Tab Sinh viên =====
        JPanel panelSV = new JPanel(new BorderLayout());
        JPanel searchPanel = new JPanel();

        JButton btnAdd = new JButton("➕ Thêm sinh viên");
        btnAdd.addActionListener(e -> openStudentForm(null));

        txtSearchMasv = new JTextField(8);
        txtSearchName = new JTextField(12);
        cbGender = new JComboBox<>(new String[]{"", "Nam", "Nữ"});
        cbTinh = new JComboBox<>();
        cbTinh.addItem("");
        for (String t : tinhDAO.getAllTinh()) cbTinh.addItem(t);

        cbKhoa = new JComboBox<>();
        cbLop = new JComboBox<>();
        cbKhoa.addItem("");
        for (String k : khoaDAO.getAllTenKhoa()) cbKhoa.addItem(k);

        cbKhoa.addActionListener(e -> {
            cbLop.removeAllItems();
            cbLop.addItem("");
            String khoa = getComboValue(cbKhoa);
            if (khoa != null) {
                for (String l : lopDAO.getTenLopByKhoa(khoa)) cbLop.addItem(l);
            }
        });

        JButton btnSearch = new JButton("🔍 Tìm");
        btnSearch.addActionListener(e -> searchStudents());

        searchPanel.add(btnAdd);
        searchPanel.add(new JLabel("MSSV:"));
        searchPanel.add(txtSearchMasv);
        searchPanel.add(new JLabel("Tên:"));
        searchPanel.add(txtSearchName);
        searchPanel.add(new JLabel("Giới tính:"));
        searchPanel.add(cbGender);
        searchPanel.add(new JLabel("Tỉnh:"));
        searchPanel.add(cbTinh);
        searchPanel.add(new JLabel("Khoa:"));
        searchPanel.add(cbKhoa);
        searchPanel.add(new JLabel("Lớp:"));
        searchPanel.add(cbLop);
        searchPanel.add(btnSearch);

        String[] colsSV = {"MSSV", "Họ tên", "Tuổi", "Giới tính", "Email", "SĐT", "Tỉnh", "Lớp", "Khoa", "Hành động"};
        studentModel = new DefaultTableModel(colsSV, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 9; // chỉ cột "Hành động" mới bấm được
            }
        };

        tblStudents = new JTable(studentModel);
        tblStudents.getColumn("Hành động").setCellRenderer(new ButtonRenderer());
        tblStudents.getColumn("Hành động").setCellEditor(new ButtonEditor(new JCheckBox()));

        panelSV.add(searchPanel, BorderLayout.NORTH);
        panelSV.add(new JScrollPane(tblStudents), BorderLayout.CENTER);

        // ===== Tab Điểm danh =====
        initAttendanceTab(tabPane);

        tabPane.addTab("Sinh viên", panelSV);
        add(tabPane);
    }

    // ===== TAB ĐIỂM DANH =====
    private void initAttendanceTab(JTabbedPane tabPane) {
        JPanel panelDD = new JPanel(new BorderLayout());
        
        // Panel chọn lớp và ngày
        JPanel controlPanel = new JPanel();
        JComboBox<String> cbLopDD = new JComboBox<>();
        cbLopDD.addItem("-- Chọn lớp --");
        
        // Load tất cả các lớp từ tất cả các khoa
        for (String k : khoaDAO.getAllTenKhoa()) {
            for (String l : lopDAO.getTenLopByKhoa(k)) {
                cbLopDD.addItem(l);
            }
        }
        
        JTextField txtNgay = new JTextField(10);
        txtNgay.setText(new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date()));
        
        JButton btnLoad = new JButton("Tải DS điểm danh");
        JButton btnSave = new JButton("Lưu điểm danh");
        
        controlPanel.add(new JLabel("Lớp:"));
        controlPanel.add(cbLopDD);
        controlPanel.add(new JLabel("Ngày:"));
        controlPanel.add(txtNgay);
        controlPanel.add(btnLoad);
        controlPanel.add(btnSave);
        
        // Bảng điểm danh
        String[] colsDD = {"MSSV", "Họ tên", "Tuổi", "Trạng thái"};
        DefaultTableModel modelDD = new DefaultTableModel(colsDD, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Chỉ cho phép sửa cột trạng thái
            }
        };
        
        JTable tblDD = new JTable(modelDD);
        
        // ComboBox cho trạng thái điểm danh
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Có mặt", "Vắng", "Muộn"});
        tblDD.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(cbStatus));
        
        panelDD.add(controlPanel, BorderLayout.NORTH);
        panelDD.add(new JScrollPane(tblDD), BorderLayout.CENTER);
        
        // Sự kiện
        btnLoad.addActionListener(e -> loadAttendance(cbLopDD, txtNgay, modelDD));
        btnSave.addActionListener(e -> saveAttendance(cbLopDD, txtNgay, modelDD));
        
        tabPane.addTab("Điểm danh", panelDD);
    }

    private void loadAttendance(JComboBox<String> cbLop, JTextField txtNgay, DefaultTableModel model) {
        try {
            String tenLop = (String) cbLop.getSelectedItem();
            if (tenLop == null || "-- Chọn lớp --".equals(tenLop)) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp!");
                return;
            }
            
            java.util.Date date = new SimpleDateFormat("dd/MM/yyyy").parse(txtNgay.getText());
            
            System.out.println("Loading attendance for class: " + tenLop + " on date: " + date);
            
            List<Student> students = service.getStudentsByClass(tenLop);
            List<Object[]> attendance = service.getAttendanceByClass(tenLop, date);
            
            System.out.println("Students count: " + students.size());
            System.out.println("Attendance records: " + attendance.size());
            
            model.setRowCount(0);
            
            // Kết hợp dữ liệu sinh viên và điểm danh
            for (Student student : students) {
                String status = "Chưa điểm danh";
                // Tìm trạng thái điểm danh tương ứng
                for (Object[] att : attendance) {
                    if (att[0] != null && att[0].equals(student.getMasv())) {
                        status = (String) att[2];
                        break;
                    }
                }
                
                model.addRow(new Object[]{
                    student.getMasv(), 
                    student.getHoten(), 
                    student.getTuoi(),
                    status
                });
            }
            
            System.out.println("Attendance table loaded with " + model.getRowCount() + " rows");
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải điểm danh: " + e.getMessage());
        }
    }

    private void saveAttendance(JComboBox<String> cbLop, JTextField txtNgay, DefaultTableModel model) {
        try {
            String tenLop = (String) cbLop.getSelectedItem();
            java.util.Date date = new SimpleDateFormat("dd/MM/yyyy").parse(txtNgay.getText());
            
            if ("-- Chọn lớp --".equals(tenLop)) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp!");
                return;
            }
            
            for (int i = 0; i < model.getRowCount(); i++) {
                String masv = model.getValueAt(i, 0).toString();
                String status = model.getValueAt(i, 3).toString();
                
                if (!"Chưa điểm danh".equals(status)) {
                    service.saveAttendance(masv, status, date);
                }
            }
            
            JOptionPane.showMessageDialog(this, "Lưu điểm danh thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu điểm danh!");
        }
    }

    // ================= HELPER =================
    private String getComboValue(JComboBox<String> combo) {
        Object val = combo.getSelectedItem();
        if (val == null) return null;
        String str = val.toString().trim();
        return str.isEmpty() ? null : str;
    }

    // ================= SINH VIÊN =================
    private void loadAllStudents() {
        try {
            List<Student> list = service.getAllStudents();
            fillStudentTable(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void searchStudents() {
        try {
            String masv = txtSearchMasv.getText().trim();
            String name = txtSearchName.getText().trim();
            String gender = getComboValue(cbGender);
            String tinh = getComboValue(cbTinh);
            String khoa = getComboValue(cbKhoa);
            String lop = getComboValue(cbLop);

            List<Student> list = service.searchStudents(masv, name, gender, tinh, khoa, lop);
            fillStudentTable(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillStudentTable(List<Student> list) {
        studentModel.setRowCount(0);
        if (list == null) return;
        for (Student s : list) {
            studentModel.addRow(new Object[]{
                    s.getMasv(),
                    s.getHoten(),
                    s.getTuoi(),
                    s.getGioitinh(),
                    s.getEmail(),
                    s.getSdt(),
                    s.getTenTinh(),
                    s.getTenLop(),
                    s.getTenKhoa(),
                    "Sửa / Xóa"
            });
        }
    }

    private void openStudentForm(Student student) {
        StudentForm form = new StudentForm(this, service, student);
        form.setVisible(true);
        loadAllStudents();
    }

    // ================= TABLE BUTTONS =================
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Sửa / Xóa");
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean clicked;
        private int row;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = "Sửa / Xóa";
            button.setText(label);
            clicked = true;
            this.row = row;
            return button;
        }

        public Object getCellEditorValue() {
            if (clicked) {
                String masv = studentModel.getValueAt(row, 0).toString();
                String hoten = studentModel.getValueAt(row, 1).toString();
                int choice = JOptionPane.showOptionDialog(null,
                        "Bạn muốn làm gì với sinh viên " + hoten + "?",
                        "Hành động",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new String[]{"Sửa", "Xóa", "Hủy"}, "Sửa");

                if (choice == 0) { // sửa
                    try {
                        Student s = service.getStudentByMasv(masv);
                        openStudentForm(s);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else if (choice == 1) { // xóa
                    try {
                        service.deleteStudent(masv);
                        loadAllStudents();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            clicked = false;
            return new String(label);
        }

        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainClient().setVisible(true));
    }
}