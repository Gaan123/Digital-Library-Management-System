package org.app.dlms.Backend.Model;

import org.app.dlms.Middleware.Enums.UserRole;

/**
 * Represents an Admin user in the library system.
 * Admins have full control over the system and inherit all attributes from User.
 */
public class Admin extends User {
    private static final UserRole role = UserRole.Admin;

    /**
     * Constructor for Admin, passing all attributes to the User superclass.
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
    public Admin(int id, String username, String password, String name, String email,
                 String gender, String address, String phone) {
        super(id, username, password, name, email, gender, address, phone,role);
    }

    /**
     * Returns the role of this user as "Admin".
     *
     * @return the string "Admin"
     */
    @Override
    public UserRole getRole() {
        return UserRole.Admin;
    }
}