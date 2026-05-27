package com.grupo18.user_service.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserDTO {

    private Long usuarioId;
    private String nombre;
    private String nickname;
    private String correo;
    private String rol;

}