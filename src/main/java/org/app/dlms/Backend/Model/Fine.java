package org.app.dlms.Backend.Model;

public class Fine {
    private int id;             // Unique identifier for the fine
    private int memberId;       // Foreign key referencing the member who incurred the fine
    private int borrowRecordId; // Foreign key referencing the associated borrow record
    private double amount;      // The fine amount
    private boolean paid;       // Whether the fine has been paid

    // Constructor
    public Fine(int id, int memberId, int borrowRecordId, double amount, boolean paid) {
        this.id = id;
        this.memberId = memberId;
        this.borrowRecordId = borrowRecordId;
        this.amount = amount;
        this.paid = paid;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getBorrowRecordId() {
        return borrowRecordId;
    }

    public void setBorrowRecordId(int borrowRecordId) {
        this.borrowRecordId = borrowRecordId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }
}