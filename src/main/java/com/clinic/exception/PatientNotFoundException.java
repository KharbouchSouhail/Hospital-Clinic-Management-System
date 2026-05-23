// File: com/clinic/exception/PatientNotFoundException.java
package com.clinic.exception;

public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException(String message)             { super(message); }
    public PatientNotFoundException(int id)                     { super("Patient not found with ID: " + id); }
    public PatientNotFoundException(String message, Throwable cause) { super(message, cause); }
}