package classifier;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseManager {

    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    private static final String BASE_URL = "jdbc:mysql://localhost:3306/";
    private static final String URL = BASE_URL + "plant_db";
    private static final String USER = dotenv.get("DB_USER", "root");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD", "");

    static {
        // Automatically setup the DB and tables on launch.
        try (Connection conn = DriverManager.getConnection(BASE_URL, USER, PASSWORD);
                java.sql.Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE DATABASE IF NOT EXISTS plant_db");
            stmt.execute("USE plant_db");

            // Create Users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) UNIQUE NOT NULL, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "name VARCHAR(100), dob DATE, age INT, gender VARCHAR(20), " +
                    "mode_preference VARCHAR(10) DEFAULT 'LIGHT')");

            // Create Plant info table
            stmt.execute("CREATE TABLE IF NOT EXISTS plant_info (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "species_name VARCHAR(100) UNIQUE NOT NULL, " +
                    "description TEXT, care_instructions TEXT, toxicity_warning VARCHAR(255))");

            // Create Scan History table
            stmt.execute("CREATE TABLE IF NOT EXISTS scan_history (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id INT NOT NULL, " +
                    "image_path VARCHAR(255) NOT NULL, " +
                    "species VARCHAR(100) NOT NULL, " +
                    "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id))");

            // Insert default dummy admin if not exists
            stmt.execute("INSERT IGNORE INTO users (username, password, name, dob, age, gender, mode_preference) " +
                    "VALUES ('admin', 'password123', 'John Doe', '1995-05-15', 30, 'Male', 'LIGHT')");

        } catch (SQLException e) {
            System.err.println("Could not auto-initialize Database. Check your MySQL credentials / port.");
            e.printStackTrace();
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static User loginUser(String username, String password) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("name"),
                        rs.getDate("dob") != null ? rs.getDate("dob").toString() : "",
                        rs.getInt("age"),
                        rs.getString("gender"),
                        rs.getString("mode_preference"));
            }
        }
        return null;
    }

    public static boolean registerUser(String username, String password, String name, String dob, int age,
            String gender) throws SQLException {
        // Check if username already exists
        String checkQuery = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = getConnection();
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                throw new SQLException("Username already exists.");
            }
        }

        String insertQuery = "INSERT INTO users (username, password, name, dob, age, gender) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, name);
            if (dob != null && !dob.isEmpty()) {
                stmt.setDate(4, java.sql.Date.valueOf(dob));
            } else {
                stmt.setNull(4, java.sql.Types.DATE);
            }
            stmt.setInt(5, age);
            stmt.setString(6, gender);

            return stmt.executeUpdate() > 0;
        }
    }

    public static boolean updateUserModePreference(int userId, String mode) {
        String query = "UPDATE users SET mode_preference = ? WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, mode);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getHistoryCount(int userId) {
        String query = "SELECT COUNT(*) FROM scan_history WHERE user_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean saveHistory(int userId, String imagePath, String species) {
        String query = "INSERT INTO scan_history (user_id, image_path, species) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setString(2, imagePath);
            stmt.setString(3, species);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<HistoryItem> getHistory(int userId) {
        List<HistoryItem> history = new ArrayList<>();
        String query = "SELECT * FROM scan_history WHERE user_id = ? ORDER BY timestamp DESC";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                history.add(new HistoryItem(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("image_path"),
                        rs.getString("species"),
                        rs.getString("timestamp")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }

    public static boolean deleteHistory(int historyId) {
        String query = "DELETE FROM scan_history WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, historyId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static PlantDetails getPlantDetails(String speciesName) {
        String query = "SELECT * FROM plant_info WHERE species_name = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, speciesName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new PlantDetails(
                        rs.getString("species_name"),
                        rs.getString("description"),
                        rs.getString("care_instructions"),
                        rs.getString("toxicity_warning"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
