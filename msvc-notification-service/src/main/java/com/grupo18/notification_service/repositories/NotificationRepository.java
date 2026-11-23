package com.grupo18.notification_service.repositories;

import com.grupo18.notification_service.models.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
	List<Notification> findByUserId(Long userId);

	List<Notification> findByTeamId(Long teamId);

	List<Notification> findByTournamentId(Long tournamentId);

	List<Notification> findByStatus(String status);
}
