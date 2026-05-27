package com.grupo18.tournament_service.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class TournamentDTO {

    private Long tournamentId;
    private String name;
    private Long gameId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String state;
    private Long organizerId;
}