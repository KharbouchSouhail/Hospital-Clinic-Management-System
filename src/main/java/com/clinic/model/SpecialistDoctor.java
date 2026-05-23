// File: com/clinic/model/SpecialistDoctor.java
package com.clinic.model;

import com.clinic.interfaces.Payable;

/**
 * A specialist doctor who charges a consultation fee.
 * Extends Doctor and implements Payable to demonstrate polymorphism.
 */
public class SpecialistDoctor extends Doctor implements Payable {

    /** Area of expertise beyond the general specialization (e.g. "Pediatric Cardiology"). */
    private String expertiseArea;

    /** Consultation fee in the local currency unit. */
    private double consultationFee;

    public SpecialistDoctor() {}

    public SpecialistDoctor(int id, String name, String specialization,
                             String phone, String expertiseArea, double consultationFee) {
        super(id, name, specialization, phone);
        this.expertiseArea   = expertiseArea;
        this.consultationFee = consultationFee;
    }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public String getExpertiseArea()               { return expertiseArea; }
    public void   setExpertiseArea(String area)    { this.expertiseArea = area; }

    public double getConsultationFee()             { return consultationFee; }
    public void   setConsultationFee(double fee)   { this.consultationFee = fee; }

    // ── Payable contract ─────────────────────────────────────────────────────

    @Override
    public double calculateCost() {
        return consultationFee;
    }

    @Override
    public String getBillingDescription() {
        return String.format("Specialist consultation with Dr. %s (%s / %s) — Fee: %.2f",
                name, specialization, expertiseArea, consultationFee);
    }

    // ── Person contract ──────────────────────────────────────────────────────

    @Override
    public String getDisplayInfo() {
        return String.format(
            "Specialist[ID=%-4d | Name=%-25s | Spec=%-15s | Expertise=%-20s | Fee=%.2f | Phone=%s]",
            id, name, specialization, expertiseArea, consultationFee, phone
        );
    }

    @Override
    public String toString() {
        return "SpecialistDoctor{id=" + id + ", name='" + name
             + "', specialization='" + specialization
             + "', expertiseArea='" + expertiseArea
             + "', fee=" + consultationFee + ", phone='" + phone + "'}";
    }
}