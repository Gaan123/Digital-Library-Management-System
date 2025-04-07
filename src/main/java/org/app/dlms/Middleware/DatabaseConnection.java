package org.app.dlms.Middleware;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * DatabaseConnection class implements the Singleton pattern to ensure a single
 * database connection instance throughout the application.
 */
public class DatabaseConnection {

    // The single instance of DatabaseConnection
    private static DatabaseConnection instance;

    // Connection pool management
    private static final int MAX_CONNECTIONS = 10;
    private Connection[] connectionPool;
    private boolean[] connectionInUse;

    // Database connection properties
    private String dbUrl;
    private String username;
    private String password;
    private String driver;

    /**
     * Private constructor to prevent instantiation from outside
     */
    private DatabaseConnection() {
        loadDatabaseProperties();
        initializeConnectionPool();
    }

    private void loadDatabaseProperties() {
        Properties props = new Properties();
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("config/db.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find db.properties");
                // Fall back to default values
                dbUrl = "jdbc:mysql://localhost:3306/dlms";
                username = "root";
                password = "blz$P7L$@y03";
                driver = "com.mysql.cj.jdbc.Driver";
            } else {
                // Load the properties file
                props.load(input);

                // Get the database properties
                dbUrl = props.getProperty("db.url");
                username = props.getProperty("db.username");
                password = props.getProperty("db.password");
                driver = props.getProperty("db.driver");
            }
            boolean databaseExists = checkIfDatabaseExists();

