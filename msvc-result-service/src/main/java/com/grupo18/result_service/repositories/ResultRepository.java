package com.grupo18.result_service.repositories;

import com.grupo18.result_service.models.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {

    // Regla de Negocio: Buscar el resultado de un partido específico
    // Usamos Optional porque un partido podría no tener resultado aún
    Optional<Result> findByMatchId(Long matchId);

    // Regla de Negocio: Obtener todos los partidos ganados por un equipo
    // Útil para generar tablas de posiciones o estadísticas históricas
    List<Result> findByWinnerTeamId(Long winnerTeamId);

    // Regla de Negocio: Verificar si ya existe un resultado para un partido
    // Esto es vital para evitar que se reporten dos resultados para el mismo evento
    boolean existsByMatchId(Long matchId);
}