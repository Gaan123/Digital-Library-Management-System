package org.app.dlms.Backend.Model;

import org.app.dlms.Middleware.Enums.UserRole;

import java.util.Date;
import java.util.List;

/**
 * Represents a Member user in the library system.
 * Members can borrow and return books and have an additional membership type attribute.
 */
public class Member extends User {
    private static final UserRole role = UserRole.Member;
    private String membershipType; // Type of membership, e.g., "Student", "Faculty"
    private String membershipStatus; // "Active" or "Inactive"
    private List<Payment> payments; // List of payments made by the member
    /**
     * Constructor for Member, passing common attributes to User and initializing membershipType.
     *
     * @param id             the unique identifier
     * @param username       the username
     * @param password       the password
     * @param name           the full name
     * @param email          the email address
     * @param gender         the gender
     * @param address        the address
     * @param phone          the phone number
//     * @param membershipType the type of membership
     */
    public Member(int id, String username, String password, String name, String email,
                  String gender, String address, String phone
//            , String membershipType,
//                  String membershipStatus, List<Payment> payments
    ) {
        super(id, username, password, name, email, gender, address, phone,role);
//        this.membershipType = membershipType;
//        this.membershipStatus = membershipStatus;
//        this.payments = payments;
    }
    /**
     * Checks if the membership is active based on the most recent payment.
     * Membership is active if a payment was made within the last 30 days.
     */
    public boolean isMembershipActive() {
        if (payments == null || payments.isEmpty()) {
            return false;
        }

        // Find the most recent payment
        Payment latestPayment = payments.stream()
                .max((p1, p2) -> p1.getPaymentDate().compareTo(p2.getPaymentDate()))
                .orElse(null);

        if (latestPayment == null) {
            return false;
        }

        // Check if the latest payment is within the last 30 days
        Date now = new Date();
        long diffInMillies = now.getTime() - latestPayment.getPaymentDate().getTime();
        long diffInDays = diffInMillies / (1000 * 60 * 60 * 24);
        return diffInDays <= 30;
    }
    /**
     * Returns the role of this user as "Member".
     *
     * @return the string "Member"
     */
    @Override
    public UserRole getRole() {
        return UserRole.Member;
    }

    /**
     * Gets the membership type of this member.
     *
     * @return the membership type
     */
    public String getMembershipType() {
        return membershipType;
    }

    /**
     * Sets the membership type of this member.
     *
     * @param membershipType the membership type to set
     */
    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }
    public String getMembershipStatus() { return membershipStatus; }
    public void setMembershipStatus(String membershipStatus) { this.membershipStatus = membershipStatus; }
    public List<Payment> getPayments() { return payments; }
    public void setPayments(List<Payment> payments) { this.payments = payments; }
}