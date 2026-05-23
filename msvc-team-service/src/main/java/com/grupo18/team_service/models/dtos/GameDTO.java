package com.grupo18.team_service.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class GameDTO {
    // Ponemos solo lo básico que necesitamos saber del juego para validar
    private Long gameId;
    private String name;
    private String state; // Para saber si está ACTIVO o INACTIVO
}