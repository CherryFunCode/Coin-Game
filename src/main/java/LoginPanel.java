import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import org.mindrot.jbcrypt.BCrypt;
public class LoginPanel extends JPanel {
    private final JTextField loginUsernameField;
    private final JPasswordField loginPasswordField;
    private final JTextField registerUsernameField;
    private final JPasswordField registerPasswordField;

    public LoginPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        // Login components
        JLabel loginUsernameLabel = new JLabel("Login Username:");
        JLabel loginPasswordLabel = new JLabel("Login Password:");
        loginUsernameField = new JTextField(10);
        loginUsernameField.setName("loginUsernameField");
        loginPasswordField = new JPasswordField(10);
        loginPasswordField.setName("loginPasswordField");
        JButton loginButton = new JButton("Login");
        // Registration components
        JLabel registerUsernameLabel = new JLabel("Register Username:");
        JLabel registerPasswordLabel = new JLabel("Register Password:");
        registerUsernameField = new JTextField(10);
        registerUsernameField.setName("registerUsernameField");
        registerPasswordField = new JPasswordField(10);
        registerPasswordField.setName("registerPasswordField");
        JButton registerButton = new JButton("Register");

        gbc.gridy = 0;
        add(loginUsernameLabel, gbc);
        gbc.gridx = 1;
        add(loginUsernameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(loginPasswordLabel, gbc);
        gbc.gridx = 1;
        add(loginPasswordField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(loginButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        add(registerUsernameLabel, gbc);
        gbc.gridx = 1;
        add(registerUsernameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(registerPasswordLabel, gbc);
        gbc.gridx = 1;
        add(registerPasswordField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        add(registerButton, gbc);
        registerButton.addActionListener(e -> {
            String baseUsername = registerUsernameField.getText();
            String password = new String(registerPasswordField.getPassword());
            if (baseUsername.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill in all fields");
                return;
            }
            // Generate a random six-digit number
            Random rnd = new Random();
            int number = rnd.nextInt(899999) + 100000; // 6-digit number
            String username = baseUsername + number;
            Connection conn = null;
            PreparedStatement stmt = null;
            // Hash the password using bcrypt
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            try {
                conn = DatabaseConnection.getConnection();
                String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.setString(2, hashedPassword);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Registration successful! Your username is: " + username);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            } finally {
                // Close resources
                try {
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        loginButton.addActionListener(e -> {
            String username = loginUsernameField.getText();
            String password = new String(loginPasswordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill in all fields");
                return;
            }
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            try {
                conn = DatabaseConnection.getConnection();
                String sql = "SELECT * FROM users WHERE username = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, username);
                rs = stmt.executeQuery();
                if (rs.next()) {
                    String dbPassword = rs.getString("password");
                    if (BCrypt.checkpw(password, dbPassword)) {
                        JOptionPane.showMessageDialog(null, "Login successful!");
                        JDialog gameDialog = new JDialog();
                        gameDialog.setTitle("Game Panel");
                        gameDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                        GamePanel game = new GamePanel(username);
                        game.setPreferredSize(new Dimension(800, 600));
                        gameDialog.add(game);
                        gameDialog.pack(); // Adjust the dialog size
                        gameDialog.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(null, "Incorrect password. Please try again.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Username not found. Please register or try again.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            } finally {
                // Close resources
                try {
                    if (rs != null) rs.close();
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    // implementation of the getComponentByName method
    public Component getComponentByName(String name) {
        for (Component child : this.getComponents()) {
            if (name.equals(child.getName())) {
                return child;
            }
        }
        return null;
    }
}