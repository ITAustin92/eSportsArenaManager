package com.grupo18.registration_service.repositories;

import com.grupo18.registration_service.models.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    Optional<Registration> findByTeamIdAndTournamentId(Long teamId, Long tournamentId);
    List<Registration> findByTournamentId(Long tournamentId);
    List<Registration> findByTeamId(Long teamId);
    List<Registration> findByStatus(String status);
}