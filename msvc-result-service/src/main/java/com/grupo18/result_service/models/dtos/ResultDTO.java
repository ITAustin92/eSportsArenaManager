package com.grupo18.result_service.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ResultDTO {

    private Long resultId;
    private Long matchId;
    private Long winnerTeamId;
    private Integer homeScore;
    private Integer awayScore;
    private String status;

}