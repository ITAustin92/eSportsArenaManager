package com.grupo18.game_service.services;
import com.grupo18.game_service.models.Game;
import java.util.List;
public interface GameService {
    List<Game> findAll();
    List<Game> findByEstado(String estado);
    Game findById(Long id);
    Game save(Game juego);
    void deleteById(Long id);
    Game updateById(Long id, Game juego);
}
