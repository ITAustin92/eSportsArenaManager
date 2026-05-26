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

import java.util.List;

@Service
public class ResultServiceImpl implements ResultService {

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private MatchClient matchClient;

    // Inyectamos el nuevo cliente del ranking
    @Autowired
    private RankingClient rankingClient;

    @Transactional
    @Override
    public Result save(Result result) {
        // 1. Validar que el partido exista (Puerto 8005)
        MatchDTO match = matchClient.getMatchById(result.getMatchId());
        if (match == null) {
            throw new ResultException("El partido reportado no existe");
        }

        // 2. Regla de Negocio: Evitar duplicados
        if (resultRepository.existsByMatchId(result.getMatchId())) {
            throw new ResultException("Ya existe un resultado registrado para este partido");
        }

        // 3. Regla de Negocio: Validar que el ganador sea uno de los participantes
        if (!result.getWinnerTeamId().equals(match.getHomeTeamId()) &&
                !result.getWinnerTeamId().equals(match.getAwayTeamId())) {
            throw new ResultException("El ganador debe ser uno de los equipos que jugó el partido");
        }

        // --- NUEVA LÓGICA DE INTEGRACIÓN ---
        // 4. Identificar quién es el perdedor para enviárselo al ranking
        Long loserTeamId = result.getWinnerTeamId().equals(match.getHomeTeamId())
                ? match.getAwayTeamId()
                : match.getHomeTeamId();

        // 5. Construir el sobre de datos (DTO)
        MatchResultUpdateDTO updateDTO = new MatchResultUpdateDTO(
                match.getTournamentId(),
                result.getWinnerTeamId(),
                loserTeamId
        );

        // 6. Enviar la información al ranking-service (Puerto 8007)
        rankingClient.updateRanking(updateDTO);
        // ------------------------------------

        result.setStatus("CONFIRMED");
        return resultRepository.save(result);
    }

    @Transactional(readOnly = true)
    @Override
    public Result findByMatchId(Long matchId) {
        return resultRepository.findByMatchId(matchId)
                .orElseThrow(() -> new ResultException("Resultado no encontrado para el partido"));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Result> findByWinnerTeamId(Long winnerTeamId) {
        return resultRepository.findByWinnerTeamId(winnerTeamId);
    }

    @Transactional(readOnly = true)
    @Override
    public Result findById(Long id) {
        return resultRepository.findById(id)
                .orElseThrow(() -> new ResultException("Resultado no encontrado"));
    }

    @Transactional
    @Override
    public Result updateById(Long id, Result result) {
        Result existing = resultRepository.findById(id)
                .orElseThrow(() -> new ResultException("Resultado no encontrado"));
        existing.setHomeScore(result.getHomeScore());
        existing.setAwayScore(result.getAwayScore());
        existing.setWinnerTeamId(result.getWinnerTeamId());
        return resultRepository.save(existing);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Result existing = resultRepository.findById(id)
                .orElseThrow(() -> new ResultException("Resultado no encontrado"));
        existing.setStatus("ANULADO");
        resultRepository.save(existing);
    }
}