import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:pokemon_match.db";
    private Connection conn;

    // Singleton pattern
    private static DatabaseManager instance;

    private DatabaseManager() {
        initDatabase();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    // Khởi tạo database và tạo bảng
    private void initDatabase() {
        try {
            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(DB_URL);
            createTables();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Tạo các bảng cần thiết
    private void createTables() {
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                created_at TEXT NOT NULL
            )
        """;

        String createGameHistoryTable = """
            CREATE TABLE IF NOT EXISTS game_history (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                time_seconds REAL NOT NULL,
                errors INTEGER NOT NULL,
                played_at TEXT NOT NULL,
                FOREIGN KEY (user_id) REFERENCES users(id)
            )
        """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createGameHistoryTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Đăng ký user mới
    public boolean registerUser(String username, String password) {
        String sql = "INSERT INTO users (username, password, created_at) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password); // Trong thực tế nên hash password
            pstmt.setString(3, getCurrentDateTime());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false; // Username đã tồn tại
        }
    }

    // Đăng nhập
    public Integer loginUser(String username, String password) {
        String sql = "SELECT id FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lưu kết quả game
    public void saveGameResult(int userId, double timeSeconds, int errors) {
        String sql = "INSERT INTO game_history (user_id, time_seconds, errors, played_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setDouble(2, timeSeconds);
            pstmt.setInt(3, errors);
            pstmt.setString(4, getCurrentDateTime());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lấy lịch sử game của user
    public List<GameRecord> getUserHistory(int userId) {
        List<GameRecord> history = new ArrayList<>();
        String sql = "SELECT time_seconds, errors, played_at FROM game_history WHERE user_id = ? ORDER BY played_at DESC LIMIT 10";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                GameRecord record = new GameRecord(
                        rs.getDouble("time_seconds"),
                        rs.getInt("errors"),
                        rs.getString("played_at")
                );
                history.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }

    // Lấy best score của user
    public GameRecord getBestScore(int userId) {
        String sql = """
            SELECT time_seconds, errors, played_at 
            FROM game_history 
            WHERE user_id = ? 
            ORDER BY time_seconds ASC, errors ASC 
            LIMIT 1
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new GameRecord(
                        rs.getDouble("time_seconds"),
                        rs.getInt("errors"),
                        rs.getString("played_at")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lấy username từ userId
    public String getUsername(int userId) {
        String sql = "SELECT username FROM users WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void close() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Class để lưu trữ thông tin game record
    public static class GameRecord {
        public double timeSeconds;
        public int errors;
        public String playedAt;

        public GameRecord(double timeSeconds, int errors, String playedAt) {
            this.timeSeconds = timeSeconds;
            this.errors = errors;
            this.playedAt = playedAt;
        }
    }
}