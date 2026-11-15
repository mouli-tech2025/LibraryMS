package com.library.exception;

/**
 * [OOP Concept: Exception Handling - Inheritance]
 * Thrown when a student is not found in the student records.
 */
public class StudentNotFoundException extends LibraryException {
    public StudentNotFoundException(String message) {
        super(message);
    }
}
