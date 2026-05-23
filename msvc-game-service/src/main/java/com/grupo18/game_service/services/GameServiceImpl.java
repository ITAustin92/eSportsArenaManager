package com.grupo18.game_service.services;


import com.grupo18.game_service.exceptions.GameException;
import com.grupo18.game_service.models.Game;
import com.grupo18.game_service.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GameServiceImpl implements GameService {

    @Autowired
    private GameRepository gameRepository;

    @Transactional(readOnly = true)
    @Override
    public List<Game> findByEstado(String estado) {
        // Cumple la regla: "Listar juegos activos"
        return this.gameRepository.findByEstado(estado);
    }

    @Transactional(readOnly = true)
    @Override
    public Game findById(Long id) {
        return this.gameRepository.findById(id).orElseThrow(
                () -> new GameException("Juego no encontrado")
        );
    }

    @Transactional
    @Override
    public Game save(Game juego) {
        // Cumple la regla de negocio: "Nombre de juego único"
        if(this.gameRepository.findByNombre(juego.getNombre()).isPresent()){
            throw new GameException("El nombre del juego ya existe");
        }
        return this.gameRepository.save(juego);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        // El caso exige "Desactivar juego" en lugar de eliminarlo físicamente
        Game juego = this.findById(id);
        juego.setEstado("INACTIVO");

        this.gameRepository.save(juego);
    }

    @Transactional
    @Override
    public Game updateById(Long id, Game juego) {
        // Cumple la regla: "Actualizar modalidad o reglas generales"
        return this.gameRepository.findById(id).map(element -> {
            element.setModalidad(juego.getModalidad());
            element.setJugadoresPorEquipo(juego.getJugadoresPorEquipo());
            element.setEstado(juego.getEstado());
            // No permitimos actualizar el nombre porque la regla dice "Nombre de juego único"
            return this.gameRepository.save(element);
        }).orElseThrow(
                () -> new GameException("Juego no encontrado")
        );
    }
}