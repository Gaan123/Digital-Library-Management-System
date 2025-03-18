package org.app.dlms.Backend.Model;

import org.app.dlms.Middleware.Enums.UserRole;

/**
 * Represents a Librarian user in the library system.
 * Librarians manage books and transactions, inheriting attributes from User.
 */
public class Librarian extends User {
    private static final UserRole role = UserRole.Librarian;

    /**
     * Constructor for Librarian, passing all attributes to the User superclass.
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
    public Librarian(int id, String username, String password, String name, String email,
                     String gender, String address, String phone) {
        super(id, username, password, name, email, gender, address, phone,role);
    }

    /**
     * Returns the role of this user as "Librarian".
     *
     * @return the string "Librarian"
     */
    @Override
    public UserRole getRole() {
        return UserRole.Librarian;
    }
}