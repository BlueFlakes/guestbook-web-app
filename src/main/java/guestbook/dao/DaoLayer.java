package guestbook.dao;

import guestbook.exceptions.DAOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.IntPredicate;

public class DaoLayer {
    private Connection connection;

    private String query;
    private List<String> queryData;

    public DaoLayer() throws DAOException {
        this.connection = DBConnection.getConnection();
    }

    public boolean executeCommand(String query, List<String> queryData)
            throws DAOException {

        prepareEnvironment(query, queryData);

        try {
            PreparedStatement stmt = prepareStatement();
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException("Invalid query statement.");
        }

        return true;
    }

    public ResultSet getQueryResultSet(String query, List<String> queryData) throws DAOException {

        prepareEnvironment(query, queryData);
        ResultSet result;

        try {
            PreparedStatement stmt = prepareStatement();
            result = stmt.executeQuery();

        } catch (SQLException e) {
            throw new IllegalStateException("Invalid query statement.");
        }

        return result;
    }

    private void prepareEnvironment(String query, List<String> queryData) {
        setGivenData(query, queryData);
        validateGivenData();
    }

    private void setGivenData(String query, List<String> queryData) {
        this.query = query;
        this.queryData = queryData;
    }

    private void validateGivenData() {
        DataValidator validator = new DataValidator();

        if (validator.isAnyInputNull())
            throw new IllegalStateException("Incorrect value delivered, found null!");

        if (!validator.isAmountOfInputsEqual())
            throw new IllegalStateException("Not equal amount of inputs delivered.");

    }

    private PreparedStatement prepareStatement() throws SQLException {

        PreparedStatement stmt = connection.prepareStatement(this.query);

        for (int i = 0; i < queryData.size(); i++) {
            String pieceOfData = queryData.get(i);
            int index = i + 1;

            stmt.setString(index, pieceOfData);
        }

        return stmt;
    }


    private class DataValidator {
        private final char injectionSign = '?';

        boolean isAnyInputNull() {
            return query == null || queryData == null;
        }

        boolean isAmountOfInputsEqual() {
            int queryDataInputsAmount = queryData.size();
            Long injectionsAmount = getAmountOfInjections();

            return queryDataInputsAmount == injectionsAmount;
        }

        private Long getAmountOfInjections() {
            IntPredicate isInjection = n -> n == injectionSign;

            return query.chars()
                        .filter(isInjection)
                        .count();
        }
    }
}
