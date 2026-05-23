package com.grupo18.registration_service.repositories;

import com.grupo18.registration_service.models.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    /**

     REGLA DE NEGOCIO CRÍTICA:
     Un equipo no puede estar inscrito dos veces en el mismo torneo.
     Este método nos permite buscar si ya existe una relación previa.*/
    Optional<Registration> findByTeamIdAndTournamentId(Long teamId, Long tournamentId);

    // Listar todos los equipos inscritos en un torneo específico
    List<Registration> findByTournamentId(Long tournamentId);

    // Listar todos los torneos en los que un equipo participa
    List<Registration> findByTeamId(Long teamId);

    // Listar según estado (útil para reportes, ej: traer solo "CONFIRMED")
    List<Registration> findByStatus(String status);
}