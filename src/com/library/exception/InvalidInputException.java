package com.library.exception;

/**
 * [OOP Concept: Exception Handling - Inheritance]
 * Thrown when user input fails domain validation rules.
 */
public class InvalidInputException extends LibraryException {
    public InvalidInputException(String message) {
        super(message);
    }
}
