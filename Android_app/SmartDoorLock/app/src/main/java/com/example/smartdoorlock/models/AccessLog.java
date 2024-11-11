package com.example.smartdoorlock.models;

public class AccessLog {
    private Long id;
    private AccessType accessType;
    private String doorStatus;
    private String accessResult;
    private String accessTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
    }

    public String getDoorStatus() {
        return doorStatus;
    }

    public void setDoorStatus(String doorStatus) {
        this.doorStatus = doorStatus;
    }

    public String getAccessResult() {
        return accessResult;
    }

    public void setAccessResult(String accessResult) {
        this.accessResult = accessResult;
    }

    public String getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(String accessTime) {
        this.accessTime = accessTime;
    }
}

//package com.example.smartdoorlock.models;
//
//import java.util.Date;
//
//public class AccessLog {
//    private Long id;
//    private AccessType accessType;
//    private String accessResult;
//    private Date accessTime;
//
//    // Constructors, getters, and setters
//
//    public AccessLog() {
//    }
//
//    public AccessLog(Long id, AccessType accessType, String accessResult, Date accessTime) {
//        this.id = id;
//        this.accessType = accessType;
//        this.accessResult = accessResult;
//        this.accessTime = accessTime;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public AccessType getAccessType() {
//        return accessType;
//    }
//
//    public void setAccessType(AccessType accessType) {
//        this.accessType = accessType;
//    }
//
//    public String getAccessResult() {
//        return accessResult;
//    }
//
//    public void setAccessResult(String accessResult) {
//        this.accessResult = accessResult;
//    }
//
//    public Date getAccessTime() {
//        return accessTime;
//    }
//
//    public void setAccessTime(Date accessTime) {
//        this.accessTime = accessTime;
//    }
//}