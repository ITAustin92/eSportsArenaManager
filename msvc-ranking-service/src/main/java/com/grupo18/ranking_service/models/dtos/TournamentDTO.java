package com.grupo18.ranking_service.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class TournamentDTO {

    private Long tournamentId;
    private String name;
    private String state; // Vital para saber si el torneo ya terminó y congelar los puntos

}