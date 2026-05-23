// File: com/clinic/exception/InvalidDataException.java
package com.clinic.exception;

public class InvalidDataException extends RuntimeException {
    public InvalidDataException(String message)              { super(message); }
    public InvalidDataException(String message, Throwable c) { super(message, c); }
}