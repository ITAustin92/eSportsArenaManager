package com.grupo18.tournament_service.services;

import com.grupo18.tournament_service.models.Tournament;

import java.util.List;

public interface TournamentService {
    List<Tournament> findAll();
    Tournament save(Tournament tournament);
    Tournament findById(Long id);
    List<Tournament> findByGameId(Long gameId);
    List<Tournament> findByOrganizerId(Long organizerId);
    List<Tournament> findByState(String state);
    Tournament updateById(Long id, Tournament tournament);
    void deleteById(Long id); // Para la desactivación lógica
}