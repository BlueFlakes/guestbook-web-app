package guestbook.dao;

import guestbook.exceptions.DAOException;
import guestbook.models.GuestStatement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class GuestBookDao {
    private DaoLayer dao;

    public GuestBookDao() throws DAOException {
        this.dao = new DaoLayer();
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
                String email = rs.getString(3);
                String message = rs.getString(4);

                GuestStatement statement = new GuestStatement(id, name, email, message);
                loadedStatements.add(statement);
            }
        } catch (SQLException e) {
            // pass
        }

        return loadedStatements;
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

    public void addGuestStatement(String firstname, String email, String message) throws DAOException {

        final String query = "INSERT INTO GuestBook VALUES(?, ?, ?, ?);";
        List<String> queryData = new ArrayList<>(Arrays.asList(firstname, email, message));
        int id = getActualMaxId() + 1;

        Function<Integer, String> getString = String::valueOf;
        BiConsumer<Integer, List<String>> addIdToRecord = (idx, temp) -> temp.add(0, getString.apply(idx));
        addIdToRecord.accept(id, queryData);

        this.dao.executeCommand(query, queryData);
    }
}
