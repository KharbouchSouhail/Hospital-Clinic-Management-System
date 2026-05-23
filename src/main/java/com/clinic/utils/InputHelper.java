// File: com/clinic/utils/InputHelper.java
package com.clinic.utils;

import java.util.Scanner;

/**
 * Thin wrapper around {@link Scanner} that provides safe, prompt-driven
 * input methods for the console UI.
 *
 * <p>A single Scanner is shared for the lifetime of the application to
 * avoid closing {@code System.in} prematurely.
 */
public final class InputHelper {

    private static final Scanner SCANNER = new Scanner(System.in);

    private InputHelper() {}

    // ── Primitive readers ─────────────────────────────────────────────────────

    /**
     * Prints {@code prompt} and returns the trimmed next line.
     * Returns an empty string if the input was blank.
     */
    public static String readString(String prompt) {
        System.out.print(prompt);
        return SCANNER.nextLine().trim();
    }

    /**
     * Reads a non-blank string.  Keeps prompting until a valid value is entered.
     */
    public static String readRequiredString(String prompt) {
        while (true) {
            String value = readString(prompt);
            if (!value.isBlank()) {
                return value;
            }
            System.out.println("  [!] This field cannot be empty. Please try again.");
        }
    }

    /**
     * Reads an integer.  Keeps prompting on non-integer input.
     */
    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = SCANNER.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("  [!] Please enter a valid integer.");
            }
        }
    }

    /**
     * Reads a positive integer (> 0).  Keeps prompting until satisfied.
     */
    public static int readPositiveInt(String prompt) {
        while (true) {
            int value = readInt(prompt);
            if (value > 0) return value;
            System.out.println("  [!] Please enter a number greater than 0.");
        }
    }

    /**
     * Reads an integer in the range [{@code min}, {@code max}].
     */
    public static int readIntInRange(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) return value;
            System.out.printf("  [!] Please enter a number between %d and %d.%n", min, max);
        }
    }

    /**
     * Reads a double.  Keeps prompting on invalid input.
     */
    public static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = SCANNER.nextLine().trim();
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("  [!] Please enter a valid decimal number.");
            }
        }
    }

    // ── Convenience helpers ───────────────────────────────────────────────────

    /**
     * Pauses execution until the user presses Enter.
     */
    public static void pressEnterToContinue() {
        System.out.print("\n  Press [Enter] to continue...");
        SCANNER.nextLine();
    }

    /**
     * Asks the user a yes/no question.
     *
     * @return {@code true} if the user typed 'y' or 'yes' (case-insensitive)
     */
    public static boolean confirm(String prompt) {
        String response = readString(prompt + " (y/n): ").toLowerCase();
        return response.equals("y") || response.equals("yes");
    }
}