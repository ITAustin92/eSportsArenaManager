package com.grupo18.tournament_service.services;

import com.grupo18.tournament_service.exceptions.TournamentException;
import com.grupo18.tournament_service.models.Tournament;
import com.grupo18.tournament_service.models.dtos.GameDTO;
import com.grupo18.tournament_service.models.dtos.UserDTO;
import com.grupo18.tournament_service.repositories.TournamentRepository;
import com.grupo18.tournament_service.clients.GameClient;
import com.grupo18.tournament_service.clients.UserClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        return this.tournamentRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Tournament findById(Long id) {
        return this.tournamentRepository.findById(id).orElseThrow(
                () -> new TournamentException("Torneo no encontrado")
        );
    }

    @Transactional(readOnly = true)
    @Override
    public List<Tournament> findByGameId(Long gameId) {
        return this.tournamentRepository.findByGameId(gameId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Tournament> findByOrganizerId(Long organizerId) {
        return this.tournamentRepository.findByOrganizerId(organizerId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Tournament> findByState(String state) {
        return this.tournamentRepository.findByState(state);
    }

    @Transactional
    @Override
    public Tournament save(Tournament tournament) {

        if (this.tournamentRepository.findByName(tournament.getName()).isPresent()) {
            throw new TournamentException("El nombre del torneo ya está registrado");
        }


        if (tournament.getEndDate().isBefore(tournament.getStartDate())) {
            throw new TournamentException("La fecha de término no puede ser anterior a la de inicio");
        }


        try {
            GameDTO game = gameClient.getGameById(tournament.getGameId());
            if (game == null || !"ACTIVO".equalsIgnoreCase(game.getEstado())) {
                throw new TournamentException("El juego seleccionado no es válido o no está activo");
            }
        } catch (Exception e) {
            throw new TournamentException("Error de conexión al validar el juego en el puerto 8001: " + e.getMessage());
        }


        try {
            UserDTO organizer = userClient.getUserById(tournament.getOrganizerId());
            if (organizer == null || "SANCIONADO".equalsIgnoreCase(organizer.getState()) || "INACTIVO".equalsIgnoreCase(organizer.getState())) {
                throw new TournamentException("El organizador no existe o no está habilitado para crear torneos");
            }
        } catch (Exception e) {
            throw new TournamentException("Error de conexión al validar al organizador en el puerto 8001: " + e.getMessage());
        }


        return this.tournamentRepository.save(tournament);
    }

    @Transactional
    @Override
    public Tournament updateById(Long id, Tournament tournament) {
        return this.tournamentRepository.findById(id).map(element -> {


            if (!element.getName().equals(tournament.getName()) &&
                    this.tournamentRepository.findByName(tournament.getName()).isPresent()) {
                throw new TournamentException("El nuevo nombre ya está ocupado por otro torneo");
            }


            if (tournament.getEndDate().isBefore(tournament.getStartDate())) {
                throw new TournamentException("La fecha de término no puede ser anterior a la de inicio");
            }

            element.setName(tournament.getName());
            element.setStartDate(tournament.getStartDate());
            element.setEndDate(tournament.getEndDate());
            element.setState(tournament.getState());


            return this.tournamentRepository.save(element);
        }).orElseThrow(
                () -> new TournamentException("Torneo no encontrado para actualizar")
        );
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Tournament tournament = this.findById(id);
        tournament.setState("CANCELLED");
        this.tournamentRepository.save(tournament);
    }
}