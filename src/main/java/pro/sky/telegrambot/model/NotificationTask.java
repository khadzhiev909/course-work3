package pro.sky.telegrambot.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class NotificationTask {
    @Id
    @GeneratedValue
    private Long id;
    private Long chatId;
    private String message;
    private LocalDateTime dataTime;


    public NotificationTask() {
    }

    public NotificationTask(Long id, Long chatId, String message, LocalDateTime dataTime) {
        this.id = id;
        this.chatId = chatId;
        this.message = message;
        this.dataTime = dataTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDataTime() {
        return dataTime;
    }

    public void setDataTime(LocalDateTime dataTime) {
        this.dataTime = dataTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTask that = (NotificationTask) o;
        return Objects.equals(id, that.id) && Objects.equals(chatId, that.chatId) && Objects.equals(message, that.message) && Objects.equals(dataTime, that.dataTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatId, message, dataTime);
    }

    @Override
    public String toString() {
        return "NotificationTask{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", message='" + message + '\'' +
                ", dataTime=" + dataTime +
                '}';
    }
}