package com.grupo18.tournament_service.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserDTO {

    private Long id; // El userId que viene del microservicio de usuarios
    private String nickname;
    private String state; // Para validar que el organizador no esté "SANCIONADO" o "INACTIVO"
    private String role; // (Opcional) Por si tu profesor exige validar que el usuario tenga rol "ORGANIZADOR" o "ADMIN"

}