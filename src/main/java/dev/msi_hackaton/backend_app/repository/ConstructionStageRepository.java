package dev.msi_hackaton.backend_app.repository;

import dev.msi_hackaton.backend_app.entity.ConstructionStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConstructionStageRepository extends JpaRepository<ConstructionStage, Long> {

    List<ConstructionStage> findByOrder_IdOrderBySequence(Long orderId);

    @Query("SELECT cs FROM ConstructionStage cs WHERE cs.order.id = :orderId ORDER BY cs.sequence")
    List<ConstructionStage> findStagesByOrderId(@Param("orderId") Long orderId);
}