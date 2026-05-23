package com.grupo18.prize_service.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RankingDTO {

    private Long id;
    private Long tournamentId;
    private Long teamId;
    private Integer points;
    private Integer wins;
    private Integer losses;
    private Integer matchesPlayed;

}