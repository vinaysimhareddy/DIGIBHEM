package com.bank;

import java.time.LocalDateTime;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Main {

    // Create static instances of our DAOs to be used throughout the application.
    private static final UserDAO userDAO = new UserDAO();
    private static final AccountDAO accountDAO = new AccountDAO();
    private static final TransactionDAO transactionDAO = new TransactionDAO();
    private static User currentUser = null; // To hold the currently logged-in user.

    public static void main(String[] args) {
        // Initialize the database tables when the application starts.
        initializeAllDatabases();

        Scanner scanner = new Scanner(System.in);

        // The main application loop. It runs forever until the user chooses to exit.
        while (true) {
            // If no user is logged in, show the main menu.
            if (currentUser == null) {
                showMainMenu();
                int choice = getIntegerInput(scanner);
                
                switch (choice) {
                    case 1:
                        handleRegister(scanner);
                        break;
                    case 2:
                        handleLogin(scanner);
                        break;
                    case 3:
                        System.out.println("Thank you for using the Online Banking System. Goodbye!");
                        scanner.close(); // Close the scanner before exiting.
                        return; // Exit the application.
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } else {
                // If a user is logged in, show the user's account menu.
                showUserMenu();
                int choice = getIntegerInput(scanner);

                switch (choice) {
                    case 1:
                        handleViewBalance();
                        break;
                    case 2:
                        handleDeposit(scanner);
                        break;
                    case 3:
                        handleWithdraw(scanner);
                        break;
                    case 4:
                        handleViewTransactionHistory();
                        break;
                    case 5:
                        currentUser = null; // Log out the user.
                        System.out.println("You have been successfully logged out.");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        }
    }

    // --- Menu Display Methods ---
    private static void showMainMenu() {
        System.out.println("\n--- ONLINE BANKING SYSTEM ---");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void showUserMenu() {
        System.out.println("\n--- Welcome, " + currentUser.getFullName() + "! ---");
        System.out.println("1. View Balance");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. View Transaction History");
        System.out.println("5. Logout");
        System.out.print("Enter your choice: ");
    }

    // --- Handler Methods for Application Logic ---
    private static void handleRegister(Scanner scanner) {
        System.out.println("\n--- User Registration ---");
        System.out.print("Enter full name: ");
        String fullName = scanner.nextLine();
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        if (userDAO.registerUser(fullName, username, password, email)) {
            System.out.println("Registration successful! Please login.");
        } else {
            System.out.println("Registration failed. Username or email may already exist.");
        }
    }

    private static void handleLogin(Scanner scanner) {
        System.out.println("\n--- User Login ---");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User user = userDAO.getUserByUsername(username);
        // We also need the user's hashed password to check it.
        // Let's modify getUserByUsername to also fetch the password, or add a new method.
        // For now, let's assume we have a method to get the hashed password.
        // Let's create a quick fix in UserDAO.
        String storedHashedPassword = userDAO.getHashedPasswordByUsername(username);

        if (user != null && storedHashedPassword != null && SecurityUtil.checkPassword(password, storedHashedPassword)) {
            currentUser = user;
            System.out.println("Login successful!");
            // Check if user has an account, if not, create one.
            Account account = accountDAO.getAccountByUserId(currentUser.getUserId());
            if (account == null) {
                System.out.println("No account found. Creating a new savings account for you.");
                String accountNumber = UUID.randomUUID().toString().substring(0, 12);
                Account newAccount = new Account(accountNumber, currentUser.getUserId(), "Savings", 0.0);
                accountDAO.createAccount(newAccount);
            }
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    private static void handleViewBalance() {
        Account account = accountDAO.getAccountByUserId(currentUser.getUserId());
        if (account != null) {
            System.out.println("\n--- Account Balance ---");
            System.out.printf("Your current balance is: $%.2f\n", account.getBalance());
        }
    }

    private static void handleDeposit(Scanner scanner) {
        System.out.println("\n--- Deposit Money ---");
        System.out.print("Enter amount to deposit: ");
        double amount = getDoubleInput(scanner);

        if (amount <= 0) {
            System.out.println("Deposit amount must be positive.");
            return;
        }

        Account account = accountDAO.getAccountByUserId(currentUser.getUserId());
        double newBalance = account.getBalance() + amount;
        accountDAO.updateBalance(account.getAccountNumber(), newBalance);

        // Log the transaction
        Transaction deposit = new Transaction(0, account.getAccountNumber(), "DEPOSIT", amount, LocalDateTime.now());
        transactionDAO.createTransaction(deposit);

        System.out.printf("Successfully deposited $%.2f. Your new balance is $%.2f\n", amount, newBalance);
    }

    private static void handleWithdraw(Scanner scanner) {
        System.out.println("\n--- Withdraw Money ---");
        System.out.print("Enter amount to withdraw: ");
        double amount = getDoubleInput(scanner);

        if (amount <= 0) {
            System.out.println("Withdrawal amount must be positive.");
            return;
        }

        Account account = accountDAO.getAccountByUserId(currentUser.getUserId());
        if (amount > account.getBalance()) {
            System.out.println("Insufficient funds. Your balance is $" + account.getBalance());
            return;
        }

        double newBalance = account.getBalance() - amount;
        accountDAO.updateBalance(account.getAccountNumber(), newBalance);

        // Log the transaction
        Transaction withdrawal = new Transaction(0, account.getAccountNumber(), "WITHDRAWAL", amount, LocalDateTime.now());
        transactionDAO.createTransaction(withdrawal);

        System.out.printf("Successfully withdrew $%.2f. Your new balance is $%.2f\n", amount, newBalance);
    }

    private static void handleViewTransactionHistory() {
        Account account = accountDAO.getAccountByUserId(currentUser.getUserId());
        List<Transaction> transactions = transactionDAO.getTransactionsByAccountNumber(account.getAccountNumber());

        System.out.println("\n--- Transaction History for Account: " + account.getAccountNumber() + " ---");
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            System.out.printf("%-15s | %-20s | %-10s\n", "Date", "Type", "Amount");
            System.out.println("-----------------------------------------------------");
            for (Transaction t : transactions) {
                System.out.printf("%-15s | %-20s | $%-10.2f\n",
                        t.getTransactionDate().toLocalDate(),
                        t.getTransactionType(),
                        t.getAmount());
            }
        }
    }


    // --- Utility Methods ---
    private static void initializeAllDatabases() {
        userDAO.initializeDatabase();
        accountDAO.initializeDatabase();
        transactionDAO.initializeDatabase();
    }

    private static int getIntegerInput(Scanner scanner) {
        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the rest of the line
            return choice;
        } catch (InputMismatchException e) {
            scanner.nextLine(); // Clear the invalid input
            return -1; // Return an invalid choice
        }
    }

    private static double getDoubleInput(Scanner scanner) {
        try {
            double amount = scanner.nextDouble();
            scanner.nextLine(); // Consume the rest of the line
            return amount;
        } catch (InputMismatchException e) {
            scanner.nextLine(); // Clear the invalid input
            return -1.0; // Return an invalid amount
        }
    }
}