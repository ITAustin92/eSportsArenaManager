package com.grupo18.team_service.repositories;


import com.grupo18.team_service.models.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByNombre(String nombre);
    List<Team> findByJuegoPrincipalId(Long juegoPrincipalId);
    List<Team> findByCapitanId(Long capitanId);
    List<Team> findByEstado(String estado);
}