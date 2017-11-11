package guestbook.dao;

import guestbook.exceptions.DAOException;
import guestbook.models.GuestStatement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class GuestBookDao {
    private DaoLayer dao;

    public GuestBookDao() throws DAOException {
        this.dao = new DaoLayer();
    }

    public static void main(String[] args) throws DAOException {
        GuestBookDao guestBookDao = new GuestBookDao();
        guestBookDao.addGuestStatement("naxxxxxme", "mexxxxxssage");
        guestBookDao.getGuestStatements().forEach(System.out::println);
    }

    public List<GuestStatement> getGuestStatements() throws DAOException {

        final String query = "SELECT * FROM GuestBook;";
        List<String> queryData = new ArrayList<>();
        ResultSet rs = this.dao.getQueryResultSet(query, queryData);

        List<GuestStatement> loadedStatements = new ArrayList<>();

        try {
            while (rs.next()) {
                Integer id = rs.getInt(1);
                String name = rs.getString(2);
                String date = rs.getString(3);
                String message = rs.getString(4);

                if (!isDateParseable(date)) continue;
                LocalDateTime parsedDate = LocalDateTime.parse(date);

                GuestStatement statement = new GuestStatement(id, name, parsedDate, message);
                loadedStatements.add(statement);
            }
        } catch (SQLException e) {
            // pass
        }

        return loadedStatements;
    }

    private boolean isDateParseable(String date) {
        try {
            LocalDateTime.parse(date);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private int getActualMaxId() throws DAOException {

        final String query = "SELECT max(id) FROM GuestBook;";
        List<String> queryData = new ArrayList<>();
        ResultSet rs = this.dao.getQueryResultSet(query, queryData);

        try {
            return rs.getInt(1);

        } catch (SQLException e) {
            // pass
        }

        return 0;
    }

    public void addGuestStatement(String firstname, String message) throws DAOException {

        final String query = "INSERT INTO GuestBook VALUES(?, ?, ?, ?);";
        Function<Integer, String> getString = String::valueOf;
        final int next = 1;

        String date = LocalDateTime.now().toString();
        String id = getString.apply(getActualMaxId() + next);

        List<String> queryData = new ArrayList<>(Arrays.asList(id, firstname, date, message));
        this.dao.executeCommand(query, queryData);
    }
}
