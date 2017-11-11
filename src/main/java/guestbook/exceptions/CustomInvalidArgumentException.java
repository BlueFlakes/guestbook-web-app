package guestbook.exceptions;

public class CustomInvalidArgumentException extends Exception {
    public CustomInvalidArgumentException(String message) {
        super(message);
    }
}
