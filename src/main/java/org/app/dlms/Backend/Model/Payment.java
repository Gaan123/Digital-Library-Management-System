package org.app.dlms.Backend.Model;

import java.util.Date;

public class Payment {
    private int id;
    private int memberId;
    private Date paymentDate;
    private double amount;

    public Payment(int id, int memberId, Date paymentDate, double amount) {
        this.id = id;
        this.memberId = memberId;
        this.paymentDate = paymentDate;
        this.amount = amount;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }
    public Date getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Date paymentDate) { this.paymentDate = paymentDate; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}