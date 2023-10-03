package ru.oke.sinebot.keenetic.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.oke.sinebot.keenetic.model.Model;

import java.util.List;
import java.util.Optional;

public interface ModelRepository extends JpaRepository<Model, Long> {
    List<Model> findByVendorId(Long vendorId);

    List<Model> findByName(String name);

    @Override
    @EntityGraph(value = "model-vendor-entity-graph")
    Optional<Model> findById(Long id);

    @Override
    @EntityGraph(value = "model-vendor-entity-graph")
    List<Model> findAll();
}
