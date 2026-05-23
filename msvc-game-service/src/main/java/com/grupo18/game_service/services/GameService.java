package com.grupo18.game_service.services;



import com.grupo18.game_service.models.Game;

import java.util.List;

public interface GameService {
    List<Game> findByEstado(String estado); // Para listar los activos
    Game findById(Long id);
    Game save(Game juego);
    void deleteById(Long id); // Usaremos este método para la "desactivación"
    Game updateById(Long id, Game juego);
}