// File: com/clinic/model/Patient.java
package com.clinic.model;

/**
 * Represents a patient registered in the clinic.
 */
public class Patient extends Person {

    private int    age;
    private String email;

    public Patient() {}

    public Patient(int id, String name, int age, String phone, String email) {
        super(id, name, phone);
        this.age   = age;
        this.email = email;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int    getAge()   { return age; }
    public String getEmail() { return email; }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setAge(int age)        { this.age   = age; }
    public void setEmail(String email) { this.email = email; }

    // ── Person contract ──────────────────────────────────────────────────────

    @Override
    public String getDisplayInfo() {
        return String.format(
            "Patient  [ID=%-4d | Name=%-25s | Age=%-3d | Phone=%-15s | Email=%s]",
            id, name, age, phone, email
        );
    }

    @Override
    public String toString() {
        return "Patient{id=" + id + ", name='" + name + "', age=" + age
             + ", phone='" + phone + "', email='" + email + "'}";
    }
}