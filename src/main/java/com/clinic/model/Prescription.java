// File: com/clinic/model/Prescription.java
package com.clinic.model;

/**
 * A prescription linked to a medical record.
 */
public class Prescription {

    private int    id;
    private int    medicalRecordId;
    private String medication;
    private String dosage;
    private String instructions;

    public Prescription() {}

    public Prescription(int id, int medicalRecordId,
                         String medication, String dosage, String instructions) {
        this.id              = id;
        this.medicalRecordId = medicalRecordId;
        this.medication      = medication;
        this.dosage          = dosage;
        this.instructions    = instructions;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int    getId()              { return id; }
    public int    getMedicalRecordId() { return medicalRecordId; }
    public String getMedication()      { return medication; }
    public String getDosage()          { return dosage; }
    public String getInstructions()    { return instructions; }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setId(int id)                        { this.id              = id; }
    public void setMedicalRecordId(int mid)          { this.medicalRecordId = mid; }
    public void setMedication(String medication)     { this.medication      = medication; }
    public void setDosage(String dosage)             { this.dosage          = dosage; }
    public void setInstructions(String instructions) { this.instructions    = instructions; }

    public String getDisplayInfo() {
        return String.format(
            "  Rx [ID=%-4d | RecordID=%-4d | Drug=%-20s | Dose=%-10s | Instructions=%s]",
            id, medicalRecordId, medication, dosage, instructions
        );
    }

    @Override
    public String toString() {
        return "Prescription{id=" + id + ", medicalRecordId=" + medicalRecordId
             + ", medication='" + medication + "', dosage='" + dosage + "'}";
    }
}