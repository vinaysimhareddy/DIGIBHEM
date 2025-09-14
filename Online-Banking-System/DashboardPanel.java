package com.bank;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public class DashboardPanel extends JPanel {
    private JLabel welcomeLabel, balanceLabel;
    private JButton depositButton, withdrawButton, transferButton, historyButton, logoutButton;

    private User currentUser;
    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;

    public DashboardPanel(User user, JPanel mainPanel, CardLayout cardLayout) {
        this.currentUser = user;
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
        
        // --- Style and Layout ---
        setBackground(new Color(245, 245, 245)); // A light whitish-gray
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- Top Panel for Welcome and Balance ---
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBackground(getBackground());
        welcomeLabel = new JLabel("Welcome, " + currentUser.getFullName(), SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        balanceLabel = new JLabel("", SwingConstants.CENTER);
        balanceLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        balanceLabel.setForeground(new Color(0, 102, 0)); // Dark green for balance
        topPanel.add(welcomeLabel);
        topPanel.add(balanceLabel);
        add(topPanel, BorderLayout.NORTH);

        // --- Center Panel for Buttons ---
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        buttonPanel.setBackground(getBackground());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        depositButton = new JButton("Deposit");
        withdrawButton = new JButton("Withdraw");
        transferButton = new JButton("Transfer");
        historyButton = new JButton("View History");
        logoutButton = new JButton("Logout");
        
        // --- NEW: Add Icons to all buttons ---
        try {
            depositButton.setIcon(new ImageIcon(getClass().getResource("/icons/deposit.png")));
            withdrawButton.setIcon(new ImageIcon(getClass().getResource("/icons/withdraw.png")));
            transferButton.setIcon(new ImageIcon(getClass().getResource("/icons/transfer.png")));
            historyButton.setIcon(new ImageIcon(getClass().getResource("/icons/history.png")));
            logoutButton.setIcon(new ImageIcon(getClass().getResource("/icons/logout.png")));
        } catch(Exception e) {
            System.out.println("Dashboard icons not found.");
        }

        buttonPanel.add(depositButton);
        buttonPanel.add(withdrawButton);
        buttonPanel.add(transferButton);
        buttonPanel.add(historyButton);
        buttonPanel.add(logoutButton);
        add(buttonPanel, BorderLayout.CENTER);

        updateBalanceLabel();

        // --- Action Listeners (Functionality remains the same) ---
        depositButton.addActionListener(e -> handleDeposit());
        withdrawButton.addActionListener(e -> handleWithdraw());
        transferButton.addActionListener(e -> handleTransfer());
        historyButton.addActionListener(e -> handleHistory());
        
        logoutButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                mainPanel.remove(this);
                cardLayout.show(mainPanel, "login");
            }
        });
    }

    // --- All other methods (updateBalanceLabel, handleDeposit, etc.) remain the same ---
    // --- PASTE THE ORIGINAL METHODS FROM THE PREVIOUS STEP HERE ---
    private void updateBalanceLabel() {
        Account account = accountDAO.getAccountByUserId(currentUser.getUserId());
        if (account != null) {
            balanceLabel.setText(String.format("Current Balance: $%.2f", account.getBalance()));
        }
    }

    private void handleDeposit() {
        String amountStr = JOptionPane.showInputDialog(this, "Enter amount to deposit:", "Deposit", JOptionPane.PLAIN_MESSAGE);
        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Deposit amount must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Account account = accountDAO.getAccountByUserId(currentUser.getUserId());
            accountDAO.updateBalance(account.getAccountNumber(), account.getBalance() + amount);
            transactionDAO.createTransaction(new Transaction(0, account.getAccountNumber(), "DEPOSIT", amount, LocalDateTime.now()));
            updateBalanceLabel();
            JOptionPane.showMessageDialog(this, "Deposit successful.");
        } catch (NumberFormatException | NullPointerException ex) {
            if (amountStr != null) JOptionPane.showMessageDialog(this, "Invalid amount entered.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleWithdraw() {
        String amountStr = JOptionPane.showInputDialog(this, "Enter amount to withdraw:", "Withdraw", JOptionPane.PLAIN_MESSAGE);
        try {
            double amount = Double.parseDouble(amountStr);
            Account account = accountDAO.getAccountByUserId(currentUser.getUserId());
            if (amount <= 0) {
                 JOptionPane.showMessageDialog(this, "Withdrawal amount must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (amount > account.getBalance()) {
                JOptionPane.showMessageDialog(this, "Insufficient funds.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            accountDAO.updateBalance(account.getAccountNumber(), account.getBalance() - amount);
            transactionDAO.createTransaction(new Transaction(0, account.getAccountNumber(), "WITHDRAWAL", amount, LocalDateTime.now()));
            updateBalanceLabel();
            JOptionPane.showMessageDialog(this, "Withdrawal successful.");
        } catch (NumberFormatException | NullPointerException ex) {
            if (amountStr != null) JOptionPane.showMessageDialog(this, "Invalid amount entered.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleHistory() {
        Account account = accountDAO.getAccountByUserId(currentUser.getUserId());
        List<Transaction> transactions = transactionDAO.getTransactionsByAccountNumber(account.getAccountNumber());
        String[] columnNames = {"Date", "Type", "Amount"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        for (Transaction t : transactions) {
            model.addRow(new Object[]{t.getTransactionDate().toLocalDate().toString(), t.getTransactionType(), String.format("%.2f", t.getAmount())});
        }
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(400, 250));
        JOptionPane.showMessageDialog(this, scrollPane, "Transaction History", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleTransfer() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField recipientAccountField = new JTextField();
        JTextField amountField = new JTextField();
        panel.add(new JLabel("Recipient's Account Number:"));
        panel.add(recipientAccountField);
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);
        int result = JOptionPane.showConfirmDialog(this, panel, "Fund Transfer", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String recipientAccountNumber = recipientAccountField.getText();
                double amount = Double.parseDouble(amountField.getText());
                Account senderAccount = accountDAO.getAccountByUserId(currentUser.getUserId());
                if (recipientAccountNumber.isEmpty() || amount <= 0) {
                    JOptionPane.showMessageDialog(this, "All fields are required and amount must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (recipientAccountNumber.equals(senderAccount.getAccountNumber())) {
                    JOptionPane.showMessageDialog(this, "Cannot transfer funds to the same account.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (accountDAO.transferFunds(senderAccount.getAccountNumber(), recipientAccountNumber, amount)) {
                    transactionDAO.createTransaction(new Transaction(0, senderAccount.getAccountNumber(), "TRANSFER_OUT", amount, LocalDateTime.now()));
                    transactionDAO.createTransaction(new Transaction(0, recipientAccountNumber, "TRANSFER_IN", amount, LocalDateTime.now()));
                    updateBalanceLabel();
                    JOptionPane.showMessageDialog(this, "Transfer successful!");
                } else {
                    JOptionPane.showMessageDialog(this, "Transfer failed. Check recipient account number and your balance.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount entered.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}