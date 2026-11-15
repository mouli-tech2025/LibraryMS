package com.library.util;

import com.library.exception.InvalidInputException;
import java.util.regex.Pattern;

/**
 * Utility class providing common validation routines for user inputs.
 */
public class Validator {

    // Regex for 10-digit Indian/standard mobile number format
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10}$");

    // Standard email validation regex: name@domain.ext
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private Validator() {
        // Prevent instantiation of utility class
    }

    /**
     * Validates that a string is not null or blank.
     */
    public static void validateNonEmpty(String value, String fieldName) throws InvalidInputException {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidInputException(fieldName + " cannot be empty.");
        }
    }

    /**
     * Validates a 10-digit phone number.
     */
    public static void validatePhone(String phone) throws InvalidInputException {
        validateNonEmpty(phone, "Phone number");
        if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
            throw new InvalidInputException("Invalid phone number: must be exactly 10 digits.");
        }
    }

    /**
     * Validates an email address format (name@domain.ext).
     */
    public static void validateEmail(String email) throws InvalidInputException {
        validateNonEmpty(email, "Email");
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new InvalidInputException("Invalid email format: must be in name@domain.ext format.");
        }
    }

    /**
     * Validates a positive integer or zero.
     */
    public static void validatePositiveOrZero(int value, String fieldName) throws InvalidInputException {
        if (value < 0) {
            throw new InvalidInputException(fieldName + " cannot be negative.");
        }
    }

    /**
     * Validates a strictly positive integer (> 0).
     */
    public static void validateStrictlyPositive(int value, String fieldName) throws InvalidInputException {
        if (value <= 0) {
            throw new InvalidInputException(fieldName + " must be greater than zero.");
        }
    }
}
