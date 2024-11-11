package com.example.smartdoorlock.models;


public class ValidCardIdRequest {
    private String cardId;

    public ValidCardIdRequest(String cardId) {
        this.cardId = cardId;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }
}

//public class ValidCardIdRequest {
//    private int id;
//    private String cardId;
//
//    // Constructor, getters, and setters
//    public ValidCardIdRequest() {
//    }
//
//    public ValidCardIdRequest(int id, String cardId) {
//        this.id = id;
//        this.cardId = cardId;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public String getCardId() {
//        return cardId;
//    }
//
//    public void setCardId(String cardId) {
//        this.cardId = cardId;
//    }
//}