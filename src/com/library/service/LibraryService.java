package com.library.service;

import com.library.exception.*;
import com.library.model.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * [OOP Concept: Abstraction, Polymorphism, Collections Framework]
 * Core service holding all in-memory data structures (ArrayList, HashMap)
 * and business logic for book management, student registration, issuing, and returns.
 */
public class LibraryService implements Searchable<LibraryItem> {

    // [OOP Concept: Collections Framework]
    private final List<LibraryItem> books = new ArrayList<>();
    private final List<Student> students = new ArrayList<>();
    private final List<IssueRecord> issueRecords = new ArrayList<>();

    // Lookup caches for fast access and unique constraint checks
    private final Map<Integer, LibraryItem> bookMap = new HashMap<>();
    private final Map<Integer, Student> studentMap = new HashMap<>();
    private final Map<Integer, IssueRecord> issueMap = new HashMap<>();

    // Auto-increment IDs
    private int nextBookId = 1;
    private int nextStudentId = 1;
    private int nextIssueId = 1;

    public LibraryService() {
        loadSampleData();
    }

    // =========================================================================
    // BOOK MANAGEMENT
    // =========================================================================

    /**
     * Adds a new book to the library catalog.
     * Checks for unique ISBN and initializes available copies equal to total copies.
     */
    public synchronized LibraryItem addBook(String type, String title, String author, String isbn,
                                            String category, int totalCopies)
            throws DuplicateEntryException, InvalidInputException {

        // Validate ISBN uniqueness
        for (LibraryItem item : books) {
            if (item.getIsbn().equalsIgnoreCase(isbn.trim())) {
                throw new DuplicateEntryException("A book with ISBN '" + isbn.trim() + "' already exists.");
            }
        }

        int bookId = nextBookId++;
        LibraryItem newItem;

        if ("TextBook".equalsIgnoreCase(type) || "Textbook".equalsIgnoreCase(type) || "1".equals(type)) {
            newItem = new TextBook(bookId, title, author, isbn, category, totalCopies);
        } else if ("Novel".equalsIgnoreCase(type) || "2".equals(type)) {
            newItem = new Novel(bookId, title, author, isbn, category, totalCopies);
        } else {
            throw new InvalidInputException("Invalid book type. Please choose TextBook or Novel.");
        }

        books.add(newItem);
        bookMap.put(bookId, newItem);
        return newItem;
    }

    /**
     * Deletes a book by ID.
     * Blocked if any active (ISSUED) record exists for it.
     */
    public synchronized void deleteBook(int bookId) throws BookNotFoundException, LibraryException {
        LibraryItem item = bookMap.get(bookId);
        if (item == null) {
            throw new BookNotFoundException("Book with ID " + bookId + " not found.");
        }

        // Check if currently issued
        boolean hasActiveIssue = issueRecords.stream()
                .anyMatch(record -> record.getBookId() == bookId && record.isIssued());
        if (hasActiveIssue) {
            throw new LibraryException("Cannot delete book ID " + bookId + " ('" + item.getTitle()
                    + "') because copies are currently issued to students.");
        }

        books.remove(item);
        bookMap.remove(bookId);
    }

