package com.bank;

import javax.swing.*;
import java.awt.*;

public class BankingAppGUI extends JFrame {

    private static final UserDAO userDAO = new UserDAO();
    private static final AccountDAO accountDAO = new AccountDAO();
    private static final TransactionDAO transactionDAO = new TransactionDAO();

    private JPanel mainPanel;
    private CardLayout cardLayout;

    public BankingAppGUI() {
        setTitle("Online Banking System");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        LoginPanel loginPanel = new LoginPanel(mainPanel, cardLayout);
        RegisterPanel registerPanel = new RegisterPanel(mainPanel, cardLayout);

        mainPanel.add(loginPanel, "login");
        mainPanel.add(registerPanel, "register");

        add(mainPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        // --- THIS IS THE NEW CODE FOR A BETTER LOOK AND FEEL ---
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // ---------------------------------------------------------

        userDAO.initializeDatabase();
        accountDAO.initializeDatabase();
        transactionDAO.initializeDatabase();

        SwingUtilities.invokeLater(() -> new BankingAppGUI());
    }
}