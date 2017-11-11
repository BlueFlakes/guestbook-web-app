package guestbook.models;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GuestStatement {
    private Integer id;
    private String firstname;
    private LocalDateTime date;
    private String message;

    public GuestStatement(Integer id, String firstname, LocalDateTime date, String message) {
        this.id = id;
        this.firstname = firstname;
        this.date = date;
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format("%d. %s, %s \n%s\n", this.id, this.firstname, this.date.toString(), this.message);
    }
}
