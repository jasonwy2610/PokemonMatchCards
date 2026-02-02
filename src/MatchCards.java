import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.ArrayList;
import java.util.List;

public class MatchCards {
    class Card {
        String cardName;
        ImageIcon cardImageIcon;

        public Card(String cardName, ImageIcon cardImageIcon) {
            this.cardName = cardName;
            this.cardImageIcon = cardImageIcon;
        }

        public String toString() {
            return cardName;
        }
    }

    String[] cardList = {
            "darkness", "double", "fairy", "fighting", "fire",
            "grass", "lightning", "metal", "psychic", "water"
    };

    int rows = 4;
    int cols = 5;
    int cardWidth = 90;
    int cardHeight = 128;

    ArrayList<Card> cardSet;
    ImageIcon cardBackImageIcon;

    int boardWidth = cols * cardWidth;
    int boardHeight = rows * cardHeight;

    JFrame frame = new JFrame("Pokemon Match Cards");

    // Left panel - Game info
    JPanel leftPanel = new JPanel();
    JLabel userLabel = new JLabel();
    JLabel textLabel = new JLabel();
    JLabel timerLabel = new JLabel();
    JLabel countdownLabel = new JLabel();
    JLabel statusLabel = new JLabel();

    // Center panel - Game board với background
    JPanel boardPanel;

    // Right panel - History
    JPanel rightPanel = new JPanel();
    JTextArea historyTextArea = new JTextArea();
    JScrollPane historyScrollPane;

    // Bottom panel - Buttons
    JPanel buttonPanel = new JPanel();
    JButton restartButton = new JButton();
    JButton pauseButton = new JButton();
    JButton logoutButton = new JButton();

    int errorCount = 0;
    ArrayList<JButton> board;
    Timer hideCardTimer;
    Timer gameTimer;
    Timer countdownTimer;
    boolean gameReady = false;
    boolean gamePaused = false;
    JButton card1Selected;
    JButton card2Selected;

    int timeElapsed = 0;
    int countdownTime = 3;
    int matchedPairs = 0;
    int totalPairs = 10;

    int userId;
    String username;
    DatabaseManager dbManager;

    // Font cho symbols
    Font symbolFont = new Font("Segoe UI Symbol", Font.PLAIN, 14);
    Font symbolFontBold = new Font("Segoe UI Symbol", Font.BOLD, 16);

