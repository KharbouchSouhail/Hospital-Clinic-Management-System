package com.clinic.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;

public class DBConnection {

    private static final String DB_DIR  = "data";
    private static final String DB_FILE = DB_DIR + File.separator + "clinic.db";
    private static final String URL     = "jdbc:sqlite:" + DB_FILE;

    private static DBConnection instance;
    private final Connection connection;

    private DBConnection() {
        try {
            // Ensure DB folder exists
            File dir = new File(DB_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Load driver
            Class.forName("org.sqlite.JDBC");

            // Open connection
            connection = DriverManager.getConnection(URL);

            // Apply PRAGMAs safely:
            // 1. busy_timeout FIRST so SQLite waits instead of throwing SQLITE_BUSY.
            // 2. Check current journal_mode before changing it (avoids conflicts
            //    when recovering from a previous crashed run).
            // 3. journal_mode=WAL returns a ResultSet → use executeQuery() and close it.
            // 4. foreign_keys=ON last.
            try (Statement stmt = connection.createStatement()) {

                // 1. Wait up to 5 s if the DB is temporarily locked
                stmt.execute("PRAGMA busy_timeout=5000;");

                // 2. Check whether WAL is already enabled
                boolean alreadyWAL = false;
                try (ResultSet rs = stmt.executeQuery("PRAGMA journal_mode;")) {
                    if (rs.next()) {
                        alreadyWAL = "wal".equalsIgnoreCase(rs.getString(1));
                    }
                }

                // 3. Switch to WAL only if needed; executeQuery() consumes the returned mode string
                if (!alreadyWAL) {
                    try (ResultSet rs = stmt.executeQuery("PRAGMA journal_mode=WAL;")) {
                        // result consumed automatically by try-with-resources
                    }
                }

                // 4. Enforce FK constraints
                stmt.execute("PRAGMA foreign_keys=ON;");
            }

            System.out.println("[DB] Connected: " + DB_FILE);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver missing", e);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to open database connection: " + e.getMessage(), e);
        }
    }

    public static synchronized DBConnection getInstance() {
        if (instance == null || isClosed()) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public static synchronized void closeConnection() {
        if (instance != null) {
            try {
                if (!instance.connection.isClosed()) {
                    instance.connection.close();
                }
            } catch (SQLException ignored) {}
            instance = null;
        }
    }

    private static boolean isClosed() {
        try {
            return instance == null || instance.connection.isClosed();
        } catch (SQLException e) {
            return true;
        }
    }
}