package com.grupo18.match_service.repositories;

import com.grupo18.match_service.models.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    // Regla de Negocio: Obtener todos los partidos de un torneo
    // Útil para mostrar el fixture o calendario del torneo
    List<Match> findByTournamentId(Long tournamentId);

    // Regla de Negocio: Obtener todos los partidos de un equipo (sea local o visitante)
    // Útil para que un equipo vea su agenda de juegos
    List<Match> findByHomeTeamIdOrAwayTeamId(Long homeTeamId, Long awayTeamId);

    // Regla de Negocio: Filtrar por estado del partido
    // Útil para buscar solo los partidos que ya terminaron (ej: "FINISHED")
    List<Match> findByStatus(String status);
}