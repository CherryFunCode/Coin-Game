import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Objects;
import java.util.Random;
import javax.swing.table.DefaultTableModel;
public class GamePanel extends JPanel {
    private static final int IMAGE_WIDTH = 100;
    private static final int IMAGE_HEIGHT = 100;
    // Game controls and components
    private JButton btnHead, btnTail, btnOneOfEach, btnSaveScore, btnToss, btnExit;
    private JLabel lblResult, lblScore, lblCoinImage, lblHighScore;
    private int score = 0;
    private final String username;
    private String playerBet = null;
    private ImageIcon iconHeads, iconTails, iconHeadTails;
    private Timer animationTimer;
    // Play count restriction
    private int playCount = 0;
    // Sub-panels for game and leaderboard
    private JPanel gameControlsPanel;
    private JPanel leaderboardPanel;
    private JTable leaderboardTable;
    private JScrollPane leaderboardScrollPane;
    private JButton btnRestart;
    public GamePanel(String user) {
        this.username = user;
        initComponents();
        setupTabbedPane();
        attachListeners();
    }

    private void initComponents() {
        btnHead = new JButton("Head");
        btnHead.setName("btnHead");
        btnTail = new JButton("Tail");
        btnTail.setName("btnTail");
        btnOneOfEach = new JButton("One of Each");
        btnSaveScore = new JButton("Save Score");
        btnToss = new JButton("Toss Coin");
        btnExit = new JButton("Exit Game");
        btnRestart = new JButton("Restart");
        lblResult = new JLabel("Result will be displayed here.");
        lblResult.setName("lblResult");
        lblScore = new JLabel("Score: " + score);
        lblScore.setName("lblScore");
        lblHighScore = new JLabel("High Score: ");
        updateHighScore();
        iconHeads = loadScaledImage("/heads.png");
        iconTails = loadScaledImage("/tails.png");
        iconHeadTails = loadScaledImage("/headtails.png");
        lblCoinImage = new JLabel(iconHeads);
        gameControlsPanel = new JPanel(new GridBagLayout());
        leaderboardPanel = new JPanel();
        setupGameControlsLayout();
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Game", gameControlsPanel);
        this.add(tabbedPane);
        leaderboardTable = new JTable(new DefaultTableModel(new Object[][] {}, new String[] {"Username", "Score"}));
        leaderboardScrollPane = new JScrollPane(leaderboardTable);
        leaderboardPanel = new JPanel(new BorderLayout());
        leaderboardPanel.add(leaderboardScrollPane, BorderLayout.CENTER); // This line was missing
        tabbedPane.addTab("Leaderboard", leaderboardPanel); // Add leaderboard tab
        updateLeaderboard();
        setupInstructionsTab(tabbedPane);
    }

