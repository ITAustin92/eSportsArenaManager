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


    @Autowired
    private RankingClient rankingClient;

    @Transactional
    @Override
    public Result save(Result result) {

        MatchDTO match = matchClient.getMatchById(result.getMatchId());
        if (match == null) {
            throw new ResultException("El partido reportado no existe");
        }


        if (resultRepository.existsByMatchId(result.getMatchId())) {
            throw new ResultException("Ya existe un resultado registrado para este partido");
        }


        if (!result.getWinnerTeamId().equals(match.getHomeTeamId()) &&
                !result.getWinnerTeamId().equals(match.getAwayTeamId())) {
            throw new ResultException("El ganador debe ser uno de los equipos que jugó el partido");
        }


        Long loserTeamId = result.getWinnerTeamId().equals(match.getHomeTeamId())
                ? match.getAwayTeamId()
                : match.getHomeTeamId();


        MatchResultUpdateDTO updateDTO = new MatchResultUpdateDTO(
                match.getTournamentId(),
                result.getWinnerTeamId(),
                loserTeamId
        );

        rankingClient.updateRanking(updateDTO);

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