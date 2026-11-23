package com.grupo18.auth_service.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

// Registro PÚBLICO: siempre crea una cuenta ROLE_JUGADOR.
// No recibe roles: así nadie puede auto-asignarse ROLE_ADMIN desde el formulario.
@Getter
@Setter
@NoArgsConstructor
public class RegisterRequest {

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
	@Size(max = 80, message = "El nombre admite hasta 80 caracteres")
	private String nombre;

	@NotBlank(message = "El correo es obligatorio")
	@Email(message = "El correo no tiene un formato válido")
	private String correo;
}
