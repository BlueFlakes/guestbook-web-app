package guestbook.dao;

import guestbook.exceptions.DAOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static Connection connection;
    private static final String path = "jdbc:sqlite:data/database.db";

    static {
        connection = null;
    }

    private DBConnection() {}

    public static Connection getConnection() throws DAOException {

        if (connection == null) {

            synchronized (DBConnection.class) {

                if (connection == null) {
                    connectWithDatabase();
                }
            }
        }

        return connection;
    }

    private static void connectWithDatabase() throws DAOException {

        try {
            connection = DriverManager.getConnection(path);

        } catch (SQLException e) {
            throw new DAOException("Could not connect with database.");
        }
    }

    public static void closeConnection() throws DAOException {

        try {
            connection.close();

        } catch (SQLException e) {
            throw new DAOException("Could not close connection with database.");
        }
    }
}
