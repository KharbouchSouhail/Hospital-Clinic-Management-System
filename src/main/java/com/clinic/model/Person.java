// File: com/clinic/model/Person.java
package com.clinic.model;

/**
 * Abstract base class representing a person in the clinic system.
 * Both Patient and Doctor extend this class.
 */
public abstract class Person {

    protected int id;
    protected String name;
    protected String phone;

    public Person() {}

    public Person(int id, String name, String phone) {
        this.id    = id;
        this.name  = name;
        this.phone = phone;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int getId()       { return id; }
    public String getName()  { return name; }
    public String getPhone() { return phone; }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setId(int id)          { this.id    = id; }
    public void setName(String name)   { this.name  = name; }
    public void setPhone(String phone) { this.phone = phone; }

    /**
     * Every concrete person type must provide a display summary.
     */
    public abstract String getDisplayInfo();

    @Override
    public String toString() {
        return "Person{id=" + id + ", name='" + name + "', phone='" + phone + "'}";
    }
}