// File: com/clinic/utils/DateHelper.java
package com.clinic.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility methods for parsing and formatting dates used throughout the system.
 */
public final class DateHelper {

    public static final DateTimeFormatter DATE_FMT      = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private DateHelper() {}

    /** Returns today's date as  yyyy-MM-dd. */
    public static String today() {
        return LocalDate.now().format(DATE_FMT);
    }

    /** Returns the current date-time as  yyyy-MM-dd HH:mm. */
    public static String now() {
        return LocalDateTime.now().format(DATE_TIME_FMT);
    }

    /** Parses a date string; returns null if it cannot be parsed. */
    public static LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FMT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /** Parses a date-time string; returns null if it cannot be parsed. */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr, DATE_TIME_FMT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /** Formats a {@link LocalDate} as  yyyy-MM-dd. */
    public static String format(LocalDate date) {
        return date == null ? "" : date.format(DATE_FMT);
    }

    /** Formats a {@link LocalDateTime} as  yyyy-MM-dd HH:mm. */
    public static String format(LocalDateTime dateTime) {
        return dateTime == null ? "" : dateTime.format(DATE_TIME_FMT);
    }

    /**
     * Returns true if  dateTime  is strictly in the future compared to now.
     */
    public static boolean isFuture(String dateTime) {
        LocalDateTime parsed = parseDateTime(dateTime);
        return parsed != null && parsed.isAfter(LocalDateTime.now());
    }
}