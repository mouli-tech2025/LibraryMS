package com.library.exception;

/**
 * [OOP Concept: Exception Handling - Inheritance]
 * Thrown when attempting to add a record with a duplicate unique field (e.g. ISBN, roll number, email).
 */
public class DuplicateEntryException extends LibraryException {
    public DuplicateEntryException(String message) {
        super(message);
    }
}
