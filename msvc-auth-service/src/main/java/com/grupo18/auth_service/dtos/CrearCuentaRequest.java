package com.grupo18.auth_service.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

// Alta de cuentas por el ADMINISTRADOR: aquí sí se elige el rol.
@Getter
@Setter
@NoArgsConstructor
public class CrearCuentaRequest {

	@NotBlank(message = "El usuario es obligatorio")
	@Size(min = 4, max = 30, message = "El usuario debe tener entre 4 y 30 caracteres")
	@Pattern(
		regexp = "^[a-zA-Z0-9_.-]+$",
		message = "El usuario solo admite letras, números, punto, guion y guion bajo"
	)
	private String username;

	@NotBlank(message = "La contraseña es obligatoria")
	@Size(min = 6, max = 72, message = "La contraseña debe tener entre 6 y 72 caracteres")
	private String password;

	@NotBlank(message = "El nombre es obligatorio")
	private String nombre;

	@NotBlank(message = "El correo es obligatorio")
	@Email(message = "El correo no tiene un formato válido")
	private String correo;

	@NotBlank(message = "El rol es obligatorio")
	@Pattern(regexp = "ROLE_ADMIN|ROLE_ORGANIZADOR|ROLE_JUGADOR", message = "Rol no válido")
	private String rol;
}