    private void setupInstructionsTab(JTabbedPane tabbedPane) {
        JTextArea txtInstructions = new JTextArea(10, 40);
        txtInstructions.setText("""
                Game Instructions:

                1. Choose your bet - Head, Tail, or One of Each.
                2. A player can play only 10 times. After that prevents the game from being played.
                3. Click 'Toss Coin' to toss the coin.
                4. If the outcome matches your bet, you score points.
                5. You can save your score after you finish with playing (10times).
                6. Use 'Restart' to reset the game and score.
                7. The 'Leaderboard' tab shows the high scores.
                8. You can exit the game using the 'Exit Game' button.

                Have fun playing!""");
        txtInstructions.setWrapStyleWord(true);
        txtInstructions.setLineWrap(true);
        txtInstructions.setEditable(false);
        txtInstructions.setFont(new Font("Arial", Font.PLAIN, 12));
        txtInstructions.setMargin(new Insets(5,5,5,5));

        JScrollPane scrollPane = new JScrollPane(txtInstructions);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel instructionsPanel = new JPanel(new BorderLayout());
        instructionsPanel.add(scrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("Instructions", instructionsPanel);
    }

    private void setupGameControlsLayout() {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        addToLayout(gameControlsPanel, lblCoinImage, c, 0, 0, 3);
        addToLayout(gameControlsPanel, btnHead, c, 1, 0);
        addToLayout(gameControlsPanel, btnTail, c, 1, 1);
        addToLayout(gameControlsPanel, btnOneOfEach, c, 1, 2);
        addToLayout(gameControlsPanel, btnToss, c, 1, 3);
        addToLayout(gameControlsPanel, btnSaveScore, c, 2, 0);
        addToLayout(gameControlsPanel, lblResult, c, 2, 1, 2);
        addToLayout(gameControlsPanel, lblScore, c, 2, 3);
        addToLayout(gameControlsPanel, lblHighScore, c, 3, 0);
        addToLayout(gameControlsPanel, btnExit, c, 3, 1);
        addToLayout(gameControlsPanel, btnRestart, c, 3, 2);

    }
    private void addToLayout(JPanel panel, JComponent comp, GridBagConstraints c, int x, int y) {
        addToLayout(panel, comp, c, x, y, 1);
    }
    private void addToLayout(JPanel panel, JComponent comp, GridBagConstraints c, int x, int y, int height) {
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = 1;
        c.gridheight = height;
        panel.add(comp, c);
    }
    private void setupTabbedPane() {
        leaderboardPanel.add(leaderboardScrollPane, BorderLayout.CENTER);
    }
    private void attachListeners() {
        btnHead.addActionListener(new BetListener("Head"));
        btnTail.addActionListener(new BetListener("Tail"));
        btnOneOfEach.addActionListener(new BetListener("One of Each"));
        btnToss.addActionListener(new TossListener());
        btnSaveScore.addActionListener(new SaveScoreListener());
        btnRestart.addActionListener(new RestartListener());
        btnExit.addActionListener(e -> System.exit(0));
    }
    private ImageIcon loadScaledImage(String path) {
        ImageIcon originalIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource(path)));
        Image scaledImage = originalIcon.getImage().getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }
    private class BetListener implements ActionListener {
        private final String bet;
        public BetListener(String bet) {
            this.bet = bet;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            playerBet = this.bet;
            lblResult.setText("Bet set to: " + playerBet);
        }
    }

    private void updateLeaderboard() {
        DefaultTableModel model = (DefaultTableModel) leaderboardTable.getModel();
        model.setRowCount(0);

        // Establish a database connection, fetch data
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT username, high_score FROM users ORDER BY high_score DESC LIMIT 10")) { // Added LIMIT 10
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String username = rs.getString("username");
                int highScore = rs.getInt("high_score");
                model.addRow(new Object[]{username, highScore});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error fetching leaderboard: " + ex.getMessage());
        }
    }
    private void updateHighScore() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT high_score FROM users WHERE username = ?")) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int highScore = rs.getInt("high_score");
                lblHighScore.setText("High Score: " + highScore);
            } else {
                lblHighScore.setText("High Score: 0");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error fetching high score: " + ex.getMessage());
        }
    }
    private class TossListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Random random = new Random();
            boolean coin1 = random.nextBoolean();
            boolean coin2 = random.nextBoolean();
            String outcome = (coin1 ? "Head" : "Tail") + " & " + (coin2 ? "Head" : "Tail");
            if (playCount >= 10) {
                lblResult.setText("You've played 10 times. Please login again to play more.");
                return; // Prevents the game from being played
            }
            // Animation
            ImageIcon[] frames = {iconHeads, iconTails, iconHeadTails};
            int frameDelay = 200;
            animationTimer = new Timer(frameDelay, new ActionListener() {
                private int frameIndex = 0;
                @Override
                public void actionPerformed(ActionEvent evt) {
                    lblCoinImage.setIcon(frames[frameIndex]);
                    frameIndex = (frameIndex + 1) % frames.length;
                }
            });
            animationTimer.start();
            Timer resultTimer = new Timer(2000, evt -> {
                animationTimer.stop();
                ImageIcon resultIcon;
                if (coin1) {
                    resultIcon = coin2 ? iconHeads : iconHeadTails;
                } else {
                    resultIcon = coin2 ? iconHeadTails : iconTails;
                }
                lblCoinImage.setIcon(resultIcon);
                // Compare outcome with player's bet
                boolean guessedCorrectly = false;

                if ("One of Each".equals(playerBet)) {
                    guessedCorrectly = "Head & Tail".equals(outcome) || "Tail & Head".equals(outcome);
                } else if ("Head".equals(playerBet) || "Tail".equals(playerBet)) {
                    guessedCorrectly = outcome.equals(playerBet + " & " + playerBet);
                }
                if (guessedCorrectly) {
                    score += 10;
                    lblResult.setText("You guessed correctly!");
                } else {
                    lblResult.setText("Try again!");
                }
                lblScore.setText("Score: " + score);
            });
            resultTimer.setRepeats(false);
            resultTimer.start();
            playCount++;
        }
    }
    private class SaveScoreListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement updatePstmt = conn.prepareStatement("UPDATE users SET high_score = ? WHERE username = ?")) {
                updatePstmt.setInt(1, score);
                updatePstmt.setString(2, username);
                updatePstmt.executeUpdate();
                updateHighScore();
                updateLeaderboard();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error saving score: " + ex.getMessage());
            }
        }
    }
    private class RestartListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            restartGame();
        }
    }
    private void restartGame() {
        score = 0;
        playCount = 0;
        playerBet = null;
        lblResult.setText("Result will be displayed here.");
        lblScore.setText("Score: " + score);
        lblCoinImage.setIcon(iconHeads);
        updateHighScore();
        updateLeaderboard();
    }

    public static void main(String[] args) {
        // Create and set up the main window
        JFrame frame = new JFrame("Coin Toss Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 400);
        // Create a GamePanel instance and add it to the main window
        GamePanel gamePanel = new GamePanel("DemoUser");
        frame.add(gamePanel);
        // Display the window
        frame.setVisible(true);
    }
}