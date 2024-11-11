package com.example.smartdoorlock.models;

public class DefaultKeyResponse {
    private int id;
    private String keyValue;
    private String createdAt;
    private String updatedAt;

    public DefaultKeyResponse() {
    }

    public DefaultKeyResponse(int id, String keyValue, String createdAt, String updatedAt) {
        this.id = id;
        this.keyValue = keyValue;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    // Getter and Setter

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
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