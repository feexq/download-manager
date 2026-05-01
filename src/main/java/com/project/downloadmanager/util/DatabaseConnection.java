package com.project.downloadmanager.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:test.db";

    static {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS downloads ("
                    + "id INTEGER PRIMARY KEY,"
                    + "url TEXT,"
                    + "size INTEGER,"
                    + "downloaded INTEGER,"
                    + "start_time TEXT,"
                    + "end_time TEXT,"
                    + "status TEXT"
                    + ")";
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
