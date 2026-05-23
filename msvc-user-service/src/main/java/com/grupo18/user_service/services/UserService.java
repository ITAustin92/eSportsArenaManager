package com.grupo18.user_service.services;


import com.grupo18.user_service.models.User;

import java.util.List;

public interface UserService {
    List<User> findAll();
    User findById(Long id);
    User findByCorreo(String correo);
    User findByNickname(String nickname);
    User save(User usuario);
    void deleteById(Long id);
    User updateById(Long id, User usuario);
    List<User> findByRol(String rol);
    List<User> findByEstado(String estado);

}