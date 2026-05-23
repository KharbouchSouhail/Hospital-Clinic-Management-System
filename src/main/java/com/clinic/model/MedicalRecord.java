// File: com/clinic/model/MedicalRecord.java
package com.clinic.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A medical record linked to a patient, containing a diagnosis and
 * an optional list of prescriptions.
 */
public class MedicalRecord {

    private int    id;
    private int    patientId;
    private String description;
    private String diagnosis;
    private String date;          // "yyyy-MM-dd"

    /** Prescriptions are lazily populated by the service layer when needed. */
    private List<Prescription> prescriptions = new ArrayList<>();

    public MedicalRecord() {}

    public MedicalRecord(int id, int patientId, String description,
                          String diagnosis, String date) {
        this.id          = id;
        this.patientId   = patientId;
        this.description = description;
        this.diagnosis   = diagnosis;
        this.date        = date;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int                getId()            { return id; }
    public int                getPatientId()     { return patientId; }
    public String             getDescription()   { return description; }
    public String             getDiagnosis()     { return diagnosis; }
    public String             getDate()          { return date; }
    public List<Prescription> getPrescriptions() { return prescriptions; }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setId(int id)                         { this.id          = id; }
    public void setPatientId(int patientId)           { this.patientId   = patientId; }
    public void setDescription(String description)    { this.description = description; }
    public void setDiagnosis(String diagnosis)        { this.diagnosis   = diagnosis; }
    public void setDate(String date)                  { this.date        = date; }
    public void setPrescriptions(List<Prescription> p){ this.prescriptions = p; }

    public String getDisplayInfo() {
        return String.format(
            "MedRecord [ID=%-4d | PatientID=%-4d | Date=%-12s | Diagnosis=%-20s | Desc=%s]",
            id, patientId, date, diagnosis, description
        );
    }

    @Override
    public String toString() {
        return "MedicalRecord{id=" + id + ", patientId=" + patientId
             + ", date='" + date + "', diagnosis='" + diagnosis + "'}";
    }
}