package Client;

import dao.KhoaDAO;
import dao.LopDAO;
import dao.TinhDAO;
import dao.MonHocDAO;
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
    private MonHocDAO monHocDAO = new MonHocDAO();

    public MainClient() {
        // Đổi font mặc định cho toàn bộ giao diện
        setUIFont(new javax.swing.plaf.FontUIResource("Segoe UI", Font.PLAIN, 15));
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

    public MainClient(String masv, String role) {
        // Thêm nút Đăng xuất cho cả hai role
        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setBackground(new Color(244, 67, 54));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLogout.setFocusPainted(false);
        btnLogout.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btnLogout.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> {
                AuthFrame auth = new AuthFrame();
                auth.setVisible(true);
            });
        });
        // Nếu là SV, thêm nút vào tabPane
        if ("SV".equalsIgnoreCase(role)) {
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            topPanel.setBackground(new Color(245, 250, 255));
            topPanel.add(btnLogout);
            getContentPane().add(topPanel, BorderLayout.NORTH);
        }
        setUIFont(new javax.swing.plaf.FontUIResource("Segoe UI", Font.PLAIN, 15));
        setTitle("Quản lý sinh viên - RMI");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        try {
            service = (IStudentService) java.rmi.Naming.lookup("rmi://localhost:1099/StudentService");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Không kết nối được RMI Server!");
            e.printStackTrace();
            return;
        }
        if ("SV".equalsIgnoreCase(role)) {
            JTabbedPane tabPane = new JTabbedPane();
            tabPane.setFont(new Font("Segoe UI", Font.BOLD, 15));
            // Tab 1: Thông tin cá nhân
            JPanel panelInfo = new JPanel(new BorderLayout());
            panelInfo.setBackground(new Color(245, 250, 255));
            final model.Student[] sv = new model.Student[1];
            try {
                sv[0] = service.getStudentByMasv(masv);
                JPanel infoPanel = new JPanel(new GridLayout(0,2,10,10));
                infoPanel.setBorder(BorderFactory.createEmptyBorder(30,60,30,60));
                JTextField txtHoten = new JTextField(sv[0].getHoten());
                JTextField txtEmail = new JTextField(sv[0].getEmail());
                JTextField txtSdt = new JTextField(sv[0].getSdt());
                JTextField txtTuoi = new JTextField(String.valueOf(sv[0].getTuoi()));
                JTextField txtTinh = new JTextField(sv[0].getTenTinh());
                JTextField txtLop = new JTextField(sv[0].getTenLop());
                JTextField txtKhoa = new JTextField(sv[0].getTenKhoa());
                infoPanel.add(new JLabel("Họ tên:")); infoPanel.add(txtHoten);
                infoPanel.add(new JLabel("Email:")); infoPanel.add(txtEmail);
                infoPanel.add(new JLabel("SĐT:")); infoPanel.add(txtSdt);
                infoPanel.add(new JLabel("Tuổi:")); infoPanel.add(txtTuoi);
                infoPanel.add(new JLabel("Tỉnh:")); infoPanel.add(txtTinh);
                infoPanel.add(new JLabel("Lớp:")); infoPanel.add(txtLop);
                infoPanel.add(new JLabel("Khoa:")); infoPanel.add(txtKhoa);
                JButton btnSave = new JButton("Lưu thông tin cá nhân");
                btnSave.setBackground(new Color(76,175,80));
                btnSave.setForeground(Color.WHITE);
                btnSave.setFont(new Font("Segoe UI", Font.BOLD, 15));
                btnSave.addActionListener(e -> {
                    try {
                        sv[0].setHoten(txtHoten.getText().trim());
                        sv[0].setEmail(txtEmail.getText().trim());
                        sv[0].setSdt(txtSdt.getText().trim());
                        sv[0].setTuoi(Integer.parseInt(txtTuoi.getText().trim()));
                        service.updateStudent(sv[0]);
                        JOptionPane.showMessageDialog(panelInfo, "Cập nhật thông tin thành công!");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(panelInfo, "Lỗi cập nhật: " + ex.getMessage());
                    }
                });
                JPanel wrapper = new JPanel(new BorderLayout());
                wrapper.add(infoPanel, BorderLayout.CENTER);
                wrapper.add(btnSave, BorderLayout.SOUTH);
                panelInfo.add(wrapper, BorderLayout.CENTER);
            } catch (Exception ex) {
                panelInfo.add(new JLabel("Không lấy được thông tin cá nhân!"), BorderLayout.CENTER);
            }
            tabPane.addTab("Thông tin cá nhân", panelInfo);
            // Tab 2: Thông tin chuyên cần (lọc theo khoa của sinh viên)
            JPanel panelAttendance = new JPanel(new BorderLayout());
            panelAttendance.setBackground(new Color(240, 248, 255));
            JLabel lblAttendanceTitle = new JLabel("THÔNG TIN CHUYÊN CẦN", JLabel.CENTER);
            lblAttendanceTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
            lblAttendanceTitle.setForeground(new Color(33, 150, 243));
            panelAttendance.add(lblAttendanceTitle, BorderLayout.NORTH);
            try {
                if (sv[0] != null) {
                    String masv_sv = sv[0].getMasv();
                    String hoten = sv[0].getHoten();
                    String maKhoa = sv[0].getMakhoa();
                    System.out.println("[DEBUG] Mã khoa lấy từ sinh viên: '" + maKhoa + "'");
                    if (maKhoa == null || maKhoa.trim().isEmpty()) {
                        panelAttendance.add(new JLabel("Không có thông tin mã khoa của sinh viên!", JLabel.CENTER), BorderLayout.CENTER);
                    } else {
                        java.util.List<Object[]> attendance = null;
                        try {
                            attendance = service.getAttendanceByStudent(masv_sv);
                        } catch (Exception e) {
                            attendance = new java.util.ArrayList<>();
                        }
                        // Lấy danh sách môn học thuộc khoa của sinh viên
                        java.util.List<String[]> monList = monHocDAO.getMonHocByMaKhoa(maKhoa);
                        java.util.Set<String> tenMonHocKhoa = new java.util.HashSet<>();
                        for (String[] mh : monList) {
                            if (mh.length > 1) tenMonHocKhoa.add(mh[1]); // mh[1] là tên môn học
                        }
                        String[] cols = {"MSSV", "Họ tên", "Tên môn học", "Số tiết nghỉ"};
                        DefaultTableModel model = new DefaultTableModel(cols, 0);
                        if (attendance != null) {
                            for (Object[] row : attendance) {
                                String tenmh = row[0] != null ? row[0].toString() : "";
                                int soTietNghi = 0;
                                if (row[1] != null) {
                                    try {
                                        soTietNghi = Integer.parseInt(row[1].toString());
                                    } catch (Exception ex) {
                                        soTietNghi = 0;
                                    }
                                }
                                // Chỉ hiển thị các môn học thuộc khoa của sinh viên
                                if (tenMonHocKhoa.contains(tenmh)) {
                                    model.addRow(new Object[]{masv_sv, hoten, tenmh, soTietNghi});
                                }
                            }
                        }
                        JTable table = new JTable(model);
                        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
                        table.setRowHeight(28);
                        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                        JScrollPane scroll = new JScrollPane(table);
                        scroll.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
                        panelAttendance.add(scroll, BorderLayout.CENTER);
                    }
                } else {
                    panelAttendance.add(new JLabel("Không lấy được dữ liệu điểm danh!", JLabel.CENTER), BorderLayout.CENTER);
                }
            } catch (Exception ex) {
                JLabel lblErr = new JLabel("Không lấy được dữ liệu điểm danh!", JLabel.CENTER);
                lblErr.setForeground(Color.RED);
                panelAttendance.add(lblErr, BorderLayout.CENTER);
            }
            tabPane.addTab("Thông tin chuyên cần", panelAttendance);
            add(tabPane);
        } else {
            // Nếu là GV hoặc role khác, hiển thị giao diện mặc định (các tab quản lý điểm, điểm danh, sinh viên)
            initUI();
        }
    }
    private void initUI() {
        // Thêm nút Đăng xuất cho giao diện mặc định (GV)
        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setBackground(new Color(244, 67, 54));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLogout.setFocusPainted(false);
        btnLogout.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btnLogout.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> {
                AuthFrame auth = new AuthFrame();
                auth.setVisible(true);
            });
        });
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setBackground(new Color(245, 250, 255));
        topPanel.add(btnLogout);
        getContentPane().add(topPanel, BorderLayout.NORTH);
    JTabbedPane tabPane = new JTabbedPane();
    tabPane.setFont(new Font("Segoe UI", Font.BOLD, 15));

    // ===== Tab Sinh viên =====
    JPanel panelSV = new JPanel(new BorderLayout());
    panelSV.setBackground(new Color(245, 250, 255));
    JPanel searchPanel = new JPanel();
    // ...existing code...

    // ===== Tab Quản lý điểm mới =====
    JPanel panelDiem = new JPanel(new BorderLayout());
    panelDiem.setBackground(new Color(245, 250, 255));

    JPanel diemControlPanel = new JPanel();
    diemControlPanel.setBackground(new Color(230, 240, 250));
    diemControlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JComboBox<String> cbKhoaDiem = new JComboBox<>();
    cbKhoaDiem.addItem("-- Chọn khoa --");
    for (String k : khoaDAO.getAllTenKhoa()) cbKhoaDiem.addItem(k);

    JComboBox<String> cbMonHocDiem = new JComboBox<>();
    cbMonHocDiem.addItem("-- Chọn môn học --");

    JComboBox<String> cbLopDiem = new JComboBox<>();
    cbLopDiem.addItem("-- Chọn lớp --");

    diemControlPanel.add(new JLabel("Khoa:"));
    diemControlPanel.add(cbKhoaDiem);
    diemControlPanel.add(new JLabel("Môn học:"));
    diemControlPanel.add(cbMonHocDiem);
    diemControlPanel.add(new JLabel("Lớp:"));
    diemControlPanel.add(cbLopDiem);

    JButton btnLoadDiem = new JButton("Tải danh sách");
    btnLoadDiem.setBackground(new Color(33, 150, 243));
    btnLoadDiem.setForeground(Color.WHITE);
    btnLoadDiem.setFont(new Font("Segoe UI", Font.BOLD, 14));
    btnLoadDiem.setFocusPainted(false);
    btnLoadDiem.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    diemControlPanel.add(btnLoadDiem);

    JButton btnSaveDiem = new JButton("Lưu điểm");
    btnSaveDiem.setBackground(new Color(76, 175, 80));
    btnSaveDiem.setForeground(Color.WHITE);
    btnSaveDiem.setFont(new Font("Segoe UI", Font.BOLD, 14));
    btnSaveDiem.setFocusPainted(false);
    btnSaveDiem.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    diemControlPanel.add(btnSaveDiem);

    JButton btnExportScore = new JButton("📤 Xuất Excel");
    btnExportScore.setBackground(new Color(255, 193, 7));
    btnExportScore.setForeground(Color.BLACK);
    btnExportScore.setFont(new Font("Segoe UI", Font.BOLD, 14));
    btnExportScore.setFocusPainted(false);
    btnExportScore.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    diemControlPanel.add(btnExportScore);

    // Sửa model bảng điểm: bỏ cột Mã GV, thêm cột Điểm CC
    String[] colsDiem = {"MSSV", "Họ tên", "Điểm CC", "Điểm QT", "Điểm CK", "Điểm TK"};
    DefaultTableModel diemModel = new DefaultTableModel(colsDiem, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            // Cho phép nhập điểm ở các cột CC, QT, CK
            return column == 2 || column == 3 || column == 4;
        }
    };

    JTable tblDiem = new JTable(diemModel);
    tblDiem.setRowHeight(32);
    tblDiem.setFont(new Font("Segoe UI", Font.PLAIN, 15));
    tblDiem.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
    tblDiem.setSelectionBackground(new Color(200, 230, 255));
    tblDiem.setGridColor(new Color(220, 220, 220));

    panelDiem.add(diemControlPanel, BorderLayout.NORTH);
    panelDiem.add(new JScrollPane(tblDiem), BorderLayout.CENTER);

    // Sự kiện chọn khoa cập nhật lớp và môn học
    cbKhoaDiem.addActionListener(e -> {
        cbLopDiem.removeAllItems();
        cbLopDiem.addItem("-- Chọn lớp --");
        cbMonHocDiem.removeAllItems();
        cbMonHocDiem.addItem("-- Chọn môn học --");
        String tenKhoa = (String) cbKhoaDiem.getSelectedItem();
        if (tenKhoa != null && !tenKhoa.startsWith("--")) {
            for (String l : lopDAO.getTenLopByKhoa(tenKhoa)) cbLopDiem.addItem(l);
            String maKhoa = khoaDAO.getMaKhoaByTen(tenKhoa);
            for (String[] mh : monHocDAO.getMonHocByMaKhoa(maKhoa)) cbMonHocDiem.addItem(mh[0] + " - " + mh[1]);
        }
    });

    // Sự kiện tải danh sách sinh viên
    btnLoadDiem.addActionListener(e -> {
        diemModel.setRowCount(0);
        String tenLop = (String) cbLopDiem.getSelectedItem();
        if (tenLop == null || tenLop.startsWith("--")) {
            JOptionPane.showMessageDialog(panelDiem, "Vui lòng chọn lớp!");
            return;
        }
        List<Student> students = null;
        try {
            students = service.getStudentsByClass(tenLop);
        } catch (java.rmi.RemoteException ex) {
            JOptionPane.showMessageDialog(panelDiem, "Lỗi khi tải danh sách sinh viên: " + ex.getMessage());
            ex.printStackTrace();
            return;
        }
        for (Student sv : students) {
            diemModel.addRow(new Object[]{
                sv.getMasv(),
                sv.getHoten(),
                "", "", "", "" // Các ô điểm để nhập
            });
        }
    });

    // Tự động tính điểm tổng kết khi nhập điểm CC, QT, CK
    // Sử dụng TableModelListener để lắng nghe thay đổi

    javax.swing.event.TableModelListener diemListener = e -> {
        int row = e.getFirstRow();
        int col = e.getColumn();
        if (col == 2 || col == 3 || col == 4) { // CC, QT, CK
            try {
                double diemCC = Double.parseDouble(diemModel.getValueAt(row, 2).toString());
                double diemQT = Double.parseDouble(diemModel.getValueAt(row, 3).toString());
                double diemCK = Double.parseDouble(diemModel.getValueAt(row, 4).toString());
                double diemTK = 0.1 * diemCC + 0.2 * diemQT + 0.7 * diemCK;
                diemModel.setValueAt(String.format("%.2f", diemTK), row, 5);
            } catch (Exception ex) {
                diemModel.setValueAt("", row, 5);
            }
        }
    };
    diemModel.addTableModelListener(diemListener);

    // Sự kiện lưu điểm
    btnSaveDiem.addActionListener(e -> {
        String tenLop = (String) cbLopDiem.getSelectedItem();
        String monHoc = (String) cbMonHocDiem.getSelectedItem();
        if (tenLop == null || tenLop.startsWith("--") || monHoc == null || monHoc.startsWith("--")) {
            JOptionPane.showMessageDialog(panelDiem, "Vui lòng chọn đầy đủ lớp và môn học!");
            return;
        }
        String maMon = monHoc.split(" - ")[0];
        System.out.println("[LOG] Bắt đầu lưu điểm cho lớp: " + tenLop + ", môn: " + maMon);
        for (int i = 0; i < diemModel.getRowCount(); i++) {
            String masv = diemModel.getValueAt(i, 0).toString();
            double diemCC = diemModel.getValueAt(i, 2).toString().isEmpty() ? 0 : Double.parseDouble(diemModel.getValueAt(i, 2).toString());
            double diemQT = diemModel.getValueAt(i, 3).toString().isEmpty() ? 0 : Double.parseDouble(diemModel.getValueAt(i, 3).toString());
            double diemCK = diemModel.getValueAt(i, 4).toString().isEmpty() ? 0 : Double.parseDouble(diemModel.getValueAt(i, 4).toString());
            double diemTK = diemModel.getValueAt(i, 5).toString().isEmpty() ? 0 : Double.parseDouble(diemModel.getValueAt(i, 5).toString());
            System.out.printf("[LOG] -> SV: %s | CC: %.2f | QT: %.2f | CK: %.2f | TK: %.2f\n", masv, diemCC, diemQT, diemCK, diemTK);
            model.BangDiem bd = new model.BangDiem(masv, maMon, diemCC, diemQT, diemCK, diemTK, null);
            try {
                service.addOrUpdateScore(bd);
                System.out.println("[LOG] Đã gọi service.addOrUpdateScore cho SV: " + masv);
            } catch (Exception ex) {
                System.out.println("[ERROR] Lỗi khi lưu điểm cho SV: " + masv + ": " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        System.out.println("[LOG] Đã hoàn thành lưu điểm cho lớp " + tenLop);
        JOptionPane.showMessageDialog(panelDiem, "Đã lưu điểm cho lớp " + tenLop);
    });

    // Sự kiện xuất Excel
    btnExportScore.addActionListener(e -> {
        try {
            java.util.List<Object[]> data = new java.util.ArrayList<>();
            for (int i = 0; i < diemModel.getRowCount(); i++) {
                Object[] row = new Object[6];
                for (int j = 0; j < 6; j++) row[j] = diemModel.getValueAt(i, j);
                data.add(row);
            }
            javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
            fileChooser.setDialogTitle("Chọn nơi lưu file Excel");
            fileChooser.setSelectedFile(new java.io.File("bangdiem.xlsx"));
            int userSelection = fileChooser.showSaveDialog(panelDiem);
            if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                util.ExcelExporter.exportScoreToExcel(data, filePath);
                javax.swing.JOptionPane.showMessageDialog(panelDiem, "Xuất file Excel bảng điểm thành công!\n" + filePath);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(panelDiem, "Lỗi khi xuất file Excel: " + ex.getMessage());
        }
    });

    tabPane.addTab("Quản lý điểm", panelDiem);
    searchPanel.setBackground(new Color(230, 240, 250));
    searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JButton btnAdd = new JButton("Thêm sinh viên");
    btnAdd.setBackground(new Color(76, 175, 80));
    btnAdd.setForeground(Color.WHITE);
    btnAdd.setFocusPainted(false);
    btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
    btnAdd.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
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

    JButton btnSearch = new JButton("Tìm");
    btnSearch.setBackground(new Color(33, 150, 243));
    btnSearch.setForeground(Color.WHITE);
    btnSearch.setFocusPainted(false);
    btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
    btnSearch.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
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
    tblStudents.setRowHeight(32);
    tblStudents.setFont(new Font("Segoe UI", Font.PLAIN, 15));
    tblStudents.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
    tblStudents.setSelectionBackground(new Color(200, 230, 255));
    tblStudents.setGridColor(new Color(220, 220, 220));
    tblStudents.getColumn("Hành động").setCellRenderer(new ButtonRenderer());
    tblStudents.getColumn("Hành động").setCellEditor(new ButtonEditor(new JCheckBox()));

        panelSV.add(searchPanel, BorderLayout.NORTH);
        panelSV.add(new JScrollPane(tblStudents), BorderLayout.CENTER);

        //  Tab Điểm danh 
        initAttendanceTab(tabPane);

        tabPane.addTab("Sinh viên", panelSV);
        add(tabPane);
    }

    private void initAttendanceTab(JTabbedPane tabPane) {
        JPanel panelDD = new JPanel(new BorderLayout());
        panelDD.setBackground(new Color(245, 250, 255));

        // Panel chọn lớp, môn học và ngày
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(230, 240, 250));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        JComboBox<String> cbKhoaDD = new JComboBox<>();
        cbKhoaDD.addItem("-- Chọn khoa --");
        for (String k : khoaDAO.getAllTenKhoa()) {
            cbKhoaDD.addItem(k);
        }

        JComboBox<String> cbLopDD = new JComboBox<>();
        cbLopDD.addItem("-- Chọn lớp --");

        JComboBox<String> cbMonHoc = new JComboBox<>();
        cbMonHoc.addItem("-- Chọn môn học --");

        // Khi chọn khoa, cập nhật lại danh sách lớp và môn học
        cbKhoaDD.addActionListener(e -> {
            cbLopDD.removeAllItems();
            cbLopDD.addItem("-- Chọn lớp --");
            cbMonHoc.removeAllItems();
            cbMonHoc.addItem("-- Chọn môn học --");
            String tenKhoa = (String) cbKhoaDD.getSelectedItem();
            if (tenKhoa != null && !"-- Chọn khoa --".equals(tenKhoa)) {
                String tenKhoaTrim = tenKhoa.trim();
                for (String l : lopDAO.getTenLopByKhoa(tenKhoaTrim)) cbLopDD.addItem(l);
                String maKhoa = khoaDAO.getMaKhoaByTen(tenKhoaTrim);
                System.out.println("[DEBUG] Mã khoa lấy được: '" + maKhoa + "'");
                java.util.List<String[]> monList = monHocDAO.getMonHocByMaKhoa(maKhoa);
                System.out.println("[DEBUG] Số lượng môn học lấy được: " + monList.size());
                for (String[] mh : monList) {
                    System.out.println("[DEBUG] Môn lấy được: " + mh[0] + " - " + mh[1] + " - " + maKhoa);
                    cbMonHoc.addItem(mh[0] + " - " + mh[1]);
                }
                // In ra toàn bộ danh sách môn học không lọc
                java.util.List<String[]> allMon = monHocDAO.getAllMonHoc();
                System.out.println("[DEBUG] Toàn bộ môn học trong DB:");
                for (String[] mh : allMon) {
                    System.out.println("[DEBUG] ALL: " + mh[0] + " - " + mh[1]);
                }
            }
            cbMonHoc.setEnabled(true);
        });

        JTextField txtNgay = new JTextField(10);
        txtNgay.setText(new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date()));

    JButton btnLoad = new JButton("Tải DS điểm danh");
    btnLoad.setBackground(new Color(33, 150, 243));
    btnLoad.setForeground(Color.WHITE);
    btnLoad.setFocusPainted(false);
    btnLoad.setFont(new Font("Segoe UI", Font.BOLD, 14));
    btnLoad.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    JButton btnSave = new JButton("Lưu điểm danh");
    btnSave.setBackground(new Color(76, 175, 80));
    btnSave.setForeground(Color.WHITE);
    btnSave.setFocusPainted(false);
    btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
    btnSave.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    JButton btnExport = new JButton("Xuất Excel");
    btnExport.setBackground(new Color(255, 193, 7));
    btnExport.setForeground(Color.BLACK);
    btnExport.setFocusPainted(false);
    btnExport.setFont(new Font("Segoe UI", Font.BOLD, 14));
    btnExport.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

    controlPanel.add(new JLabel("Khoa:"));
    controlPanel.add(cbKhoaDD);
    controlPanel.add(new JLabel("Lớp:"));
    controlPanel.add(cbLopDD);
    controlPanel.add(new JLabel("Môn học:"));
    controlPanel.add(cbMonHoc);
    controlPanel.add(new JLabel("Ngày:"));
    controlPanel.add(txtNgay);
    controlPanel.add(btnLoad);
    controlPanel.add(btnSave);
    controlPanel.add(btnExport);

        // Bảng điểm danh
    String[] colsDD = {"MSSV", "Họ tên", "Tuổi", "Số tiết vắng"};
        DefaultTableModel modelDD = new DefaultTableModel(colsDD, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Chỉ cho phép sửa cột trạng thái
            }
        };

        JTable tblDD = new JTable(modelDD);

        // ComboBox cho trạng thái điểm danh
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"0", "1", "2", "3", "4", "5"});
        tblDD.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(cbStatus));

    panelDD.setBackground(new Color(245, 250, 255));
    controlPanel.setBackground(new Color(230, 240, 250));
    controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    JScrollPane scrollDD = new JScrollPane(tblDD);
    scrollDD.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
    tblDD.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
    tblDD.setRowHeight(28);
    tblDD.setFont(new Font("Segoe UI", Font.PLAIN, 15));
    panelDD.add(controlPanel, BorderLayout.NORTH);
    panelDD.add(scrollDD, BorderLayout.CENTER);

        // Sự kiện
        btnLoad.addActionListener(e -> loadAttendance(cbLopDD, cbMonHoc, txtNgay, modelDD));
        btnSave.addActionListener(e -> saveAttendance(cbLopDD, cbMonHoc, txtNgay, modelDD));
        btnExport.addActionListener(e -> {
            try {
                // Lấy dữ liệu hiện tại từ bảng modelDD
                java.util.List<Object[]> data = new java.util.ArrayList<>();
                for (int i = 0; i < modelDD.getRowCount(); i++) {
                    Object[] row = new Object[3];
                    row[0] = modelDD.getValueAt(i, 0); // MSSV
                    row[1] = modelDD.getValueAt(i, 1); // Họ tên
                    row[2] = modelDD.getValueAt(i, 3); // Trạng thái
                    data.add(row);
                }
                System.out.println("Số dòng xuất Excel: " + data.size());
                javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
                fileChooser.setDialogTitle("Chọn nơi lưu file Excel");
                fileChooser.setSelectedFile(new java.io.File("diemdanh.xlsx"));
                int userSelection = fileChooser.showSaveDialog(panelDD);
                if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) {
                    String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                    System.out.println("Đường dẫn file xuất: " + filePath);
                    util.ExcelExporter.exportAttendanceToExcel(data, filePath);
                    javax.swing.JOptionPane.showMessageDialog(panelDD, "Xuất file Excel thành công!\n" + filePath);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                javax.swing.JOptionPane.showMessageDialog(panelDD, "Lỗi khi xuất file Excel: " + ex.getMessage());
            }
        });

        tabPane.addTab("Điểm danh", panelDD);
    }

    private void loadAttendance(JComboBox<String> cbLop, JComboBox<String> cbMonHoc, JTextField txtNgay, DefaultTableModel model) {
        try {
            String tenLop = (String) cbLop.getSelectedItem();
            String monHocStr = (String) cbMonHoc.getSelectedItem();
            if (tenLop == null || "-- Chọn lớp --".equals(tenLop)) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp!");
                return;
            }
            if (monHocStr == null || "-- Chọn môn học --".equals(monHocStr)) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn môn học!");
                return;
            }
            String maMH = monHocStr.split(" - ")[0];
            java.util.Date date = new SimpleDateFormat("dd/MM/yyyy").parse(txtNgay.getText());

            System.out.println("Loading attendance for class: " + tenLop + ", subject: " + maMH + " on date: " + date);

            List<Student> students = service.getStudentsByClass(tenLop);
            List<Object[]> attendance = service.getAttendanceByClassAndSubject(tenLop, maMH, date);

            System.out.println("Students count: " + students.size());
            System.out.println("Attendance records: " + attendance.size());

            model.setRowCount(0);

            // Kết hợp dữ liệu sinh viên và điểm danh
            for (Student student : students) {
                String status = "0";
                // Tìm trạng thái điểm danh tương ứng
                for (Object[] att : attendance) {
                    if (att[0] != null && att[0].equals(student.getMasv())) {
                        Object stt = att[2];
                        if (stt != null && stt.toString().matches("[0-5]")) {
                            status = stt.toString();
                        }
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

    private void saveAttendance(JComboBox<String> cbLop, JComboBox<String> cbMonHoc, JTextField txtNgay, DefaultTableModel model) {
        try {
            String tenLop = (String) cbLop.getSelectedItem();
            String monHocStr = (String) cbMonHoc.getSelectedItem();
            java.util.Date date = new SimpleDateFormat("dd/MM/yyyy").parse(txtNgay.getText());

            if ("-- Chọn lớp --".equals(tenLop)) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp!");
                return;
            }
            if (monHocStr == null || "-- Chọn môn học --".equals(monHocStr)) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn môn học!");
                return;
            }
            String maMH = monHocStr.split(" - ")[0];

            for (int i = 0; i < model.getRowCount(); i++) {
                String masv = model.getValueAt(i, 0).toString();
                String status = model.getValueAt(i, 3) == null ? "" : model.getValueAt(i, 3).toString().trim();
                System.out.println("[DEBUG] saveAttendance row " + i + ": masv=" + masv + ", status='" + status + "', mamh=" + maMH);
                if (status.matches("[1-5]")) {
                    service.saveAttendance(masv, maMH, status, date);
                } else {
                    // Bỏ qua các dòng chưa điểm danh hoặc giá trị không hợp lệ
                }
            }

            JOptionPane.showMessageDialog(this, "Lưu điểm danh thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu điểm danh!");
        }
    }

    private String getComboValue(JComboBox<String> combo) {
        Object val = combo.getSelectedItem();
        if (val == null) return null;
        String str = val.toString().trim();
        return str.isEmpty() ? null : str;
    }

    //  SINH VIÊN 
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

    // TABLE BUTTONS 
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBackground(new Color(255, 193, 7));
            setForeground(Color.BLACK);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
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
            button.setBackground(new Color(255, 193, 7));
            button.setForeground(Color.BLACK);
            button.setFont(new Font("Segoe UI", Font.BOLD, 13));
            button.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
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
        SwingUtilities.invokeLater(() -> {
            AuthFrame auth = new AuthFrame();
            auth.setVisible(true);
            // MainClient sẽ được mở sau khi đăng nhập thành công trong AuthFrame
        });
    }

    // Đổi font mặc định cho toàn bộ giao diện
    public static void setUIFont(javax.swing.plaf.FontUIResource f) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put(key, f);
        }
    }

    // Add this method to fix the compile error
    @SuppressWarnings("unused")
    private void openScoreForm(Student student, DefaultTableModel diemModel) {
        // TODO: Implement the logic to open the score form.
        // For now, show a message dialog as a placeholder.
        JOptionPane.showMessageDialog(this, "Chức năng thêm/sửa điểm chưa được triển khai.");
    }

    // Implement missing method to fix compile error
    @SuppressWarnings("unused")
    private void loadBangDiem(DefaultTableModel diemModel) {
        try {
            diemModel.setRowCount(0);
            java.util.List<model.BangDiem> scores = service.getAllScores();
            for (model.BangDiem bd : scores) {
                diemModel.addRow(new Object[]{
                    bd.getMasv(),
                    bd.getMamh(),
                    bd.getDiemCC(),
                    bd.getDiemQT(),
                    bd.getDiemCK(),
                    bd.getDiemTK(),
                    bd.getMagv(),
                    "Sửa/Xóa"
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}