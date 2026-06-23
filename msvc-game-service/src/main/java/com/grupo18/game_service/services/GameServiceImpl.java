package com.grupo18.game_service.services;


import com.grupo18.game_service.exceptions.GameException;
import com.grupo18.game_service.models.Game;
import com.grupo18.game_service.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GameServiceImpl implements GameService {

    @Autowired
    private GameRepository gameRepository;

    @Transactional(readOnly = true)
    @Override
    public List<Game> findAll() {
        log.info("Obteniendo listado completo de juegos");
        List<Game> game = gameRepository.findAll();
        log.info("Se encontraron {} juego(s) en total", game.size());
        return game;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Game> findByEstado(String estado) {
        log.info("Buscando juegos con estado: {}", estado);
        List<Game> juegos = this.gameRepository.findByEstado(estado);
        log.info("Se encontraron {} juego(s) con estado: {}", juegos.size(), estado);
        return juegos;
    }


    @Transactional(readOnly = true)
    @Override
    public Game findById(Long id) {
        log.info("Buscando juego con ID: {}", id);
        return this.gameRepository.findById(id).orElseThrow(() ->{
            log.info("Jego con ID {} no encontrado", id);
            return new GameException("Juego no encontrado");
        });
    }

    @Transactional
    @Override
    public Game save(Game juego) {
        log.info("Intentando registrar nuevo juego con nombre: '{}'", juego.getNombre());
        if(this.gameRepository.findByNombre(juego.getNombre()).isPresent()){
            log.warn("Intenta crear juego duplicado. Nombre:'{}'", juego.getNombre());
            throw new GameException("El nombre del juego ya existe");
        }
        Game saved = this.gameRepository.save(juego);
        log.info("Juego registrado exitosament. ID: {}, Nombre'{}'", saved.getJuegoId(), saved.getNombre());
        return saved;

    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        log.info("Intentando desactivar juego con ID: {}", id);
        Game juego = this.findById(id);
        juego.setEstado("INACTIVO");
        this.gameRepository.save(juego);
        log.info("Juego con ID: {} desactivado exitosamente", id);
    }

    @Transactional
    @Override
    public Game updateById(Long id, Game juego) {
        log.info("Intentando actualizar juego con ID: {}", id);
        return this.gameRepository.findById(id).map(element -> {
            element.setModalidad(juego.getModalidad());
            element.setJugadoresPorEquipo(juego.getJugadoresPorEquipo());
            element.setEstado(juego.getEstado());
            Game updated = this.gameRepository.save(element);
            log.info("Juego ID {} actualizado. Modalidad: '{}', Estado: '{}'", updated.getJuegoId(), updated.getModalidad(), updated.getEstado());
            return updated;
        }).orElseThrow(() -> {
            log.warn("No se encontró juego con ID {} para actualizar", id);
            return new GameException("Juego no encontrado");
        });
    }
}