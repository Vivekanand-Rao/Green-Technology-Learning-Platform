package com.greentech.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DBConnection {
    private static String url = "jdbc:mysql://localhost:3306/greentech_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static String user = "root";
    private static String password = "Vivek007";

    static {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("[DBConnection] MySQL JDBC Driver not found: " + e.getMessage());
        }

        // Load db.properties from classpath if available
        try (InputStream in = DBConnection.class.getResourceAsStream("/db.properties")) {
            if (in != null) {
                Properties props = new Properties();
                props.load(in);
                if (props.getProperty("db.url") != null && !props.getProperty("db.url").trim().isEmpty()) {
                    url = props.getProperty("db.url").trim();
                }
                if (props.getProperty("db.user") != null && !props.getProperty("db.user").trim().isEmpty()) {
                    user = props.getProperty("db.user").trim();
                }
                if (props.getProperty("db.password") != null) {
                    password = props.getProperty("db.password").trim();
                }
            }
        } catch (Exception e) {
            System.err.println("[DBConnection] Warning: Could not load db.properties: " + e.getMessage());
        }

        // System property overrides
        if (System.getProperty("db.url") != null) url = System.getProperty("db.url");
        if (System.getProperty("db.user") != null) user = System.getProperty("db.user");
        if (System.getProperty("db.password") != null) password = System.getProperty("db.password");

        // Environment variable overrides
        if (System.getenv("DB_URL") != null) url = System.getenv("DB_URL");
        if (System.getenv("DB_USER") != null) user = System.getenv("DB_USER");
        if (System.getenv("DB_PASSWORD") != null) password = System.getenv("DB_PASSWORD");
    }

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public static String getUrl() {
        return url;
    }

    public static String getUser() {
        return user;
    }

    public static void setCredentials(String newUrl, String newUser, String newPassword) {
        if (newUrl != null && !newUrl.trim().isEmpty()) url = newUrl.trim();
        if (newUser != null && !newUser.trim().isEmpty()) user = newUser.trim();
        if (newPassword != null) password = newPassword.trim();
    }

    public static String testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                return "SUCCESS: Connected successfully to " + conn.getCatalog() + " as " + user;
            }
            return "FAILED: Connection is closed or null.";
        } catch (SQLException e) {
            return "FAILED: " + e.getMessage() + " (SQLState: " + e.getSQLState() + ", ErrorCode: " + e.getErrorCode() + ")";
        }
    }
}
