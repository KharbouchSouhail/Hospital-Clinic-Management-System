// File: com/clinic/service/AppointmentService.java
package com.clinic.service;

import com.clinic.exception.AppointmentNotFoundException;
import com.clinic.exception.DoctorNotFoundException;
import com.clinic.exception.InvalidDataException;
import com.clinic.exception.PatientNotFoundException;
import com.clinic.model.Appointment;
import com.clinic.model.Appointment.Status;
import com.clinic.repository.AppointmentRepository;
import com.clinic.repository.DoctorRepository;
import com.clinic.repository.PatientRepository;
import com.clinic.utils.ValidationHelper;

import java.util.List;

/**
 * Business logic for appointment management.
 *
 * <p>Key rule: a doctor cannot have two SCHEDULED appointments at the exact
 * same date_time (double-booking prevention).
 *
 * <p>No SQL here – all database access goes through the repositories.
 */
public class AppointmentService {

    private final AppointmentRepository appointmentRepo;
    private final PatientRepository     patientRepo;
    private final DoctorRepository      doctorRepo;

    public AppointmentService(AppointmentRepository appointmentRepo,
                               PatientRepository     patientRepo,
                               DoctorRepository      doctorRepo) {
        this.appointmentRepo = appointmentRepo;
        this.patientRepo     = patientRepo;
        this.doctorRepo      = doctorRepo;
    }

    // ── Create ────────────────────────────────────────────────────────────────

    /**
     * Creates a new SCHEDULED appointment after verifying:
     * <ul>
     *   <li>The patient exists</li>
     *   <li>The doctor exists</li>
     *   <li>No conflicting appointment for the same doctor+dateTime exists</li>
     * </ul>
     *
     * @throws PatientNotFoundException if patient does not exist
     * @throws DoctorNotFoundException  if doctor does not exist
     * @throws InvalidDataException     on validation failures or double-booking
     */
    public Appointment createAppointment(int patientId, int doctorId, String dateTime) {
        // Validate IDs
        ValidationHelper.requirePositive(patientId, "Patient ID");
        ValidationHelper.requirePositive(doctorId,  "Doctor ID");
        ValidationHelper.requireValidDateTime(dateTime);

        // Verify patient and doctor exist
        patientRepo.findById(patientId)
                   .orElseThrow(() -> new PatientNotFoundException(patientId));
        doctorRepo.findById(doctorId)
                  .orElseThrow(() -> new DoctorNotFoundException(doctorId));

        // ─── Double-booking check ─────────────────────────────────────────
        if (appointmentRepo.existsConflict(doctorId, dateTime)) {
            throw new InvalidDataException(
                "Doctor ID " + doctorId + " already has a SCHEDULED appointment at " + dateTime
                + ". Please choose a different time."
            );
        }

        Appointment appt = new Appointment(0, patientId, doctorId, dateTime, Status.SCHEDULED);
        appointmentRepo.save(appt);
        return appt;
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    public Appointment getAppointmentById(int id) {
        ValidationHelper.requirePositive(id, "Appointment ID");
        return appointmentRepo.findById(id)
                              .orElseThrow(() -> new AppointmentNotFoundException(id));
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepo.findAll();
    }

    public List<Appointment> getAppointmentsForPatient(int patientId) {
        patientRepo.findById(patientId)
                   .orElseThrow(() -> new PatientNotFoundException(patientId));
        return appointmentRepo.findByPatientId(patientId);
    }

    public List<Appointment> getAppointmentsForDoctor(int doctorId) {
        doctorRepo.findById(doctorId)
                  .orElseThrow(() -> new DoctorNotFoundException(doctorId));
        return appointmentRepo.findByDoctorId(doctorId);
    }

    // ── Status updates ────────────────────────────────────────────────────────

    /**
     * Marks an appointment as COMPLETED.
     *
     * @throws AppointmentNotFoundException if not found
     * @throws InvalidDataException         if already cancelled
     */
    public void completeAppointment(int id) {
        Appointment appt = getAppointmentById(id);
        if (appt.getStatus() == Status.CANCELLED) {
            throw new InvalidDataException("Cannot complete a cancelled appointment (ID=" + id + ").");
        }
        appointmentRepo.updateStatus(id, Status.COMPLETED);
    }

    /**
     * Cancels an appointment.
     *
     * @throws AppointmentNotFoundException if not found
     * @throws InvalidDataException         if already completed
     */
    public void cancelAppointment(int id) {
        Appointment appt = getAppointmentById(id);
        if (appt.getStatus() == Status.COMPLETED) {
            throw new InvalidDataException("Cannot cancel a completed appointment (ID=" + id + ").");
        }
        appointmentRepo.updateStatus(id, Status.CANCELLED);
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    public void deleteAppointment(int id) {
        getAppointmentById(id);
        appointmentRepo.deleteById(id);
    }
}