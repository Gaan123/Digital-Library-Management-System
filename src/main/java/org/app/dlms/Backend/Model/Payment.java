package org.app.dlms.Backend.Model;

import java.util.Date;

public class Payment {
    private int id;
    private int memberId;
    private Date paymentDate;
    private double amount;
    private String type; // "Subscription" or "Fine"
    private String description;
    private int relatedRecordId; // ID of the related fine or subscription

    public Payment(int id, int memberId, Date paymentDate, double amount) {
        this.id = id;
        this.memberId = memberId;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.type = "Subscription"; // Default type
        this.description = "";
        this.relatedRecordId = 0;
    }

    public Payment(int id, int memberId, Date paymentDate, double amount, String type, String description, int relatedRecordId) {
        this.id = id;
        this.memberId = memberId;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.relatedRecordId = relatedRecordId;
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
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getRelatedRecordId() { return relatedRecordId; }
    public void setRelatedRecordId(int relatedRecordId) { this.relatedRecordId = relatedRecordId; }
}