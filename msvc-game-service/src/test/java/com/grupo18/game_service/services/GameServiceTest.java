package com.grupo18.game_service.services;

import com.grupo18.game_service.exceptions.GameException;
import com.grupo18.game_service.models.Game;
import com.grupo18.game_service.repositories.GameRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class): habilita Mockito. Son pruebas unitarias:
// NO levantan Spring ni la base de datos, todo se simula con @Mock.
@ExtendWith(MockitoExtension.class)
public class GameServiceTest {

// @Mock: objeto falso. Decide qué devuelve con when(...).
    @Mock
    private GameRepository gameRepository;

   // @InjectMocks: crea el servicio real y le inyecta los @Mock de arriba.
    @InjectMocks
    private GameServiceImpl gameService;

    private Game gamePrueba;
    private List<Game> gameList = new ArrayList<>();

 // @BeforeEach: se ejecuta antes de CADA test para dejar los datos en estado conocido.
    @BeforeEach
    public void setUp() {
        gamePrueba = new Game();
        gamePrueba.setJuegoId(1L);
        gamePrueba.setNombre("Valorant");
        gamePrueba.setGenero("FPS");
        gamePrueba.setModalidad("EQUIPOS");
        gamePrueba.setJugadoresPorEquipo(5);
        gamePrueba.setEstado("ACTIVO");

        Faker faker = new Faker();
        for (int i = 0; i < 5; i++) {
            Game g = new Game();
            g.setNombre(faker.esports().game());
            g.setGenero("MOBA");
            g.setModalidad("EQUIPOS");
            g.setJugadoresPorEquipo(5);
            g.setEstado("ACTIVO");
            gameList.add(g);
        }
    }

// Patrón AAA: Arrange (preparar) → Act (ejecutar) → Assert (verificar).

    @Test
    @DisplayName("Debe listar todos los juegos")
    public void shouldListAllGames() {
// Arrange
        List<Game> games = new ArrayList<>(gameList);
        games.add(gamePrueba);
        when(gameRepository.findAll()).thenReturn(games);

   // Act
        List<Game> result = gameService.findAll();

// Assert
        assertThat(result).hasSize(6);
        assertThat(result).contains(gamePrueba);
        verify(gameRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar un juego por su ID")
    public void shouldFindGameById() {
// Arrange
        when(gameRepository.findById(1L)).thenReturn(Optional.of(gamePrueba));

// Act
        Game result = gameService.findById(1L);

// Assert
        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("Valorant");
        verify(gameRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar ID inexistente")
    public void shouldThrowWhenGameNotFoundById() {
 // Arrange
        when(gameRepository.findById(9999L)).thenReturn(Optional.empty());

  // Act + Assert
        assertThatThrownBy(() -> gameService.findById(9999L))
                .isInstanceOf(GameException.class)
                .hasMessage("Juego no encontrado");
        verify(gameRepository, times(1)).findById(9999L);
    }

    @Test
    @DisplayName("Debe guardar un juego nuevo")
    public void shouldSaveGame() {
        // Arrange
        when(gameRepository.findByNombre("Valorant")).thenReturn(Optional.empty());
        when(gameRepository.save(gamePrueba)).thenReturn(gamePrueba);

        // Act
        Game result = gameService.save(gamePrueba);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("Valorant");
        verify(gameRepository, times(1)).findByNombre("Valorant");
        verify(gameRepository, times(1)).save(gamePrueba);
    }

    @Test
    @DisplayName("Debe lanzar excepción al guardar juego con nombre duplicado")
    public void shouldThrowWhenGameNameExists() {
        // Arrange
        when(gameRepository.findByNombre("Valorant")).thenReturn(Optional.of(gamePrueba));

        // Act + Assert
        assertThatThrownBy(() -> gameService.save(gamePrueba))
                .isInstanceOf(GameException.class)
                .hasMessage("El nombre del juego ya existe");
        verify(gameRepository, times(1)).findByNombre("Valorant");
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    @DisplayName("Debe actualizar un juego existente")
    public void shouldUpdateGame() {
        // Arrange
        Long id = 1L;

        Game gameExistenteEnBd = new Game();
        gameExistenteEnBd.setJuegoId(id);
        gameExistenteEnBd.setModalidad("EQUIPOS");
        gameExistenteEnBd.setJugadoresPorEquipo(5);
        gameExistenteEnBd.setEstado("INACTIVO");

        Game cambios = new Game();
        cambios.setModalidad("INDIVIDUAL");
        cambios.setJugadoresPorEquipo(1);
        cambios.setEstado("ACTIVO");

        when(gameRepository.findById(id)).thenReturn(Optional.of(gameExistenteEnBd));
        when(gameRepository.save(any(Game.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Game result = gameService.updateById(id, cambios);

        // Assert de la respuesta
        assertThat(result.getModalidad()).isEqualTo("INDIVIDUAL");
        assertThat(result.getJugadoresPorEquipo()).isEqualTo(1);
        assertThat(result.getEstado()).isEqualTo("ACTIVO");

        verify(gameRepository).findById(id);

        // 🛠️ LA SOLUCIÓN: verify con any() para que Mockito no se ponga estricto con la referencia
        ArgumentCaptor<Game> gameCaptor = ArgumentCaptor.forClass(Game.class);
        verify(gameRepository).save(gameCaptor.capture()); // Captura lo que sea que haya enviado el servicio

        // Aquí es donde realmente testeamos si los datos internos están bien
        Game gameGuardado = gameCaptor.getValue();
        assertThat(gameGuardado.getJuegoId()).isEqualTo(id);
        assertThat(gameGuardado.getModalidad()).isEqualTo("INDIVIDUAL");
        assertThat(gameGuardado.getJugadoresPorEquipo()).isEqualTo(1);
        assertThat(gameGuardado.getEstado()).isEqualTo("ACTIVO");
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar juego inexistente")
    public void shouldThrowWhenUpdateNotFound() {
        // Arrange
        when(gameRepository.findById(9999L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> gameService.updateById(9999L, gamePrueba))
                .isInstanceOf(GameException.class)
                .hasMessage("Juego no encontrado");
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    @DisplayName("Debe desactivar (soft-delete) un juego existente")
    public void shouldDeactivateGame() {

        Long id = 1L;

        Game gameParaDesactivar = new Game();
        gameParaDesactivar.setJuegoId(id);
        gameParaDesactivar.setEstado("ACTIVO");

        when(gameRepository.findById(id)).thenReturn(Optional.of(gameParaDesactivar));
        when(gameRepository.save(any(Game.class))).thenAnswer(inv -> inv.getArgument(0));
        gameService.deleteById(id);

        verify(gameRepository).findById(id);

        ArgumentCaptor<Game> gameCaptor = ArgumentCaptor.forClass(Game.class);

        verify(gameRepository).save(gameCaptor.capture());

        Game gameGuardado = gameCaptor.getValue();
        assertThat(gameGuardado.getEstado()).isEqualTo("INACTIVO");
        assertThat(gameGuardado.getJuegoId()).isEqualTo(id);
    }

    @Test
    @DisplayName("Debe listar juegos por estado")
    public void shouldFindByEstado() {
        // Arrange
        when(gameRepository.findByEstado("ACTIVO")).thenReturn(gameList);

        // Act
        List<Game> result = gameService.findByEstado("ACTIVO");

        // Assert
        assertThat(result).hasSize(5);
        verify(gameRepository, times(1)).findByEstado("ACTIVO");
    }
}
