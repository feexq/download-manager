package com.project.downloadmanager.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:test.db";

    static {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             java.io.InputStream is = DatabaseConnection.class.getResourceAsStream("/schema.sql")) {
            
            if (is != null) {
                String schemaSql = new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                String[] statements = schemaSql.split(";");
                for (String sqlStmt : statements) {
                    if (!sqlStmt.trim().isEmpty()) {
                        stmt.execute(sqlStmt.trim());
                    }
                }
            } else {
                System.err.println("schema.sql not found in resources");
            }
        } catch (SQLException | java.io.IOException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
