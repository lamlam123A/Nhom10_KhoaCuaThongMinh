package com.example.smartdoorlock.models;

public class ValidCardIdResponse {
    private int id;
    private String cardId;
    private String createdAt;
    private String updatedAt;

    // Constructor, getters, and setters
    public ValidCardIdResponse() {
    }

    public ValidCardIdResponse(int id, String cardId, String createdAt, String updatedAt) {
        this.id = id;
        this.cardId = cardId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}