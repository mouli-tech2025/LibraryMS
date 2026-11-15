package com.library.model;

import com.library.exception.InvalidInputException;

/**
 * [OOP Concept: Inheritance & Polymorphism]
 * Represents a Novel with a 14-day loan period and ₹2.00/day fine.
 */
public class Novel extends LibraryItem {

    public Novel(int bookId, String title, String author, String isbn, String category, int totalCopies)
            throws InvalidInputException {
        super(bookId, title, author, isbn, category, totalCopies);
    }

    @Override
    public int getLoanPeriodDays() {
        return 14; // 14-day loan
    }

    @Override
    public double calculateFine(long daysLate) {
        return daysLate > 0 ? daysLate * 2.00 : 0.00; // Rs 2.00/day fine
    }

    @Override
    public String getItemType() {
        return "Novel";
    }

    @Override
    public String toString() {
        return String.format("Novel [ID: %d | Title: %s | Author: %s | ISBN: %s | Category: %s | Available: %d/%d]",
                getBookId(), getTitle(), getAuthor(), getIsbn(), getCategory(), getAvailableCopies(), getTotalCopies());
    }
}
