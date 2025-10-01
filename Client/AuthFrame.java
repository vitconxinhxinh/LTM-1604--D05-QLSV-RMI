package Client;

import javax.swing.*;
import java.awt.*;

public class AuthFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;

    public AuthFrame() {
        setTitle("Đăng nhập/Đăng ký hệ thống");
        setSize(600, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        loginPanel = new LoginPanel(this);
        registerPanel = new RegisterPanel(this);
        mainPanel.add(loginPanel, "login");
        mainPanel.add(registerPanel, "register");
        add(mainPanel);
        showLogin();
    }
    public void showLogin() {
        cardLayout.show(mainPanel, "login");
    }
    public void showRegister() {
        cardLayout.show(mainPanel, "register");
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AuthFrame().setVisible(true));
    }
}

// LoginPanel
class LoginPanel extends JPanel {
    private AuthFrame parent;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cbRole;
    public LoginPanel(AuthFrame parent) {
        this.parent = parent;
        setLayout(new GridBagLayout());
        setBackground(new Color(245, 250, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        JLabel lblTitle = new JLabel("ĐĂNG NHẬP HỆ THỐNG", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(33, 150, 243));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; add(lblTitle, gbc);
        gbc.gridwidth = 1;
        JLabel lblUsername = new JLabel("Tài khoản:");
        txtUsername = new JTextField();
        JLabel lblPassword = new JLabel("Mật khẩu:");
        txtPassword = new JPasswordField();
        JLabel lblRole = new JLabel("Vai trò:");
        cbRole = new JComboBox<>(new String[]{"SV", "GV", "ADMIN"});
        gbc.gridx = 0; gbc.gridy = 1; add(lblUsername, gbc);
        gbc.gridx = 1; add(txtUsername, gbc);
        gbc.gridx = 0; gbc.gridy = 2; add(lblPassword, gbc);
        gbc.gridx = 1; add(txtPassword, gbc);
        gbc.gridx = 0; gbc.gridy = 3; add(lblRole, gbc);
        gbc.gridx = 1; add(cbRole, gbc);
        JButton btnLogin = new JButton("Đăng nhập");
        btnLogin.setBackground(new Color(76, 175, 80));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JButton btnRegister = new JButton("Đăng ký");
        btnRegister.setBackground(new Color(33, 150, 243));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JButton btnForgot = new JButton("Quên mật khẩu");
        btnForgot.setBackground(new Color(255, 193, 7));
        btnForgot.setForeground(Color.BLACK);
        btnForgot.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(245, 250, 255));
        btnPanel.add(btnLogin);
        btnPanel.add(btnRegister);
        btnPanel.add(btnForgot);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; add(btnPanel, gbc);
        btnLogin.addActionListener(e -> login());
        btnRegister.addActionListener(e -> parent.showRegister());
        btnForgot.addActionListener(e -> JOptionPane.showMessageDialog(this, "Chức năng quên mật khẩu sẽ cập nhật sau!"));
    }
    private void login() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String role = cbRole.getSelectedItem().toString().trim();
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }
        try (java.sql.Connection conn = dao.DBConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM taikhoan WHERE TRIM(UPPER(username)) = TRIM(UPPER(?)) AND TRIM(UPPER(password)) = TRIM(UPPER(?)) AND TRIM(UPPER(role)) = TRIM(UPPER(?))")) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");
                String masv = rs.getString("masv");
                parent.dispose();
                new MainClient(masv, role).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Sai tài khoản, mật khẩu hoặc vai trò!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối CSDL!");
        }
    }
}

