package com.library.exception;

/**
 * [OOP Concept: Exception Handling]
 * Base checked exception class for all library-related errors.
 */
public class LibraryException extends Exception {
    public LibraryException(String message) {
        super(message);
    }

    public LibraryException(String message, Throwable cause) {
        super(message, cause);
    }
}
