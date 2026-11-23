package com.grupo18.auth_service.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.*;
import lombok.*;

// Credencial de acceso. La contraseña se guarda con BCrypt (nunca en texto plano).
// perfilId enlaza la cuenta con su perfil en msvc-user-service (mismo usuario, dos servicios).
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "usuario_id")
	private Long usuarioId;

	@NotBlank
	@Column(unique = true, nullable = false)
	private String username;

	@NotBlank
	@Column(nullable = false)
	private String password;

	@Column(name = "nombre")
	private String nombre;

	@Column(name = "perfil_id")
	private Long perfilId;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
		name = "usuario_roles",
		joinColumns = @JoinColumn(name = "usuario_id"),
		inverseJoinColumns = @JoinColumn(name = "rol_id")
	)
	private Set<Rol> roles = new HashSet<>();
}
