// File: com/clinic/service/PatientService.java
package com.clinic.service;

import com.clinic.exception.InvalidDataException;
import com.clinic.exception.PatientNotFoundException;
import com.clinic.model.Patient;
import com.clinic.repository.PatientRepository;
import com.clinic.utils.ValidationHelper;

import java.util.List;

/**
 * Business logic for patient management.
 * <p>No SQL here – all database access goes through {@link PatientRepository}.
 */
public class PatientService {

    private final PatientRepository patientRepo;

    public PatientService(PatientRepository patientRepo) {
        this.patientRepo = patientRepo;
    }

    // ── Create ────────────────────────────────────────────────────────────────

    /**
     * Validates input and persists a new patient.
     *
     * @return the saved patient with its generated ID
     * @throws InvalidDataException if any field fails validation
     */
    public Patient addPatient(String name, int age, String phone, String email) {
        ValidationHelper.requireNonBlank(name,  "Name");
        ValidationHelper.requireMaxLength(name, 100, "Name");
        ValidationHelper.requireAgeRange(age);
        ValidationHelper.requireValidPhone(phone);
        ValidationHelper.requireValidEmail(email);

        Patient patient = new Patient(0, name.trim(), age, phone.trim(), email.trim().toLowerCase());
        patientRepo.save(patient);
        return patient;
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    /**
     * Retrieves a patient by ID.
     *
     * @throws PatientNotFoundException if no patient with that ID exists
     */
    public Patient getPatientById(int id) {
        ValidationHelper.requirePositive(id, "Patient ID");
        return patientRepo.findById(id)
                          .orElseThrow(() -> new PatientNotFoundException(id));
    }

    /**
     * Returns all patients, sorted by ID ascending.
     */
    public List<Patient> getAllPatients() {
        return patientRepo.findAll();
    }

    // ── Update ────────────────────────────────────────────────────────────────

    /**
     * Updates an existing patient's details.
     *
     * @throws PatientNotFoundException if the patient does not exist
     * @throws InvalidDataException     if any field fails validation
     */
    public Patient updatePatient(int id, String name, int age, String phone, String email) {
        Patient existing = getPatientById(id);   // throws if not found

        ValidationHelper.requireNonBlank(name, "Name");
        ValidationHelper.requireMaxLength(name, 100, "Name");
        ValidationHelper.requireAgeRange(age);
        ValidationHelper.requireValidPhone(phone);
        ValidationHelper.requireValidEmail(email);

        existing.setName(name.trim());
        existing.setAge(age);
        existing.setPhone(phone.trim());
        existing.setEmail(email.trim().toLowerCase());

        patientRepo.update(existing);
        return existing;
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    /**
     * Deletes a patient and (via FK CASCADE) their appointments and records.
     *
     * @throws PatientNotFoundException if the patient does not exist
     */
    public void deletePatient(int id) {
        // Verify existence first so we throw a typed exception
        getPatientById(id);
        patientRepo.deleteById(id);
    }
}