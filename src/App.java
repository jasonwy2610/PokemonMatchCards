import javax.swing.*;

public class App {
    public static void main(String[] args) {


        // Chạy app
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}