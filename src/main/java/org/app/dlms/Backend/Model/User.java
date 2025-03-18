package org.app.dlms.Backend.Model;

import org.app.dlms.Middleware.Enums.UserRole;

/**
 * Abstract base class representing a user in the library system.
 * Contains common attributes for all user types and cannot be instantiated directly.
 */
public  class User {
    private int id;
    private String username;
    private String password;
    private String name;
    private String email;
    private String gender;
    private String address;
    private String phone;
    private UserRole role;

    public User() {}

    /**
     * Constructor to initialize a User object with all common attributes.
     *
     * @param id       the unique identifier
     * @param username the username
     * @param password the password
     * @param name     the full name
     * @param email    the email address
     * @param gender   the gender
     * @param address  the address
     * @param phone    the phone number
     */
    public User(int id, String username, String password, String name, String email,
                String gender, String address, String phone, UserRole role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.address = address;
        this.phone = phone;
        this.role = role;
    }

    // Getters and setters for all attributes

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserRole getRole() {
        return role;
    }
//
//    public void setRole(String phone) {
//        this.phone = phone;
//    }

    /**
     * Abstract method to get the role of the user.
     * Must be implemented by subclasses to specify their role.
     *
     * @return the role as a String
     */
//    public abstract String getRole();
}