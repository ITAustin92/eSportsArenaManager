package com.grupo18.auth_service.dtos;

import java.util.Set;
import lombok.*;

// Vista pública de una cuenta (nunca incluye la contraseña).
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CuentaResponse {

	private Long id;
	private String username;
	private String nombre;
	private Long perfilId;
	private Set<String> roles;
}
