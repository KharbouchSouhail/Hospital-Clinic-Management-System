// File: com/clinic/exception/DoctorNotFoundException.java
package com.clinic.exception;

public class DoctorNotFoundException extends RuntimeException {
    public DoctorNotFoundException(String message)              { super(message); }
    public DoctorNotFoundException(int id)                      { super("Doctor not found with ID: " + id); }
    public DoctorNotFoundException(String message, Throwable c) { super(message, c); }
}