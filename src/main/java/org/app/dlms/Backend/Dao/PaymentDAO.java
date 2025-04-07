package org.app.dlms.Backend.Dao;

import org.app.dlms.Backend.Model.Payment;
import org.app.dlms.Middleware.DatabaseConnection;
import org.app.dlms.Middleware.Services.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Data Access Object for payment-related operations
 */
public class PaymentDAO {
    private DatabaseConnection dbConnection;

    public PaymentDAO() {
        dbConnection = DatabaseConnection.getInstance();
    }
    /**
     * Add a new payment record
     * 
     * @param payment The payment to add
     * @return The ID of the newly added payment, or -1 if operation fails
     */
    public int addPayment(Payment payment) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int generatedId = -1;
        
        try {
            conn = dbConnection.getConnection();
            String sql = "INSERT INTO payments (member_id, payment_date, amount) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            
            stmt.setInt(1, payment.getMemberId());
            stmt.setDate(2, new java.sql.Date(payment.getPaymentDate().getTime()));
            stmt.setDouble(3, payment.getAmount());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                    payment.setId(generatedId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding payment for member ID: " + payment.getMemberId());
            e.printStackTrace();
        } finally {
            dbConnection.closeResources(conn, stmt, rs);
        }
        
        return generatedId;
    }
    
    /**
     * Get all payments for a specific member
     * 
     * @param memberId The ID of the member
     * @return List of payment records for the member
     */
    public List<Payment> getPaymentsByMemberId(int memberId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Payment> payments = new ArrayList<>();
        
        try {
            conn = dbConnection.getConnection();
            String sql = "SELECT * FROM payments WHERE member_id = ? ORDER BY payment_date DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, memberId);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Payment payment = new Payment(
                    rs.getInt("id"),
                    rs.getInt("member_id"),
                    new Date(rs.getTimestamp("payment_date").getTime()),
                    rs.getDouble("amount")
                );
                
                payments.add(payment);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving payments for member ID: " + memberId);
            e.printStackTrace();
        } finally {
            dbConnection.closeResources(conn, stmt, rs);
        }
        
        return payments;
    }
    
    /**
     * Get payment by ID
     * 
     * @param paymentId The ID of the payment
     * @return The payment record or null if not found
     */
    public Payment getPaymentById(int paymentId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Payment payment = null;
        
        try {
            conn = dbConnection.getConnection();
            String sql = "SELECT * FROM payments WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, paymentId);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                payment = new Payment(
                    rs.getInt("id"),
                    rs.getInt("member_id"),
                    new Date(rs.getTimestamp("payment_date").getTime()),
                    rs.getDouble("amount")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving payment with ID: " + paymentId);
            e.printStackTrace();
        } finally {
            dbConnection.closeResources(conn, stmt, rs);
        }
        
        return payment;
    }
    
    /**
     * Get all payments in a date range
     * 
     * @param startDate The start date of the range
     * @param endDate The end date of the range
     * @return List of payment records in the date range
     */
    public List<Payment> getPaymentsByDateRange(Date startDate, Date endDate) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Payment> payments = new ArrayList<>();
        
        try {
            conn = dbConnection.getConnection();
            String sql = "SELECT * FROM payments WHERE payment_date BETWEEN ? AND ? ORDER BY payment_date";
            stmt = conn.prepareStatement(sql);
            stmt.setDate(1, new java.sql.Date(startDate.getTime()));
            stmt.setDate(2, new java.sql.Date(endDate.getTime()));
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Payment payment = new Payment(
                    rs.getInt("id"),
                    rs.getInt("member_id"),
                    new Date(rs.getTimestamp("payment_date").getTime()),
                    rs.getDouble("amount")
                );
                
                payments.add(payment);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving payments in date range");
            e.printStackTrace();
        } finally {
            dbConnection.closeResources(conn, stmt, rs);
        }
        
        return payments;
    }
    
    /**
     * Get total payment amount for a specific member
     * 
     * @param memberId The ID of the member
     * @return Total payment amount
     */
    public double getTotalPaymentsByMemberId(int memberId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        double totalAmount = 0.0;
        
        try {
            conn = dbConnection.getConnection();
            String sql = "SELECT SUM(amount) as total FROM payments WHERE member_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, memberId);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                totalAmount = rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Error calculating total payments for member ID: " + memberId);
            e.printStackTrace();
        } finally {
            dbConnection.closeResources(conn, stmt, rs);
        }
        
        return totalAmount;
    }

    /**
     * Get total payment amount across all members
     * 
     * @return Total payment amount
     */
    public double getTotalPayments() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        double totalAmount = 0.0;
        
        try {
            conn = dbConnection.getConnection();
            String sql = "SELECT SUM(amount) as total FROM payments";
            stmt = conn.prepareStatement(sql);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                totalAmount = rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Error calculating total payments");
            e.printStackTrace();
        } finally {
            dbConnection.closeResources(conn, stmt, rs);
        }
        
        return totalAmount;
    }

    /**
     * Get all payments in the system
     * 
     * @return List of all payment records
     */
    public List<Payment> getAllPayments() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Payment> payments = new ArrayList<>();
        
        try {
            conn = dbConnection.getConnection();
            String sql = "SELECT * FROM payments ORDER BY payment_date DESC";
            stmt = conn.prepareStatement(sql);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Payment payment = new Payment(
                    rs.getInt("id"),
                    rs.getInt("member_id"),
                    new Date(rs.getTimestamp("payment_date").getTime()),
                    rs.getDouble("amount"),
                    rs.getString("type") != null ? rs.getString("type") : "Subscription",
                    rs.getString("description") != null ? rs.getString("description") : "",
                    rs.getInt("related_record_id")
                );
                
                payments.add(payment);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all payments");
            e.printStackTrace();
        } finally {
            dbConnection.closeResources(conn, stmt, rs);
        }
        
        return payments;
    }
}