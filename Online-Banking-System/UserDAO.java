package com.bank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDAO {

    /**
     * Creates the 'users' table in the database if it doesn't already exist.
     * This is useful for the initial setup.
     */
    public void initializeDatabase() {
        // This is the SQL command to create our table.
        // INT PRIMARY KEY AUTO_INCREMENT is the MySQL way of creating a unique, auto-generating ID.
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users ("
                + "user_id INT PRIMARY KEY AUTO_INCREMENT,"
                + "full_name VARCHAR(100) NOT NULL,"
                + "username VARCHAR(50) NOT NULL UNIQUE,"
                + "hashed_password VARCHAR(255) NOT NULL,"
                + "email VARCHAR(100) NOT NULL UNIQUE"
                + ");";
        
        // We use a 'try-with-resources' block to ensure the connection and statement are closed automatically.
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            // Executes the SQL command.
            stmt.execute(createTableSQL);
            System.out.println("Users table initialized successfully.");
        } catch (SQLException e) {
            System.out.println("Error initializing users table: " + e.getMessage());
        }
    }

    /**
     * Registers a new user by inserting their details into the 'users' table.
     * @param fullName The user's full name.
     * @param username The user's chosen username.
     * @param password The user's plain-text password (will be hashed before storing).
     * @param email The user's email address.
     * @return true if registration is successful, false otherwise.
     */
    public boolean registerUser(String fullName, String username, String password, String email) {
        // First, hash the password for secure storage.
        String hashedPassword = SecurityUtil.hashPassword(password);
        
        // The SQL command for inserting a new user. The '?' are placeholders for our data.
        String sql = "INSERT INTO users(full_name, username, hashed_password, email) VALUES(?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             // A 'PreparedStatement' is a special type of statement that is more secure and efficient.
             // It prevents a type of attack called SQL Injection.
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // We set the value for each placeholder '?' in order.
            pstmt.setString(1, fullName);
            pstmt.setString(2, username);
            pstmt.setString(3, hashedPassword);
            pstmt.setString(4, email);
            
            // Executes the insert operation and returns the number of rows affected.
            int affectedRows = pstmt.executeUpdate();
            
            // If one row was affected, it means the insert was successful.
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Finds a user by their username to verify their login credentials.
     * @param username The username to search for.
     * @return a User object if found, null otherwise.
     */
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            
            // ResultSet is an object that holds the data returned from a SELECT query.
            ResultSet rs = pstmt.executeQuery();
            
            // Check if the ResultSet contains any rows.
            if (rs.next()) {
                // If a user is found, create a new User object with the data from the database.
                return new User(
                    rs.getInt("user_id"),
                    rs.getString("full_name"),
                    rs.getString("username"),
                    rs.getString("email")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error fetching user: " + e.getMessage());
        }
        
        // If no user is found or an error occurs, return null.
        return null;
    }
    // Add this method to your UserDAO.java file
    public String getHashedPasswordByUsername(String username) {
        String sql = "SELECT hashed_password FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("hashed_password");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching hashed password: " + e.getMessage());
        }
        return null;
    }
}