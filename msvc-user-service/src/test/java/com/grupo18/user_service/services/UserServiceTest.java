package com.grupo18.user_service.services;

import com.grupo18.user_service.exceptions.UserException;
import com.grupo18.user_service.models.User;
import com.grupo18.user_service.repositories.UserRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User userPrueba;
    private List<User> userList = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        userPrueba = new User();
        userPrueba.setUsuarioId(1L);
        userPrueba.setNombre("Carlos Pérez");
        userPrueba.setNickname("carlitos99");
        userPrueba.setCorreo("carlos@esports.cl");
        userPrueba.setRol("JUGADOR");
        userPrueba.setEstado("ACTIVO");

        Faker faker = new Faker();
        for (int i = 0; i < 10; i++) {
            User u = new User();
            u.setNombre(faker.name().fullName());
            u.setNickname(faker.internet().username());
            u.setCorreo(faker.internet().emailAddress());
            u.setRol("JUGADOR");
            u.setEstado("ACTIVO");
            userList.add(u);
        }
    }

    @Test
    @DisplayName("Debe listar todos los usuarios")
    public void shouldListAllUsers() {
        List<User> users = new ArrayList<>(userList);
        users.add(userPrueba);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.findAll();

        assertThat(result).hasSize(11);
        assertThat(result).contains(userPrueba);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar usuario por ID")
    public void shouldFindUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userPrueba));

        User result = userService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getNickname()).isEqualTo("carlitos99");
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar ID inexistente")
    public void shouldThrowWhenUserNotFound() {
        when(userRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(9999L))
                .isInstanceOf(UserException.class)
                .hasMessage("Usuario no encontrado");
        verify(userRepository, times(1)).findById(9999L);
    }

    @Test
    @DisplayName("Debe guardar un usuario nuevo")
    public void shouldSaveUser() {
        when(userRepository.findByCorreo(userPrueba.getCorreo())).thenReturn(Optional.empty());
        when(userRepository.findByNickname(userPrueba.getNickname())).thenReturn(Optional.empty());
        when(userRepository.save(userPrueba)).thenReturn(userPrueba);

        User result = userService.save(userPrueba);

        assertThat(result).isNotNull();
        assertThat(result.getNickname()).isEqualTo("carlitos99");
        verify(userRepository, times(1)).save(userPrueba);
    }

    @Test
    @DisplayName("Debe lanzar excepción al guardar usuario con correo duplicado")
    public void shouldThrowWhenEmailExists() {
        when(userRepository.findByCorreo(userPrueba.getCorreo())).thenReturn(Optional.of(userPrueba));

        assertThatThrownBy(() -> userService.save(userPrueba))
                .isInstanceOf(UserException.class)
                .hasMessage("Usuario ya existe");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al guardar usuario con nickname duplicado")
    public void shouldThrowWhenNicknameExists() {
        when(userRepository.findByCorreo(userPrueba.getCorreo())).thenReturn(Optional.empty());
        when(userRepository.findByNickname(userPrueba.getNickname())).thenReturn(Optional.of(userPrueba));

        assertThatThrownBy(() -> userService.save(userPrueba))
                .isInstanceOf(UserException.class)
                .hasMessage("Usuario ya existe");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Debe actualizar un usuario existente")
    public void shouldUpdateUser() {
        Long id = 1L;
        User cambios = new User();
        cambios.setNombre("Carlos Actualizado");
        cambios.setNickname("carlitos99");
        cambios.setCorreo("carlos@esports.cl");
        cambios.setRol("ORGANIZADOR");
        cambios.setEstado("ACTIVO");

        when(userRepository.findById(id)).thenReturn(Optional.of(userPrueba));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.updateById(id, cambios);

        assertThat(result.getNombre()).isEqualTo("Carlos Actualizado");
        assertThat(result.getRol()).isEqualTo("ORGANIZADOR");
        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).save(userPrueba);
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar usuario inexistente")
    public void shouldThrowWhenUpdateNotFound() {
        when(userRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateById(9999L, userPrueba))
                .isInstanceOf(UserException.class)
                .hasMessage("Usuario no encontrado");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Debe eliminar un usuario")
    public void shouldDeleteUser() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteById(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe filtrar usuarios por rol")
    public void shouldFindByRol() {
        when(userRepository.findByRol("JUGADOR")).thenReturn(userList);

        List<User> result = userService.findByRol("JUGADOR");

        assertThat(result).hasSize(10);
        verify(userRepository, times(1)).findByRol("JUGADOR");
    }
}
