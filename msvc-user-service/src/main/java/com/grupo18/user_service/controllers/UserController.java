package com.grupo18.user_service.controllers;


import com.grupo18.user_service.models.User;
import com.grupo18.user_service.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> findAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findById(id));
    }

    // Adaptado al caso eSports: En vez de RUT, buscamos por el nickname del jugador
    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<User> findByNickname(@PathVariable String nickname) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findByNickname(nickname));
    }

    @GetMapping("/correo/{correo}")
    public ResponseEntity<User> findByCorreo(@PathVariable String correo) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findByCorreo(correo));
    }

    @PostMapping
    public ResponseEntity<User> save(@Valid @RequestBody User usuario) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.save(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @Valid @RequestBody User usuario) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.updateById(id, usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // En nuestro caso, el servicio se encargará de hacer la "desactivación lógica"
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<User>> findByRol(@PathVariable String rol) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findByRol(rol));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<User>> findByEstado(@PathVariable String estado) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.findByEstado(estado));
    }
}