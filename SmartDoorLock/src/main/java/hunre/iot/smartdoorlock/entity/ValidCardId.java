package hunre.iot.smartdoorlock.entity;

import java.sql.Timestamp;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class ValidCardId {
    @Id
    private int id = 1;
    private String cardId;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Getters and Setters if not using Lombok
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}