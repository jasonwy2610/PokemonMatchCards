import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel registerLabel;
    private DatabaseManager dbManager;
    private JPanel backgroundPanel;

    public LoginFrame() {
        dbManager = DatabaseManager.getInstance();

        setTitle("Pokemon Match Cards - Login");
        setSize(1200, 675);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Set logo
        try {
            Image logoImg = new ImageIcon(
                    getClass().getResource("/img/back.jpg")).getImage();
            setIconImage(logoImg);
        } catch (Exception e) {
            System.out.println("Logo not found");
        }

        initComponents();
    }

    private void initComponents() {
        // Background panel với hình ảnh
        backgroundPanel = new JPanel() {
            private Image backgroundImage;

            {
                try {
                    backgroundImage = new ImageIcon(
                            getClass().getResource("/img/background1.jpg")).getImage();
                } catch (Exception e) {
                    System.out.println("Background image not found");
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

        backgroundPanel.setLayout(null); // Absolute positioning

        // Semi-transparent login panel
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(null);
        loginPanel.setBackground(new Color(255, 255, 255, 230)); // Semi-transparent white
        loginPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 150), 3));
        loginPanel.setBounds(360, 137, 480, 400);

        // Title
        JLabel titleLabel = new JLabel("POKEMON MATCH CARDS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(220, 50, 50));
        titleLabel.setBounds(0, 30, 480, 35);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitleLabel = new JLabel("Login to Play");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setBounds(0, 70, 480, 25);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Username
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userLabel.setForeground(new Color(50, 50, 50));
        userLabel.setBounds(95, 120, 100, 25);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBounds(95, 150, 290, 35);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 200), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passLabel.setForeground(new Color(50, 50, 50));
        passLabel.setBounds(95, 195, 100, 25);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBounds(95, 225, 290, 35);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 200), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Login button
        loginButton = new JButton("LOGIN");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBounds(95, 280, 290, 40);
        loginButton.setBackground(new Color(50, 150, 250));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        loginButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(new Color(30, 130, 230));
            }
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(new Color(50, 150, 250));
            }
        });

        // Register label (clickable)
        JPanel registerPanel = new JPanel();
        registerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        registerPanel.setOpaque(false);
        registerPanel.setBounds(0, 340, 480, 30);

        JLabel noAccountLabel = new JLabel("Don't have an account?");
        noAccountLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        noAccountLabel.setForeground(new Color(80, 80, 80));

        registerLabel = new JLabel("Register here");
        registerLabel.setFont(new Font("Arial", Font.BOLD, 13));
        registerLabel.setForeground(new Color(50, 150, 250));
        registerLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect cho register label
        registerLabel.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                registerLabel.setForeground(new Color(220, 50, 50));
                registerLabel.setText("<html><u>Register here</u></html>");
            }
            public void mouseExited(MouseEvent e) {
                registerLabel.setForeground(new Color(50, 150, 250));
                registerLabel.setText("Register here");
            }
        });

        registerPanel.add(noAccountLabel);
        registerPanel.add(registerLabel);

        // Add components to login panel
        loginPanel.add(titleLabel);
        loginPanel.add(subtitleLabel);
        loginPanel.add(userLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passLabel);
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);
        loginPanel.add(registerPanel);

        // Add login panel to background
        backgroundPanel.add(loginPanel);

        add(backgroundPanel);

        // Action listeners
        loginButton.addActionListener(e -> handleLogin());
        registerLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                openRegisterFrame();
            }
        });

        // Enter key to login
        passwordField.addActionListener(e -> handleLogin());
        usernameField.addActionListener(e -> passwordField.requestFocus());
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password!");
            return;
        }

        Integer userId = dbManager.loginUser(username, password);

        if (userId != null) {
            // Login successful
            dispose();
            new MatchCards(userId, username);
        } else {
            showError("Invalid username or password!");
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }

    private void openRegisterFrame() {
        dispose();
        new RegisterFrame().setVisible(true);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}