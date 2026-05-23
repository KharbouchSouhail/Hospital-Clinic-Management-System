// File: com/clinic/utils/ValidationHelper.java
package com.clinic.utils;

import com.clinic.exception.InvalidDataException;

/**
 * Centralises all input-validation logic used by the service layer.
 * All methods throw {@link InvalidDataException} on failure.
 */
public final class ValidationHelper {

    private ValidationHelper() {}

    // ── String validators ─────────────────────────────────────────────────────

    public static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new InvalidDataException(fieldName + " must not be blank.");
        }
    }

    public static void requireMaxLength(String value, int max, String fieldName) {
        if (value != null && value.length() > max) {
            throw new InvalidDataException(
                fieldName + " must not exceed " + max + " characters (got " + value.length() + ").");
        }
    }

    // ── Numeric validators ────────────────────────────────────────────────────

    public static void requirePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new InvalidDataException(fieldName + " must be a positive integer (got " + value + ").");
        }
    }

    public static void requirePositive(double value, String fieldName) {
        if (value < 0) {
            throw new InvalidDataException(fieldName + " must be >= 0 (got " + value + ").");
        }
    }

    public static void requireAgeRange(int age) {
        if (age < 0 || age > 150) {
            throw new InvalidDataException("Age must be between 0 and 150 (got " + age + ").");
        }
    }

    // ── Email validator ───────────────────────────────────────────────────────

    public static void requireValidEmail(String email) {
        requireNonBlank(email, "Email");
        // Simple regex: must contain '@' and at least one '.' after it
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new InvalidDataException("Email is not valid: " + email);
        }
    }

    // ── Phone validator ───────────────────────────────────────────────────────

    public static void requireValidPhone(String phone) {
        requireNonBlank(phone, "Phone");
        // Accepts digits, spaces, +, -, (, )
        if (!phone.matches("^[\\d\\s+\\-()/]{6,20}$")) {
            throw new InvalidDataException("Phone number is not valid: " + phone);
        }
    }

    // ── DateTime validator ────────────────────────────────────────────────────

    /**
     * Validates that dateTime follows the pattern  yyyy-MM-dd HH:mm.
     */
    public static void requireValidDateTime(String dateTime) {
        requireNonBlank(dateTime, "Date/Time");
        if (!dateTime.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$")) {
            throw new InvalidDataException(
                "Date/Time must be in the format yyyy-MM-dd HH:mm (got: " + dateTime + ").");
        }
    }

    /**
     * Validates that date follows the pattern  yyyy-MM-dd.
     */
    public static void requireValidDate(String date) {
        requireNonBlank(date, "Date");
        if (!date.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            throw new InvalidDataException(
                "Date must be in the format yyyy-MM-dd (got: " + date + ").");
        }
    }
}