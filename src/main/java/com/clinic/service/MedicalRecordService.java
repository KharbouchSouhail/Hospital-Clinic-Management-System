// File: com/clinic/service/MedicalRecordService.java
package com.clinic.service;

import com.clinic.exception.InvalidDataException;
import com.clinic.exception.PatientNotFoundException;
import com.clinic.model.MedicalRecord;
import com.clinic.model.Prescription;
import com.clinic.repository.MedicalRecordRepository;
import com.clinic.repository.PatientRepository;
import com.clinic.repository.PrescriptionRepository;
import com.clinic.utils.DateHelper;
import com.clinic.utils.ValidationHelper;

import java.util.List;

/**
 * Business logic for medical records and prescriptions.
 * <p>No SQL here – all database access goes through repositories.
 */
public class MedicalRecordService {

    private final MedicalRecordRepository recordRepo;
    private final PrescriptionRepository  prescriptionRepo;
    private final PatientRepository       patientRepo;

    public MedicalRecordService(MedicalRecordRepository recordRepo,
                                 PrescriptionRepository  prescriptionRepo,
                                 PatientRepository       patientRepo) {
        this.recordRepo       = recordRepo;
        this.prescriptionRepo = prescriptionRepo;
        this.patientRepo      = patientRepo;
    }

    // ── Medical records ───────────────────────────────────────────────────────

    /**
     * Creates and persists a new medical record linked to an existing patient.
     *
     * @throws PatientNotFoundException if the patient does not exist
     * @throws InvalidDataException     on validation failure
     */
    public MedicalRecord addRecord(int patientId, String description,
                                    String diagnosis, String date) {
        ValidationHelper.requirePositive(patientId, "Patient ID");
        ValidationHelper.requireNonBlank(description, "Description");
        ValidationHelper.requireNonBlank(diagnosis,   "Diagnosis");

        // Use today's date if blank
        String recordDate = (date == null || date.isBlank()) ? DateHelper.today() : date.trim();
        ValidationHelper.requireValidDate(recordDate);

        patientRepo.findById(patientId)
                   .orElseThrow(() -> new PatientNotFoundException(patientId));

        MedicalRecord record = new MedicalRecord(
            0, patientId, description.trim(), diagnosis.trim(), recordDate);
        recordRepo.save(record);
        return record;
    }

    /**
     * Returns a medical record by ID, optionally hydrating its prescriptions.
     *
     * @throws InvalidDataException if not found
     */
    public MedicalRecord getRecordById(int id) {
        ValidationHelper.requirePositive(id, "Record ID");
        return recordRepo.findById(id)
                         .orElseThrow(() -> new InvalidDataException("Medical record not found with ID: " + id));
    }

    /**
     * Returns all medical records for a patient, with prescriptions loaded.
     *
     * @throws PatientNotFoundException if the patient does not exist
     */
    public List<MedicalRecord> getRecordsForPatient(int patientId) {
        patientRepo.findById(patientId)
                   .orElseThrow(() -> new PatientNotFoundException(patientId));
        List<MedicalRecord> records = recordRepo.findByPatientId(patientId);
        // Hydrate prescriptions for each record
        records.forEach(r -> r.setPrescriptions(prescriptionRepo.findByMedicalRecordId(r.getId())));
        return records;
    }

    public List<MedicalRecord> getAllRecords() {
        List<MedicalRecord> records = recordRepo.findAll();
        records.forEach(r -> r.setPrescriptions(prescriptionRepo.findByMedicalRecordId(r.getId())));
        return records;
    }

    public void deleteRecord(int id) {
        getRecordById(id);
        recordRepo.deleteById(id);
    }

    // ── Prescriptions ─────────────────────────────────────────────────────────

    /**
     * Adds a prescription to an existing medical record.
     *
     * @throws InvalidDataException if the medical record does not exist
     *                              or inputs are invalid
     */
    public Prescription addPrescription(int medicalRecordId, String medication,
                                         String dosage, String instructions) {
        // Verify the record exists
        getRecordById(medicalRecordId);

        ValidationHelper.requireNonBlank(medication,   "Medication");
        ValidationHelper.requireNonBlank(dosage,       "Dosage");
        ValidationHelper.requireNonBlank(instructions, "Instructions");

        Prescription rx = new Prescription(
            0, medicalRecordId, medication.trim(), dosage.trim(), instructions.trim());
        prescriptionRepo.save(rx);
        return rx;
    }

    public List<Prescription> getPrescriptionsForRecord(int medicalRecordId) {
        getRecordById(medicalRecordId);
        return prescriptionRepo.findByMedicalRecordId(medicalRecordId);
    }

    public List<Prescription> getAllPrescriptions() {
        return prescriptionRepo.findAll();
    }

    public void deletePrescription(int id) {
        ValidationHelper.requirePositive(id, "Prescription ID");
        prescriptionRepo.findById(id)
                        .orElseThrow(() -> new InvalidDataException("Prescription not found with ID: " + id));
        prescriptionRepo.deleteById(id);
    }
}