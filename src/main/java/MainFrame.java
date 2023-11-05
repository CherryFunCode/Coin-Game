import javax.swing.*;
import java.awt.*;
public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Two-up Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        LoginPanel loginPanel = new LoginPanel();
        GamePanel gamePanel = new GamePanel("DefaultUser");
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Game", gamePanel);
        setLayout(new BorderLayout());
        add(loginPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
