package com.bank;

public class Account {
    private String accountNumber;
    private int userId;
    private String accountType;
    private double balance;

    public Account(String accountNumber, int userId, String accountType, double balance){
        this.accountNumber = accountNumber;
        this.userId = userId;
        this.accountType = accountType;
        this.balance = balance;
    }

    public String getAccountNumber(){
        return accountNumber;
    }
    public int getUserId(){
        return userId;
    }
    public String getAccountType(){
        return accountType;
    }
    public double getBalance(){
        return balance;
    }
}
