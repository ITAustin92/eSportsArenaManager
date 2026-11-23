package com.grupo18.auth_service.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {

	@NotBlank(message = "El usuario es obligatorio")
	private String username;

	@NotBlank(message = "La contraseña es obligatoria")
	private String password;
}
