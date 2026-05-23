// File: com/clinic/repository/DoctorRepository.java
package com.clinic.repository;

import com.clinic.database.DBConnection;
import com.clinic.model.Doctor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles all SQL operations for the {@code doctors} table.
 */
public class DoctorRepository {

    private Connection conn() {
        return DBConnection.getInstance().getConnection();
    }

    // ── Create ────────────────────────────────────────────────────────────────

    public int save(Doctor doctor) {
        final String sql = "INSERT INTO doctors (name, specialization, phone) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, doctor.getName());
            ps.setString(2, doctor.getSpecialization());
            ps.setString(3, doctor.getPhone());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    doctor.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save doctor: " + e.getMessage(), e);
        }
        return -1;
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    public Optional<Doctor> findById(int id) {
        final String sql = "SELECT * FROM doctors WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find doctor by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<Doctor> findAll() {
        final String sql = "SELECT * FROM doctors ORDER BY id";
        List<Doctor> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list doctors: " + e.getMessage(), e);
        }
        return list;
    }

    // ── Update ────────────────────────────────────────────────────────────────

    public boolean update(Doctor doctor) {
        final String sql = "UPDATE doctors SET name=?, specialization=?, phone=? WHERE id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, doctor.getName());
            ps.setString(2, doctor.getSpecialization());
            ps.setString(3, doctor.getPhone());
            ps.setInt   (4, doctor.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update doctor: " + e.getMessage(), e);
        }
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    public boolean deleteById(int id) {
        final String sql = "DELETE FROM doctors WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete doctor: " + e.getMessage(), e);
        }
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    private Doctor map(ResultSet rs) throws SQLException {
        return new Doctor(
            rs.getInt   ("id"),
            rs.getString("name"),
            rs.getString("specialization"),
            rs.getString("phone")
        );
    }
}