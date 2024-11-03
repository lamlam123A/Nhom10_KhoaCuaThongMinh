package hunre.iot.smartdoorlock.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hunre.iot.smartdoorlock.entity.ValidCardId;

public interface ValidCardIdRepository extends JpaRepository<ValidCardId, Integer> {
}