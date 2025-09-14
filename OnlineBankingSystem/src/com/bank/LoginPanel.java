package com.bank;

import javax.swing.*;
import java.awt.*;
import java.util.UUID;

public class LoginPanel extends JPanel {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    public LoginPanel(JPanel mainPanel, CardLayout cardLayout) {
        // --- Style and Layout ---
        setBackground(new Color(240, 248, 255)); // A light AliceBlue color
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // --- Components ---
        JLabel titleLabel = new JLabel("Banking App Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        // --- NEW: Add Icons ---
        try {
            loginButton.setIcon(new ImageIcon(getClass().getResource("/icons/login.png")));
            registerButton.setIcon(new ImageIcon(getClass().getResource("/icons/register.png")));
        } catch (Exception e) {
            System.out.println("Login/Register icons not found.");
        }
        
        // --- Add Components to Panel ---
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        add(usernameField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        add(passwordField, gbc);
        
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(getBackground()); // Match panel background
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        add(buttonPanel, gbc);

        // --- Action Listeners (Functionality remains the same) ---
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            UserDAO userDAO = new UserDAO();
            String storedHashedPassword = userDAO.getHashedPasswordByUsername(username);
            User user = userDAO.getUserByUsername(username);

            if (user != null && storedHashedPassword != null && SecurityUtil.checkPassword(password, storedHashedPassword)) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                AccountDAO accountDAO = new AccountDAO();
                Account account = accountDAO.getAccountByUserId(user.getUserId());
                if (account == null) {
                    String accountNumber = UUID.randomUUID().toString().substring(0, 12);
                    Account newAccount = new Account(accountNumber, user.getUserId(), "Savings", 0.0);
                    accountDAO.createAccount(newAccount);
                }
                DashboardPanel dashboardPanel = new DashboardPanel(user, mainPanel, cardLayout);
                mainPanel.add(dashboardPanel, "dashboard");
                cardLayout.show(mainPanel, "dashboard");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerButton.addActionListener(e -> cardLayout.show(mainPanel, "register"));
    }
}