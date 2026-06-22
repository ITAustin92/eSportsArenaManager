package com.grupo18.auth_service.config;
import com.grupo18.auth_service.models.*;
import com.grupo18.auth_service.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Set;
// Siembra datos al arrancar: los 3 roles y 3 usuarios de prueba.
@Component
public class DataLoader implements CommandLineRunner {
    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    public DataLoader(RolRepository r, UsuarioRepository u, PasswordEncoder p) {
        this.rolRepository=r; this.usuarioRepository=u; this.passwordEncoder=p;
    }
    @Override public void run(String... args) {
        Rol admin = obtenerOCrearRol("ROLE_ADMIN");
        Rol organizador = obtenerOCrearRol("ROLE_ORGANIZADOR");
        Rol jugador = obtenerOCrearRol("ROLE_JUGADOR");
        crearSiNoExiste("admin",       "admin123",   Set.of(admin));
        crearSiNoExiste("organizador1","org123",     Set.of(organizador));
        crearSiNoExiste("jugador1",    "jugador123", Set.of(jugador));
    }
    private Rol obtenerOCrearRol(String nombre) {
        return rolRepository.findByNombre(nombre).orElseGet(() -> rolRepository.save(new Rol(nombre)));
    }
    private void crearSiNoExiste(String username, String pass, Set<Rol> roles) {
        if (usuarioRepository.existsByUsername(username)) return;
        Usuario u = new Usuario();
        u.setUsername(username); u.setPassword(passwordEncoder.encode(pass)); u.setRoles(roles);
        usuarioRepository.save(u);
    }
}
