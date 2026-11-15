package com.library.model;

import com.library.exception.InvalidInputException;
import com.library.util.Validator;

/**
 * [OOP Concept: Abstraction & Encapsulation]
 * Abstract base class representing a generic Person in the library system.
 * Demonstrates data hiding through private fields and validating getters/setters.
 */
public abstract class Person {
    private String name;
    private String email;
    private String phone;

    public Person(String name, String email, String phone) throws InvalidInputException {
        setName(name);
        setEmail(email);
        setPhone(phone);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws InvalidInputException {
        Validator.validateNonEmpty(name, "Name");
        this.name = name.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) throws InvalidInputException {
        Validator.validateEmail(email);
        this.email = email.trim().toLowerCase();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) throws InvalidInputException {
        Validator.validatePhone(phone);
        this.phone = phone.trim();
    }
}
