package com.grupo18.team_service.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserDTO {

    private Long id; // El ID del usuario que viene desde el otro microservicio
    private String nickname; // El nombre de jugador del capitán
    private String state; // Vital para validar que el jugador no esté "SANCIONADO"

}