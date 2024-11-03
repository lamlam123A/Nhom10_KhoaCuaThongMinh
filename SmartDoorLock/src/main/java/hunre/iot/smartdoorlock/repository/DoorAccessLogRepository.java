package hunre.iot.smartdoorlock.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hunre.iot.smartdoorlock.entity.DoorAccessLog;

public interface DoorAccessLogRepository extends JpaRepository<DoorAccessLog, Long> {
}