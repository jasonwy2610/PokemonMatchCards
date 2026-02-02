import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RegisterFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JLabel backToLoginLabel;
    private DatabaseManager dbManager;
    private JPanel backgroundPanel;

    public RegisterFrame() {
        dbManager = DatabaseManager.getInstance();

        setTitle("Pokemon Match Cards - Register");
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
        // Background panel with image
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

        backgroundPanel.setLayout(null);

        // Semi-transparent register panel
        JPanel registerPanel = new JPanel();
        registerPanel.setLayout(null);
        registerPanel.setBackground(new Color(255, 255, 255, 230));
        registerPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 100), 3));
        registerPanel.setBounds(360, 80, 480, 510);

        // Title
        JLabel titleLabel = new JLabel("CREATE ACCOUNT");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(50, 180, 100));
        titleLabel.setBounds(0, 30, 480, 35);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitleLabel = new JLabel("Register to Start Playing");
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
                BorderFactory.createLineBorder(new Color(150, 200, 150), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JLabel userHintLabel = new JLabel("(min. 3 characters)");
        userHintLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        userHintLabel.setForeground(new Color(120, 120, 120));
        userHintLabel.setBounds(95, 187, 150, 15);

        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passLabel.setForeground(new Color(50, 50, 50));
        passLabel.setBounds(95, 210, 100, 25);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBounds(95, 240, 290, 35);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 200, 150), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JLabel passHintLabel = new JLabel("(min. 4 characters)");
        passHintLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        passHintLabel.setForeground(new Color(120, 120, 120));
        passHintLabel.setBounds(95, 277, 150, 15);

        // Confirm Password
        JLabel confirmPassLabel = new JLabel("Confirm Password:");
        confirmPassLabel.setFont(new Font("Arial", Font.BOLD, 14));
        confirmPassLabel.setForeground(new Color(50, 50, 50));
        confirmPassLabel.setBounds(95, 300, 150, 25);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        confirmPasswordField.setBounds(95, 330, 290, 35);
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 200, 150), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Register button
        registerButton = new JButton("REGISTER");
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        registerButton.setBounds(95, 385, 290, 40);
        registerButton.setBackground(new Color(50, 200, 100));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        registerButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                registerButton.setBackground(new Color(30, 180, 80));
            }
            public void mouseExited(MouseEvent e) {
                registerButton.setBackground(new Color(50, 200, 100));
            }
        });

        // Back to login label (clickable)
        JPanel backPanel = new JPanel();
        backPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        backPanel.setOpaque(false);
        backPanel.setBounds(0, 445, 480, 30);

        JLabel alreadyLabel = new JLabel("Already have an account?");
        alreadyLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        alreadyLabel.setForeground(new Color(80, 80, 80));

        backToLoginLabel = new JLabel("Login here");
        backToLoginLabel.setFont(new Font("Arial", Font.BOLD, 13));
        backToLoginLabel.setForeground(new Color(50, 150, 250));
        backToLoginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        backToLoginLabel.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                backToLoginLabel.setForeground(new Color(220, 50, 50));
                backToLoginLabel.setText("<html><u>Login here</u></html>");
            }
            public void mouseExited(MouseEvent e) {
                backToLoginLabel.setForeground(new Color(50, 150, 250));
                backToLoginLabel.setText("Login here");
            }
        });

        backPanel.add(alreadyLabel);
        backPanel.add(backToLoginLabel);

        // Add components to register panel
        registerPanel.add(titleLabel);
        registerPanel.add(subtitleLabel);
        registerPanel.add(userLabel);
        registerPanel.add(usernameField);
        registerPanel.add(userHintLabel);
        registerPanel.add(passLabel);
        registerPanel.add(passwordField);
        registerPanel.add(passHintLabel);
        registerPanel.add(confirmPassLabel);
        registerPanel.add(confirmPasswordField);
        registerPanel.add(registerButton);
        registerPanel.add(backPanel);

        // Add register panel to background
        backgroundPanel.add(registerPanel);

        add(backgroundPanel);

        // Action listeners
        registerButton.addActionListener(e -> handleRegister());
        backToLoginLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                backToLogin();
            }
        });

        // Enter key navigation
        usernameField.addActionListener(e -> passwordField.requestFocus());
        passwordField.addActionListener(e -> confirmPasswordField.requestFocus());
        confirmPasswordField.addActionListener(e -> handleRegister());
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validation
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please fill in all fields!");
            return;
        }

        if (username.length() < 3) {
            showError("Username must be at least 3 characters!");
            usernameField.requestFocus();
            return;
        }

        if (password.length() < 4) {
            showError("Password must be at least 4 characters!");
            passwordField.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match!");
            confirmPasswordField.setText("");
            confirmPasswordField.requestFocus();
            return;
        }

        // Try to register
        boolean success = dbManager.registerUser(username, password);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Registration successful!\nYou can now login with your account.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            backToLogin();
        } else {
            showError("Username already exists!\nPlease choose a different username.");
            usernameField.requestFocus();
            usernameField.selectAll();
        }
    }

    private void backToLogin() {
        dispose();
        new LoginFrame().setVisible(true);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}