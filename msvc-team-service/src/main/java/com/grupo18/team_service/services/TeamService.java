package com.grupo18.team_service.services;

import com.grupo18.team_service.models.Team;

import java.util.List;

public interface TeamService {
    List<Team> findByJuegoPrincipalId(Long juegoId);
    List<Team> findByCapitanId(Long capitanId);
    List<Team> findByEstado(String estado);
    Team findById(Long id);
    Team save(Team equipo);
    void deleteById(Long id);
    Team updateById(Long id, Team equipo);
}