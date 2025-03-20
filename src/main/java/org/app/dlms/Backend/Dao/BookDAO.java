package org.app.dlms.Backend.Dao;

import org.app.dlms.Backend.Model.Book;
import org.app.dlms.Middleware.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for Book entities
 */
public class BookDAO {

    private DatabaseConnection dbConnection;

    public BookDAO() {
        dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Get a book by its ISBN
     *
     * @param isbn The ISBN of the book
     * @return The book if found, null otherwise
     */
    public Book getBookByIsbn(String isbn) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Book book = null;

        try {
            conn = dbConnection.getConnection();
            String sql = "SELECT * FROM books WHERE isbn = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, isbn);
            rs = stmt.executeQuery();

            if (rs.next()) {
                book = new Book();
                book.setId(rs.getInt("id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setIsbn(rs.getString("isbn"));
                book.setPublisher(rs.getString("publisher"));
                book.setYear(rs.getInt("year"));
                book.setAvailable(rs.getBoolean("available"));
                book.setStock(rs.getInt("stock"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving book by ISBN: " + isbn);
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }

        return book;
    }
    /**
     * Get a book by its ISBN
     *
     * @param id The ISBN of the book
     * @return The book if found, null otherwise
     */
    public Book getBookById(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Book book = null;

        try {
            conn = dbConnection.getConnection();
            String sql = "SELECT * FROM books WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, String.valueOf(id));
            rs = stmt.executeQuery();

            if (rs.next()) {
                book = new Book();
                book.setId(rs.getInt("id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setIsbn(rs.getString("isbn"));
                book.setPublisher(rs.getString("publisher"));
                book.setYear(rs.getInt("year"));
                book.setAvailable(rs.getBoolean("available"));
                book.setStock(rs.getInt("stock"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving book by ISBN: " + id);
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }

        return book;
    }

    /**
     * Get all books in the library
     *
     * @return List of all books
     */
    public List<Book> getAllBooks() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Book> books = new ArrayList<>();

        try {
            conn = dbConnection.getConnection();
            String sql = "SELECT * FROM books";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setIsbn(rs.getString("isbn"));
                book.setPublisher(rs.getString("publisher"));
                book.setYear(rs.getInt("year"));
                book.setAvailable(rs.getBoolean("available"));
                book.setStock(rs.getInt("stock"));
                books.add(book);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all books");
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }

        return books;
    }

    /**
     * Add a new book to the library
     *
     * @param book The book to add
     * @return The ID of the newly added book, or -1 if operation fails
     */
    public int addBook(Book book) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int generatedId = -1;

        try {
            conn = dbConnection.getConnection();
            String sql = "INSERT INTO books (title, author, isbn, publisher, year, genre_id, available, stock) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getIsbn());
            stmt.setString(4, book.getPublisher());
            stmt.setInt(5, book.getYear());
            stmt.setInt(6, book.getGenreId());
            stmt.setBoolean(7, book.isAvailable());
            stmt.setInt(8, book.getStock());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                    book.setId(generatedId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding book: " + book.getTitle());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }

        return generatedId;
    }

    /**
     * Update book information
     *
     * @param book The book with updated information
     * @return True if successful, false otherwise
     */
    public boolean updateBook(Book book) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;

        try {
            conn = dbConnection.getConnection();
            String sql = "UPDATE books SET title = ?, author = ?, publisher = ?, " +
                    "year = ?, " +
//                    "genre_id = ?," +
                    " available = ?, stock = ? " +
                    "WHERE isbn = ?";
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getPublisher());
            stmt.setInt(4, book.getYear());
//            stmt.setInt(5, book.getGenreId());
            stmt.setBoolean(5, book.isAvailable());
            stmt.setInt(6, book.getStock());
            stmt.setString(7, book.getIsbn());

            int affectedRows = stmt.executeUpdate();
            success = (affectedRows > 0);
        } catch (SQLException e) {
            System.err.println("Error updating book: " + book.getTitle());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, null);
        }

        return success;
    }

    /**
     * Delete a book from the library
     *
     * @param isbn The ISBN of the book to delete
     * @return True if successful, false otherwise
     */
    public boolean deleteBook(String isbn) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;

        try {
            conn = dbConnection.getConnection();
            String sql = "DELETE FROM books WHERE isbn = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, isbn);

            int affectedRows = stmt.executeUpdate();
            success = (affectedRows > 0);
        } catch (SQLException e) {
            System.err.println("Error deleting book with ISBN: " + isbn);
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, null);
        }

        return success;
    }

    /**
     * Search for books by title, author, or category
     *
     * @param searchTerm The search term
     * @return List of books matching the search term
     */
    public List<Book> searchBooks(String searchTerm) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Book> books = new ArrayList<>();

        try {
            conn = dbConnection.getConnection();
            String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR isbn LIKE ?";
            stmt = conn.prepareStatement(sql);

            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);

            rs = stmt.executeQuery();

            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setIsbn(rs.getString("isbn"));
                book.setPublisher(rs.getString("publisher"));
                book.setYear(rs.getInt("year"));
                book.setAvailable(rs.getBoolean("available"));
                book.setStock(rs.getInt("stock"));
                books.add(book);
            }
        } catch (SQLException e) {
            System.err.println("Error searching for books with term: " + searchTerm);
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, null);
        }
        return books;
    }
    /**
     * Utility method to close database resources
     *
     * @param conn The database connection
     * @param stmt The prepared statement
     * @param rs The result set
     */
    private void closeResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                // Release the connection back to the pool instead of closing it
                DatabaseConnection.getInstance().releaseConnection(conn);
            }
        } catch (SQLException e) {
            System.err.println("Error closing database resources");
            e.printStackTrace();
        }
    }
    /**
     * Update the stock of a book
     *
     * @param isbn The ISBN of the book
     * @param quantity The quantity to add (positive) or remove (negative)
     * @return True if successful, false otherwise
     */
    public boolean updateBookStock(String isbn, int quantity) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;

        try {
            conn = dbConnection.getConnection();
            String sql = "UPDATE books SET stock = stock + ? WHERE isbn = ? AND (stock + ?) >= 0";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, quantity);
            stmt.setString(2, isbn);
            stmt.setInt(3, quantity); // Prevent negative stock

            int affectedRows = stmt.executeUpdate();
            success = (affectedRows > 0);

            // Update availability status based on stock
            if (success) {
                updateAvailabilityStatus(isbn);
            }
        } catch (SQLException e) {
            System.err.println("Error updating stock for book with ISBN: " + isbn);
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, null);
        }

        return success;
    }

    /**
     * Update the availability status based on current stock
     *
     * @param isbn The ISBN of the book
     */
    private void updateAvailabilityStatus(String isbn) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = dbConnection.getConnection();
            String sql = "UPDATE books SET available = (stock > 0) WHERE isbn = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, isbn);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating availability status for book with ISBN: " + isbn);
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    /**
     * Check if a book has enough copies in stock
     *
     * @param isbn The ISBN of the book
     * @param quantity The quantity needed
     * @return True if enough copies are available, false otherwise
     */
    public boolean hasEnoughStock(String isbn, int quantity) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean hasEnough = false;

        try {
            conn = dbConnection.getConnection();
            String sql = "SELECT stock FROM books WHERE isbn = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, isbn);
            rs = stmt.executeQuery();

            if (rs.next()) {
                int currentStock = rs.getInt("stock");
                hasEnough = currentStock >= quantity;
            }
        } catch (SQLException e) {
            System.err.println("Error checking stock for book with ISBN: " + isbn);
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }

        return hasEnough;
    }
}
