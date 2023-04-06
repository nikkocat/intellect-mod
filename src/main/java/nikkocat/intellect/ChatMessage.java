package nikkocat.intellect;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatMessage {
    private int id;
    private String user;
    private String message;
    private LocalDateTime timestamp;

    public ChatMessage(int id, String user, String message, LocalDateTime timestamp) {
        this.id = id;
        this.user = user;
        this.message = message;
        this.timestamp = timestamp;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public String getUser() {return user;}
    public void setUser(String user) {this.user = user;}
    public String getMessage() {return message;}
    public void setMessage(String message) {this.message = message;}
    public LocalDateTime getTimestamp() {return timestamp;}
    public void setTimestamp(LocalDateTime timestamp) {this.timestamp = timestamp;}

    public String toStringLine() {
        return "[" + timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] " + user + ": " + message;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id=" + id +
                ", user='" + user + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
