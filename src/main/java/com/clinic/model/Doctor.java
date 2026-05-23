// File: com/clinic/model/Doctor.java
package com.clinic.model;

/**
 * Represents a general doctor working at the clinic.
 */
public class Doctor extends Person {

    protected String specialization;

    public Doctor() {}

    public Doctor(int id, String name, String specialization, String phone) {
        super(id, name, phone);
        this.specialization = specialization;
    }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public String getSpecialization()                       { return specialization; }
    public void   setSpecialization(String specialization)  { this.specialization = specialization; }

    // ── Person contract ──────────────────────────────────────────────────────

    @Override
    public String getDisplayInfo() {
        return String.format(
            "Doctor   [ID=%-4d | Name=%-25s | Specialization=%-20s | Phone=%s]",
            id, name, specialization, phone
        );
    }

    @Override
    public String toString() {
        return "Doctor{id=" + id + ", name='" + name
             + "', specialization='" + specialization + "', phone='" + phone + "'}";
    }
}