            if (!databaseExists) {
                // Only create database if it doesn't exist
                createDatabaseIfNotExists();
            }
            // Create database if it doesn't exist
//            createDatabaseIfNotExists();

        } catch (Exception ex){
            ex.printStackTrace();
            // Fall back to default values
            dbUrl = "jdbc:mysql://localhost:3306/dlms";
            username = "root";
            password = "blz$P7L$@y03";
            driver = "com.mysql.cj.jdbc.Driver";

            // Create database if it doesn't exist
            createDatabaseIfNotExists();
        }

    }
    private boolean checkIfDatabaseExists() {
        String dbName = extractDatabaseName(dbUrl);
        String baseUrl = dbUrl.substring(0, dbUrl.lastIndexOf("/"));

        try {
            Class.forName(driver);
            try (Connection conn = DriverManager.getConnection(baseUrl, username, password);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SHOW DATABASES LIKE '" + dbName + "'")) {

                return rs.next(); // Returns true if the database exists
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    private void createDatabaseIfNotExists() {
        // Extract database name from connection URL
        String dbName = extractDatabaseName(dbUrl);

        // Create a connection URL without the database name
        String baseUrl = dbUrl.substring(0, dbUrl.lastIndexOf("/"));

        try {
            // Load the JDBC driver
            Class.forName(driver);

            // Connect to MySQL server (not a specific database)
            try (Connection conn = DriverManager.getConnection(baseUrl, username, password);
                 Statement stmt = conn.createStatement()) {

                // Create database if it doesn't exist
                String createDbSQL = "CREATE DATABASE IF NOT EXISTS " + dbName;
                stmt.executeUpdate(createDbSQL);
                System.out.println("Database check complete. Database " + dbName + " is available.");

                // Use the created database
                stmt.executeUpdate("USE " + dbName);

                // Create genres table first (since it's referenced by books)
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS genres  (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "name VARCHAR(100) NOT NULL UNIQUE" +
                        ")");

                // Create books table
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS books  (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "title VARCHAR(255) NOT NULL, " +
                        "author VARCHAR(255) NOT NULL, " +
                        "isbn VARCHAR(20) NOT NULL UNIQUE, " +
                        "publisher VARCHAR(255) NOT NULL, " +
                        "year INT NOT NULL, " +
                        "genre_id INT NOT NULL, " +
                        "available BOOLEAN NOT NULL DEFAULT TRUE, " +
                        "stock INT NOT NULL DEFAULT 0, " +
                        "FOREIGN KEY (genre_id) REFERENCES genres(id)" +
                        ")");

                // Create users table
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users  (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "username VARCHAR(50) NOT NULL UNIQUE, " +
                        "password VARCHAR(255) NOT NULL, " +
                        "name VARCHAR(100) NOT NULL, " +
                        "email VARCHAR(100) NOT NULL, " +
                        "gender VARCHAR(10), " +
                        "address VARCHAR(255), " +
                        "phone VARCHAR(20), " +
                        "role ENUM('Admin', 'Librarian', 'Member') NOT NULL, " +
                        "membership_type VARCHAR(50), " +
                        "membership_status ENUM('Active', 'Inactive') DEFAULT 'Active'" +
                        ")");

                // Create payments table
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS payments  (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "member_id INT NOT NULL, " +
                        "payment_date DATE NOT NULL, " +
                        "amount DECIMAL(10, 2) NOT NULL, " +
                        "type VARCHAR(50) DEFAULT 'Subscription', " +
                        "description VARCHAR(255), " +
                        "related_record_id INT DEFAULT 0, " +
                        "FOREIGN KEY (member_id) REFERENCES users(id)" +
                        ")");

                // Create borrow_records table (which was missing but referenced by fines)
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS borrow_records  (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "member_id INT NOT NULL, " +
                        "book_id INT NOT NULL, " +
                        "borrow_date DATE NOT NULL, " +
                        "due_date DATE NOT NULL, " +
                        "return_date DATE, " +
                        "FOREIGN KEY (member_id) REFERENCES users(id), " +
                        "FOREIGN KEY (book_id) REFERENCES books(id)" +
                        ")");



                // Create fines table
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS fines  (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "member_id INT NOT NULL, " +
                        "borrow_record_id INT NOT NULL, " +
                        "amount DECIMAL(10, 2) NOT NULL, " +
                        "paid BOOLEAN DEFAULT FALSE, " +
                        "FOREIGN KEY (member_id) REFERENCES users(id), " +
                        "FOREIGN KEY (borrow_record_id) REFERENCES borrow_records(id)" +
                        ")");

                // Insert sample data
                stmt.executeUpdate("INSERT INTO genres (name) VALUES ('Fiction'), ('Non-Fiction'), ('Science')");
                String password=hashPassword("password");
                stmt.executeUpdate("INSERT INTO users (username, password, name, email, gender, address, phone, role) " +
                        "VALUES ('admin1','"+password+"' , 'Admin One', 'admin1@example.com', 'Male', '123 Admin St', '1234567890', 'Admin')");

                stmt.executeUpdate("INSERT INTO users (username, password, name, email, gender, address, phone, role) " +
                        "VALUES ('librarian1', '"+password+"', 'Librarian One', 'librarian1@example.com', 'Female', '456 Library Rd', '0987654321', 'Librarian')");

                stmt.executeUpdate("INSERT INTO users (username, password, name, email, gender, address, phone, role, membership_type) " +
                        "VALUES ('member1','"+password+"' , 'Member One', 'member1@example.com', 'Other', '789 Member Ln', '1122334455', 'Member', 'Student')");

            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error creating database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * Utility method to close database resources
     *
     * @param conn The database connection
     * @param stmt The statement
     * @param rs The result set
     */
    public void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                // Release the connection back to the pool instead of closing it
                releaseConnection(conn);
            }
        } catch (SQLException e) {
            System.err.println("Error closing database resources");
            e.printStackTrace();
        }
    }
    private String hashPassword(String plainPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashedBytes = md.digest(plainPassword.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error hashing password: " + e.getMessage());
            return "ERROR_HASHING_PASSWORD";
        }
    }
    private String extractDatabaseName(String connectionUrl) {
        // Extract database name from JDBC URL
        // Example: jdbc:mysql://localhost:3306/dlms -> dlms
        return connectionUrl.substring(connectionUrl.lastIndexOf("/") + 1);
    }

    /**
     * Initialize the connection pool
     */
    private void initializeConnectionPool() {
        try {
            // Load the JDBC driver
            Class.forName(driver);

            connectionPool = new Connection[MAX_CONNECTIONS];
            connectionInUse = new boolean[MAX_CONNECTIONS];

            // Create all connections and add to the pool
            for (int i = 0; i < MAX_CONNECTIONS; i++) {
                connectionPool[i] = DriverManager.getConnection(dbUrl, username, password);
                connectionInUse[i] = false;
            }

            System.out.println("Database connection pool initialized with " + MAX_CONNECTIONS + " connections");
        } catch (ClassNotFoundException e) {
            System.err.println("Database driver not found: " + driver);
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to initialize database connection pool");
            e.printStackTrace();
        }
    }

    /**
     * Get the singleton instance of DatabaseConnection
     * @return The single instance of DatabaseConnection
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Get a connection from the pool
     * @return A database connection
     * @throws SQLException if no connection is available
     */
    public synchronized Connection getConnection() throws SQLException {
        // Find an available connection
        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            if (!connectionInUse[i]) {
                // Check if connection is valid and reconnect if needed
                if (connectionPool[i] == null || connectionPool[i].isClosed()) {
                    connectionPool[i] = DriverManager.getConnection(dbUrl, username, password);
                }

                // Check if connection is valid
                if (connectionPool[i].isValid(5)) {
                    connectionInUse[i] = true;
                    return connectionPool[i];
                } else {
                    // Connection is invalid, create a new one
                    connectionPool[i] = DriverManager.getConnection(dbUrl, username, password);
                    connectionInUse[i] = true;
                    return connectionPool[i];
                }
            }
        }

        // No available connections
        throw new SQLException("No available database connections in the pool");
    }

    /**
     * Release a connection back to the pool
     * @param connection The connection to release
     */
    public synchronized void releaseConnection(Connection connection) {
        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            if (connectionPool[i] == connection) {
                connectionInUse[i] = false;
                break;
            }
        }
    }

    /**
     * Close all connections in the pool
     */
    public synchronized void closeAllConnections() {
        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            try {
                if (connectionPool[i] != null && !connectionPool[i].isClosed()) {
                    connectionPool[i].close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection");
                e.printStackTrace();
            }
        }
    }
    /**
     * Check if a table exists in the database
     * @param tableName Name of the table to check
     * @return true if table exists, false otherwise
     */
    private static boolean tableExists( String tableName) throws SQLException {
        DatabaseConnection connection=DatabaseConnection.getInstance();
        DatabaseMetaData meta = connection.getConnection().getMetaData();
        ResultSet resultSet = meta.getTables(null, null, tableName, new String[] {"TABLE"});

        boolean exists = resultSet.next();
        resultSet.close();
        return exists;
    }
    /**
     * Test the database connection
     * @return true if connection is successful, false otherwise
     */
    public boolean testConnection() {
        Connection conn = null;
        try {
            conn = getConnection();
            boolean isValid = conn.isValid(5);
            releaseConnection(conn);
            return isValid;
        } catch (SQLException e) {
            System.err.println("Database connection test failed");
            e.printStackTrace();
            return false;
        }
    }
}