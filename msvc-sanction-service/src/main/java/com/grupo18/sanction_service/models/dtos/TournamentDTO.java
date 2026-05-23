package com.grupo18.sanction_service.models.dtos;

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
    private String state; // Para saber si el torneo sigue activo al poner la sanción

}