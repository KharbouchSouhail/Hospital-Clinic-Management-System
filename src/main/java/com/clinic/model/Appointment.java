// File: com/clinic/model/Appointment.java
package com.clinic.model;

/**
 * Represents a scheduled appointment between a patient and a doctor.
 */
public class Appointment {

    public enum Status {
        SCHEDULED, COMPLETED, CANCELLED
    }

    private int    id;
    private int    patientId;
    private int    doctorId;
    private String dateTime;   // stored as ISO-8601 text: "yyyy-MM-dd HH:mm"
    private Status status;

    public Appointment() {}

    public Appointment(int id, int patientId, int doctorId, String dateTime, Status status) {
        this.id        = id;
        this.patientId = patientId;
        this.doctorId  = doctorId;
        this.dateTime  = dateTime;
        this.status    = status;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int    getId()        { return id; }
    public int    getPatientId() { return patientId; }
    public int    getDoctorId()  { return doctorId; }
    public String getDateTime()  { return dateTime; }
    public Status getStatus()    { return status; }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setId(int id)              { this.id        = id; }
    public void setPatientId(int pid)      { this.patientId = pid; }
    public void setDoctorId(int did)       { this.doctorId  = did; }
    public void setDateTime(String dt)     { this.dateTime  = dt; }
    public void setStatus(Status status)   { this.status    = status; }

    public String getDisplayInfo() {
        return String.format(
            "Appointment[ID=%-4d | PatientID=%-4d | DoctorID=%-4d | DateTime=%-17s | Status=%s]",
            id, patientId, doctorId, dateTime, status
        );
    }

    @Override
    public String toString() {
        return "Appointment{id=" + id + ", patientId=" + patientId
             + ", doctorId=" + doctorId + ", dateTime='" + dateTime
             + "', status=" + status + "}";
    }
}