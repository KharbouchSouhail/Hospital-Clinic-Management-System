// File: com/clinic/exception/AppointmentNotFoundException.java
package com.clinic.exception;

public class AppointmentNotFoundException extends RuntimeException {
    public AppointmentNotFoundException(String message)              { super(message); }
    public AppointmentNotFoundException(int id)                      { super("Appointment not found with ID: " + id); }
    public AppointmentNotFoundException(String message, Throwable c) { super(message, c); }
}