package com.Library;

import java.util.ArrayList;
import java.util.List;

public class Library {

    private final DatabaseManager dbManager = new DatabaseManager();

    public String addBook(String title, String author) {
        if (title == null || title.trim().isEmpty() || author == null || author.trim().isEmpty()) return "Error: Title and Author cannot be empty.";
        if (dbManager.bookExists(title.trim(), author.trim())) return "This book already exists in the library.";
        Book newBook = new Book(0, title.trim(), author.trim());
        return dbManager.addBookToDB(newBook) ? "Book added successfully!" : "Failed to add book to database.";
    }

    public String getAllBooksAsString() {
        return formatBookList(dbManager.getAllBooksFromDB());
    }

    public Book findBookById(int bookId) {
        for (Book book : dbManager.getAllBooksFromDB()) if (book.id == bookId) return book;
        return null;
    }

    public List<Book> findBooksByTitle(String titleQuery) {
        List<Book> foundBooks = new ArrayList<>();
        for (Book book : dbManager.getAllBooksFromDB()) if (book.title.toLowerCase().contains(titleQuery.toLowerCase())) foundBooks.add(book);
        return foundBooks;
    }

    public List<Book> findBooksByAuthor(String authorQuery) {
        List<Book> foundBooks = new ArrayList<>();
        for (Book book : dbManager.getAllBooksFromDB()) if (book.author.toLowerCase().contains(authorQuery.toLowerCase())) foundBooks.add(book);
        return foundBooks;
    }

    public String formatBookList(List<Book> bookList) {
        if (bookList.isEmpty()) return "No books found.";
        StringBuilder sb = new StringBuilder("---\n");
        for (Book book : bookList) sb.append(book.toString()).append("\n");
        return sb.toString();
    }

    public String deleteBook(int bookId) {
        return dbManager.deleteBookFromDB(bookId) ? "Book with ID " + bookId + " deleted successfully." : "Failed to delete book with ID " + bookId + ". It may not exist.";
    }

    public String registerMember(String name) {
        if (name == null || name.trim().isEmpty()) return "Member name cannot be empty.";
        if (dbManager.memberExists(name.trim())) return "A member with this name already exists.";
        Member newMember = dbManager.addMemberToDB(name.trim());
        return newMember != null ? "Registration successful!\nYour new Member ID is: " + newMember.memberId : "Registration failed. Please try again.";
    }

    public Member findMemberById(int memberId) {
        return dbManager.findMemberById(memberId);
    }

    public String issueBook(int bookId, int memberId) {
        Book book = findBookById(bookId);
        if (book == null) return "Error: Book with ID " + bookId + " not found.";
        if (book.isIssued) return "Sorry, this book is already issued.";
        return dbManager.issueBookToMember(bookId, memberId) ? "Success! You have issued: " + book.title : "Failed to issue book.";
    }

    public String returnBook(int bookId, int currentMemberId) {
        Book book = findBookById(bookId);
        if (book == null) return "Error: Book with ID " + bookId + " not found.";
        if (!book.isIssued) return "This book was not issued in the first place.";
        int borrowerId = dbManager.getBorrowerId(bookId);
        if (borrowerId == -1) return "Error: Could not verify the borrower for this book.";
        if (borrowerId != currentMemberId) return "Error: This book was issued by another member (ID: " + borrowerId + ").";
        return dbManager.returnBookFromMember(bookId) ? "Thank you for returning: " + book.title : "Failed to return book.";
    }
}