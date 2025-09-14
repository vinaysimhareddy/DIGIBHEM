package com.bank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
// import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    /**
     * Creates the 'transactions' table.
     */
    public void initializeDatabase() {
        // TIMESTAMP is used to store a precise date and time.
        String createTableSQL = "CREATE TABLE IF NOT EXISTS transactions ("
                + "transaction_id INT PRIMARY KEY AUTO_INCREMENT,"
                + "account_number VARCHAR(20) NOT NULL,"
                + "transaction_type VARCHAR(50) NOT NULL,"
                + "amount DECIMAL(15, 2) NOT NULL,"
                + "transaction_date TIMESTAMP NOT NULL,"
                + "FOREIGN KEY (account_number) REFERENCES accounts(account_number)"
                + ");";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Transactions table initialized successfully.");
        } catch (SQLException e) {
            System.out.println("Error initializing transactions table: " + e.getMessage());
        }
    }

    /**
     * Logs a new transaction in the database.
     * @param transaction The Transaction object to log.
     */
    public void createTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions(account_number, transaction_type, amount, transaction_date) VALUES(?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, transaction.getAccountNumber());
            pstmt.setString(2, transaction.getTransactionType());
            pstmt.setDouble(3, transaction.getAmount());
            pstmt.setTimestamp(4, Timestamp.valueOf(transaction.getTransactionDate()));
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error creating transaction: " + e.getMessage());
        }
    }

    /**
     * Retrieves a list of all transactions for a specific account.
     * @param accountNumber The account number to get history for.
     * @return A list of Transaction objects.
     */
    public List<Transaction> getTransactionsByAccountNumber(String accountNumber) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_number = ? ORDER BY transaction_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Transaction transaction = new Transaction(
                    rs.getInt("transaction_id"),
                    rs.getString("account_number"),
                    rs.getString("transaction_type"),
                    rs.getDouble("amount"),
                    rs.getTimestamp("transaction_date").toLocalDateTime()
                );
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching transactions: " + e.getMessage());
        }
        return transactions;
    }
}
