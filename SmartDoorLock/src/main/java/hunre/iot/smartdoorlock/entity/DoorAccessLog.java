package hunre.iot.smartdoorlock.entity;

import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class DoorAccessLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AccessType accessType; // Sử dụng enum mới
    private String doorStatus;
    private String accessResult;
    private Timestamp accessTime;

    // Getters and Setters if not using Lombok
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

    public Timestamp getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(Timestamp accessTime) {
        this.accessTime = accessTime;
    }
}

// package hunre.iot.smartdoorlock.entity;
//
// import java.sql.Timestamp;
//
// import jakarta.persistence.AccessType;
// import jakarta.persistence.Entity;
// import jakarta.persistence.EnumType;
// import jakarta.persistence.Enumerated;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import lombok.Data;
//
// @Entity
// @Data
// public class DoorAccessLog {
// @Id
// @GeneratedValue(strategy = GenerationType.IDENTITY)
// private Long id;
//
// @Enumerated(EnumType.STRING)
// private AccessType accessType;
//
// private String accessResult;
// private Timestamp accessTime;
//
// // Getters and Setters if not using Lombok
// public Long getId() {
// return id;
// }
//
// public void setId(Long id) {
// this.id = id;
// }
//
// public AccessType getAccessType() {
// return accessType;
// }
//
// public void setAccessType(AccessType accessType) {
// this.accessType = accessType;
// }
//
// public String getAccessResult() {
// return accessResult;
// }
//
// public void setAccessResult(String accessResult) {
// this.accessResult = accessResult;
// }
//
// public Timestamp getAccessTime() {
// return accessTime;
// }
//
// public void setAccessTime(Timestamp accessTime) {
// this.accessTime = accessTime;
// }
// }