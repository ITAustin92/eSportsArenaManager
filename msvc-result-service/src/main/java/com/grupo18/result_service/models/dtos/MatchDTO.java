package com.grupo18.result_service.models.dtos;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MatchDTO {

    private Long matchId;
    private Long tournamentId;
    private Long homeTeamId;
    private Long awayTeamId;
    private String status;

}