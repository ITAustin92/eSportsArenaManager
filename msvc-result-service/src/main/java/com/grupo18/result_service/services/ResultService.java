package com.grupo18.result_service.services;

import com.grupo18.result_service.models.Result;
import java.util.List;

public interface ResultService {
    java.util.List<Result> findAll();
    Result save(Result result);
    Result findByMatchId(Long matchId);
    List<Result> findByWinnerTeamId(Long winnerTeamId);
    Result findById(Long id);
    Result updateById(Long id, Result result);
    void deleteById(Long id);
}