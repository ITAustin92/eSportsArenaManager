package com.grupo18.ranking_service.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RankingDTO {

    private Long rankingId;
    private Long tournamentId;
    private Long teamId;
    private Integer points;
    private Integer matchesPlayed;
    private Integer wins;
    private Integer losses;

}