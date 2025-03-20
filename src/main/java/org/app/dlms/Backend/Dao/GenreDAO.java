package org.app.dlms.Backend.Dao;

import org.app.dlms.Backend.Model.Genre;
import org.app.dlms.Middleware.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GenreDAO {
    
    private static final Logger LOGGER = Logger.getLogger(GenreDAO.class.getName());
    private final DatabaseConnection dbConnector;
    
    public GenreDAO() {
        this.dbConnector = DatabaseConnection.getInstance();
    }
    
    /**
     * Retrieves all genres from the database
     * @return List of all genres
     */
    public List<Genre> getAllGenres() {
        List<Genre> genres = new ArrayList<>();
        String query = "SELECT id, name FROM genres ORDER BY name";
        
        try (Connection conn = dbConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Genre genre = new Genre();
                genre.setId(rs.getInt("id"));
                genre.setName(rs.getString("name"));
                genres.add(genre);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving genres", e);
        }
        
        return genres;
    }
    
    /**
     * Retrieves a genre by its ID
     * @param id The genre ID
     * @return The genre object if found, null otherwise
     */
    public Genre getGenreById(int id) {
        String query = "SELECT id, name FROM genres WHERE id = ?";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Genre genre = new Genre();
                    genre.setId(rs.getInt("id"));
                    genre.setName(rs.getString("name"));
                    return genre;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving genre with ID: " + id, e);
        }
        
        return null;
    }
    
    /**
     * Adds a new genre to the database
     * @param genre The genre to add
     * @return The ID of the newly created genre, or -1 if operation failed
     */
    public int addGenre(Genre genre) {
        String query = "INSERT INTO genres (name) VALUES (?)";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, genre.getName());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return -1;
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding genre: " + genre.getName(), e);
        }
        
        return -1;
    }
    
    /**
     * Updates an existing genre in the database
     * @param genre The genre with updated information
     * @return true if update was successful, false otherwise
     */
    public boolean updateGenre(Genre genre) {
        String query = "UPDATE genres SET name = ? WHERE id = ?";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, genre.getName());
            stmt.setInt(2, genre.getId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating genre: " + genre.getId(), e);
            return false;
        }
    }
    
    /**
     * Deletes a genre from the database
     * @param genreId The ID of the genre to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteGenre(int genreId) {
        String query = "DELETE FROM genres WHERE id = ?";
        
        try (Connection conn = dbConnector.getConnection()) {
            // First check if genre is in use by any books
            String checkQuery = "SELECT COUNT(*) FROM books WHERE genre_id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, genreId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        // Genre is in use, cannot delete
                        return false;
                    }
                }
            }
            
            // If genre is not in use, proceed with deletion
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, genreId);
                int affectedRows = stmt.executeUpdate();
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting genre: " + genreId, e);
            return false;
        }
    }
    
    /**
     * Searches for genres by name
     * @param searchTerm The search term to look for in genre names
     * @return List of genres matching the search term
     */
    public List<Genre> searchGenres(String searchTerm) {
        List<Genre> genres = new ArrayList<>();
        String query = "SELECT id, name FROM genres WHERE name LIKE ? ORDER BY name";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, "%" + searchTerm + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Genre genre = new Genre();
                    genre.setId(rs.getInt("id"));
                    genre.setName(rs.getString("name"));
                    genres.add(genre);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching genres with term: " + searchTerm, e);
        }
        
        return genres;
    }
}