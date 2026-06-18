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
import static reactor.netty.http.HttpConnectionLiveness.log;
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
        log.info("Obteniendo listado completo de inscripciones");
        List<Registration> lista = registrationRepository.findAll();
        log.info("Se encontraron {} inscripción(es) en total", lista.size());
        return lista;
    }

    @Transactional
    @Override
    public Registration save(Registration registration) {
        log.info("Intentando inscribir equipo ID: {} en torneo ID: {}",
                registration.getTeamId(), registration.getTournamentId());

        if (registrationRepository.findByTeamIdAndTournamentId(
                registration.getTeamId(), registration.getTournamentId()).isPresent()) {
            log.warn("Inscripción duplicada detectada. Equipo ID: {} ya está en torneo ID: {}",
                    registration.getTeamId(), registration.getTournamentId());
            throw new RegistrationException("El equipo ya se encuentra inscrito en este torneo");
        }

        try {
            log.info("Validando equipo ID: {} contra team-service", registration.getTeamId());
            TeamDTO team = teamClient.getTeamById(registration.getTeamId());
            if (team == null || !"ACTIVO".equalsIgnoreCase(team.getEstado())) {
                log.warn("Equipo ID: {} no válido para inscripción. Estado: {}",
                        registration.getTeamId(), team != null ? team.getEstado() : "null");
                throw new RegistrationException("El equipo no existe o no está activo para competir");
            }
        } catch (RegistrationException re) {
            throw re;
        } catch (Exception e) {
            log.error("Error al conectar con team-service para validar equipo ID: {}. Causa: {}",
                    registration.getTeamId(), e.getMessage());
            throw new RegistrationException("Error al conectar con el servicio de equipos: " + e.getMessage());
        }

        try {
            log.info("Validando torneo ID: {} contra tournament-service", registration.getTournamentId());
            TournamentDTO tournament = tournamentClient.getTournamentById(registration.getTournamentId());
            if (tournament == null || !"UPCOMING".equalsIgnoreCase(tournament.getState())) {
                log.warn("Torneo ID: {} no admite inscripciones. Estado: {}",
                        registration.getTournamentId(), tournament != null ? tournament.getState() : "null");
                throw new RegistrationException("El torneo no existe o no admite nuevas inscripciones");
            }
        } catch (RegistrationException re) {
            throw re;
        } catch (Exception e) {
            log.error("Error al conectar con tournament-service para validar torneo ID: {}. Causa: {}",
                    registration.getTournamentId(), e.getMessage());
            throw new RegistrationException("Error al conectar con el servicio de torneos: " + e.getMessage());
        }

        registration.setRegistrationDate(LocalDate.now());
        registration.setStatus("PENDING");
        Registration saved = registrationRepository.save(registration);
        log.info("Inscripción ID {} creada exitosamente. Equipo ID: {} en torneo ID: {}",
                saved.getRegistrationId(), saved.getTeamId(), saved.getTournamentId());
        return saved;
    }

    @Transactional
    @Override
    public Registration updateStatus(Long id, String status) {
        log.info("Actualizando estado de inscripción ID: {} a '{}'", id, status);
        Registration existing = registrationRepository.findById(id).orElseThrow(() -> {
            log.warn("Inscripción ID: {} no encontrada para actualizar estado", id);
            return new RegistrationException("Inscripción no encontrada");
        });
        existing.setStatus(status);
        Registration updated = registrationRepository.save(existing);
        log.info("Inscripción ID {} actualizada a estado '{}'", updated.getRegistrationId(), updated.getStatus());
        return updated;
    }

    @Transactional(readOnly = true)
    @Override
    public Registration findById(Long id) {
        log.info("Buscando inscripción con ID: {}", id);
        return registrationRepository.findById(id).orElseThrow(() -> {
            log.warn("Inscripción con ID: {} no encontrada", id);
            return new RegistrationException("Inscripción no encontrada");
        });
    }

    @Transactional(readOnly = true)
    @Override
    public List<Registration> findByTeamId(Long teamId) {
        log.info("Buscando inscripciones del equipo ID: {}", teamId);
        List<Registration> lista = registrationRepository.findByTeamId(teamId);
        log.info("Se encontraron {} inscripción(es) para equipo ID: {}", lista.size(), teamId);
        return lista;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Registration> findByTournamentId(Long tournamentId) {
        log.info("Buscando inscripciones del torneo ID: {}", tournamentId);
        List<Registration> lista = registrationRepository.findByTournamentId(tournamentId);
        log.info("Se encontraron {} inscripción(es) para torneo ID: {}", lista.size(), tournamentId);
        return lista;
    }

    @Transactional
    @Override
    public void cancelById(Long id) {
        log.info("Cancelando inscripción con ID: {}", id);
        Registration existing = registrationRepository.findById(id).orElseThrow(() -> {
            log.warn("Inscripción ID: {} no encontrada para cancelar", id);
            return new RegistrationException("Inscripción no encontrada");
        });
        existing.setStatus("CANCELLED");
        registrationRepository.save(existing);
        log.info("Inscripción ID {} cancelada exitosamente", id);
    }

    @Override
    public boolean existsByTeamIdAndTournamentId(Long teamId, Long tournamentId) {
        boolean existe = registrationRepository.findByTeamIdAndTournamentId(teamId, tournamentId).isPresent();
        log.info("Verificación de inscripción: equipo ID {} en torneo ID {} → {}",
                teamId, tournamentId, existe ? "INSCRITO" : "NO INSCRITO");
        return existe;
    }
}