package com.grupo18.result_service.services;

import com.grupo18.result_service.clients.MatchClient;
import com.grupo18.result_service.clients.RankingClient;
import com.grupo18.result_service.exceptions.ResultException;
import com.grupo18.result_service.models.Result;
import com.grupo18.result_service.models.dtos.MatchDTO;
import com.grupo18.result_service.models.dtos.MatchResultUpdateDTO;
import com.grupo18.result_service.repositories.ResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
@Service
public class ResultServiceImpl implements ResultService {

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private MatchClient matchClient;

    @Autowired
    private RankingClient rankingClient;
    @Transactional(readOnly = true)
    @Override
    public List<Result> findAll() {
        log.info("Obteniendo listado completo de resultados");
        return resultRepository.findAll();
    }

    @Transactional
    @Override
    public Result save(Result result) {
        log.info("Registrando resultado para partido ID: {}. Ganador equipo ID: {}",
                result.getMatchId(), result.getWinnerTeamId());

        log.info("Consultando partido ID: {} al match-service", result.getMatchId());
        MatchDTO match = matchClient.getMatchById(result.getMatchId());
        if (match == null) {
            log.warn("Partido ID: {} no encontrado al registrar resultado", result.getMatchId());
            throw new ResultException("El partido reportado no existe");
        }

        if (resultRepository.existsByMatchId(result.getMatchId())) {
            log.warn("Intento de registrar resultado duplicado para partido ID: {}", result.getMatchId());
            throw new ResultException("Ya existe un resultado registrado para este partido");
        }

        if (!result.getWinnerTeamId().equals(match.getHomeTeamId()) &&
                !result.getWinnerTeamId().equals(match.getAwayTeamId())) {
            log.warn("Ganador inválido. Equipo ID: {} no participó en el partido ID: {}. " +
                            "Participantes: Local={}, Visitante={}",
                    result.getWinnerTeamId(), result.getMatchId(),
                    match.getHomeTeamId(), match.getAwayTeamId());
            throw new ResultException("El ganador debe ser uno de los equipos que jugó el partido");
        }

        Long loserTeamId = result.getWinnerTeamId().equals(match.getHomeTeamId())
                ? match.getAwayTeamId()
                : match.getHomeTeamId();
        log.info("Perdedor determinado automáticamente: equipo ID: {}", loserTeamId);

        MatchResultUpdateDTO updateDTO = new MatchResultUpdateDTO(
                match.getTournamentId(), result.getWinnerTeamId(), loserTeamId);
        log.info("Notificando resultado al ranking-service. Torneo ID: {}", match.getTournamentId());
        rankingClient.updateRanking(updateDTO);

        result.setStatus("CONFIRMED");
        Result saved = resultRepository.save(result);
        log.info("Resultado ID {} registrado exitosamente. Ganador: equipo ID {}, Marcador: {}-{}",
                saved.getResultId(), saved.getWinnerTeamId(), saved.getHomeScore(), saved.getAwayScore());
        return saved;
    }

    @Transactional(readOnly = true)
    @Override
    public Result findByMatchId(Long matchId) {
        log.info("Buscando resultado del partido ID: {}", matchId);
        return resultRepository.findByMatchId(matchId).orElseThrow(() -> {
            log.warn("No se encontró resultado para el partido ID: {}", matchId);
            return new ResultException("Resultado no encontrado para el partido");
        });
    }

    @Transactional(readOnly = true)
    @Override
    public List<Result> findByWinnerTeamId(Long winnerTeamId) {
        log.info("Buscando victorias del equipo ID: {}", winnerTeamId);
        List<Result> lista = resultRepository.findByWinnerTeamId(winnerTeamId);
        log.info("Se encontraron {} victoria(s) para equipo ID: {}", lista.size(), winnerTeamId);
        return lista;
    }

    @Transactional(readOnly = true)
    @Override
    public Result findById(Long id) {
        log.info("Buscando resultado con ID: {}", id);
        return resultRepository.findById(id).orElseThrow(() -> {
            log.warn("Resultado con ID: {} no encontrado", id);
            return new ResultException("Resultado no encontrado");
        });
    }

    @Transactional
    @Override
    public Result updateById(Long id, Result result) {
        log.info("Actualizando resultado con ID: {}", id);
        Result existing = resultRepository.findById(id).orElseThrow(() -> {
            log.warn("Resultado ID: {} no encontrado para actualizar", id);
            return new ResultException("Resultado no encontrado");
        });
        existing.setHomeScore(result.getHomeScore());
        existing.setAwayScore(result.getAwayScore());
        existing.setWinnerTeamId(result.getWinnerTeamId());
        Result updated = resultRepository.save(existing);
        log.info("Resultado ID {} actualizado. Nuevo marcador: {}-{}, Ganador: equipo ID {}",
                updated.getResultId(), updated.getHomeScore(), updated.getAwayScore(), updated.getWinnerTeamId());
        return updated;
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        log.info("Anulando resultado con ID: {}", id);
        Result existing = resultRepository.findById(id).orElseThrow(() -> {
            log.warn("Resultado ID: {} no encontrado para anular", id);
            return new ResultException("Resultado no encontrado");
        });
        existing.setStatus("ANULADO");
        resultRepository.save(existing);
        log.info("Resultado ID {} anulado exitosamente", id);
    }
}