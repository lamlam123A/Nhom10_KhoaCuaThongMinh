package hunre.iot.smartdoorlock.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hunre.iot.smartdoorlock.entity.DefaultKey;

public interface DefaultKeyRepository extends JpaRepository<DefaultKey, Integer> {
}