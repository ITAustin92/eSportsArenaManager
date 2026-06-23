package com.grupo18.user_service.services;

import com.grupo18.user_service.exceptions.UserException;
import com.grupo18.user_service.models.User;
import com.grupo18.user_service.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.util.List;


@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public List<User> findAll() {
        log.info("Obteniendo listado completo de usuarios");
        List<User> usuarios = this.userRepository.findAll();
        log.info("Se encontraron {} usuario(s) en total", usuarios.size());
        return usuarios;
    }

    @Transactional(readOnly = true)
    @Override
    public User findById(Long id) {
        log.info("Buscando usuario con ID: {}", id);
        return this.userRepository.findById(id).orElseThrow(() -> {
            log.warn("Usuario con ID {} no encontrado", id);
            return new UserException("Usuario no encontrado");
        });
    }

    @Transactional(readOnly = true)
    @Override
    public User findByCorreo(String correo) {
        log.info("Buscando usuario por correo: {}", correo);
        return this.userRepository.findByCorreo(correo).orElseThrow(() -> {
            log.warn("No existe usuario con correo: {}", correo);
            return new UserException("Usuario no encontrado");
        });
    }

    @Transactional(readOnly = true)
    @Override
    public User findByNickname(String nickname) {
        log.info("Buscando usuario por nickname: '{}'", nickname);
        return this.userRepository.findByNickname(nickname).orElseThrow(() -> {
            log.warn("No existe usuario con nickname: '{}'", nickname);
            return new UserException("Usuario no encontrado");
        });
    }

    @Transactional
    @Override
    public User save(User usuario) {
        log.info("Intentando registrar usuario con correo: '{}' y nickname: '{}'",
                usuario.getCorreo(), usuario.getNickname());

        if (this.userRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
            log.warn("Intento de registro con correo duplicado: '{}'", usuario.getCorreo());
            throw new UserException("Usuario ya existe");
        }
        if (this.userRepository.findByNickname(usuario.getNickname()).isPresent()) {
            log.warn("Intento de registro con nickname duplicado: '{}'", usuario.getNickname());
            throw new UserException("Usuario ya existe");
        }

        User saved = this.userRepository.save(usuario);
        log.info("Usuario registrado exitosamente. ID: {}, Nickname: '{}'",
                saved.getUsuarioId(), saved.getNickname());
        return saved;
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        log.info("Intentando desactivar usuario con ID: {}", id);
        User usuario = this.userRepository.findById(id).orElseThrow(() -> {
            log.warn("No se encontró usuario con ID {} para desactivar", id);
            return new UserException("Usuario no encontrado");
        });
        usuario.setEstado("INACTIVO");
        this.userRepository.save(usuario);
        log.info("Usuario con ID {} desactivado exitosamente", id);
    }

    @Transactional
    @Override
    public User updateById(Long id, User usuario) {
        log.info("Intentando actualizar usuario con ID: {}", id);
        return this.userRepository.findById(id).map(element -> {
            if (!element.getNickname().equals(usuario.getNickname()) &&
                    this.userRepository.findByNickname(usuario.getNickname()).isPresent()) {
                log.warn("Conflicto de nickname al actualizar usuario ID {}: '{}' ya está en uso",
                        id, usuario.getNickname());
                throw new UserException("El nuevo nickname ya está en uso por otro jugador");
            }
            element.setNombre(usuario.getNombre());
            element.setNickname(usuario.getNickname());
            element.setCorreo(usuario.getCorreo());
            element.setRol(usuario.getRol());
            element.setEstado(usuario.getEstado());
            User updated = this.userRepository.save(element);
            log.info("Usuario ID {} actualizado exitosamente. Nuevo nickname: '{}'",
                    updated.getUsuarioId(), updated.getNickname());
            return updated;
        }).orElseThrow(() -> {
            log.warn("No se encontró usuario con ID {} para actualizar", id);
            return new UserException("Usuario no encontrado");
        });
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> findByRol(String rol) {
        log.info("Buscando usuarios con rol: '{}'", rol);
        List<User> usuarios = this.userRepository.findByRol(rol);
        log.info("Se encontraron {} usuario(s) con rol '{}'", usuarios.size(), rol);
        return usuarios;
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> findByEstado(String estado) {
        log.info("Buscando usuarios con estado: '{}'", estado);
        List<User> usuarios = this.userRepository.findByEstado(estado);
        log.info("Se encontraron {} usuario(s) con estado '{}'", usuarios.size(), estado);
        return usuarios;
    }
}