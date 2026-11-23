package com.grupo18.auth_service.repositories;

import com.grupo18.auth_service.models.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	Optional<Usuario> findByUsername(String username);
	boolean existsByUsername(String username);
}
