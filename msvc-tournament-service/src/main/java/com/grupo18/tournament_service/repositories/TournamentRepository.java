package com.grupo18.tournament_service.repositories;

import com.grupo18.tournament_service.models.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {

    // Método para validar que no se repitan los nombres de los torneos
    Optional<Tournament> findByName(String name);

    // CRUD: Listar todos los torneos que pertenecen a un juego específico (ej: todos los de Valorant)
    List<Tournament> findByGameId(Long gameId);

    // CRUD: Listar todos los torneos creados por un organizador en específico (validando su userId)
    List<Tournament> findByOrganizerId(Long organizerId);

    // CRUD: Listar torneos según su estado (ej: traer solo los "UPCOMING" o "IN_PROGRESS")
    List<Tournament> findByState(String state);
}