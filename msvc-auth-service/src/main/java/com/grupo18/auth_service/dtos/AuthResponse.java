package com.grupo18.auth_service.dtos;

import java.util.Set;
import lombok.*;

// Respuesta de login, registro y renovación. El frontend guarda el token y usa
// rol/roles solo para decidir qué mostrar: el backend vuelve a validar el rol en cada petición.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

	private String token;
	private String tokenType;
	private String username;
	private String nombre;
	private Long userId;
	private String rol;
	private Set<String> roles;
	private long expiraEn;
}
