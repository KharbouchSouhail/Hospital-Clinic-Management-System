// File: com/clinic/repository/AppointmentRepository.java
package com.clinic.repository;

import com.clinic.database.DBConnection;
import com.clinic.model.Appointment;
import com.clinic.model.Appointment.Status;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles all SQL operations for the {@code appointments} table.
 */
public class AppointmentRepository {

    private Connection conn() {
        return DBConnection.getInstance().getConnection();
    }

    // ── Create ────────────────────────────────────────────────────────────────

    public int save(Appointment appt) {
        final String sql =
            "INSERT INTO appointments (patient_id, doctor_id, date_time, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt   (1, appt.getPatientId());
            ps.setInt   (2, appt.getDoctorId());
            ps.setString(3, appt.getDateTime());
            ps.setString(4, appt.getStatus().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    appt.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save appointment: " + e.getMessage(), e);
        }
        return -1;
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    public Optional<Appointment> findById(int id) {
        final String sql = "SELECT * FROM appointments WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find appointment: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<Appointment> findAll() {
        final String sql = "SELECT * FROM appointments ORDER BY date_time";
        List<Appointment> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list appointments: " + e.getMessage(), e);
        }
        return list;
    }

    public List<Appointment> findByPatientId(int patientId) {
        final String sql = "SELECT * FROM appointments WHERE patient_id = ? ORDER BY date_time";
        List<Appointment> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find appointments by patient: " + e.getMessage(), e);
        }
        return list;
    }

    public List<Appointment> findByDoctorId(int doctorId) {
        final String sql = "SELECT * FROM appointments WHERE doctor_id = ? ORDER BY date_time";
        List<Appointment> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find appointments by doctor: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Used for double-booking detection: checks if a SCHEDULED appointment
     * already exists for the given doctor at the given date-time.
     */
    public boolean existsConflict(int doctorId, String dateTime) {
        final String sql =
            "SELECT COUNT(*) FROM appointments " +
            "WHERE doctor_id = ? AND date_time = ? AND status = 'SCHEDULED'";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt   (1, doctorId);
            ps.setString(2, dateTime);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check appointment conflict: " + e.getMessage(), e);
        }
    }

    // ── Update ────────────────────────────────────────────────────────────────

    public boolean updateStatus(int id, Status status) {
        final String sql = "UPDATE appointments SET status = ? WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt   (2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update appointment status: " + e.getMessage(), e);
        }
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    public boolean deleteById(int id) {
        final String sql = "DELETE FROM appointments WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete appointment: " + e.getMessage(), e);
        }
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    private Appointment map(ResultSet rs) throws SQLException {
        return new Appointment(
            rs.getInt   ("id"),
            rs.getInt   ("patient_id"),
            rs.getInt   ("doctor_id"),
            rs.getString("date_time"),
            Status.valueOf(rs.getString("status"))
        );
    }
}