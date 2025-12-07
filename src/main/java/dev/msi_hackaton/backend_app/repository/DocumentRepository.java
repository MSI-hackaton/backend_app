package dev.msi_hackaton.backend_app.repository;

import dev.msi_hackaton.backend_app.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByOrder_Id(Long orderId);

    @Query("SELECT d FROM Document d WHERE d.order.id = :orderId AND d.status = 'PENDING'")
    List<Document> findPendingDocumentsByOrderId(@Param("orderId") Long orderId);
}