package guestbook.models;

import guestbook.dao.GuestBookDao;
import guestbook.exceptions.DAOException;

import java.time.LocalDateTime;
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
