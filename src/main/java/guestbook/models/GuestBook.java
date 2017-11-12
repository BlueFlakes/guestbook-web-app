package guestbook.models;

import guestbook.dao.GuestBookDao;
import guestbook.exceptions.DAOException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class GuestBook {
    private List<GuestStatement> guestStatements;
    private GuestBookDao guestBookDao;

    {
        this.guestStatements = new LinkedList<>();
    }

    public GuestBook() throws DAOException {
        this.guestBookDao = new GuestBookDao();
        loadStatements();
    }

    public List<GuestStatement> getGuestStatements() {
        return this.guestStatements;
    }

    public List<List<String>> getGuestStatementsAsNestedStrings() {
        List<List<String>> accumulator = new ArrayList<>();

        for (GuestStatement gStmt : this.guestStatements) {
            String firstname = gStmt.getFirstname();
            String date = getFormatedDate(gStmt.getDate());
            String message = gStmt.getMessage();

            accumulator.add(Arrays.asList(message, firstname, date));
        }

        return accumulator;
    }

    private String getFormatedDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy");
        return date.format(formatter);
    }

    private void loadStatements() throws DAOException {
        this.guestStatements = this.guestBookDao.getGuestStatements();
    }

    public void addGuestStatement(String name, String message) {
        addGuestStatementToList(name, message);
        saveGuestStatementToDatabase(name, message);
    }

    private void addGuestStatementToList(String name, String message) {
        int probablyLastIndex = this.guestStatements.size() - 1;
        int highestId = this.guestStatements.size() > 0 ? this.guestStatements.get(probablyLastIndex).getId()
                                                        : 0;
        int uniqueId = highestId + 1;

        GuestStatement stmt = new GuestStatement(uniqueId, name, LocalDateTime.now(), message);
        this.guestStatements.add(stmt);
    }

    private void saveGuestStatementToDatabase(String name, String message) {
        try {
            guestBookDao.addGuestStatement(name, message);
        } catch (DAOException e) {
            // TODO exception management
        }
    }
}
