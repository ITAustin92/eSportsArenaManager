package com.grupo18.team_service.services;

import com.grupo18.team_service.exceptions.TeamException;
import com.grupo18.team_service.models.MemberTeam;
import com.grupo18.team_service.models.Team;
import com.grupo18.team_service.repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class TeamServiceImpl implements TeamService {

    @Autowired
    private TeamRepository equipoRepository;

    @Transactional(readOnly = true)
    @Override
    public List<Team> findAll() {
        log.info("Obteniendo listado completo de equipos");
        List<Team> equipos = this.equipoRepository.findAll();
        log.info("Se encontraron {} equipo(s) en total", equipos.size());
        return equipos;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Team> findByJuegoPrincipalId(Long juegoId) {
        log.info("Buscando equipos del juego ID: {}", juegoId);
        List<Team> equipos = this.equipoRepository.findByJuegoPrincipalId(juegoId);
        log.info("Se encontraron {} equipo(s) para juego ID: {}", equipos.size(), juegoId);
        return equipos;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Team> findByCapitanId(Long capitanId) {
        log.info("Buscando equipos del capitán ID: {}", capitanId);
        List<Team> equipos = this.equipoRepository.findByCapitanId(capitanId);
        log.info("Se encontraron {} equipo(s) liderados por capitán ID: {}", equipos.size(), capitanId);
        return equipos;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Team> findByEstado(String estado) {
        log.info("Buscando equipos con estado: '{}'", estado);
        List<Team> equipos = this.equipoRepository.findByEstado(estado);
        log.info("Se encontraron {} equipo(s) con estado '{}'", equipos.size(), estado);
        return equipos;
    }

    @Transactional(readOnly = true)
    @Override
    public Team findById(Long id) {
        log.info("Buscando equipo con ID: {}", id);
        return this.equipoRepository.findById(id).orElseThrow(() -> {
            log.warn("Equipo con ID: {} no encontrado", id);
            return new TeamException("Equipo no encontrado");
        });
    }

    @Transactional
    @Override
    public Team save(Team equipo) {
        log.info("Intentando crear equipo con nombre: '{}', Capitán ID: {}",
                equipo.getNombre(), equipo.getCapitanId());

        if (this.equipoRepository.findByNombre(equipo.getNombre()).isPresent()) {
            log.warn("Nombre de equipo duplicado: '{}'", equipo.getNombre());
            throw new TeamException("El nombre del equipo ya está en uso");
        }

        log.info("Validando {} miembro(s) del equipo para duplicados",
                equipo.getMiembros() != null ? equipo.getMiembros().size() : 0);
        validarJugadoresDuplicados(equipo.getMiembros());

        Team saved = this.equipoRepository.save(equipo);
        log.info("Equipo '{}' creado exitosamente con ID: {}", saved.getNombre(), saved.getEquipoId());
        return saved;
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        log.info("Desactivando equipo con ID: {}", id);
        Team equipo = this.findById(id);
        equipo.setEstado("INACTIVO");
        this.equipoRepository.save(equipo);
        log.info("Equipo '{}' (ID: {}) desactivado exitosamente", equipo.getNombre(), id);
    }

    @Transactional
    @Override
    public Team updateById(Long id, Team equipo) {
        log.info("Actualizando equipo con ID: {}", id);
        return this.equipoRepository.findById(id).map(element -> {

            if (!element.getNombre().equals(equipo.getNombre()) &&
                    this.equipoRepository.findByNombre(equipo.getNombre()).isPresent()) {
                log.warn("Conflicto de nombre al actualizar equipo ID {}: '{}' ya está en uso",
                        id, equipo.getNombre());
                throw new TeamException("El nuevo nombre del equipo ya está ocupado");
            }

            validarJugadoresDuplicados(equipo.getMiembros());

            element.setNombre(equipo.getNombre());
            element.setCapitanId(equipo.getCapitanId());
            element.setEstado(equipo.getEstado());
            element.getMiembros().clear();
            if (equipo.getMiembros() != null) {
                element.getMiembros().addAll(equipo.getMiembros());
            }

            Team updated = this.equipoRepository.save(element);
            log.info("Equipo ID {} actualizado. Nuevo nombre: '{}', Estado: '{}'",
                    updated.getEquipoId(), updated.getNombre(), updated.getEstado());
            return updated;

        }).orElseThrow(() -> {
            log.warn("Equipo con ID: {} no encontrado para actualizar", id);
            return new TeamException("Equipo no encontrado");
        });
    }

    private void validarJugadoresDuplicados(List<MemberTeam> miembros) {
        if (miembros != null && !miembros.isEmpty()) {
            List<Long> idsRegistrados = new ArrayList<>();
            for (MemberTeam miembro : miembros) {
                if (idsRegistrados.contains(miembro.getUsuarioId())) {
                    log.warn("Jugador duplicado detectado en los miembros. Usuario ID: {}", miembro.getUsuarioId());
                    throw new TeamException("No se puede duplicar un jugador dentro del mismo equipo: ID " + miembro.getUsuarioId());
                }
                idsRegistrados.add(miembro.getUsuarioId());
            }
        }
    }
}