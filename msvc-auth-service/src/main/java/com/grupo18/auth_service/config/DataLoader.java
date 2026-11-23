package com.grupo18.auth_service.config;

import com.grupo18.auth_service.models.*;
import com.grupo18.auth_service.repositories.*;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// Siembra los 3 roles y las cuentas de prueba. perfilId apunta al id que cada persona
// tiene en msvc-user-service (ver data.sql de ese servicio), así el JWT lleva su userId real.
@Component
public class DataLoader implements CommandLineRunner {

	@org.springframework.beans.factory.annotation.Value("${app.demo.enabled:true}")
	private boolean demo;

	@org.springframework.beans.factory.annotation.Value("${ADMIN_USERNAME:}")
	private String adminUsuario;

	@org.springframework.beans.factory.annotation.Value("${ADMIN_PASSWORD:}")
	private String adminClave;

	@org.springframework.beans.factory.annotation.Value("${ADMIN_NOMBRE:Administrador}")
	private String adminNombre;

	@org.springframework.beans.factory.annotation.Value("${ADMIN_CORREO:}")
	private String adminCorreo;

	@org.springframework.beans.factory.annotation.Autowired
	private com.grupo18.auth_service.services.AuthService cuentas;

	private final RolRepository rolRepository;
	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;

	public DataLoader(RolRepository r, UsuarioRepository u, PasswordEncoder p) {
		this.rolRepository = r;
		this.usuarioRepository = u;
		this.passwordEncoder = p;
	}

	@Override
	public void run(String... args) {
		Rol admin = obtenerOCrearRol("ROLE_ADMIN");
		Rol organizador = obtenerOCrearRol("ROLE_ORGANIZADOR");
		Rol jugador = obtenerOCrearRol("ROLE_JUGADOR");
		if (!demo) {
			if (!adminUsuario.isBlank() && !usuarioRepository.existsByUsername(adminUsuario)) {
				if (adminClave.length() < 12 || !adminCorreo.contains("@")) {
					throw new IllegalStateException(
						"Configura ADMIN_PASSWORD (mínimo 12 caracteres) y ADMIN_CORREO"
					);
				}
				var solicitud = new com.grupo18.auth_service.dtos.CrearCuentaRequest();
				solicitud.setUsername(adminUsuario);
				solicitud.setPassword(adminClave);
				solicitud.setNombre(adminNombre);
				solicitud.setCorreo(adminCorreo);
				solicitud.setRol("ROLE_ADMIN");
				cuentas.crearCuentaAdmin(solicitud);
			}
			return;
		}
		crearOActualizar("admin", "admin123", "Carlos Perez", 1L, Set.of(admin));
		crearOActualizar("organizador1", "org123", "Roberto Fuentes", 11L, Set.of(organizador));
		crearOActualizar("jugador1", "jugador123", "Maria Gonzalez", 2L, Set.of(jugador));
		crearOActualizar("jugador2", "jugador123", "Camila Vargas", 10L, Set.of(jugador));
	}

	private Rol obtenerOCrearRol(String nombre) {
		return rolRepository.findByNombre(nombre).orElseGet(() -> rolRepository.save(new Rol(nombre)));
	}

	private void crearOActualizar(
		String username,
		String pass,
		String nombre,
		Long perfilId,
		Set<Rol> roles
	) {
		Usuario u = usuarioRepository.findByUsername(username).orElseGet(() -> {
			Usuario nuevo = new Usuario();
			nuevo.setUsername(username);
			nuevo.setPassword(passwordEncoder.encode(pass));
			nuevo.setRoles(new java.util.HashSet<>(roles));
			return nuevo;
		});
		u.setNombre(nombre);
		u.setPerfilId(perfilId);
		usuarioRepository.save(u);
	}
}
