package com.bank;

import java.time.LocalDateTime;

public class Transaction {
    
    // Private fields for the transaction details.
    private int transactionId;
    private String accountNumber;
    private String transactionType;
    private double amount;
    private LocalDateTime transactionDate;

    // The constructor to create a new Transaction object.
    public Transaction(int transactionId, String accountNumber, String type, double amount, LocalDateTime date) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.transactionType = type;
        this.amount = amount;
        this.transactionDate = date;
    }

    // --- Public Getters ---
    // These methods allow other classes to safely read the private data.

    public int getTransactionId() { 
        return transactionId; 
    }

    public String getAccountNumber() { 
        return accountNumber; 
    }
    
    // The previously missing method is now included here.
    public String getTransactionType() { 
        return transactionType; 
    }
    
    public double getAmount() { 
        return amount; 
    }

    public LocalDateTime getTransactionDate() { 
        return transactionDate; 
    }
}