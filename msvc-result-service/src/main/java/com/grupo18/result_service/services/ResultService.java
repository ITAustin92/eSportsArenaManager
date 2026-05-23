package com.grupo18.result_service.services;

import com.grupo18.result_service.models.Result;
import java.util.List;

public interface ResultService {
    Result save(Result result);
    Result findByMatchId(Long matchId);
    List<Result> findByWinnerTeamId(Long winnerTeamId);
}