package com.grupo18.tournament_service.repositories;

import com.grupo18.tournament_service.models.Tournament;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
	Optional<Tournament> findByName(String name);
	List<Tournament> findByGameId(Long gameId);
	List<Tournament> findByOrganizerId(Long organizerId);
	List<Tournament> findByState(String state);
}
