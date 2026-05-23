package com.grupo18.team_service.repositories;


import com.grupo18.team_service.models.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    // Método para validar que no se repitan los nombres de los equipos
    Optional<Team> findByNombre(String nombre);

    // CRUD obligatorio: Listar equipos por el juego en el que compiten
    List<Team> findByJuegoPrincipalId(Long juegoPrincipalId);

    // CRUD obligatorio: Listar equipos buscando por el ID de su capitán
    List<Team> findByCapitanId(Long capitanId);

    // CRUD obligatorio: Listar equipos por su estado (ej: "ACTIVO" o "INACTIVO")
    List<Team> findByEstado(String estado);
}