    /**
     * Returns all books sorted by title using Comparator.comparing(LibraryItem::getTitle).
     */
    public List<LibraryItem> getAllBooks() {
        return books.stream()
                .sorted(Comparator.comparing(LibraryItem::getTitle, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public LibraryItem getBookById(int bookId) throws BookNotFoundException {
        LibraryItem item = bookMap.get(bookId);
        if (item == null) {
            throw new BookNotFoundException("Book with ID " + bookId + " not found.");
        }
        return item;
    }

    // =========================================================================
    // SEARCH (Overloading & Interface Implementation)
    // =========================================================================

    /**
     * [OOP Concept: Abstraction (Searchable) & Compile-Time Polymorphism (Overloading)]
     * Searches books matching title, author, or ISBN (case-insensitive).
     */
    @Override
    public List<LibraryItem> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllBooks();
        }
        String lower = keyword.trim().toLowerCase();
        return books.stream()
                .filter(b -> b.getTitle().toLowerCase().contains(lower)
                        || b.getAuthor().toLowerCase().contains(lower)
                        || b.getIsbn().toLowerCase().contains(lower))
                .sorted(Comparator.comparing(LibraryItem::getTitle, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    /**
     * [OOP Concept: Compile-Time Polymorphism (Method Overloading)]
     * Searches books matching keyword in title/author/ISBN and matching specific category.
     */
    public List<LibraryItem> search(String keyword, String category) {
        String catLower = (category != null) ? category.trim().toLowerCase() : "";
        List<LibraryItem> baseResults = search(keyword);

        if (catLower.isEmpty()) {
            return baseResults;
        }

        return baseResults.stream()
                .filter(b -> b.getCategory().toLowerCase().contains(catLower))
                .collect(Collectors.toList());
    }

    // =========================================================================
    // STUDENT MANAGEMENT
    // =========================================================================

    /**
     * Registers a student with unique roll number and unique email checks.
     */
    public synchronized Student registerStudent(String name, String rollNo, String department,
                                                String email, String phone)
            throws DuplicateEntryException, InvalidInputException {

        String trimmedRoll = rollNo != null ? rollNo.trim().toUpperCase() : "";
        String trimmedEmail = email != null ? email.trim().toLowerCase() : "";

        // Uniqueness checks
        for (Student s : students) {
            if (s.getRollNo().equalsIgnoreCase(trimmedRoll)) {
                throw new DuplicateEntryException("Student with roll number '" + trimmedRoll + "' is already registered.");
            }
            if (s.getEmail().equalsIgnoreCase(trimmedEmail)) {
                throw new DuplicateEntryException("Student with email '" + trimmedEmail + "' is already registered.");
            }
        }

        int studentId = nextStudentId++;
        Student student = new Student(studentId, name, trimmedRoll, department, trimmedEmail, phone);
        students.add(student);
        studentMap.put(studentId, student);
        return student;
    }

    public List<Student> getAllStudents() {
        return students.stream()
                .sorted(Comparator.comparing(Student::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public Student getStudentById(int studentId) throws StudentNotFoundException {
        Student s = studentMap.get(studentId);
        if (s == null) {
            throw new StudentNotFoundException("Student with ID " + studentId + " not found.");
        }
        return s;
    }

    // =========================================================================
    // ISSUE & RETURN OPERATIONS
    // =========================================================================

    /**
     * Issues a book to a student adhering to all business rules:
     * - Student must exist
     * - Book must exist
     * - availableCopies > 0
     * - Student must not already hold the same book
     * - Student can have at most 3 active loans
     * - Due date = today + item.getLoanPeriodDays() (polymorphic)
     * - Decrement availableCopies
     */
    public synchronized IssueRecord issueBook(int studentId, int bookId)
            throws StudentNotFoundException, BookNotFoundException, BookNotAvailableException, LibraryException {

        Student student = getStudentById(studentId);
        LibraryItem item = getBookById(bookId);

        // Check availability
        if (item.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException("Book '" + item.getTitle() + "' has no copies currently available.");
        }

        // Active loans by this student
        List<IssueRecord> activeLoans = issueRecords.stream()
                .filter(r -> r.getStudentId() == studentId && r.isIssued())
                .collect(Collectors.toList());

        // Max 3 loans
        if (activeLoans.size() >= 3) {
            throw new LibraryException("Student " + student.getName() + " already has maximum allowed active loans (3). Return a book before borrowing another.");
        }

        // Check if student already holds the same book
        boolean alreadyHolds = activeLoans.stream()
                .anyMatch(r -> r.getBookId() == bookId);
        if (alreadyHolds) {
            throw new LibraryException("Student " + student.getName() + " already holds an active copy of book '" + item.getTitle() + "'.");
        }

        // Calculate due date using item's polymorphic loan period
        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(item.getLoanPeriodDays());

        // Decrement available copies
        item.setAvailableCopies(item.getAvailableCopies() - 1);

        int issueId = nextIssueId++;
        IssueRecord record = new IssueRecord(issueId, bookId, studentId, issueDate, dueDate);
        issueRecords.add(record);
        issueMap.put(issueId, record);

        return record;
    }

    /**
     * Returns a book by Issue ID.
     * [OOP Concept: Runtime Polymorphism]
     * Fine is calculated via item.calculateFine(daysLate) without instanceof.
     * Days late is computed between due date and today using ChronoUnit.DAYS.
     * Increments availableCopies, sets return date, fine, and status RETURNED.
     */
    public synchronized ReturnResult returnBook(int issueId) throws LibraryException {
        IssueRecord record = issueMap.get(issueId);
        if (record == null) {
            throw new LibraryException("Issue record with ID " + issueId + " not found.");
        }

        if (!record.isIssued()) {
            throw new LibraryException("This book has already been returned on " + record.getReturnDate() + ".");
        }

        LibraryItem item = bookMap.get(record.getBookId());
        if (item == null) {
            throw new BookNotFoundException("Associated book ID " + record.getBookId() + " not found in catalog.");
        }

        LocalDate returnDate = LocalDate.now();
        record.setReturnDate(returnDate);

        // Calculate days late: positive if today is past dueDate
        long daysLate = ChronoUnit.DAYS.between(record.getDueDate(), returnDate);
        if (daysLate < 0) {
            daysLate = 0;
        }

        // Polymorphic fine calculation (no instanceof!)
        double fine = item.calculateFine(daysLate);

        record.setFine(fine);
        record.setStatus(IssueRecord.Status.RETURNED);

        // Increase available copies
        item.setAvailableCopies(item.getAvailableCopies() + 1);

        return new ReturnResult(record, item, daysLate, fine);
    }

    public List<IssueRecord> getAllIssueRecords() {
        return Collections.unmodifiableList(issueRecords);
    }

    public List<IssueRecord> getActiveIssueRecords() {
        return issueRecords.stream()
                .filter(IssueRecord::isIssued)
                .collect(Collectors.toList());
    }

    // =========================================================================
    // STATISTICS
    // =========================================================================

    /**
     * [OOP Concept: Collections Framework]
     * Computes library statistics and returns them in a LinkedHashMap to preserve order.
     */
    public Map<String, Integer> getStatistics() {
        Map<String, Integer> stats = new LinkedHashMap<>();

        int totalBooks = books.size();
        int totalCopies = books.stream().mapToInt(LibraryItem::getTotalCopies).sum();
        int availableCopies = books.stream().mapToInt(LibraryItem::getAvailableCopies).sum();
        int activeIssues = (int) issueRecords.stream().filter(IssueRecord::isIssued).count();
        int registeredStudents = students.size();

        stats.put("Total Books (Titles)", totalBooks);
        stats.put("Total Copies Across Catalog", totalCopies);
        stats.put("Available Copies", availableCopies);
        stats.put("Active Issues", activeIssues);
        stats.put("Registered Students", registeredStudents);

        return stats;
    }

    // =========================================================================
    // SAMPLE DATA INITIALIZATION
    // =========================================================================

    /**
     * Loads sample data at startup:
     * - 6+ Students
     * - 10+ Books (mix of TextBook and Novel, different categories)
     * - 3 Issue Records (at least one overdue so fine feature can be demonstrated)
     */
    private void loadSampleData() {
        try {
            // Register Students
            registerStudent("Aarav Sharma", "CS2024-001", "Computer Science", "aarav.sharma@univ.edu", "9876543210");
            registerStudent("Ananya Iyer", "CS2024-002", "Computer Science", "ananya.iyer@univ.edu", "9876543211");
            registerStudent("Rohan Verma", "EC2024-015", "Electronics", "rohan.verma@univ.edu", "9876543212");
            registerStudent("Sneha Patel", "IT2024-008", "Information Tech", "sneha.patel@univ.edu", "9876543213");
            registerStudent("Vikram Singh", "ME2024-022", "Mechanical", "vikram.singh@univ.edu", "9876543214");
            registerStudent("Pooja Nair", "CV2024-005", "Civil Eng", "pooja.nair@univ.edu", "9876543215");

            // Add Books (Textbooks & Novels across various categories)
            addBook("TextBook", "Core Java: Fundamentals", "Cay S. Horstmann", "978-0135166307", "Computer Science", 4);
            addBook("TextBook", "Introduction to Algorithms (CLRS)", "Thomas H. Cormen", "978-0262046305", "Algorithms", 3);
            addBook("Novel", "To Kill a Mockingbird", "Harper Lee", "978-0061120084", "Classic Fiction", 2);
            addBook("TextBook", "Clean Code: Agile Software Craftsmanship", "Robert C. Martin", "978-0132350884", "Software Engineering", 3);
            addBook("Novel", "1984", "George Orwell", "978-0451524935", "Dystopian Fiction", 3);
            addBook("TextBook", "Operating System Concepts", "Abraham Silberschatz", "978-1119800361", "Computer Science", 2);
            addBook("Novel", "The Great Gatsby", "F. Scott Fitzgerald", "978-0743273565", "Classic Literature", 2);
            addBook("TextBook", "Artificial Intelligence: A Modern Approach", "Stuart Russell", "978-0134610993", "Artificial Intelligence", 3);
            addBook("Novel", "The Alchemist", "Paulo Coelho", "978-0062315007", "Philosophical Fiction", 4);
            addBook("TextBook", "Database System Concepts", "Abraham Silberschatz", "978-0078022159", "Database Systems", 3);

            // Pre-seed 3 Issue records:
            // Issue 1: OVERDUE Novel issued to student 1 (Aarav). Issued 18 days ago, Novel loan is 14 days, so 4 days late!
            LocalDate today = LocalDate.now();
            LibraryItem novel1 = getBookById(5); // 1984
            novel1.setAvailableCopies(novel1.getAvailableCopies() - 1);
            IssueRecord issue1 = new IssueRecord(nextIssueId++, novel1.getBookId(), 1,
                    today.minusDays(18), today.minusDays(4));
            issueRecords.add(issue1);
            issueMap.put(issue1.getIssueId(), issue1);

            // Issue 2: Active on-time Textbook issued to student 2 (Ananya). Issued 5 days ago, due in 25 days.
            LibraryItem textbook1 = getBookById(1); // Core Java
            textbook1.setAvailableCopies(textbook1.getAvailableCopies() - 1);
            IssueRecord issue2 = new IssueRecord(nextIssueId++, textbook1.getBookId(), 2,
                    today.minusDays(5), today.minusDays(5).plusDays(textbook1.getLoanPeriodDays()));
            issueRecords.add(issue2);
            issueMap.put(issue2.getIssueId(), issue2);

            // Issue 3: Already Returned record for student 3 (Rohan)
            LibraryItem novel2 = getBookById(3); // To Kill a Mockingbird
            // Returned on time, so available copies is back to full
            IssueRecord issue3 = new IssueRecord(nextIssueId++, novel2.getBookId(), 3,
                    today.minusDays(20), today.minusDays(6), today.minusDays(7), 0.0, IssueRecord.Status.RETURNED);
            issueRecords.add(issue3);
            issueMap.put(issue3.getIssueId(), issue3);

        } catch (LibraryException e) {
            System.err.println("Failed to initialize sample data: " + e.getMessage());
        }
    }

    /**
     * Helper record to return return-operation details.
     */
    public static class ReturnResult {
        private final IssueRecord record;
        private final LibraryItem item;
        private final long daysLate;
        private final double fine;

        public ReturnResult(IssueRecord record, LibraryItem item, long daysLate, double fine) {
            this.record = record;
            this.item = item;
            this.daysLate = daysLate;
            this.fine = fine;
        }

        public IssueRecord getRecord() {
            return record;
        }

        public LibraryItem getItem() {
            return item;
        }

        public long getDaysLate() {
            return daysLate;
        }

        public double getFine() {
            return fine;
        }
    }
}
