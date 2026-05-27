package com.grupo18.registration_service.services;
import com.grupo18.registration_service.models.Registration;
import java.util.List;

public interface RegistrationService {
    Registration save(Registration registration);
    Registration findById(Long id);
    List<Registration> findByTeamId(Long teamId);
    List<Registration> findByTournamentId(Long tournamentId);
    List<Registration> findAll();
    Registration updateStatus(Long id, String status);
    void cancelById(Long id);
    boolean existsByTeamIdAndTournamentId(Long teamId, Long tournamentId);
}