// File: com/clinic/repository/PatientRepository.java
package com.clinic.repository;

import com.clinic.database.DBConnection;
import com.clinic.model.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles all SQL operations for the {@code patients} table.
 * <p>No business logic here – only CRUD via PreparedStatement.
 */
public class PatientRepository {

    private Connection conn() {
        return DBConnection.getInstance().getConnection();
    }

    // ── Create ────────────────────────────────────────────────────────────────

    /**
     * Inserts a new patient and returns the generated ID.
     */
    public int save(Patient patient) {
        final String sql = "INSERT INTO patients (name, age, phone, email) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, patient.getName());
            ps.setInt   (2, patient.getAge());
            ps.setString(3, patient.getPhone());
            ps.setString(4, patient.getEmail());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    patient.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save patient: " + e.getMessage(), e);
        }
        return -1;
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    public Optional<Patient> findById(int id) {
        final String sql = "SELECT * FROM patients WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find patient by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<Patient> findAll() {
        final String sql = "SELECT * FROM patients ORDER BY id";
        List<Patient> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list patients: " + e.getMessage(), e);
        }
        return list;
    }

    // ── Update ────────────────────────────────────────────────────────────────

    public boolean update(Patient patient) {
        final String sql = "UPDATE patients SET name=?, age=?, phone=?, email=? WHERE id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, patient.getName());
            ps.setInt   (2, patient.getAge());
            ps.setString(3, patient.getPhone());
            ps.setString(4, patient.getEmail());
            ps.setInt   (5, patient.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update patient: " + e.getMessage(), e);
        }
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    public boolean deleteById(int id) {
        final String sql = "DELETE FROM patients WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete patient: " + e.getMessage(), e);
        }
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    private Patient map(ResultSet rs) throws SQLException {
        return new Patient(
            rs.getInt   ("id"),
            rs.getString("name"),
            rs.getInt   ("age"),
            rs.getString("phone"),
            rs.getString("email")
        );
    }
}