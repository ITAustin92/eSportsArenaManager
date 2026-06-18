package com.grupo18.sanction_service.services;

import com.grupo18.sanction_service.clients.TeamClient;
import com.grupo18.sanction_service.clients.TournamentClient;
import com.grupo18.sanction_service.clients.UserClient;
import com.grupo18.sanction_service.exceptions.SanctionException;
import com.grupo18.sanction_service.models.Sanction;
import com.grupo18.sanction_service.models.dtos.TeamDTO;
import com.grupo18.sanction_service.models.dtos.TournamentDTO;
import com.grupo18.sanction_service.models.dtos.UserDTO;
import com.grupo18.sanction_service.repositories.SanctionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static reactor.netty.http.HttpConnectionLiveness.log;

@Service
public class SanctionServiceImpl implements SanctionService {

    @Autowired
    private SanctionRepository sanctionRepository;

    @Autowired
    private TournamentClient tournamentClient;

    @Autowired
    private TeamClient teamClient;

    @Autowired
    private UserClient userClient;

    @Transactional
    @Override
    public Sanction save(Sanction sanction) {
        log.info("Creando sanción de tipo '{}' para equipo ID: {} en torneo ID: {}",
                sanction.getType(), sanction.getTeamId(), sanction.getTournamentId());

        // Regla 1: El torneo debe existir
        log.info("Validando torneo ID: {} contra tournament-service", sanction.getTournamentId());
        TournamentDTO tournament = tournamentClient.getTournamentById(sanction.getTournamentId());
        if (tournament == null) {
            log.warn("Torneo ID: {} no encontrado al crear sanción", sanction.getTournamentId());
            throw new SanctionException("El torneo indicado no existe");
        }

        // Regla 2: El equipo debe existir
        log.info("Validando equipo ID: {} contra team-service", sanction.getTeamId());
        TeamDTO team = teamClient.getTeamById(sanction.getTeamId());
        if (team == null) {
            log.warn("Equipo ID: {} no encontrado al crear sanción", sanction.getTeamId());
            throw new SanctionException("El equipo indicado no existe");
        }

        // Regla 3: Si viene userId, el usuario debe existir (sanción individual)
        if (sanction.getUserId() != null) {
            log.info("Validando usuario infractor ID: {} contra user-service", sanction.getUserId());
            UserDTO user = userClient.getUserById(sanction.getUserId());
            if (user == null) {
                log.warn("Usuario infractor ID: {} no encontrado", sanction.getUserId());
                throw new SanctionException("El usuario infractor no existe");
            }
        }

        Sanction saved = sanctionRepository.save(sanction);
        log.info("Sanción ID {} creada exitosamente. Tipo: '{}', Estado: '{}'",
                saved.getSanctionId(), saved.getType(), saved.getStatus());
        return saved;
    }

    @Transactional(readOnly = true)
    @Override
    public Sanction findById(Long id) {
        log.info("Buscando sanción con ID: {}", id);
        return sanctionRepository.findById(id).orElseThrow(() -> {
            log.warn("Sanción con ID: {} no encontrada", id);
            return new SanctionException("Sanción no encontrada");
        });
    }

    @Transactional(readOnly = true)
    @Override
    public List<Sanction> findAll() {
        log.info("Obteniendo listado completo de sanciones");
        List<Sanction> lista = sanctionRepository.findAll();
        log.info("Se encontraron {} sanción(es) en total", lista.size());
        return lista;
    }

    @Transactional
    @Override
    public Sanction updateStatus(Long id, String newStatus) {
        log.info("Actualizando estado de sanción ID: {} a '{}'", id, newStatus);
        Sanction existing = this.findById(id);
        existing.setStatus(newStatus);
        Sanction updated = sanctionRepository.save(existing);
        log.info("Sanción ID {} actualizada a estado '{}'", updated.getSanctionId(), updated.getStatus());
        return updated;
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        log.info("Eliminando sanción con ID: {}", id);
        Sanction sanction = this.findById(id);
        sanctionRepository.delete(sanction);
        log.info("Sanción ID {} eliminada exitosamente", id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Sanction> findByTournamentId(Long tournamentId) {
        log.info("Buscando sanciones del torneo ID: {}", tournamentId);
        List<Sanction> lista = sanctionRepository.findByTournamentId(tournamentId);
        log.info("Se encontraron {} sanción(es) para torneo ID: {}", lista.size(), tournamentId);
        return lista;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Sanction> findByTeamId(Long teamId) {
        log.info("Buscando sanciones del equipo ID: {}", teamId);
        List<Sanction> lista = sanctionRepository.findByTeamId(teamId);
        log.info("Se encontraron {} sanción(es) para equipo ID: {}", lista.size(), teamId);
        return lista;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Sanction> findByUserId(Long userId) {
        log.info("Buscando sanciones del usuario ID: {}", userId);
        List<Sanction> lista = sanctionRepository.findByUserId(userId);
        log.info("Se encontraron {} sanción(es) para usuario ID: {}", lista.size(), userId);
        return lista;
    }
}