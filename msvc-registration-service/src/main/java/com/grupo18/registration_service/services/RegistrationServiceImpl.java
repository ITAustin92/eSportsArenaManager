package com.grupo18.registration_service.services;

import com.grupo18.registration_service.clients.TeamClient;
import com.grupo18.registration_service.clients.TournamentClient;
import com.grupo18.registration_service.exceptions.RegistrationException;
import com.grupo18.registration_service.models.Registration;
import com.grupo18.registration_service.models.dtos.TeamDTO;
import com.grupo18.registration_service.models.dtos.TournamentDTO;
import com.grupo18.registration_service.repositories.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private TeamClient teamClient;

    @Autowired
    private TournamentClient tournamentClient;

    @Transactional(readOnly = true)
    @Override
    public List<Registration> findAll() {
        return registrationRepository.findAll();
    }

    @Transactional
    @Override
    public Registration save(Registration registration) {
        // 1. Regla de Negocio: Evitar duplicados (Un equipo, un torneo)
        if (registrationRepository.findByTeamIdAndTournamentId(
                registration.getTeamId(), registration.getTournamentId()).isPresent()) {
            throw new RegistrationException("El equipo ya se encuentra inscrito en este torneo");
        }

        // 2. Validación de equipo (Puerto 8002)
        try {
            TeamDTO team = teamClient.getTeamById(registration.getTeamId());
            if (team == null || !"ACTIVO".equalsIgnoreCase(team.getEstado())) {
                throw new RegistrationException("El equipo no existe o no está activo para competir");
            }
        } catch (Exception e) {
            throw new RegistrationException("Error al conectar con el servicio de equipos: " + e.getMessage());
        }

        // 3. Validación de torneo (Puerto 8003)
        try {
            TournamentDTO tournament = tournamentClient.getTournamentById(registration.getTournamentId());
            if (tournament == null || !"UPCOMING".equalsIgnoreCase(tournament.getState())) {
                throw new RegistrationException("El torneo no existe o no admite nuevas inscripciones");
            }
        } catch (Exception e) {
            throw new RegistrationException("Error al conectar con el servicio de torneos: " + e.getMessage());
        }

        // Si todo está bien, seteamos la fecha y guardamos
        registration.setRegistrationDate(LocalDate.now());
        registration.setStatus("PENDING"); // Estado inicial
        return registrationRepository.save(registration);
    }

    @Transactional
    @Override
    public Registration updateStatus(Long id, String status) {
        Registration existing = registrationRepository.findById(id)
                .orElseThrow(() -> new RegistrationException("Inscripción no encontrada"));
        existing.setStatus(status);
        return registrationRepository.save(existing);
    }

    @Transactional(readOnly = true)
    @Override
    public Registration findById(Long id) {
        return registrationRepository.findById(id)
                .orElseThrow(() -> new RegistrationException("Inscripción no encontrada"));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Registration> findByTeamId(Long teamId) {
        return registrationRepository.findByTeamId(teamId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Registration> findByTournamentId(Long tournamentId) {
        return registrationRepository.findByTournamentId(tournamentId);
    }

    @Transactional
    @Override
    public void cancelById(Long id) {
        Registration existing = registrationRepository.findById(id)
                .orElseThrow(() -> new RegistrationException("Inscripción no encontrada"));
        existing.setStatus("CANCELLED");
        registrationRepository.save(existing);
    }

    @Override
    public boolean existsByTeamIdAndTournamentId(Long teamId, Long tournamentId) {
        return registrationRepository.findByTeamIdAndTournamentId(teamId, tournamentId).isPresent();
    }
}