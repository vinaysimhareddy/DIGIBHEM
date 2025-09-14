package com.Library;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    // 🚨 IMPORTANT: Update these with your MySQL details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/library_db";
    private static final String USER = "root";
    private static final String PASS = "Vinayreddy@7780";

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    // --- Book Methods ---
    public boolean bookExists(String title, String author) {
        String sql = "SELECT COUNT(*) FROM books WHERE LOWER(title) = ? AND LOWER(author) = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title.toLowerCase());
            pstmt.setString(2, author.toLowerCase());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { System.out.println(e.getMessage()); }
        return false;
    }

    public boolean addBookToDB(Book book) {
        String sql = "INSERT INTO books(title, author) VALUES(?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.title);
            pstmt.setString(2, book.author);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { System.out.println(e.getMessage()); return false; }
    }

    public List<Book> getAllBooksFromDB() {
        String sql = "SELECT id, title, author, is_issued FROM books ORDER BY id";
        List<Book> books = new ArrayList<>();
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Book book = new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"));
                book.isIssued = rs.getBoolean("is_issued");
                books.add(book);
            }
        } catch (SQLException e) { System.out.println(e.getMessage()); }
        return books;
    }

    public boolean deleteBookFromDB(int bookId) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { System.out.println(e.getMessage()); return false; }
    }

    // --- Member Methods ---
    public boolean memberExists(String name) {
        String sql = "SELECT COUNT(*) FROM members WHERE LOWER(member_name) = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name.toLowerCase());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { System.out.println(e.getMessage()); }
        return false;
    }

    public Member addMemberToDB(String name) {
        String sql = "INSERT INTO members(member_name) VALUES(?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            if (pstmt.executeUpdate() > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) return new Member(rs.getInt(1), name);
                }
            }
        } catch (SQLException e) { System.out.println(e.getMessage()); }
        return null;
    }

    public Member findMemberById(int memberId) {
        String sql = "SELECT member_id, member_name FROM members WHERE member_id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return new Member(rs.getInt("member_id"), rs.getString("member_name"));
        } catch (SQLException e) { System.out.println(e.getMessage()); }
        return null;
    }

    // --- Transaction Methods ---
    public int getBorrowerId(int bookId) {
        String sql = "SELECT member_id FROM issued_books WHERE book_id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("member_id");
        } catch (SQLException e) { System.out.println(e.getMessage()); }
        return -1;
    }

    public boolean issueBookToMember(int bookId, int memberId) {
        String issueSql = "INSERT INTO issued_books(book_id, member_id) VALUES(?, ?)";
        String updateBookSql = "UPDATE books SET is_issued = TRUE WHERE id = ?";
        try (Connection conn = connect()) {
            conn.setAutoCommit(false);
            try (PreparedStatement issuePstmt = conn.prepareStatement(issueSql); PreparedStatement updatePstmt = conn.prepareStatement(updateBookSql)) {
                issuePstmt.setInt(1, bookId);
                issuePstmt.setInt(2, memberId);
                issuePstmt.executeUpdate();
                updatePstmt.setInt(1, bookId);
                updatePstmt.executeUpdate();
                conn.commit();
                return true;
            } catch (SQLException e) { conn.rollback(); System.out.println("Transaction rolled back: " + e.getMessage()); return false; }
        } catch (SQLException e) { System.out.println(e.getMessage()); return false; }
    }

    public boolean returnBookFromMember(int bookId) {
        String returnSql = "DELETE FROM issued_books WHERE book_id = ?";
        String updateBookSql = "UPDATE books SET is_issued = FALSE WHERE id = ?";
        try (Connection conn = connect()) {
            conn.setAutoCommit(false);
            try (PreparedStatement returnPstmt = conn.prepareStatement(returnSql); PreparedStatement updatePstmt = conn.prepareStatement(updateBookSql)) {
                returnPstmt.setInt(1, bookId);
                if (returnPstmt.executeUpdate() > 0) {
                    updatePstmt.setInt(1, bookId);
                    updatePstmt.executeUpdate();
                    conn.commit();
                    return true;
                } else { conn.rollback(); return false; }
            } catch (SQLException e) { conn.rollback(); System.out.println("Transaction rolled back: " + e.getMessage()); return false; }
        } catch (SQLException e) { System.out.println(e.getMessage()); return false; }
    }
}