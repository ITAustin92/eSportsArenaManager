package com.grupo18.game_service.services;

import com.grupo18.game_service.exceptions.GameException;
import com.grupo18.game_service.models.Game;
import com.grupo18.game_service.repositories.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class GameServiceImpl implements GameService {

    private static final Logger log = LoggerFactory.getLogger(GameServiceImpl.class);

    @Autowired private GameRepository gameRepository;

    @Transactional(readOnly = true)
    @Override public List<Game> findAll() { return gameRepository.findAll(); }

    @Transactional(readOnly = true)
    @Override public List<Game> findByEstado(String estado) { return gameRepository.findByEstado(estado); }

    @Transactional(readOnly = true)
    @Override public Game findById(Long id) {
        return gameRepository.findById(id).orElseThrow(() -> new GameException("Juego no encontrado"));
    }

    @Transactional
    @Override public Game save(Game juego) {
        if (gameRepository.findByNombre(juego.getNombre()).isPresent())
            throw new GameException("El nombre del juego ya existe");
        log.info("Creando juego: {}", juego.getNombre());
        return gameRepository.save(juego);
    }

    @Transactional
    @Override public void deleteById(Long id) {
        Game juego = findById(id);
        juego.setEstado("INACTIVO");
        log.info("Desactivando juego id: {}", id);
        gameRepository.save(juego);
    }

    @Transactional
    @Override public Game updateById(Long id, Game juego) {
        return gameRepository.findById(id).map(element -> {
            element.setModalidad(juego.getModalidad());
            element.setJugadoresPorEquipo(juego.getJugadoresPorEquipo());
            element.setEstado(juego.getEstado());
            log.info("Actualizando juego id: {}", id);
            return gameRepository.save(element);
        }).orElseThrow(() -> new GameException("Juego no encontrado"));
    }
}
