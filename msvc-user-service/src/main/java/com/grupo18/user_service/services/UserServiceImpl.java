package com.grupo18.user_service.services;


import com.grupo18.user_service.exceptions.UserException;
import com.grupo18.user_service.models.User;
import com.grupo18.user_service.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    // No inyectamos ningún Client aquí porque user-service no consume a nadie

    @Transactional(readOnly = true)
    @Override
    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public User findById(Long id) {
        return this.userRepository.findById(id).orElseThrow(
                () -> new UserException("Usuario no encontrado")
        );
    }

    @Transactional(readOnly = true)
    @Override
    public User findByCorreo(String correo) {
        return this.userRepository.findByCorreo(correo).orElseThrow(
                () -> new UserException("Usuario no encontrado")
        );
    }

    @Transactional(readOnly = true)
    @Override
    public User findByNickname(String nickname) {
        return this.userRepository.findByNickname(nickname).orElseThrow(
                () -> new UserException("Usuario no encontrado")
        );
    }

    @Transactional
    @Override
    public User save(User usuario) {
        // Replicamos la validación del profe: verificamos que correo o nickname no se repitan
        if(this.userRepository.findByCorreo(usuario.getCorreo()).isPresent()){
            throw new UserException("Usuario ya existe");
        }
        if(this.userRepository.findByNickname(usuario.getNickname()).isPresent()){
            throw new UserException("Usuario ya existe");
        }
        return this.userRepository.save(usuario);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        // Al no tener dependencias directas en este punto, eliminamos directo
        this.userRepository.deleteById(id);
    }

    @Transactional
    @Override
    public User updateById(Long id, User usuario) {
        return this.userRepository.findById(id).map(element -> {

            // Si el jugador quiere cambiar su nickname por uno nuevo, validamos que no esté ocupado
            if (!element.getNickname().equals(usuario.getNickname()) &&
                    this.userRepository.findByNickname(usuario.getNickname()).isPresent()) {
                throw new UserException("El nuevo nickname ya está en uso por otro jugador");
            }

            element.setNombre(usuario.getNombre());
            element.setNickname(usuario.getNickname()); // ¡Ahora sí dejamos que lo cambie!
            element.setCorreo(usuario.getCorreo());
            element.setRol(usuario.getRol());
            element.setEstado(usuario.getEstado()); // Agregamos la actualización del estado

            return this.userRepository.save(element);
        }).orElseThrow(
                () -> new UserException("Usuario no encontrado")
        );
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> findByRol(String rol) {
        return this.userRepository.findByRol(rol);
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> findByEstado(String estado) {
        return this.userRepository.findByEstado(estado);
    }

}