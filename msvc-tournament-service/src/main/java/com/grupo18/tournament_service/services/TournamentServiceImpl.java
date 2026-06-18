package com.grupo18.tournament_service.services;

import com.grupo18.tournament_service.clients.GameClient;
import com.grupo18.tournament_service.clients.UserClient;
import com.grupo18.tournament_service.exceptions.TournamentException;
import com.grupo18.tournament_service.models.Tournament;
import com.grupo18.tournament_service.models.dtos.GameDTO;
import com.grupo18.tournament_service.models.dtos.UserDTO;
import com.grupo18.tournament_service.repositories.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static reactor.netty.http.HttpConnectionLiveness.log;

@Service
public class TournamentServiceImpl implements TournamentService {
    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private GameClient gameClient;

    @Autowired
    private UserClient userClient;

    @Transactional(readOnly = true)
    @Override
    public List<Tournament> findAll() {
        log.info("Obteniendo listado completo de torneos");
        List<Tournament> torneos = this.tournamentRepository.findAll();
        log.info("Se encontraron {} torneo(s) en total", torneos.size());
        return torneos;
    }

    @Transactional(readOnly = true)
    @Override
    public Tournament findById(Long id) {
        log.info("Buscando torneo con ID: {}", id);
        return this.tournamentRepository.findById(id).orElseThrow(() -> {
            log.warn("Torneo con ID: {} no encontrado", id);
            return new TournamentException("Torneo no encontrado");
        });
    }

    @Transactional(readOnly = true)
    @Override
    public List<Tournament> findByGameId(Long gameId) {
        log.info("Buscando torneos del juego ID: {}", gameId);
        List<Tournament> torneos = this.tournamentRepository.findByGameId(gameId);
        log.info("Se encontraron {} torneo(s) para juego ID: {}", torneos.size(), gameId);
        return torneos;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Tournament> findByOrganizerId(Long organizerId) {
        log.info("Buscando torneos del organizador ID: {}", organizerId);
        List<Tournament> torneos = this.tournamentRepository.findByOrganizerId(organizerId);
        log.info("Se encontraron {} torneo(s) del organizador ID: {}", torneos.size(), organizerId);
        return torneos;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Tournament> findByState(String state) {
        log.info("Buscando torneos con estado: '{}'", state);
        List<Tournament> torneos = this.tournamentRepository.findByState(state);
        log.info("Se encontraron {} torneo(s) con estado '{}'", torneos.size(), state);
        return torneos;
    }

    @Transactional
    @Override
    public Tournament save(Tournament tournament) {
        log.info("Intentando crear torneo '{}'. Jugeo ID: {}, Organizador ID: {}",
                tournament.getName(), tournament.getGameId(), tournament.getOrganizerId());

        if (this.tournamentRepository.findByName(tournament.getName()).isPresent()) {
            log.warn("Nombre de torneo duplicado: '{}'", tournament.getName());
            throw new TournamentException("El nombre del torneo ya está registrado");
        }

        if (tournament.getEndDate().isBefore(tournament.getStartDate())) {
            log.warn("Fechas invalidas para torneo '{}'. Inicio: {}, Fin: {}",
                    tournament.getName(), tournament.getStartDate(), tournament.getEndDate());
            throw new TournamentException("La fecha de termino no puede ser anterior a la de inicio");
        }

        try {
            log.info("Validando juego ID: {} contra game-service", tournament.getGameId());
            GameDTO game = gameClient.getGameById(tournament.getGameId());
            if (game == null || !"ACTIVO".equalsIgnoreCase(game.getEstado())) {
                log.warn("Juego ID: {} no válido. Estado: {}",
                        tournament.getGameId(), game != null ? game.getEstado() : "null");
                throw new TournamentException("El juego seleccionado no es válido o no está activo");
            }
        } catch (TournamentException te) {
            throw te;
        } catch (Exception e) {
            log.error("Error al conectar con game-service para juego ID: {}. Causa: {}",
                    tournament.getGameId(), e.getMessage());
            throw new TournamentException("Error de conexión al validar el juego: " + e.getMessage());
        }

        try {
            log.info("Validando organizador ID: {} contra user-service", tournament.getOrganizerId());
            UserDTO organizer = userClient.getUserById(tournament.getOrganizerId());
            if (organizer == null
                    || "SANCIONADO".equalsIgnoreCase(organizer.getState())
                    || "INACTIVO".equalsIgnoreCase(organizer.getState())) {
                log.warn("Organizador ID: {} no habilitado. Estado: {}",
                        tournament.getOrganizerId(), organizer != null ? organizer.getState() : "null");
                throw new TournamentException("El organizador no existe o no está habilitado para crear torneos");
            }
        } catch (TournamentException te) {
            throw te;
        } catch (Exception e) {
            log.error("Error al conectar con user-service para organizador ID: {}. Causa: {}",
                    tournament.getOrganizerId(), e.getMessage());
            throw new TournamentException("Error de conexión al validar al organizador: " + e.getMessage());
        }

        Tournament saved = this.tournamentRepository.save(tournament);
        log.info("Torneo '{}' creado exitosamente con ID: {}", saved.getName(), saved.getTournamentId());
        return saved;
    }

    @Transactional
    @Override
    public Tournament updateById(Long id, Tournament tournament) {
        log.info("Actualizando torneo con ID: {}", id);
        return this.tournamentRepository.findById(id).map(element -> {

            if (!element.getName().equals(tournament.getName()) &&
                    this.tournamentRepository.findByName(tournament.getName()).isPresent()) {
                log.warn("Conflicto de nombre al actualizar torneo ID {}: '{}' ya está en uso",
                        id, tournament.getName());
                throw new TournamentException("El nuevo nombre ya está ocupado por otro torneo");
            }

            if (tournament.getEndDate().isBefore(tournament.getStartDate())) {
                log.warn("Fechas inválidas al actualizar torneo ID {}. Inicio: {}, Fin: {}",
                        id, tournament.getStartDate(), tournament.getEndDate());
                throw new TournamentException("La fecha de término no puede ser anterior a la de inicio");
            }

            element.setName(tournament.getName());
            element.setStartDate(tournament.getStartDate());
            element.setEndDate(tournament.getEndDate());
            element.setState(tournament.getState());

            Tournament updated = this.tournamentRepository.save(element);
            log.info("Torneo ID {} actualizado. Nuevo nombre: '{}', Estado: '{}'",
                    updated.getTournamentId(), updated.getName(), updated.getState());
            return updated;

        }).orElseThrow(() -> {
            log.warn("Torneo con ID: {} no encontrado para actualizar", id);
            return new TournamentException("Torneo no encontrado para actualizar");
        });
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        log.info("Cancelando torneo con ID: {}", id);
        Tournament tournament = this.findById(id);
        tournament.setState("CANCELLED");
        this.tournamentRepository.save(tournament);
        log.info("Torneo '{}' (ID: {}) cancelado exitosamente", tournament.getName(), id);
    }
}