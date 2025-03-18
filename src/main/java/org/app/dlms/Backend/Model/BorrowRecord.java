package org.app.dlms.Backend.Model;

import java.util.Date;

public class BorrowRecord {
    private int id;          // Unique identifier for the borrow record
    private int memberId;    // Foreign key referencing the member who borrowed the book
    private int bookId;      // Foreign key referencing the borrowed book
    private Date borrowDate; // Date when the book was borrowed
    private Date dueDate;    // Date when the book is due to be returned
    private Date returnDate; // Date when the book was actually returned (null if not returned)

    // Constructor
    public BorrowRecord(int id, int memberId, int bookId, Date borrowDate, Date dueDate, Date returnDate) {
        this.id = id;
        this.memberId = memberId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
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

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }
}