    MatchCards(int userId, String username) {
        this.userId = userId;
        this.username = username;
        this.dbManager = DatabaseManager.getInstance();

        setupCards();
        shuffleCards();

        // Tạo main panel với background
        JPanel mainPanel = new JPanel() {
            private Image backgroundImage;

            {
                try {
                    backgroundImage = new ImageIcon(
                            getClass().getResource("/img/background2.jpg")).getImage();
                    if (backgroundImage != null) {
                        System.out.println("Background2 loaded successfully!");
                    }
                } catch (Exception e) {
                    System.out.println("Background2 image not found: " + e.getMessage());
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        mainPanel.setLayout(new BorderLayout(5, 5));
        frame.setContentPane(mainPanel);

        frame.setSize(950, boardHeight + 100);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set logo
        try {
            Image logoImg = new ImageIcon(
                    getClass().getResource("/img/back.jpg")).getImage();
            frame.setIconImage(logoImg);
        } catch (Exception e) {
            System.out.println("Logo not found");
        }

        setupLeftPanel();
        setupBoardPanel();
        setupRightPanel();
        setupButtonPanel();

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(boardPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

        setupTimers();
        updateHistoryDisplay();
        startCountdown();
    }

    void setupLeftPanel() {
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(200, boardHeight));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        leftPanel.setOpaque(false); // Trong suốt để thấy background

        // Title
        JLabel titleLabel = new JLabel("Game Info");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(Color.YELLOW);

        // Player info
        userLabel.setFont(new Font("Arial", Font.BOLD, 16));
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userLabel.setText("Player: " + username);
        userLabel.setForeground(new Color(50, 100, 200));

        // Stats panel
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 220), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        textLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textLabel.setText("Errors: 0");
        textLabel.setForeground(Color.RED);

        timerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timerLabel.setText("Time: 0.0s");
        timerLabel.setForeground(new Color(0, 120, 200));

        countdownLabel.setFont(new Font("Arial", Font.BOLD, 24));
        countdownLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        countdownLabel.setText("Get Ready: 3");
        countdownLabel.setForeground(Color.RED);

        statusLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setText("Match: 0/" + totalPairs);
        statusLabel.setForeground(new Color(100, 150, 100));

        statsPanel.add(textLabel);
        statsPanel.add(Box.createVerticalStrut(10));
        statsPanel.add(timerLabel);
        statsPanel.add(Box.createVerticalStrut(10));
        statsPanel.add(statusLabel);
        statsPanel.add(Box.createVerticalStrut(15));
        statsPanel.add(countdownLabel);

        leftPanel.add(titleLabel);
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(userLabel);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(statsPanel);
        leftPanel.add(Box.createVerticalGlue());
    }

    void setupBoardPanel() {
        // Tạo panel trong suốt cho board
        boardPanel = new JPanel();
        boardPanel.setOpaque(false); // Trong suốt để thấy background

        board = new ArrayList<JButton>();
        boardPanel.setLayout(new GridLayout(rows, cols, 3, 3));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        for (int i = 0; i < cardSet.size(); i++) {
            JButton tile = new JButton();
            tile.setPreferredSize(new Dimension(cardWidth, cardHeight));
            tile.setOpaque(true); // Giữ card không trong suốt
            tile.setIcon(cardSet.get(i).cardImageIcon);
            tile.setFocusable(false);
            tile.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 180), 2));
            tile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!gameReady || gamePaused) {
                        return;
                    }
                    JButton tile = (JButton) e.getSource();
                    if (tile.getIcon() == cardBackImageIcon) {
                        if (card1Selected == null) {
                            card1Selected = tile;
                            int index = board.indexOf(card1Selected);
                            card1Selected.setIcon(cardSet.get(index).cardImageIcon);
                        } else if (card2Selected == null) {
                            card2Selected = tile;
                            int index = board.indexOf(card2Selected);
                            card2Selected.setIcon(cardSet.get(index).cardImageIcon);

                            if (card1Selected.getIcon() != card2Selected.getIcon()) {
                                errorCount += 1;
                                textLabel.setText("Errors: " + errorCount);
                                hideCardTimer.start();
                            } else {
                                matchedPairs++;
                                statusLabel.setText("Match: " + matchedPairs + "/" + totalPairs);
                                card1Selected = null;
                                card2Selected = null;

                                if (matchedPairs == totalPairs) {
                                    gameCompleted();
                                }
                            }
                        }
                    }
                }
            });
            board.add(tile);
            boardPanel.add(tile);
        }
    }

    void setupRightPanel() {
        rightPanel.setLayout(new BorderLayout(5, 5));
        rightPanel.setPreferredSize(new Dimension(200, boardHeight));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        rightPanel.setOpaque(false); // Trong suốt để thấy background

        JLabel historyTitle = new JLabel("History");
        historyTitle.setFont(new Font("Arial", Font.BOLD, 18));
        historyTitle.setHorizontalAlignment(JLabel.CENTER);
        historyTitle.setForeground(Color.CYAN);

        historyTextArea.setEditable(false);
        historyTextArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        historyTextArea.setBackground(Color.WHITE);
        historyTextArea.setMargin(new Insets(5, 5, 5, 5));

        historyScrollPane = new JScrollPane(historyTextArea);
        historyScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 220), 2));

        rightPanel.add(historyTitle, BorderLayout.NORTH);
        rightPanel.add(historyScrollPane, BorderLayout.CENTER);
    }

    void setupButtonPanel() {
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(false); // Trong suốt để thấy background

        restartButton.setFont(new Font("Arial", Font.BOLD, 14));
        restartButton.setText("Restart");
        restartButton.setPreferredSize(new Dimension(130, 35));
        restartButton.setFocusable(false);
        restartButton.setEnabled(false);
        restartButton.setBackground(Color.pink);
        restartButton.addActionListener(e -> {
            if (gameReady) {
                restartGame();
            }
        });

        pauseButton.setFont(new Font("Arial", Font.BOLD, 14));
        pauseButton.setText("Pause");
        pauseButton.setPreferredSize(new Dimension(130, 35));
        pauseButton.setFocusable(false);
        pauseButton.setEnabled(false);
        pauseButton.addActionListener(e -> togglePause());
        pauseButton.setBackground(Color.ORANGE);

        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton.setText("Logout");
        logoutButton.setPreferredSize(new Dimension(130, 35));
        logoutButton.setFocusable(false);
        logoutButton.addActionListener(e -> logout());
        logoutButton.setBackground(Color.RED);


        buttonPanel.add(restartButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(logoutButton);
    }

    void setupTimers() {
        hideCardTimer = new Timer(1000, e -> hideSelectedCards());
        hideCardTimer.setRepeats(false);

        gameTimer = new Timer(100, e -> {
            timeElapsed++;
            updateTimerDisplay();
        });

        countdownTimer = new Timer(1000, e -> {
            countdownTime--;
            if (countdownTime > 0) {
                countdownLabel.setText("Get Ready: " + countdownTime);
            } else {
                countdownLabel.setText("GO!");
                countdownTimer.stop();
                hideAllCards();
                gameTimer.start();
                gameReady = true;
                restartButton.setEnabled(true);
                pauseButton.setEnabled(true);

                Timer clearGoTimer = new Timer(500, evt -> countdownLabel.setText(""));
                clearGoTimer.setRepeats(false);
                clearGoTimer.start();
            }
        });
    }

    void togglePause() {
        gamePaused = !gamePaused;
        if (gamePaused) {
            gameTimer.stop();
            pauseButton.setText("Resume");
            countdownLabel.setText("PAUSED");
            countdownLabel.setForeground(new Color(200, 100, 0));
        } else {
            gameTimer.start();
            pauseButton.setText("Pause");
            countdownLabel.setText("");
        }
    }

    void updateHistoryDisplay() {
        StringBuilder sb = new StringBuilder();

        DatabaseManager.GameRecord bestScore = dbManager.getBestScore(userId);
        if (bestScore != null) {
            sb.append("=== BEST SCORE ===\n");
            sb.append("==================\n");
            sb.append(String.format("%.1fs | %d errors\n\n",
                    bestScore.timeSeconds, bestScore.errors));
        }

        List<DatabaseManager.GameRecord> history = dbManager.getUserHistory(userId);
        if (!history.isEmpty()) {
            sb.append("RECENT (Last 10)\n");
            sb.append("==================\n");
            for (int i = 0; i < Math.min(history.size(), 10); i++) {
                DatabaseManager.GameRecord record = history.get(i);
                sb.append(String.format("%d. %.1fs | %d err\n",
                        i + 1, record.timeSeconds, record.errors));
            }
        } else {
            sb.append("\n\n  No games yet!\n  Play to see\n  your history.");
        }

        historyTextArea.setText(sb.toString());
        historyTextArea.setCaretPosition(0);
    }

    void startCountdown() {
        countdownTime = 3;
        countdownLabel.setText("Get Ready: " + countdownTime);
        countdownTimer.start();
    }

    void updateTimerDisplay() {
        double seconds = timeElapsed / 10.0;
        timerLabel.setText(String.format("Time: %.1fs", seconds));
    }

    void hideSelectedCards() {
        if (card1Selected != null) {
            card1Selected.setIcon(cardBackImageIcon);
            card1Selected = null;
        }
        if (card2Selected != null) {
            card2Selected.setIcon(cardBackImageIcon);
            card2Selected = null;
        }
    }

    void hideAllCards() {
        for (int i = 0; i < board.size(); i++) {
            board.get(i).setIcon(cardBackImageIcon);
        }
    }

    void gameCompleted() {
        gameTimer.stop();
        gameReady = false;
        pauseButton.setEnabled(false);

        double finalTime = timeElapsed / 10.0;

        dbManager.saveGameResult(userId, finalTime, errorCount);
        updateHistoryDisplay();

        countdownLabel.setText("YOU WIN!");
        countdownLabel.setForeground(new Color(0, 150, 0));

        DatabaseManager.GameRecord bestScore = dbManager.getBestScore(userId);
        String bestScoreText = "";
        if (bestScore != null) {
            bestScoreText = String.format(
                    "\nYour Best: %.1fs with %d errors",
                    bestScore.timeSeconds, bestScore.errors
            );
        }

        String message = String.format(
                "Congratulations, %s!\n\n" +
                        "Time: %.1f seconds\n" +
                        "Errors: %d%s\n\n" +
                        "Do you want to play again?",
                username, finalTime, errorCount, bestScoreText
        );

        int choice = JOptionPane.showConfirmDialog(
                frame,
                message,
                "Game Completed!",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            restartGame();
        } else {
            logout();
        }
    }

    void restartGame() {
        gameTimer.stop();
        countdownTimer.stop();
        hideCardTimer.stop();

        gameReady = false;
        gamePaused = false;
        restartButton.setEnabled(false);
        pauseButton.setEnabled(false);
        pauseButton.setText("Pause");
        card1Selected = null;
        card2Selected = null;
        errorCount = 0;
        timeElapsed = 0;
        matchedPairs = 0;

        shuffleCards();
        for (int i = 0; i < board.size(); i++) {
            board.get(i).setIcon(cardSet.get(i).cardImageIcon);
        }

        textLabel.setText("Errors: 0");
        timerLabel.setText("Time: 0.0s");
        statusLabel.setText("Match: 0/" + totalPairs);
        countdownLabel.setForeground(Color.RED);

        startCountdown();
    }

    void logout() {
        frame.dispose();
        new LoginFrame().setVisible(true);
    }

    void setupCards() {
        cardSet = new ArrayList<Card>();
        for (String cardName : cardList) {
            Image cardImg = new ImageIcon(
                    getClass().getResource("/img/" + cardName + ".jpg")).getImage();
            ImageIcon cardImageIcon = new ImageIcon(
                    cardImg.getScaledInstance(cardWidth, cardHeight, java.awt.Image.SCALE_SMOOTH));
            Card card = new Card(cardName, cardImageIcon);
            cardSet.add(card);
        }
        cardSet.addAll(cardSet);

        Image carBackImg = new ImageIcon(
                getClass().getResource("/img/back.jpg")).getImage();
        cardBackImageIcon = new ImageIcon(
                carBackImg.getScaledInstance(cardWidth, cardHeight, java.awt.Image.SCALE_SMOOTH));
    }

    void shuffleCards() {
        for (int i = 0; i < cardSet.size(); i++) {
            int j = (int) (Math.random() * cardSet.size());
            Card temp = cardSet.get(i);
            cardSet.set(i, cardSet.get(j));
            cardSet.set(j, temp);
        }
    }
}