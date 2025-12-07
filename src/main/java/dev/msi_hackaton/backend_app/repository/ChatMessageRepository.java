package dev.msi_hackaton.backend_app.repository;

import dev.msi_hackaton.backend_app.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByOrder_IdOrderBySentAt(Long orderId);

    List<ChatMessage> findByOrder_IdAndIsReadFalse(Long orderId);

    Long countByOrder_IdAndIsReadFalse(Long orderId);

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.order.id = :orderId ORDER BY cm.sentAt")
    List<ChatMessage> findMessagesByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.order.id = :orderId AND cm.isRead = false")
    List<ChatMessage> findUnreadMessagesByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.order.id = :orderId AND cm.isRead = false")
    Long countUnreadMessagesByOrderId(@Param("orderId") Long orderId);
}