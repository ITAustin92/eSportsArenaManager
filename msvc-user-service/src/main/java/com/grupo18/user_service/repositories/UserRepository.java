package com.grupo18.user_service.repositories;


import com.grupo18.user_service.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByNickname(String nickname);
    Optional<User> findByCorreo(String correo);
    List<User> findByRol(String rol);
    List<User> findByEstado(String estado);
}