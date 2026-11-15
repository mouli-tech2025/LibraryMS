package com.library.exception;

/**
 * [OOP Concept: Exception Handling - Inheritance]
 * Thrown when an issue is attempted for a book that has zero available copies.
 */
public class BookNotAvailableException extends LibraryException {
    public BookNotAvailableException(String message) {
        super(message);
    }
}
