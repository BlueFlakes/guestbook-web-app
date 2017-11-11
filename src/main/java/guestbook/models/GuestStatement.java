package guestbook.models;

import lombok.Getter;

@Getter
public class GuestStatement {
    private Integer id;
    private String firstname;
    private String email;
    private String message;

    public GuestStatement(Integer id, String firstname, String email, String message) {
        this.id = id;
        this.firstname = firstname;
        this.email = email;
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format("%d. %s, %s \n%s\n", this.id, this.firstname, this.email, this.message);
    }
}
