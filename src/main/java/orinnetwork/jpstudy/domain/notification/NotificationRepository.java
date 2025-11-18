package orinnetwork.jpstudy.domain.notification;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientIdAndIsReadFalseOrderByIdDesc(Long recipientId);

    long countByRecipientIdAndIsReadFalse(Long recipientId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true "
            + "WHERE n.recipientId = :recipientId AND n.isRead = false")
    void markAllAsReadForUser(@Param("recipientId") Long recipientId);
}
