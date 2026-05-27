package com.grupo18.team_service.services;


import com.grupo18.team_service.exceptions.TeamException;
import com.grupo18.team_service.models.MemberTeam;
import com.grupo18.team_service.models.Team;
import com.grupo18.team_service.repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeamServiceImpl implements TeamService {

    @Autowired
    private TeamRepository equipoRepository;

    @Transactional(readOnly = true)
    @Override
    public List<Team> findAll() {
        return this.equipoRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Team> findByJuegoPrincipalId(Long juegoId) {
        return this.equipoRepository.findByJuegoPrincipalId(juegoId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Team> findByCapitanId(Long capitanId) {
        return this.equipoRepository.findByCapitanId(capitanId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Team> findByEstado(String estado) {
        return this.equipoRepository.findByEstado(estado);
    }

    @Transactional(readOnly = true)
    @Override
    public Team findById(Long id) {
        return this.equipoRepository.findById(id).orElseThrow(
                () -> new TeamException("Equipo no encontrado")
        );
    }

    @Transactional
    @Override
    public Team save(Team equipo) {
        if(this.equipoRepository.findByNombre(equipo.getNombre()).isPresent()){
            throw new TeamException("El nombre del equipo ya está en uso");
        }

        validarJugadoresDuplicados(equipo.getMiembros());

        return this.equipoRepository.save(equipo);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Team equipo = this.findById(id);
        equipo.setEstado("INACTIVO");
        this.equipoRepository.save(equipo);
    }

    @Transactional
    @Override
    public Team updateById(Long id, Team equipo) {
        return this.equipoRepository.findById(id).map(element -> {

            if (!element.getNombre().equals(equipo.getNombre()) &&
                    this.equipoRepository.findByNombre(equipo.getNombre()).isPresent()) {
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

            return this.equipoRepository.save(element);
        }).orElseThrow(
                () -> new TeamException("Equipo no encontrado")
        );
    }


    private void validarJugadoresDuplicados(List<MemberTeam> miembros) {
        if (miembros != null && !miembros.isEmpty()) {
            List<Long> idsRegistrados = new ArrayList<>();
            for (MemberTeam miembro : miembros) {
                if (idsRegistrados.contains(miembro.getUsuarioId())) {
                    throw new TeamException("No se puede duplicar un jugador dentro del mismo equipo: ID " + miembro.getUsuarioId());
                }
                idsRegistrados.add(miembro.getUsuarioId());
            }
        }
    }
}