package com.grupo18.registration_service.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class TeamDTO {

    private Long id; // El ID del equipo que viene del team-service
    private String name;
    private String estado; // Vital para la regla de negocio: verificar si el equipo está ACTIVO

}