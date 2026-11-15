package com.library.model;

import com.library.exception.InvalidInputException;

/**
 * [OOP Concept: Inheritance & Polymorphism]
 * Represents an academic Textbook with a 30-day loan period and ₹1.00/day fine.
 */
public class TextBook extends LibraryItem {

    public TextBook(int bookId, String title, String author, String isbn, String category, int totalCopies)
            throws InvalidInputException {
        super(bookId, title, author, isbn, category, totalCopies);
    }

    @Override
    public int getLoanPeriodDays() {
        return 30; // 30-day loan
    }

    @Override
    public double calculateFine(long daysLate) {
        return daysLate > 0 ? daysLate * 1.00 : 0.00; // Rs 1.00/day fine
    }

    @Override
    public String getItemType() {
        return "Textbook";
    }

    @Override
    public String toString() {
        return String.format("TextBook [ID: %d | Title: %s | Author: %s | ISBN: %s | Category: %s | Available: %d/%d]",
                getBookId(), getTitle(), getAuthor(), getIsbn(), getCategory(), getAvailableCopies(), getTotalCopies());
    }
}
