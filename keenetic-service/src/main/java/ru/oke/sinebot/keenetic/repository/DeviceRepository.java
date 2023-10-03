package ru.oke.sinebot.keenetic.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.oke.sinebot.keenetic.model.Device;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    @EntityGraph(value = "device-info-entity-graph")
    List<Device> findByUserId(Long userId);

    @Override
    @EntityGraph(value = "device-info-entity-graph")
    Optional<Device> findById(Long id);

    @Override
    @EntityGraph(value = "device-info-entity-graph")
    List<Device> findAll();

    List<Device> findByIdIn(List<Long> ids);

    Optional<Device> findByDomainName(String domainName);
}
