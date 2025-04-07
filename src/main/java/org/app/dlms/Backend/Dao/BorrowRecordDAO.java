package org.app.dlms.Backend.Dao;

import org.app.dlms.Backend.Model.BorrowRecord;
import org.app.dlms.Middleware.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for BorrowRecord entities
 * Handles database operations for borrowing records
 */
public class BorrowRecordDAO {
    private DatabaseConnection dbConnection;
    private static final String TABLE_NAME = "borrow_records";
    
    /**
     * Constructor initializes database connection
     */
    public BorrowRecordDAO() {
        dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Add a new borrow record to the database
     * 
     * @param borrowRecord The borrow record to add
     * @return true if successful, false otherwise
     */
    public boolean addBorrowRecord(BorrowRecord borrowRecord) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean success = false;
        
        try {
            conn = dbConnection.getConnection();
            String sql = "INSERT INTO " + TABLE_NAME + 
                     " (member_id, book_id, borrow_date, due_date, return_date) " +
                     "VALUES (?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setInt(1, borrowRecord.getMemberId());
            stmt.setInt(2, borrowRecord.getBookId());
            stmt.setDate(3, new java.sql.Date(borrowRecord.getBorrowDate().getTime()));
            stmt.setDate(4, new java.sql.Date(borrowRecord.getDueDate().getTime()));
            
            if (borrowRecord.getReturnDate() != null) {
                stmt.setDate(5, new java.sql.Date(borrowRecord.getReturnDate().getTime()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get the generated ID and set it in the borrowRecord object
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    borrowRecord.setId(rs.getInt(1));
                }
                
                // Update book availability
                updateBookAvailability(borrowRecord.getBookId(), false);
                success = true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding borrow record: " + e.getMessage());
        } finally {
            dbConnection.closeResources(conn, stmt, rs);
        }
        
        return success;
    }

    /**
     * Get a borrow record by its ID
     * 
     * @param id The ID of the borrow record to retrieve
     * @return The borrow record if found, null otherwise
     */
    public BorrowRecord getBorrowRecordById(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        BorrowRecord borrowRecord = null;
        
        try {
            conn = dbConnection.getConnection();
            String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                borrowRecord = extractBorrowRecordFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting borrow record by ID: " + e.getMessage());
        } finally {
            dbConnection.closeResources(conn, stmt, rs);
        }
        
        return borrowRecord;
    }

    /**
     * Get all borrow records from the database
     * 
     * @return List of all borrow records
     */
    public List<BorrowRecord> getAllBorrowRecords() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<BorrowRecord> borrowRecords = new ArrayList<>();
        
        try {
            conn = dbConnection.getConnection();
            String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY borrow_date DESC";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                borrowRecords.add(extractBorrowRecordFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all borrow records: " + e.getMessage());
        } finally {
            dbConnection.closeResources(conn, stmt, rs);
        }
        
        return borrowRecords;
    }

    /**
     * Get all active borrow records (not returned yet)
     * 
     * @return List of active borrow records
     */
    public List<BorrowRecord> getActiveBorrowRecords() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<BorrowRecord> borrowRecords = new ArrayList<>();
        
        try {
            conn = dbConnection.getConnection();
            String sql = "SELECT * FROM " + TABLE_NAME + " WHERE return_date IS NULL ORDER BY due_date ASC";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                borrowRecords.add(extractBorrowRecordFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting active borrow records: " + e.getMessage());
        } finally {
            dbConnection.closeResources(conn, stmt, rs);
        }
        
        return borrowRecords;
    }

    /**
     * Get all overdue borrow records
     * 
     * @return List of overdue borrow records
     */
    public List<BorrowRecord> getOverdueBorrowRecords() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<BorrowRecord> borrowRecords = new ArrayList<>();
        
        try {
            conn = dbConnection.getConnection();
            String sql = "SELECT * FROM " + TABLE_NAME + 
                         " WHERE return_date IS NULL AND due_date < CURRENT_DATE " +
                         "ORDER BY due_date ASC";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                borrowRecords.add(extractBorrowRecordFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting overdue borrow records: " + e.getMessage());
        } finally {
            dbConnection.closeResources(conn, stmt, rs);
        }
        
        return borrowRecords;
    }

    /**
     * Get all borrow records for a specific user
     * 
     * @param memberId The ID of the member
     * @return List of borrow records for the specified member
     */
    public List<BorrowRecord> getBorrowRecordsByMember(int memberId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<BorrowRecord> borrowRecords = new ArrayList<>();
        
        try {
            conn = dbConnection.getConnection();
            String sql = "SELECT * FROM " + TABLE_NAME + " WHERE member_id = ? ORDER BY borrow_date DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, memberId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                borrowRecords.add(extractBorrowRecordFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting borrow records by member: " + e.getMessage());
        } finally {
            dbConnection.closeResources(conn, stmt, rs);
        }
        
