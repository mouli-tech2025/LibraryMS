package com.library.model;

import com.library.exception.InvalidInputException;
import com.library.util.Validator;

/**
 * [OOP Concept: Inheritance & Encapsulation]
 * Represents a Student in the library system, inheriting common fields from Person.
 */
public class Student extends Person {
    private int studentId;
    private String rollNo;
    private String department;

    public Student(int studentId, String name, String rollNo, String department, String email, String phone)
            throws InvalidInputException {
        super(name, email, phone);
        setStudentId(studentId);
        setRollNo(rollNo);
        setDepartment(department);
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) throws InvalidInputException {
        Validator.validateStrictlyPositive(studentId, "Student ID");
        this.studentId = studentId;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) throws InvalidInputException {
        Validator.validateNonEmpty(rollNo, "Roll number");
        this.rollNo = rollNo.trim().toUpperCase();
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) throws InvalidInputException {
        Validator.validateNonEmpty(department, "Department");
        this.department = department.trim();
    }

    @Override
    public String toString() {
        return String.format("Student [ID: %d | Roll: %s | Name: %s | Dept: %s | Email: %s | Phone: %s]",
                studentId, rollNo, getName(), department, getEmail(), getPhone());
    }
}
