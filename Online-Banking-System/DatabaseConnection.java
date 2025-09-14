package com.bank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection{
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/banking_db";
    private static final String DATABASE_USER = "root";
    private static final String DATABASE_PASSWORD = "Vinayreddy@7780";

    /**
     * Establishes and returns a connection to the MySQL database.
     * @return a Connection object to the database, or null if it fails.
     */
    public static Connection getConnection(){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
        } catch(SQLException e){
            System.out.println("error connecting to the MYSQL database: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }
}