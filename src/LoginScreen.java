import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;

    public LoginScreen() {
        setTitle("Virtual File Vault - Login");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));

        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        panel.add(loginButton);
        panel.add(registerButton);

        add(panel, BorderLayout.CENTER);

        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> register());
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/virtual_vault \", \"root\", \"i=V~egU99+%W4/(*(#$")) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, CryptoUtil.encrypt(password));
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User(rs.getInt("id"), rs.getString("username"));
                JOptionPane.showMessageDialog(this, "Login Successful");
                dispose();
                new VaultDashboard(user).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void register() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection("Connection conn = DBConnection.getConnection();\n")) {
            String checkSql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setString(1, username);
            ResultSet checkRs = checkPs.executeQuery();

            if (checkRs.next()) {
                JOptionPane.showMessageDialog(this, "Username already exists", "Register Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String insertSql = "INSERT INTO users(username, password) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(insertSql);
            ps.setString(1, username);
            ps.setString(2, CryptoUtil.encrypt(password));
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Registered Successfully. You can login now.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
