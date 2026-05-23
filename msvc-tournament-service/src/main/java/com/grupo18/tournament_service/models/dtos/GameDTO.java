package com.grupo18.tournament_service.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class GameDTO {

    private Long id;
    private String name;
    private String state; // Vital para verificar que el juego esté "ACTIVO" antes de crear el torneo

}