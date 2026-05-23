// File: com/clinic/service/DoctorService.java
package com.clinic.service;

import com.clinic.exception.DoctorNotFoundException;
import com.clinic.exception.InvalidDataException;
import com.clinic.model.Doctor;
import com.clinic.repository.DoctorRepository;
import com.clinic.utils.ValidationHelper;

import java.util.List;

/**
 * Business logic for doctor management.
 * <p>No SQL here – all database access goes through {@link DoctorRepository}.
 */
public class DoctorService {

    private final DoctorRepository doctorRepo;

    public DoctorService(DoctorRepository doctorRepo) {
        this.doctorRepo = doctorRepo;
    }

    // ── Create ────────────────────────────────────────────────────────────────

    /**
     * Validates and persists a new doctor.
     *
     * @return the saved doctor with its generated ID
     * @throws InvalidDataException if any field fails validation
     */
    public Doctor addDoctor(String name, String specialization, String phone) {
        ValidationHelper.requireNonBlank(name,           "Name");
        ValidationHelper.requireNonBlank(specialization, "Specialization");
        ValidationHelper.requireValidPhone(phone);

        Doctor doctor = new Doctor(0, name.trim(), specialization.trim(), phone.trim());
        doctorRepo.save(doctor);
        return doctor;
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    /**
     * Retrieves a doctor by ID.
     *
     * @throws DoctorNotFoundException if no doctor with that ID exists
     */
    public Doctor getDoctorById(int id) {
        ValidationHelper.requirePositive(id, "Doctor ID");
        return doctorRepo.findById(id)
                         .orElseThrow(() -> new DoctorNotFoundException(id));
    }

    /**
     * Returns all doctors, sorted by ID.
     */
    public List<Doctor> getAllDoctors() {
        return doctorRepo.findAll();
    }

    // ── Update ────────────────────────────────────────────────────────────────

    /**
     * Updates an existing doctor's details.
     *
     * @throws DoctorNotFoundException if the doctor does not exist
     * @throws InvalidDataException    if any field fails validation
     */
    public Doctor updateDoctor(int id, String name, String specialization, String phone) {
        Doctor existing = getDoctorById(id);

        ValidationHelper.requireNonBlank(name,           "Name");
        ValidationHelper.requireNonBlank(specialization, "Specialization");
        ValidationHelper.requireValidPhone(phone);

        existing.setName(name.trim());
        existing.setSpecialization(specialization.trim());
        existing.setPhone(phone.trim());

        doctorRepo.update(existing);
        return existing;
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    /**
     * Deletes a doctor.
     *
     * @throws DoctorNotFoundException if the doctor does not exist
     */
    public void deleteDoctor(int id) {
        getDoctorById(id);
        doctorRepo.deleteById(id);
    }
}