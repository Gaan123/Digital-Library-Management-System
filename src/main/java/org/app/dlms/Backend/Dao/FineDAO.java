package org.app.dlms.Backend.Dao;

import org.app.dlms.Backend.Model.Fine;
import org.app.dlms.Middleware.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for fine-related operations
 */
public class FineDAO {
    private DatabaseConnection dbConnection;

    public FineDAO() {
        dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Add a new fine record
     * 
     * @param fine The fine to add
     * @return The ID of the newly added fine, or -1 if operation fails
     */
    public int addFine(Fine fine) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int generatedId = -1;
        
        try {
            conn = dbConnection.getConnection();
            String sql = "INSERT INTO fines (member_id, borrow_record_id, amount, paid) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            
            stmt.setInt(1, fine.getMemberId());
            stmt.setInt(2, fine.getBorrowRecordId());
            stmt.setDouble(3, fine.getAmount());
            stmt.setBoolean(4, fine.isPaid());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                    fine.setId(generatedId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding fine for member ID: " + fine.getMemberId());
            e.printStackTrace();
        } finally {
            dbConnection.closeResources(conn, stmt, rs);
        }
        
        return generatedId;
    }
    
    /**
     * Get all fines for a specific member
     * 
     * @param memberId The ID of the member
     * @return List of fine records for the member
     */
    public List<Fine> getFinesByMemberId(int memberId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Fine> fines = new ArrayList<>();
        
        try {
            conn = dbConnection.getConnection();
            String sql = "SELECT * FROM fines WHERE member_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, memberId);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Fine fine = new Fine(
                    rs.getInt("id"),
                    rs.getInt("member_id"),
                    rs.getInt("borrow_record_id"),
                    rs.getDouble("amount"),
                    rs.getBoolean("paid")
                );
                
                fines.add(fine);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving fines for member ID: " + memberId);
            e.printStackTrace();
        } finally {
            dbConnection.closeResources(conn, stmt, rs);
        }
        
        return fines;
    }
    
    /**
     * Get all unpaid fines
     * 
     * @return List of all unpaid fine records
     */
    public List<Fine> getUnpaidFines() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Fine> fines = new ArrayList<>();
        
        try {
            conn = dbConnection.getConnection();
            String sql = "SELECT * FROM fines WHERE paid = false";
            stmt = conn.prepareStatement(sql);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Fine fine = new Fine(
                    rs.getInt("id"),
                    rs.getInt("member_id"),
                    rs.getInt("borrow_record_id"),
                    rs.getDouble("amount"),
                    rs.getBoolean("paid")
                );
                
                fines.add(fine);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving unpaid fines");
            e.printStackTrace();
        } finally {
            dbConnection.closeResources(conn, stmt, rs);
        }
        
        return fines;
    }
    
    /**
     * Update a fine's payment status
     * 
     * @param fineId The ID of the fine
     * @param paid The new payment status
     * @return true if successful, false otherwise
     */
    public boolean updateFinePaymentStatus(int fineId, boolean paid) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = dbConnection.getConnection();
            String sql = "UPDATE fines SET paid = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            
            stmt.setBoolean(1, paid);
            stmt.setInt(2, fineId);
            
            int affectedRows = stmt.executeUpdate();
            success = (affectedRows > 0);
        } catch (SQLException e) {
            System.err.println("Error updating fine payment status for fine ID: " + fineId);
            e.printStackTrace();
        } finally {
            dbConnection.closeResources(conn, stmt, null);
        }
        
        return success;
    }
    
    /**
     * Get a fine by ID
     * 
     * @param fineId The ID of the fine
     * @return The fine record or null if not found
     */
    public Fine getFineById(int fineId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Fine fine = null;
        
        try {
            conn = dbConnection.getConnection();
            String sql = "SELECT * FROM fines WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, fineId);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                fine = new Fine(
                    rs.getInt("id"),
                    rs.getInt("member_id"),
                    rs.getInt("borrow_record_id"),
                    rs.getDouble("amount"),
                    rs.getBoolean("paid")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving fine with ID: " + fineId);
            e.printStackTrace();
        } finally {
            dbConnection.closeResources(conn, stmt, rs);
        }
        
        return fine;
    }
    
    /**
     * Get total unpaid fine amount for a specific member
     * 
     * @param memberId The ID of the member
     * @return Total unpaid fine amount
     */
    public double getTotalUnpaidFinesByMemberId(int memberId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        double totalAmount = 0.0;
        
        try {
            conn = dbConnection.getConnection();
            String sql = "SELECT SUM(amount) as total FROM fines WHERE member_id = ? AND paid = false";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, memberId);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                totalAmount = rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Error calculating total unpaid fines for member ID: " + memberId);
            e.printStackTrace();
        } finally {
            dbConnection.closeResources(conn, stmt, rs);
        }
        
        return totalAmount;
    }

    /**
     * Get total fines amount across all members
     * 
     * @return Total fines amount
     */
    public double getTotalFines() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        double totalAmount = 0.0;
        
        try {
            conn = dbConnection.getConnection();
            String sql = "SELECT SUM(amount) as total FROM fines";
            stmt = conn.prepareStatement(sql);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                totalAmount = rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Error calculating total fines");
            e.printStackTrace();
        } finally {
            dbConnection.closeResources(conn, stmt, rs);
        }
        
        return totalAmount;
    }

    /**
     * Get total unpaid fines amount across all members
     * 
     * @return Total unpaid fines amount
     */
    public double getTotalUnpaidFines() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        double totalAmount = 0.0;
        
        try {
            conn = dbConnection.getConnection();
            String sql = "SELECT SUM(amount) as total FROM fines WHERE paid = false";
            stmt = conn.prepareStatement(sql);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                totalAmount = rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Error calculating total unpaid fines");
            e.printStackTrace();
        } finally {
            dbConnection.closeResources(conn, stmt, rs);
        }
        
        return totalAmount;
    }

    /**
     * Get all fines in the system
     * 
     * @return List of all fine records
     */
    public List<Fine> getAllFines() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Fine> fines = new ArrayList<>();
        
        try {
            conn = dbConnection.getConnection();
            String sql = "SELECT * FROM fines ORDER BY id DESC";
            stmt = conn.prepareStatement(sql);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Fine fine = new Fine(
                    rs.getInt("id"),
                    rs.getInt("member_id"),
                    rs.getInt("borrow_record_id"),
                    rs.getDouble("amount"),
                    rs.getBoolean("paid")
                );
                
                fines.add(fine);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all fines");
            e.printStackTrace();
        } finally {
            dbConnection.closeResources(conn, stmt, rs);
        }
        
        return fines;
    }
} 