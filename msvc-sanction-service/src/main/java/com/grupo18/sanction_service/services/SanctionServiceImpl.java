package com.grupo18.sanction_service.services;

import com.grupo18.sanction_service.clients.TeamClient;
import com.grupo18.sanction_service.clients.TournamentClient;
import com.grupo18.sanction_service.clients.UserClient;
import com.grupo18.sanction_service.models.Sanction;
import com.grupo18.sanction_service.models.dtos.TeamDTO;
import com.grupo18.sanction_service.models.dtos.TournamentDTO;
import com.grupo18.sanction_service.models.dtos.UserDTO;
import com.grupo18.sanction_service.repositories.SanctionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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


        TournamentDTO tournament = tournamentClient.getTournamentById(sanction.getTournamentId());
        if (tournament == null) {
            throw new RuntimeException("Error: El torneo indicado no existe.");
        }


        TeamDTO team = teamClient.getTeamById(sanction.getTeamId());
        if (team == null) {
            throw new RuntimeException("Error: El equipo indicado no existe.");
        }


        if (sanction.getUserId() != null) {
            UserDTO user = userClient.getUserById(sanction.getUserId());
            if (user == null) {
                throw new RuntimeException("Error: El usuario infractor no existe.");
            }
        }


        return sanctionRepository.save(sanction);
    }

    @Transactional(readOnly = true)
    @Override
    public Sanction findById(Long id) {
        return sanctionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sanción no encontrada"));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Sanction> findAll() {
        return sanctionRepository.findAll();
    }

    @Transactional
    @Override
    public Sanction updateStatus(Long id, String newStatus) {
        Sanction existingSanction = findById(id);
        existingSanction.setStatus(newStatus);
        return sanctionRepository.save(existingSanction);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Sanction sanction = findById(id);
        sanctionRepository.delete(sanction);
    }


    @Transactional(readOnly = true)
    @Override
    public List<Sanction> findByTournamentId(Long tournamentId) {
        return sanctionRepository.findByTournamentId(tournamentId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Sanction> findByTeamId(Long teamId) {
        return sanctionRepository.findByTeamId(teamId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Sanction> findByUserId(Long userId) {
        return sanctionRepository.findByUserId(userId);
    }
}