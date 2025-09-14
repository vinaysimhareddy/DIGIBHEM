package com.Library;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
// import java.util.List;

public class LibraryGUI {

    private final Library library = new Library();
    private final JFrame frame;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel;
    private int currentMemberId = -1;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LibraryGUI::new);
    }

    public LibraryGUI() {
        frame = new JFrame("Library Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 600);
        mainPanel = new JPanel(cardLayout);
        mainPanel.add(createWelcomePanel(), "Welcome");
        mainPanel.add(createAdminPanel(), "Admin");
        mainPanel.add(createUserPanel(), "User");
        frame.add(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        JLabel welcomeLabel = new JLabel("Library Management System", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        JButton adminButton = new JButton("Admin Login");
        JButton userButton = new JButton("Member Login/Registration");
        adminButton.addActionListener(e -> cardLayout.show(mainPanel, "Admin"));
        userButton.addActionListener(e -> cardLayout.show(mainPanel, "User"));
        panel.add(welcomeLabel);
        panel.add(adminButton);
        panel.add(userButton);
        return panel;
    }

    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JTextArea adminBooksTextArea = new JTextArea(15, 40);
        adminBooksTextArea.setEditable(false);
        adminBooksTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JPanel addPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        addPanel.setBorder(BorderFactory.createTitledBorder("Add New Book"));
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JButton addBookButton = new JButton("Add Book");
        addPanel.add(new JLabel("Title:")); addPanel.add(titleField);
        addPanel.add(new JLabel("Author:")); addPanel.add(authorField);
        addPanel.add(new JLabel()); addPanel.add(addBookButton);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel deletePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        deletePanel.setBorder(BorderFactory.createTitledBorder("Delete Book"));
        JTextField deleteIdField = new JTextField(5);
        JButton deleteBookButton = new JButton("Delete Book by ID");
        deletePanel.add(new JLabel("Enter Book ID:")); deletePanel.add(deleteIdField); deletePanel.add(deleteBookButton);
        JPanel controlPanel = new JPanel();
        JButton viewBooksButton = new JButton("View/Refresh All Books");
        JButton backButton = new JButton("Back to Main Menu");
        controlPanel.add(viewBooksButton); controlPanel.add(backButton);
        bottomPanel.add(deletePanel, BorderLayout.CENTER); bottomPanel.add(controlPanel, BorderLayout.SOUTH);
        panel.add(addPanel, BorderLayout.NORTH); panel.add(new JScrollPane(adminBooksTextArea), BorderLayout.CENTER); panel.add(bottomPanel, BorderLayout.SOUTH);
        addBookButton.addActionListener(e -> { String message = library.addBook(titleField.getText(), authorField.getText()); JOptionPane.showMessageDialog(frame, message); titleField.setText(""); authorField.setText(""); adminBooksTextArea.setText(library.getAllBooksAsString()); });
        deleteBookButton.addActionListener(e -> { try { int bookId = Integer.parseInt(deleteIdField.getText()); int choice = JOptionPane.showConfirmDialog(frame, "Are you sure you want to permanently delete this book?", "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE); if (choice == JOptionPane.YES_OPTION) { String message = library.deleteBook(bookId); JOptionPane.showMessageDialog(frame, message); deleteIdField.setText(""); adminBooksTextArea.setText(library.getAllBooksAsString()); } } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(frame, "Invalid ID. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE); } });
        viewBooksButton.addActionListener(e -> adminBooksTextArea.setText(library.getAllBooksAsString()));
        backButton.addActionListener(e -> { cardLayout.show(mainPanel, "Welcome"); adminBooksTextArea.setText(""); });
        return panel;
    }

    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel accessPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        JPanel registerPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        registerPanel.setBorder(BorderFactory.createTitledBorder("New Member Registration"));
        JTextField registerNameField = new JTextField();
        JButton registerButton = new JButton("Register");
        registerPanel.add(new JLabel("Enter Your Full Name:")); registerPanel.add(registerNameField); registerPanel.add(registerButton);
        JPanel loginPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        loginPanel.setBorder(BorderFactory.createTitledBorder("Existing Member Login"));
        JTextField memberIdField = new JTextField();
        JButton loginButton = new JButton("Login");
        loginPanel.add(new JLabel("Enter Your Member ID:")); loginPanel.add(memberIdField); loginPanel.add(loginButton);
        accessPanel.add(registerPanel); accessPanel.add(loginPanel);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setVisible(false);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] searchOptions = {"Title", "Author", "ID"};
        JComboBox<String> searchCriteriaDropdown = new JComboBox<>(searchOptions);
        JTextField searchQueryField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchPanel.add(new JLabel("Search By:")); searchPanel.add(searchCriteriaDropdown); searchPanel.add(searchQueryField); searchPanel.add(searchButton);
        JTextArea userBooksTextArea = new JTextArea(15, 40);
        userBooksTextArea.setEditable(false); userBooksTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField bookIdField = new JTextField(5);
        JButton issueButton = new JButton("Issue Book");
        JButton returnButton = new JButton("Return Book");
        actionPanel.add(new JLabel("Enter Book ID to Action:")); actionPanel.add(bookIdField); actionPanel.add(issueButton); actionPanel.add(returnButton);
        contentPanel.add(searchPanel, BorderLayout.NORTH); contentPanel.add(new JScrollPane(userBooksTextArea), BorderLayout.CENTER); contentPanel.add(actionPanel, BorderLayout.SOUTH);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JLabel welcomeUserLabel = new JLabel("Please register or log in.", SwingConstants.CENTER);
        JButton backButton = new JButton("Back to Main Menu");
        bottomPanel.add(welcomeUserLabel, BorderLayout.CENTER); bottomPanel.add(backButton, BorderLayout.EAST);

        panel.add(accessPanel, BorderLayout.NORTH); panel.add(contentPanel, BorderLayout.CENTER); panel.add(bottomPanel, BorderLayout.SOUTH);

        registerButton.addActionListener(e -> { String message = library.registerMember(registerNameField.getText()); JOptionPane.showMessageDialog(frame, message, "Registration Status", JOptionPane.INFORMATION_MESSAGE); registerNameField.setText(""); });
        loginButton.addActionListener(e -> { try { int memberId = Integer.parseInt(memberIdField.getText()); Member member = library.findMemberById(memberId); if (member != null) { currentMemberId = member.memberId; welcomeUserLabel.setText("Welcome, " + member.name + " (ID: " + member.memberId + ")"); contentPanel.setVisible(true); accessPanel.setVisible(false); userBooksTextArea.setText(library.getAllBooksAsString()); } else { JOptionPane.showMessageDialog(frame, "Member ID not found.", "Login Failed", JOptionPane.ERROR_MESSAGE); } } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(frame, "Please enter a valid Member ID.", "Error", JOptionPane.ERROR_MESSAGE); } });
        backButton.addActionListener(e -> { cardLayout.show(mainPanel, "Welcome"); contentPanel.setVisible(false); accessPanel.setVisible(true); welcomeUserLabel.setText("Please register or log in."); memberIdField.setText(""); currentMemberId = -1; });
        issueButton.addActionListener(e -> { handleBookAction(bookIdField, "issue"); userBooksTextArea.setText(library.getAllBooksAsString()); });
        returnButton.addActionListener(e -> { handleBookAction(bookIdField, "return"); userBooksTextArea.setText(library.getAllBooksAsString()); });
        searchButton.addActionListener(e -> { String criteria = (String) searchCriteriaDropdown.getSelectedItem(); String query = searchQueryField.getText(); String results; if (query.trim().isEmpty()) { results = "Please enter a search query."; } else { switch (criteria) { case "ID": try { int bookId = Integer.parseInt(query); Book foundBook = library.findBookById(bookId); results = (foundBook != null) ? library.formatBookList(Collections.singletonList(foundBook)) : "No book found with ID: " + bookId; } catch (NumberFormatException ex) { results = "Invalid ID. Please enter a number."; } break; case "Title": results = library.formatBookList(library.findBooksByTitle(query)); break; case "Author": results = library.formatBookList(library.findBooksByAuthor(query)); break; default: results = "Invalid search criteria."; } } userBooksTextArea.setText(results); });
        return panel;
    }

    private void handleBookAction(JTextField bookIdField, String action) {
        if (currentMemberId == -1) { JOptionPane.showMessageDialog(frame, "An unexpected error occurred. Please log in again.", "Error", JOptionPane.ERROR_MESSAGE); return; }
        try {
            int bookId = Integer.parseInt(bookIdField.getText());
            String message;
            if (action.equals("issue")) { message = library.issueBook(bookId, currentMemberId); } 
            else { message = library.returnBook(bookId, currentMemberId); }
            JOptionPane.showMessageDialog(frame, message);
            bookIdField.setText("");
        } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(frame, "Error: Please enter a valid number for the Book ID.", "Error", JOptionPane.ERROR_MESSAGE); }
    }
}