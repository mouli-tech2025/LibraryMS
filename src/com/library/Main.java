package com.library;

import com.library.exception.*;
import com.library.model.*;
import com.library.service.LibraryService;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * [OOP Concept: Classes & Objects, Exception Handling]
 * Menu-driven console interface for the BookSphere Library Management System.
 */
public class Main {

    private final LibraryService service;
    private final Scanner scanner;

    public Main(Scanner scanner) {
        this.service = new LibraryService();
        this.scanner = scanner;
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            Main app = new Main(scanner);
            app.run();
        }
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter your choice: ");
            System.out.println();

            switch (choice) {
                case 1:
                    handleAddBook();
                    break;
                case 2:
                    handleViewAllBooks();
                    break;
                case 3:
                    handleSearchBooks();
                    break;
                case 4:
                    handleDeleteBook();
                    break;
                case 5:
                    handleRegisterStudent();
                    break;
                case 6:
                    handleViewAllStudents();
                    break;
                case 7:
                    handleIssueBook();
                    break;
                case 8:
                    handleReturnBook();
                    break;
                case 9:
                    handleViewIssuedBooks();
                    break;
                case 10:
                    handleLibraryStatistics();
                    break;
                case 0:
                    running = false;
                    System.out.println("Thank you for using BookSphere. Goodbye!");
                    break;
                default:
                    System.out.println("✘ Invalid option. Please select a valid number from the menu (0-10).");
            }
            System.out.println();
        }
    }

    private void printMenu() {
        System.out.println("========================================");
        System.out.println("        BOOKSPHERE - LIBRARY SYSTEM     ");
        System.out.println("========================================");
        System.out.println(" 1. Add Book");
        System.out.println(" 2. View All Books");
        System.out.println(" 3. Search Books");
        System.out.println(" 4. Delete Book");
        System.out.println(" 5. Register Student");
        System.out.println(" 6. View All Students");
        System.out.println(" 7. Issue Book");
        System.out.println(" 8. Return Book");
        System.out.println(" 9. View Issued Books");
        System.out.println("10. Library Statistics");
        System.out.println(" 0. Exit");
        System.out.println("----------------------------------------");
    }

    // =========================================================================
    // MENU HANDLERS
    // =========================================================================

    private void handleAddBook() {
        System.out.println("--- Add New Book ---");
        System.out.println("Choose Book Type:");
        System.out.println("  1. TextBook (30-day loan, ₹1.00/day fine)");
        System.out.println("  2. Novel    (14-day loan, ₹2.00/day fine)");
        int typeChoice = readInt("Enter type (1 for TextBook, 2 for Novel): ");
        String type = (typeChoice == 1) ? "TextBook" : (typeChoice == 2 ? "Novel" : "Unknown");

        String title = readLine("Enter Title: ");
        String author = readLine("Enter Author: ");
        String isbn = readLine("Enter ISBN: ");
        String category = readLine("Enter Category: ");
        int totalCopies = readInt("Enter Total Copies: ");

        try {
            LibraryItem item = service.addBook(type, title, author, isbn, category, totalCopies);
            System.out.printf("✔ Book added successfully! Assigned Book ID: %d [%s: %s]%n",
                    item.getBookId(), item.getItemType(), item.getTitle());
        } catch (LibraryException e) {
            System.out.println("✘ Failed to add book: " + e.getMessage());
        }
    }

    private void handleViewAllBooks() {
        System.out.println("--- Library Catalog (All Books) ---");
        List<LibraryItem> books = service.getAllBooks();
        displayBooksTable(books);
    }

    private void handleSearchBooks() {
        System.out.println("--- Search Books ---");
        System.out.println("Search mode:");
        System.out.println("  1. By Keyword (Title, Author, or ISBN)");
        System.out.println("  2. By Keyword and Category Filter");
        int mode = readInt("Choose mode (1-2): ");

        String keyword = readLine("Enter search keyword: ");
        List<LibraryItem> results;
        if (mode == 2) {
            String category = readLine("Enter category filter: ");
            results = service.search(keyword, category);
        } else {
            results = service.search(keyword);
        }

        System.out.printf("%nFound %d matching book(s):%n", results.size());
        displayBooksTable(results);
    }

    private void handleDeleteBook() {
        System.out.println("--- Delete Book ---");
        int bookId = readInt("Enter Book ID to delete: ");
        try {
            service.deleteBook(bookId);
            System.out.printf("✔ Book ID %d deleted successfully from catalog.%n", bookId);
        } catch (LibraryException e) {
            System.out.println("✘ Failed to delete book: " + e.getMessage());
        }
    }

    private void handleRegisterStudent() {
        System.out.println("--- Register New Student ---");
        String name = readLine("Enter Student Name: ");
        String rollNo = readLine("Enter Roll Number (e.g. CS2024-001): ");
        String department = readLine("Enter Department: ");
        String email = readLine("Enter Email (name@domain.ext): ");
        String phone = readLine("Enter 10-digit Phone: ");

        try {
            Student student = service.registerStudent(name, rollNo, department, email, phone);
            System.out.printf("✔ Student registered successfully! Assigned Student ID: %d [%s]%n",
                    student.getStudentId(), student.getName());
        } catch (LibraryException e) {
            System.out.println("✘ Registration failed: " + e.getMessage());
        }
    }

    private void handleViewAllStudents() {
        System.out.println("--- Registered Students ---");
        List<Student> students = service.getAllStudents();
        displayStudentsTable(students);
    }

    private void handleIssueBook() {
        System.out.println("--- Issue Book ---");
        int studentId = readInt("Enter Student ID: ");
        int bookId = readInt("Enter Book ID: ");

        try {
            IssueRecord record = service.issueBook(studentId, bookId);
            System.out.printf("✔ Book issued successfully. Issue ID: %d | Due date: %s%n",
                    record.getIssueId(), record.getDueDate());
        } catch (LibraryException e) {
            System.out.println("✘ Issue failed: " + e.getMessage());
        }
    }

    private void handleReturnBook() {
        System.out.println("--- Return Book ---");
        int issueId = readInt("Enter Issue ID: ");

        try {
            LibraryService.ReturnResult result = service.returnBook(issueId);
            if (result.getDaysLate() > 0) {
                System.out.printf("✔ Book returned. Days late: %d | Fine: ₹%.2f%n",
                        result.getDaysLate(), result.getFine());
            } else {
                System.out.println("✔ Book returned on time. No fine incurred.");
            }
        } catch (LibraryException e) {
            System.out.println("✘ Return failed: " + e.getMessage());
        }
    }

    private void handleViewIssuedBooks() {
        System.out.println("--- Issued Books Status ---");
        System.out.println("View option:");
        System.out.println("  1. Active Issues only");
        System.out.println("  2. All Issue Records (including returned)");
        int opt = readInt("Choose option (1-2): ");

        List<IssueRecord> records = (opt == 2) ? service.getAllIssueRecords() : service.getActiveIssueRecords();
        displayIssueRecordsTable(records);
    }

    private void handleLibraryStatistics() {
        System.out.println("========================================");
        System.out.println("          LIBRARY STATISTICS            ");
        System.out.println("========================================");
        Map<String, Integer> stats = service.getStatistics();
        System.out.printf("%-32s : %s%n", "Metric", "Count");
        System.out.println("----------------------------------------");
        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            System.out.printf("%-32s : %d%n", entry.getKey(), entry.getValue());
        }
        System.out.println("========================================");
    }

    // =========================================================================
    // TABLE RENDERING (String.format / printf)
    // =========================================================================

    private void displayBooksTable(List<LibraryItem> books) {
        if (books.isEmpty()) {
            System.out.println("No books found.");
            return;
        }

        String separator = "+----+----------+-------------------------------------+-----------------------+---------------+----------------------+-----------+";
        System.out.println(separator);
        System.out.printf("| %-2s | %-8s | %-35s | %-21s | %-13s | %-20s | %-9s |%n",
                "ID", "Type", "Title", "Author", "ISBN", "Category", "Copies");
        System.out.println(separator);

        for (LibraryItem b : books) {
            String copiesStr = String.format("%d/%d", b.getAvailableCopies(), b.getTotalCopies());
            System.out.printf("| %-2d | %-8s | %-35s | %-21s | %-13s | %-20s | %-9s |%n",
                    b.getBookId(),
                    truncate(b.getItemType(), 8),
                    truncate(b.getTitle(), 35),
                    truncate(b.getAuthor(), 21),
                    truncate(b.getIsbn(), 13),
                    truncate(b.getCategory(), 20),
                    copiesStr);
        }
        System.out.println(separator);
    }

    private void displayStudentsTable(List<Student> students) {
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }

        String separator = "+----+---------------+----------------------+----------------------+---------------------------+------------+";
        System.out.println(separator);
        System.out.printf("| %-2s | %-13s | %-20s | %-20s | %-25s | %-10s |%n",
                "ID", "Roll No", "Name", "Department", "Email", "Phone");
        System.out.println(separator);

        for (Student s : students) {
            System.out.printf("| %-2d | %-13s | %-20s | %-20s | %-25s | %-10s |%n",
                    s.getStudentId(),
                    truncate(s.getRollNo(), 13),
                    truncate(s.getName(), 20),
                    truncate(s.getDepartment(), 20),
                    truncate(s.getEmail(), 25),
                    truncate(s.getPhone(), 10));
        }
        System.out.println(separator);
    }

    private void displayIssueRecordsTable(List<IssueRecord> records) {
        if (records.isEmpty()) {
            System.out.println("No issue records found.");
            return;
        }

        String separator = "+----+----------+---------+------------+------------+------------+---------+----------+";
        System.out.println(separator);
        System.out.printf("| %-2s | %-8s | %-7s | %-10s | %-10s | %-10s | %-7s | %-8s |%n",
                "ID", "Book ID", "Stud ID", "Issue Date", "Due Date", "Return Date", "Fine (₹)", "Status");
        System.out.println(separator);

        for (IssueRecord r : records) {
            String retDate = (r.getReturnDate() != null) ? r.getReturnDate().toString() : "N/A";
            System.out.printf("| %-2d | %-8d | %-7d | %-10s | %-10s | %-10s | %7.2f | %-8s |%n",
                    r.getIssueId(),
                    r.getBookId(),
                    r.getStudentId(),
                    r.getIssueDate(),
                    r.getDueDate(),
                    retDate,
                    r.getFine(),
                    r.getStatus());
        }
        System.out.println(separator);
    }

    private String truncate(String val, int maxLen) {
        if (val == null) return "";
        if (val.length() <= maxLen) return val;
        return val.substring(0, maxLen - 1) + "…";
    }

    // =========================================================================
    // INPUT HELPERS (Robust input handling without crashing)
    // =========================================================================

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("✘ Invalid input. Please enter a valid integer number.");
            }
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}
