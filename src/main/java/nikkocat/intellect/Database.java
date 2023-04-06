package nikkocat.intellect;

import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static nikkocat.intellect.Intellect.LOGGER;

// I'm not going to bother with SQL injection safety
// That is future me's problem

public class Database {
    private static Connection connection;

    public static  Connection connection() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        try {
            if (connection == null) {
                connection = DriverManager.getConnection("jdbc:sqlite:config/intellect/intellect.db");
                LOGGER.info("Database connection established");
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
        return connection;
    }

    public static void createTableIfNotExist() throws SQLException {
        Statement statement = connection.createStatement();
        String createTableSQL = "CREATE TABLE IF NOT EXISTS chatlog (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user VARCHAR(16) NOT NULL CHECK (LENGTH(user) >= 3)," +
                "message VARCHAR(256) NOT NULL," +
                "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";
        statement.executeUpdate(createTableSQL);
    }

    public static int countMessages() throws SQLException {
        int count = 0;
            String countSQL = "SELECT COUNT(*) FROM chatlog";
            PreparedStatement statement = connection.prepareStatement(countSQL);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        return count;
    }

    public static void addMessage(String user, String message) throws SQLException {
        Statement statement = connection.createStatement();
        String insertSQL = "INSERT INTO chatlog (user, message) VALUES " +
                "('" + user + "', '" + message + "')";
        statement.executeUpdate(insertSQL);
    }

    public static void purgeMessages() throws SQLException {
        Statement statement = connection.createStatement();
        String deleteSQL = "DELETE FROM chatlog";
        statement.executeUpdate(deleteSQL);
    }
    public static void purgeMessages(int n) throws SQLException {
        Statement statement = connection.createStatement();
        String deleteSQL = "DELETE FROM chatlog WHERE id IN " +
                "(SELECT id FROM chatlog ORDER BY id DESC LIMIT " + n + ");";
        statement.executeUpdate(deleteSQL);
    }
    public static void purgeMessages(String user) throws SQLException {
        Statement statement = connection.createStatement();
        String deleteSQL = "DELETE FROM chatlog WHERE user = '" + user + "'";
        statement.executeUpdate(deleteSQL);
    }
    public static void purgeMessages(String user, int n) throws SQLException {
        Statement statement = connection.createStatement();
        String deleteSQL = "DELETE FROM chatlog WHERE user = '" + user + "' ORDER BY id DESC LIMIT " + n;
        statement.executeUpdate(deleteSQL);
    }

    public static List<ChatMessage> getMessages(int n) throws SQLException {
        List<ChatMessage> messages = new ArrayList<>();
        Statement statement = connection.createStatement();
        String selectSQL = "SELECT * FROM chatlog ORDER BY id DESC LIMIT " + n;
        ResultSet resultSet = statement.executeQuery(selectSQL);
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String user = resultSet.getString("user");
            String message = resultSet.getString("message");
            LocalDateTime timestamp = resultSet.getTimestamp("timestamp").toLocalDateTime();

            ChatMessage chatMessage = new ChatMessage(id, user, message, timestamp);
            messages.add(chatMessage);
        }
        return messages;
    }
    public static List<ChatMessage> getMessages(int skip, int n) throws SQLException {
        List<ChatMessage> messages = new ArrayList<>();
        Statement statement = connection.createStatement();
        String selectSQL = "SELECT * FROM chatlog ORDER BY id DESC LIMIT " + skip + ", " + n;
        ResultSet resultSet = statement.executeQuery(selectSQL);
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String user = resultSet.getString("user");
            String message = resultSet.getString("message");
            LocalDateTime timestamp = resultSet.getTimestamp("timestamp").toLocalDateTime();

            ChatMessage chatMessage = new ChatMessage(id, user, message, timestamp);
            messages.add(chatMessage);
        }
        return messages;
    }

    public static void printMessages() throws SQLException {
        Statement statement = connection.createStatement();
        String selectSQL = "SELECT * FROM chatlog";
        ResultSet resultSet = statement.executeQuery(selectSQL);
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String user = resultSet.getString("user");
            String message = resultSet.getString("message");
            Timestamp timestamp = resultSet.getTimestamp("timestamp");

            LOGGER.info("[" + timestamp + "] " + user + ": " + message);
        }
    }
}
