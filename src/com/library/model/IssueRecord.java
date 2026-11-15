package com.library.model;

import java.time.LocalDate;

/**
 * [OOP Concept: Classes & Objects, Encapsulation]
 * Represents an issue/loan transaction linking a Student and a LibraryItem.
 */
public class IssueRecord {
    public enum Status {
        ISSUED,
        RETURNED
    }

    private int issueId;
    private int bookId;
    private int studentId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private double fine;
    private Status status;

    public IssueRecord(int issueId, int bookId, int studentId, LocalDate issueDate, LocalDate dueDate) {
        this.issueId = issueId;
        this.bookId = bookId;
        this.studentId = studentId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = null;
        this.fine = 0.0;
        this.status = Status.ISSUED;
    }

    // Overloaded constructor for historical/sample data where return details are known
    public IssueRecord(int issueId, int bookId, int studentId, LocalDate issueDate, LocalDate dueDate,
                       LocalDate returnDate, double fine, Status status) {
        this.issueId = issueId;
        this.bookId = bookId;
        this.studentId = studentId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fine = fine;
        this.status = status;
    }

    public int getIssueId() {
        return issueId;
    }

    public void setIssueId(int issueId) {
        this.issueId = issueId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public double getFine() {
        return fine;
    }

    public void setFine(double fine) {
        this.fine = fine;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isIssued() {
        return this.status == Status.ISSUED;
    }

    @Override
    public String toString() {
        return String.format("IssueRecord [IssueID: %d | BookID: %d | StudentID: %d | Issued: %s | Due: %s | Returned: %s | Fine: ₹%.2f | Status: %s]",
                issueId, bookId, studentId, issueDate, dueDate,
                returnDate != null ? returnDate.toString() : "N/A", fine, status);
    }
}
