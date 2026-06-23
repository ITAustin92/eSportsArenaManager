package com.grupo18.auth_service.services;
import com.grupo18.auth_service.dtos.*;
import com.grupo18.auth_service.models.*;
import com.grupo18.auth_service.repositories.*;
import com.grupo18.auth_service.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    public AuthService(UsuarioRepository u, RolRepository r, PasswordEncoder p, JwtService j) {
        this.usuarioRepository=u; this.rolRepository=r; this.passwordEncoder=p; this.jwtService=j;
    }
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByUsername(request.getUsername()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario ya existe");
        Set<String> nombresRoles = (request.getRoles()==null||request.getRoles().isEmpty())
                ? Set.of("ROLE_JUGADOR") : request.getRoles();
        Set<Rol> roles = nombresRoles.stream()
                .map(n -> rolRepository.findByNombre(n).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rol no existe: "+n)))
                .collect(Collectors.toCollection(HashSet::new));
        Usuario u = new Usuario();
        u.setUsername(request.getUsername());
        u.setPassword(passwordEncoder.encode(request.getPassword()));
        u.setRoles(roles);
        usuarioRepository.save(u);
        return construirRespuesta(u);
    }
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Usuario u = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));
        if (!passwordEncoder.matches(request.getPassword(), u.getPassword()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        return construirRespuesta(u);
    }
    private AuthResponse construirRespuesta(Usuario u) {
        String token = jwtService.generarToken(u);
        Set<String> roles = u.getRoles().stream().map(Rol::getNombre).collect(Collectors.toSet());
        return new AuthResponse(token, "Bearer", u.getUsername(), roles);
    }
}
