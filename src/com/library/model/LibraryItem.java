package com.library.model;

import com.library.exception.InvalidInputException;
import com.library.util.Validator;

/**
 * [OOP Concept: Abstraction & Encapsulation]
 * Abstract base class for all catalog items in the library.
 * Declares abstract methods for loan period and fine calculation implemented polymorphically.
 */
public abstract class LibraryItem {
    private int bookId;
    private String title;
    private String author;
    private String isbn;
    private String category;
    private int totalCopies;
    private int availableCopies;

    public LibraryItem(int bookId, String title, String author, String isbn, String category, int totalCopies)
            throws InvalidInputException {
        setBookId(bookId);
        setTitle(title);
        setAuthor(author);
        setIsbn(isbn);
        setCategory(category);
        setTotalCopies(totalCopies);
        this.availableCopies = totalCopies; // availableCopies starts equal to totalCopies
    }

    // [OOP Concept: Polymorphism / Abstraction]
    // Subclasses provide their specific loan periods and fine calculation algorithms
    public abstract int getLoanPeriodDays();
    public abstract double calculateFine(long daysLate);

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) throws InvalidInputException {
        Validator.validateStrictlyPositive(bookId, "Book ID");
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) throws InvalidInputException {
        Validator.validateNonEmpty(title, "Title");
        this.title = title.trim();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) throws InvalidInputException {
        Validator.validateNonEmpty(author, "Author");
        this.author = author.trim();
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) throws InvalidInputException {
        Validator.validateNonEmpty(isbn, "ISBN");
        this.isbn = isbn.trim();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) throws InvalidInputException {
        Validator.validateNonEmpty(category, "Category");
        this.category = category.trim();
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) throws InvalidInputException {
        Validator.validatePositiveOrZero(totalCopies, "Total copies");
        this.totalCopies = totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) throws InvalidInputException {
        Validator.validatePositiveOrZero(availableCopies, "Available copies");
        if (availableCopies > this.totalCopies) {
            throw new InvalidInputException("Available copies cannot exceed total copies.");
        }
        this.availableCopies = availableCopies;
    }

    public abstract String getItemType();
}