        return borrowRecords;
    }

    /**
     * Get all borrow records for a specific book
     * 
     * @param bookId The ID of the book
     * @return List of borrow records for the specified book
     */
    public List<BorrowRecord> getBorrowRecordsByBook(int bookId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<BorrowRecord> borrowRecords = new ArrayList<>();
        
        try {
            conn = dbConnection.getConnection();
            String sql = "SELECT * FROM " + TABLE_NAME + " WHERE book_id = ? ORDER BY borrow_date DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, bookId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                borrowRecords.add(extractBorrowRecordFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting borrow records by book: " + e.getMessage());
        } finally {
            dbConnection.closeResources(conn, stmt, rs);
        }
        
        return borrowRecords;
    }

    /**
     * Update a borrow record in the database
     * 
     * @param borrowRecord The borrow record to update
     * @return true if successful, false otherwise
     */
    public boolean updateBorrowRecord(BorrowRecord borrowRecord) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = dbConnection.getConnection();
            String sql = "UPDATE " + TABLE_NAME + 
                       " SET member_id = ?, book_id = ?, borrow_date = ?, due_date = ?, return_date = ? " +
                       "WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            
            stmt.setInt(1, borrowRecord.getMemberId());
            stmt.setInt(2, borrowRecord.getBookId());
            stmt.setDate(3, new java.sql.Date(borrowRecord.getBorrowDate().getTime()));
            stmt.setDate(4, new java.sql.Date(borrowRecord.getDueDate().getTime()));
            
            // Check if a book is being returned
            BorrowRecord oldRecord = getBorrowRecordById(borrowRecord.getId());
            boolean bookBeingReturned = oldRecord != null && 
                                      oldRecord.getReturnDate() == null && 
                                      borrowRecord.getReturnDate() != null;
            
            if (borrowRecord.getReturnDate() != null) {
                stmt.setDate(5, new java.sql.Date(borrowRecord.getReturnDate().getTime()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            
            stmt.setInt(6, borrowRecord.getId());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0 && bookBeingReturned) {
                // Update book availability when returned
                updateBookAvailability(borrowRecord.getBookId(), true);
            }
            
            success = (rowsAffected > 0);
        } catch (SQLException e) {
            System.err.println("Error updating borrow record: " + e.getMessage());
        } finally {
            dbConnection.closeResources(conn, stmt, null);
        }
        
        return success;
    }

    /**
     * Delete a borrow record from the database
     * 
     * @param id The ID of the borrow record to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteBorrowRecord(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        // First, get the record to know if we need to update book availability
        BorrowRecord record = getBorrowRecordById(id);
        if (record == null) {
            return false;
        }
        
        try {
            conn = dbConnection.getConnection();
            String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0 && record.getReturnDate() == null) {
                // If the record is active (not returned), update book availability
                updateBookAvailability(record.getBookId(), true);
            }
            
            success = (rowsAffected > 0);
        } catch (SQLException e) {
            System.err.println("Error deleting borrow record: " + e.getMessage());
        } finally {
            dbConnection.closeResources(conn, stmt, null);
        }
        
        return success;
    }

    /**
     * Get the current borrow record for a book if it exists
     * 
     * @param bookId The ID of the book
     * @return The active borrow record if exists, null otherwise
     */
    public BorrowRecord getCurrentBorrowForBook(int bookId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        BorrowRecord borrowRecord = null;
        
        try {
            conn = dbConnection.getConnection();
            String sql = "SELECT * FROM " + TABLE_NAME + 
                       " WHERE book_id = ? AND return_date IS NULL " +
                       "ORDER BY borrow_date DESC LIMIT 1";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, bookId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                borrowRecord = extractBorrowRecordFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting current borrow for book: " + e.getMessage());
        } finally {
            dbConnection.closeResources(conn, stmt, rs);
        }
        
        return borrowRecord;
    }

    /**
     * Check if a book is currently borrowed
     * 
     * @param bookId The ID of the book
     * @return true if borrowed, false otherwise
     */
    public boolean isBookCurrentlyBorrowed(int bookId) {
        return getCurrentBorrowForBook(bookId) != null;
    }

    /**
     * Get the borrowing history for a member
     * 
     * @param memberId The ID of the member
     * @return List of borrow records for the member
     */
    public List<BorrowRecord> getMemberBorrowingHistory(int memberId) {
        return getBorrowRecordsByMember(memberId);
    }

    /**
     * Get the borrowing history for a book
     * 
     * @param bookId The ID of the book
     * @return List of borrow records for the book
     */
    public List<BorrowRecord> getBookBorrowingHistory(int bookId) {
        return getBorrowRecordsByBook(bookId);
    }

    /**
     * Update the availability status of a book
     * 
     * @param bookId The ID of the book
     * @param available true if available, false if borrowed
     */
    private void updateBookAvailability(int bookId, boolean available) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbConnection.getConnection();
            String sql = "UPDATE books SET available = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setBoolean(1, available);
            stmt.setInt(2, bookId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating book availability: " + e.getMessage());
        } finally {
            dbConnection.closeResources(conn, stmt, null);
        }
    }

    /**
     * Extract a BorrowRecord object from a ResultSet
     * 
     * @param rs The ResultSet containing borrow record data
     * @return A BorrowRecord object
     * @throws SQLException If there's an error accessing the ResultSet
     */
    private BorrowRecord extractBorrowRecordFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int memberId = rs.getInt("member_id");
        int bookId = rs.getInt("book_id");
        Date borrowDate = rs.getDate("borrow_date");
        Date dueDate = rs.getDate("due_date");
        Date returnDate = rs.getDate("return_date");
        
        return new BorrowRecord(id, memberId, bookId, borrowDate, dueDate, returnDate);
    }

    /**
     * Close the database connection (should be called when application shuts down)
     */
    public void closeConnection() {
        // No need to explicitly close connection as it's managed by the connection pool
    }

    /**
     * Get the total number of active loans
     * 
     * @return The total count of active loans
     */
    public int getActiveLoansCount() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        int count = 0;
        
        try {
            conn = dbConnection.getConnection();
            String sql = "SELECT COUNT(*) AS total FROM " + TABLE_NAME + " WHERE return_date IS NULL";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error getting active loans count: " + e.getMessage());
        } finally {
            dbConnection.closeResources(conn, stmt, rs);
        }
        
        return count;
    }
}