// RegisterPanel
class RegisterPanel extends JPanel {
    private AuthFrame parent;
    private JTextField txtMaSV, txtUsername;
    private JPasswordField txtPassword, txtConfirmPassword;
    public RegisterPanel(AuthFrame parent) {
        this.parent = parent;
        setLayout(new GridBagLayout());
        setBackground(new Color(245, 250, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        JLabel lblTitle = new JLabel("ĐĂNG KÝ TÀI KHOẢN MỚI", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(76, 175, 80));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; add(lblTitle, gbc);
        gbc.gridwidth = 1;
    JLabel lblMaSV = new JLabel("Mã SV:");
    txtMaSV = new JTextField();
    JLabel lblUsername = new JLabel("Tài khoản:");
    txtUsername = new JTextField();
    JLabel lblPassword = new JLabel("Mật khẩu:");
    txtPassword = new JPasswordField();
    JLabel lblConfirmPassword = new JLabel("Xác nhận mật khẩu:");
    txtConfirmPassword = new JPasswordField();
    gbc.gridx = 0; gbc.gridy = 1; add(lblMaSV, gbc);
    gbc.gridx = 1; add(txtMaSV, gbc);
    gbc.gridx = 0; gbc.gridy = 2; add(lblUsername, gbc);
    gbc.gridx = 1; add(txtUsername, gbc);
    gbc.gridx = 0; gbc.gridy = 3; add(lblPassword, gbc);
    gbc.gridx = 1; add(txtPassword, gbc);
    gbc.gridx = 0; gbc.gridy = 4; add(lblConfirmPassword, gbc);
    gbc.gridx = 1; add(txtConfirmPassword, gbc);
        JButton btnRegister = new JButton("Đăng ký");
        btnRegister.setBackground(new Color(33, 150, 243));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JButton btnBack = new JButton("Quay lại");
        btnBack.setBackground(new Color(244, 67, 54));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(245, 250, 255));
        btnPanel.add(btnRegister);
        btnPanel.add(btnBack);
    gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; add(btnPanel, gbc);
        btnRegister.addActionListener(e -> register());
        btnBack.addActionListener(e -> parent.showLogin());
        JLabel lblInfo = new JLabel("Nếu đã có tài khoản, hãy quay về đăng nhập.");
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblInfo.setForeground(new Color(33, 150, 243));
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; add(lblInfo, gbc);
        gbc.gridwidth = 1;
    }
    private void register() {
        String maSV = txtMaSV.getText().trim();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());
        if (maSV.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!");
            return;
        }
        try (java.sql.Connection conn = dao.DBConnection.getConnection()) {
            // Kiểm tra trùng username (phân biệt hoa thường)
            java.sql.PreparedStatement checkPs = conn.prepareStatement("SELECT * FROM taikhoan WHERE username=?");
            checkPs.setString(1, username);
            java.sql.ResultSet rs = checkPs.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Tài khoản đã tồn tại!");
                return;
            }
            // Kiểm tra mã SV tồn tại và chỉ đăng ký một lần
            java.sql.PreparedStatement ps = conn.prepareStatement("SELECT masv FROM sinhvien WHERE masv=?");
            ps.setString(1, maSV);
            java.sql.ResultSet rsSv = ps.executeQuery();
            boolean validCode = rsSv.next();
            rsSv.close();
            ps.close();
            // Kiểm tra mã SV đã có tài khoản chưa
            java.sql.PreparedStatement checkSv = conn.prepareStatement("SELECT * FROM taikhoan WHERE masv=?");
            checkSv.setString(1, maSV);
            java.sql.ResultSet rsCheckSv = checkSv.executeQuery();
            if (rsCheckSv.next()) {
                JOptionPane.showMessageDialog(this, "Mã SV này đã đăng ký tài khoản!");
                rsCheckSv.close();
                checkSv.close();
                return;
            }
            rsCheckSv.close();
            checkSv.close();
            if (!validCode) {
                JOptionPane.showMessageDialog(this, "Mã SV không tồn tại trong hệ thống!");
                return;
            }
            // Insert vào bảng tài khoản
            java.sql.PreparedStatement psTk = conn.prepareStatement("INSERT INTO taikhoan (username, password, role, masv) VALUES (?, ?, ?, ?)");
            psTk.setString(1, username);
            psTk.setString(2, password);
            psTk.setString(3, "SV");
            psTk.setString(4, maSV);
            psTk.executeUpdate();
            JOptionPane.showMessageDialog(this, "Đăng ký thành công!");
            parent.showLogin();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi đăng ký tài khoản!");
        }
    }
}
