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
    private Connection connection;
    private static final String TABLE_NAME = "borrow_records";
    /**
     * Constructor initializes database connection
     */
    public BorrowRecordDAO() {
        try {
            // Assuming there's a DatabaseConnection class or similar mechanism
            // to get a database connection
//            DatabaseMetaData DatabaseConnection = dbConnector.getInstance();
           DatabaseConnection dbConnector = DatabaseConnection.getInstance();
            this.connection = dbConnector.getConnection();
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
        }
    }

    /**
     * Add a new borrow record to the database
     * 
     * @param borrowRecord The borrow record to add
     * @return true if successful, false otherwise
     */
    public boolean addBorrowRecord(BorrowRecord borrowRecord) {
        String sql = "INSERT INTO " + TABLE_NAME + 
                     " (member_id, book_id, borrow_date, due_date, return_date) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    borrowRecord.setId(generatedKeys.getInt(1));
                }
                
                // Update book availability
                updateBookAvailability(borrowRecord.getBookId(), false);
                return true;
            }
            
            return false;
        } catch (SQLException e) {
            System.err.println("Error adding borrow record: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get a borrow record by its ID
     * 
     * @param id The ID of the borrow record to retrieve
     * @return The borrow record if found, null otherwise
     */
    public BorrowRecord getBorrowRecordById(int id) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractBorrowRecordFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting borrow record by ID: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Get all borrow records from the database
     * 
     * @return List of all borrow records
     */
    public List<BorrowRecord> getAllBorrowRecords() {
        List<BorrowRecord> borrowRecords = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY borrow_date DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                borrowRecords.add(extractBorrowRecordFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all borrow records: " + e.getMessage());
        }
        
        return borrowRecords;
    }

    /**
     * Get all active borrow records (not returned yet)
     * 
     * @return List of active borrow records
     */
    public List<BorrowRecord> getActiveBorrowRecords() {
        List<BorrowRecord> borrowRecords = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE return_date IS NULL ORDER BY due_date ASC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                borrowRecords.add(extractBorrowRecordFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting active borrow records: " + e.getMessage());
        }
        
        return borrowRecords;
    }

    /**
     * Get all overdue borrow records
     * 
     * @return List of overdue borrow records
     */
    public List<BorrowRecord> getOverdueBorrowRecords() {
        List<BorrowRecord> borrowRecords = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + 
                     " WHERE return_date IS NULL AND due_date < CURRENT_DATE " +
                     "ORDER BY due_date ASC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                borrowRecords.add(extractBorrowRecordFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting overdue borrow records: " + e.getMessage());
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
        List<BorrowRecord> borrowRecords = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE member_id = ? ORDER BY borrow_date DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                borrowRecords.add(extractBorrowRecordFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting borrow records by member: " + e.getMessage());
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
        List<BorrowRecord> borrowRecords = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE book_id = ? ORDER BY borrow_date DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                borrowRecords.add(extractBorrowRecordFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting borrow records by book: " + e.getMessage());
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
        String sql = "UPDATE " + TABLE_NAME + 
                     " SET member_id = ?, book_id = ?, borrow_date = ?, due_date = ?, return_date = ? " +
                     "WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating borrow record: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete a borrow record from the database
     * 
     * @param id The ID of the borrow record to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteBorrowRecord(int id) {
        // First, get the record to know if we need to update book availability
        BorrowRecord record = getBorrowRecordById(id);
        if (record == null) {
            return false;
        }
        
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0 && record.getReturnDate() == null) {
                // If the record is active (not returned), update book availability
                updateBookAvailability(record.getBookId(), true);
            }
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting borrow record: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get the current borrow record for a book if it exists
     * 
     * @param bookId The ID of the book
     * @return The active borrow record if exists, null otherwise
     */
    public BorrowRecord getCurrentBorrowForBook(int bookId) {
        String sql = "SELECT * FROM " + TABLE_NAME + 
                     " WHERE book_id = ? AND return_date IS NULL " +
                     "ORDER BY borrow_date DESC LIMIT 1";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractBorrowRecordFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting current borrow for book: " + e.getMessage());
        }
        
        return null;
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
        String sql = "UPDATE books SET available = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, available);
            stmt.setInt(2, bookId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating book availability: " + e.getMessage());
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
     * Close the database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}