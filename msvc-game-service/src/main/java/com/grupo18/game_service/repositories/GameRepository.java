package com.grupo18.game_service.repositories;


import com.grupo18.game_service.models.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    Optional<Game> findByNombre(String nombre);

    List<Game> findByEstado(String estado);
}