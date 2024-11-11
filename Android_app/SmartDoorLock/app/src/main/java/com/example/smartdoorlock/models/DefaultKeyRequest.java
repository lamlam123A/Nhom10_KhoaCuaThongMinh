package com.example.smartdoorlock.models;

//public class DefaultKeyRequest {
//    private String keyValue;
//
//    public DefaultKeyRequest(String keyValue) {
//        this.keyValue = keyValue;
//    }
//
//    public String getKeyValue() {
//        return keyValue;
//    }
//
//    public void setKeyValue(String keyValue) {
//        this.keyValue = keyValue;
//    }
//}

public class DefaultKeyRequest {
    private int id;
    private String keyValue;


    public DefaultKeyRequest() {
    }

    public DefaultKeyRequest(int id, String keyValue) {
        this.id = id;
        this.keyValue = keyValue;
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
}

