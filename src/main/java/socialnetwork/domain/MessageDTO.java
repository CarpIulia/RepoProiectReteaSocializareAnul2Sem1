package socialnetwork.domain;

import java.time.LocalDateTime;
import java.util.List;

public class MessageDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String message;
    private String date;

    public MessageDTO(Long id, Utilizator from, String message, String date) {
        this.firstName = from.getFirstName();
        this.lastName = from.getLastName();
        this.message = message;
        this.date = date;
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }
}
