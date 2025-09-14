package com.bank;

import javax.swing.*;
import java.awt.*;

public class RegisterPanel extends JPanel {
    private JTextField fullNameField, usernameField, emailField;
    private JPasswordField passwordField;
    private JButton registerButton, backButton;

    public RegisterPanel(JPanel mainPanel, CardLayout cardLayout) {
        // --- Style and Layout ---
        setBackground(new Color(240, 248, 255)); // AliceBlue
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // --- Components ---
        JLabel titleLabel = new JLabel("Create a New Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        
        fullNameField = new JTextField(15);
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        emailField = new JTextField(15);
        registerButton = new JButton("Create Account");
        backButton = new JButton("Back to Login");

        // --- NEW: Add Icons ---
        try {
            registerButton.setIcon(new ImageIcon(getClass().getResource("/icons/register.png")));
        } catch (Exception e) {
            System.out.println("Register icon not found.");
        }
        
        // --- Add Components to Panel ---
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);
        
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        add(fullNameField, gbc);
        
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
        add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        add(emailField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(getBackground());
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        add(buttonPanel, gbc);

        // --- Action Listeners (Functionality remains the same) ---
        registerButton.addActionListener(e -> {
            String fullName = fullNameField.getText();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String email = emailField.getText();

            UserDAO userDAO = new UserDAO();
            if (userDAO.registerUser(fullName, username, password, email)) {
                JOptionPane.showMessageDialog(this, "Registration Successful! Please login.");
                cardLayout.show(mainPanel, "login");
            } else {
                JOptionPane.showMessageDialog(this, "Registration Failed. Username or email may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "login"));
    }
}