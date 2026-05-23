// File: com/clinic/repository/MedicalRecordRepository.java
package com.clinic.repository;

import com.clinic.database.DBConnection;
import com.clinic.model.MedicalRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles all SQL operations for the {@code medical_records} table.
 */
public class MedicalRecordRepository {

    private Connection conn() {
        return DBConnection.getInstance().getConnection();
    }

    // ── Create ────────────────────────────────────────────────────────────────

    public int save(MedicalRecord record) {
        final String sql =
            "INSERT INTO medical_records (patient_id, description, diagnosis, date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt   (1, record.getPatientId());
            ps.setString(2, record.getDescription());
            ps.setString(3, record.getDiagnosis());
            ps.setString(4, record.getDate());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    record.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save medical record: " + e.getMessage(), e);
        }
        return -1;
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    public Optional<MedicalRecord> findById(int id) {
        final String sql = "SELECT * FROM medical_records WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find medical record: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<MedicalRecord> findAll() {
        final String sql = "SELECT * FROM medical_records ORDER BY date DESC";
        List<MedicalRecord> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list medical records: " + e.getMessage(), e);
        }
        return list;
    }

    public List<MedicalRecord> findByPatientId(int patientId) {
        final String sql = "SELECT * FROM medical_records WHERE patient_id = ? ORDER BY date DESC";
        List<MedicalRecord> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find records by patient: " + e.getMessage(), e);
        }
        return list;
    }

    // ── Update ────────────────────────────────────────────────────────────────

    public boolean update(MedicalRecord record) {
        final String sql =
            "UPDATE medical_records SET description=?, diagnosis=?, date=? WHERE id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, record.getDescription());
            ps.setString(2, record.getDiagnosis());
            ps.setString(3, record.getDate());
            ps.setInt   (4, record.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update medical record: " + e.getMessage(), e);
        }
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    public boolean deleteById(int id) {
        final String sql = "DELETE FROM medical_records WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete medical record: " + e.getMessage(), e);
        }
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    private MedicalRecord map(ResultSet rs) throws SQLException {
        return new MedicalRecord(
            rs.getInt   ("id"),
            rs.getInt   ("patient_id"),
            rs.getString("description"),
            rs.getString("diagnosis"),
            rs.getString("date")
        );
    }
}