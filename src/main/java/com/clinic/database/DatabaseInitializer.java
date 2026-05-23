package com.clinic.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    private static final String CREATE_PATIENTS =
            "CREATE TABLE IF NOT EXISTS patients (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name TEXT NOT NULL," +
            "age INTEGER NOT NULL," +
            "phone TEXT NOT NULL," +
            "email TEXT NOT NULL);";

    private static final String CREATE_DOCTORS =
            "CREATE TABLE IF NOT EXISTS doctors (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name TEXT NOT NULL," +
            "specialization TEXT NOT NULL," +
            "phone TEXT NOT NULL);";

    private static final String CREATE_APPOINTMENTS =
            "CREATE TABLE IF NOT EXISTS appointments (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "patient_id INTEGER NOT NULL," +
            "doctor_id INTEGER NOT NULL," +
            "date_time TEXT NOT NULL," +
            "status TEXT NOT NULL DEFAULT 'SCHEDULED'," +
            "FOREIGN KEY(patient_id) REFERENCES patients(id) ON DELETE CASCADE," +
            "FOREIGN KEY(doctor_id) REFERENCES doctors(id) ON DELETE CASCADE);";

    private static final String CREATE_MEDICAL_RECORDS =
            "CREATE TABLE IF NOT EXISTS medical_records (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "patient_id INTEGER NOT NULL," +
            "description TEXT NOT NULL," +
            "diagnosis TEXT NOT NULL," +
            "date TEXT NOT NULL," +
            "FOREIGN KEY(patient_id) REFERENCES patients(id) ON DELETE CASCADE);";

    private static final String CREATE_PRESCRIPTIONS =
            "CREATE TABLE IF NOT EXISTS prescriptions (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "medical_record_id INTEGER NOT NULL," +
            "medication TEXT NOT NULL," +
            "dosage TEXT NOT NULL," +
            "instructions TEXT NOT NULL," +
            "FOREIGN KEY(medical_record_id) REFERENCES medical_records(id) ON DELETE CASCADE);";

    public static void initialize() {
        Connection conn = DBConnection.getInstance().getConnection();

        try (Statement stmt = conn.createStatement()) {

            stmt.execute(CREATE_PATIENTS);
            stmt.execute(CREATE_DOCTORS);
            stmt.execute(CREATE_APPOINTMENTS);
            stmt.execute(CREATE_MEDICAL_RECORDS);
            stmt.execute(CREATE_PRESCRIPTIONS);

            System.out.println("[DB] Schema initialized successfully.");

        } catch (SQLException e) {
            throw new RuntimeException("Database init failed: " + e.getMessage(), e);
        }
    }
}