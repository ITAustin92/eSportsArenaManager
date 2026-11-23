package com.grupo18.auth_service.services;

import com.grupo18.auth_service.clients.PerfilClient;
import com.grupo18.auth_service.dtos.*;
import com.grupo18.auth_service.models.*;
import com.grupo18.auth_service.repositories.*;
import com.grupo18.auth_service.security.JwtService;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

	private final UsuarioRepository usuarioRepository;
	private final RolRepository rolRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final PerfilClient perfilClient;

	public AuthService(
		UsuarioRepository u,
		RolRepository r,
		PasswordEncoder p,
		JwtService j,
		PerfilClient perfilClient
	) {
		this.usuarioRepository = u;
		this.rolRepository = r;
		this.passwordEncoder = p;
		this.jwtService = j;
		this.perfilClient = perfilClient;
	}

	// Registro público: siempre ROLE_JUGADOR y perfil PLAYER en user-service.
	@Transactional
	public AuthResponse register(RegisterRequest request) {
		Usuario u = crearCuenta(
			request.getUsername(),
			request.getPassword(),
			request.getNombre(),
			request.getCorreo(),
			"ROLE_JUGADOR"
		);
		perfilClient.exigirActivo(u.getPerfilId());
		return construirRespuesta(u);
	}

	@Transactional(readOnly = true)
	public AuthResponse login(LoginRequest request) {
		Usuario u = usuarioRepository
			.findByUsername(request.getUsername().trim())
			.orElseThrow(() ->
				new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos")
			);
		if (
			!passwordEncoder.matches(request.getPassword(), u.getPassword())
		) throw new ResponseStatusException(
			HttpStatus.UNAUTHORIZED,
			"Usuario o contraseña incorrectos"
		);
		perfilClient.exigirActivo(u.getPerfilId());
		return construirRespuesta(u);
	}

	// Renovación: recibe un token AÚN VIGENTE (ya validado por Spring Security) y entrega uno nuevo
	// con los roles actuales de la cuenta. Un token vencido no se puede renovar: hay que iniciar sesión.
	@Transactional(readOnly = true)
	public AuthResponse renovar(String username) {
		Usuario u = usuarioRepository
			.findByUsername(username)
			.orElseThrow(() ->
				new ResponseStatusException(HttpStatus.UNAUTHORIZED, "La cuenta ya no existe")
			);
		perfilClient.exigirActivo(u.getPerfilId());
		return construirRespuesta(u);
	}

	@Transactional(readOnly = true)
	public CuentaResponse perfil(String username) {
		return usuarioRepository
			.findByUsername(username)
			.map(this::aCuenta)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta no encontrada"));
	}

	// ---- Gestión de cuentas (solo ROLE_ADMIN) ----
	@Transactional(readOnly = true)
	public List<CuentaResponse> listarCuentas() {
		return usuarioRepository.findAll().stream().map(this::aCuenta).toList();
	}

	@Transactional
	public CuentaResponse crearCuentaAdmin(CrearCuentaRequest request) {
		return aCuenta(
			crearCuenta(
				request.getUsername(),
				request.getPassword(),
				request.getNombre(),
				request.getCorreo(),
				request.getRol()
			)
		);
	}

	@Transactional
	public void eliminarCuenta(Long id, String solicitante) {
		Usuario u = usuarioRepository
			.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta no encontrada"));
		if (u.getUsername().equals(solicitante)) throw new ResponseStatusException(
			HttpStatus.CONFLICT,
			"No puedes eliminar tu propia cuenta"
		);
		perfilClient.desactivar(u.getPerfilId());
		usuarioRepository.delete(u);
	}

	private Usuario crearCuenta(
		String username,
		String password,
		String nombre,
		String correo,
		String nombreRol
	) {
		String usuario = username.trim();
		if (usuarioRepository.existsByUsername(usuario)) throw new ResponseStatusException(
			HttpStatus.CONFLICT,
			"El usuario ya existe"
		);
		Rol rol = rolRepository
			.findByNombre(nombreRol)
			.orElseThrow(() ->
				new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rol no existe: " + nombreRol)
			);
		// Primero el perfil: si user-service falla, la credencial no se guarda (no quedan cuentas huérfanas).
		Long perfilId = perfilClient.crearPerfil(
			nombre.trim(),
			usuario,
			correo.trim(),
			rolPerfil(nombreRol)
		);
		Usuario u = new Usuario();
		u.setUsername(usuario);
		u.setNombre(nombre.trim());
		u.setPerfilId(perfilId);
		u.setPassword(passwordEncoder.encode(password));
		u.setRoles(new HashSet<>(Set.of(rol)));
		return usuarioRepository.save(u);
	}

	private static String rolPerfil(String nombreRol) {
		return switch (nombreRol) {
			case "ROLE_ADMIN" -> "ADMIN";
			case "ROLE_ORGANIZADOR" -> "ORGANIZER";
			default -> "PLAYER";
		};
	}

	private CuentaResponse aCuenta(Usuario u) {
		Set<String> roles = u
			.getRoles()
			.stream()
			.map(Rol::getNombre)
			.collect(Collectors.toCollection(TreeSet::new));
		return new CuentaResponse(
			u.getUsuarioId(),
			u.getUsername(),
			u.getNombre(),
			u.getPerfilId(),
			roles
		);
	}

	private AuthResponse construirRespuesta(Usuario u) {
		Jwt jwt = jwtService.generarToken(u);
		List<String> lista = u.getRoles().stream().map(Rol::getNombre).sorted().toList();
		Set<String> roles = new TreeSet<>(lista);
		long expira = jwt.getExpiresAt() == null ? 0L : jwt.getExpiresAt().toEpochMilli();
		return new AuthResponse(
			jwt.getTokenValue(),
			"Bearer",
			u.getUsername(),
			u.getNombre() == null ? u.getUsername() : u.getNombre(),
			u.getPerfilId(),
			JwtService.rolPrincipal(lista),
			roles,
			expira
		);
	}
}
