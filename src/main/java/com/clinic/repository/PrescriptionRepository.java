// File: com/clinic/repository/PrescriptionRepository.java
package com.clinic.repository;

import com.clinic.database.DBConnection;
import com.clinic.model.Prescription;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles all SQL operations for the {@code prescriptions} table.
 */
public class PrescriptionRepository {

    private Connection conn() {
        return DBConnection.getInstance().getConnection();
    }

    // ── Create ────────────────────────────────────────────────────────────────

    public int save(Prescription prescription) {
        final String sql =
            "INSERT INTO prescriptions (medical_record_id, medication, dosage, instructions) " +
            "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt   (1, prescription.getMedicalRecordId());
            ps.setString(2, prescription.getMedication());
            ps.setString(3, prescription.getDosage());
            ps.setString(4, prescription.getInstructions());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    prescription.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save prescription: " + e.getMessage(), e);
        }
        return -1;
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    public Optional<Prescription> findById(int id) {
        final String sql = "SELECT * FROM prescriptions WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find prescription: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<Prescription> findAll() {
        final String sql = "SELECT * FROM prescriptions ORDER BY id";
        List<Prescription> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list prescriptions: " + e.getMessage(), e);
        }
        return list;
    }

    public List<Prescription> findByMedicalRecordId(int recordId) {
        final String sql = "SELECT * FROM prescriptions WHERE medical_record_id = ? ORDER BY id";
        List<Prescription> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, recordId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find prescriptions by record: " + e.getMessage(), e);
        }
        return list;
    }

    // ── Update ────────────────────────────────────────────────────────────────

    public boolean update(Prescription prescription) {
        final String sql =
            "UPDATE prescriptions SET medication=?, dosage=?, instructions=? WHERE id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, prescription.getMedication());
            ps.setString(2, prescription.getDosage());
            ps.setString(3, prescription.getInstructions());
            ps.setInt   (4, prescription.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update prescription: " + e.getMessage(), e);
        }
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    public boolean deleteById(int id) {
        final String sql = "DELETE FROM prescriptions WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete prescription: " + e.getMessage(), e);
        }
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    private Prescription map(ResultSet rs) throws SQLException {
        return new Prescription(
            rs.getInt   ("id"),
            rs.getInt   ("medical_record_id"),
            rs.getString("medication"),
            rs.getString("dosage"),
            rs.getString("instructions")
        );
    }
}