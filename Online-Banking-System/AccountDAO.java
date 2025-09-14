package com.bank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AccountDAO {

    // ... initializeDatabase(), createAccount(), getAccountByUserId(), updateBalance() methods are unchanged ...
    
    // --- PASTE THE ORIGINAL METHODS HERE ---
    public void initializeDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS accounts ("
                + "account_number VARCHAR(20) PRIMARY KEY,"
                + "user_id INT NOT NULL,"
                + "account_type VARCHAR(50),"
                + "balance DECIMAL(15, 2) NOT NULL,"
                + "FOREIGN KEY (user_id) REFERENCES users(user_id)"
                + ");";
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            System.out.println("Error initializing accounts table: " + e.getMessage());
        }
    }
    public boolean createAccount(Account account) {
        String sql = "INSERT INTO accounts(account_number, user_id, account_type, balance) VALUES(?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, account.getAccountNumber());
            pstmt.setInt(2, account.getUserId());
            pstmt.setString(3, account.getAccountType());
            pstmt.setDouble(4, account.getBalance());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error creating account: " + e.getMessage());
            return false;
        }
    }
    public Account getAccountByUserId(int userId) {
        String sql = "SELECT * FROM accounts WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Account(rs.getString("account_number"), rs.getInt("user_id"), rs.getString("account_type"), rs.getDouble("balance"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching account: " + e.getMessage());
        }
        return null;
    }
    public void updateBalance(String accountNumber, double newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newBalance);
            pstmt.setString(2, accountNumber);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating balance: " + e.getMessage());
        }
    }
    // ----------------------------------------------------

    /**
     * NEW METHOD: Retrieves an account by its unique account number.
     */
    public Account getAccountByAccountNumber(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Account(rs.getString("account_number"), rs.getInt("user_id"), rs.getString("account_type"), rs.getDouble("balance"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching account by number: " + e.getMessage());
        }
        return null;
    }

    /**
     * NEW METHOD: Handles the fund transfer within a database transaction for safety.
     * This ensures that both the debit and credit operations succeed, or neither does.
     */
    public boolean transferFunds(String fromAccountNumber, String toAccountNumber, double amount) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            // Start a transaction
            conn.setAutoCommit(false);

            // 1. Debit from the sender's account
            String debitSql = "UPDATE accounts SET balance = balance - ? WHERE account_number = ? AND balance >= ?";
            try (PreparedStatement debitPstmt = conn.prepareStatement(debitSql)) {
                debitPstmt.setDouble(1, amount);
                debitPstmt.setString(2, fromAccountNumber);
                debitPstmt.setDouble(3, amount);
                int rowsAffected = debitPstmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Insufficient funds or invalid sender account.");
                }
            }

            // 2. Credit to the recipient's account
            String creditSql = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
            try (PreparedStatement creditPstmt = conn.prepareStatement(creditSql)) {
                creditPstmt.setDouble(1, amount);
                creditPstmt.setString(2, toAccountNumber);
                int rowsAffected = creditPstmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Invalid recipient account.");
                }
            }

            // If both operations are successful, commit the transaction
            conn.commit();
            return true;

        } catch (SQLException e) {
            System.out.println("Fund transfer failed: " + e.getMessage());
            // If any error occurs, roll back the transaction
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.out.println("Error rolling back transaction: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Restore default behavior
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}