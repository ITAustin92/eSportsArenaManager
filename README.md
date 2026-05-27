# eSports Arena Manager

Plataforma backend para organizar torneos gamer. Permite administrar juegos, jugadores, 
equipos, torneos, inscripciones, partidas, resultados, rankings, sanciones, premios y 
notificaciones.

## Integrantes — Grupo 18
- Luis Ahumada
- Cristian Villavicencio
- Ángel Quintero

## Tecnologías
- Java 21 + Spring Boot 4.x
- H2 Database (embebida, archivo)
- OpenFeign para comunicación entre servicios
- Maven

## Instrucciones de ejecución

1. Clonar el repositorio
2. Abrir el proyecto en IntelliJ IDEA
3. Iniciar los microservicios en este orden desde IntelliJ:

| Orden | Microservicio | Puerto |
|-------|--------------|--------|
| 1 | msvc-user-service | 8000 |
| 2 | msvc-game-service | 8001 |
| 3 | msvc-team-service | 8002 |
| 4 | msvc-tournament-service | 8003 |
| 5 | msvc-registration-service | 8004 |
| 6 | msvc-match-service | 8005 |
| 7 | msvc-result-service | 8006 |
| 8 | msvc-ranking-service | 8007 |
| 9 | msvc-sanction-service | 8008 |
| 10 | msvc-prize-service | 8009 |
| 11 | msvc-notification-service | 8010 |

## Consola H2
Cada servicio expone su consola en `http://localhost:{puerto}/h2-console`  
JDBC URL: `jdbc:h2:file:./data/{nombre}` (ej: `jdbc:h2:file:./data/game`)  
Usuario: `sa` | Contraseña: `sa`

## Endpoints principales

### user-service (8000)
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | /api/v1/users | Listar todos |
| GET | /api/v1/users/{id} | Buscar por ID |
| GET | /api/v1/users/rol/{rol} | Filtrar por rol |
| GET | /api/v1/users/estado/{estado} | Filtrar por estado |
| POST | /api/v1/users | Crear usuario |
| PUT | /api/v1/users/{id} | Actualizar |
| DELETE | /api/v1/users/{id} | Desactivar |

### game-service (8001)
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | /api/v1/games | Listar activos |
| GET | /api/v1/games/{id} | Buscar por ID |
| GET | /api/v1/games/estado/{estado} | Filtrar por estado |
| POST | /api/v1/games | Crear juego |
| PUT | /api/v1/games/{id} | Actualizar |
| DELETE | /api/v1/games/{id} | Desactivar |

### team-service (8002)
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | /api/v1/teams | Listar todos |
| GET | /api/v1/teams/{id} | Buscar por ID |
| GET | /api/v1/teams/estado/{estado} | Filtrar por estado |
| GET | /api/v1/teams/juego/{juegoId} | Filtrar por juego |
| GET | /api/v1/teams/capitan/{capitanId} | Filtrar por capitán |
| POST | /api/v1/teams | Crear equipo |
| PUT | /api/v1/teams/{id} | Actualizar |
| DELETE | /api/v1/teams/{id} | Desactivar |

### tournament-service (8003)
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | /api/v1/tournaments | Listar todos |
| GET | /api/v1/tournaments/{id} | Buscar por ID |
| GET | /api/v1/tournaments/state/{state} | Filtrar por estado |
| GET | /api/v1/tournaments/game/{gameId} | Filtrar por juego |
| POST | /api/v1/tournaments | Crear torneo |
| PUT | /api/v1/tournaments/{id} | Actualizar |
| DELETE | /api/v1/tournaments/{id} | Cancelar |

### registration-service (8004)
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | /api/v1/registrations | Listar todas |
| GET | /api/v1/registrations/{id} | Buscar por ID |
| GET | /api/v1/registrations/team/{teamId} | Por equipo |
| GET | /api/v1/registrations/tournament/{tournamentId} | Por torneo |
| POST | /api/v1/registrations | Crear inscripción |
| PATCH | /api/v1/registrations/{id}/status?status= | Actualizar estado |
| DELETE | /api/v1/registrations/{id} | Cancelar |

### match-service (8005)
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | /api/v1/matches/{id} | Buscar por ID |
| GET | /api/v1/matches/tournament/{tournamentId} | Por torneo |
| GET | /api/v1/matches/team/{teamId} | Por equipo |
| POST | /api/v1/matches | Crear partida |
| PUT | /api/v1/matches/{id} | Actualizar |
| DELETE | /api/v1/matches/{id} | Cancelar |

### result-service (8006)
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | /api/v1/results/{id} | Buscar por ID |
| GET | /api/v1/results/match/{matchId} | Por partida |
| GET | /api/v1/results/winner/{teamId} | Por ganador |
| POST | /api/v1/results | Registrar resultado |
| PUT | /api/v1/results/{id} | Actualizar |
| DELETE | /api/v1/results/{id} | Anular |

### ranking-service (8007)
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | /api/v1/rankings/tournament/{tournamentId} | Tabla de posiciones |
| GET | /api/v1/rankings/tournament/{tournamentId}/team/{teamId} | Posición de equipo |
| POST | /api/v1/rankings/update | Actualizar ranking |

### sanction-service (8008)
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | /api/v1/sanctions | Listar todas |
| GET | /api/v1/sanctions/{id} | Buscar por ID |
| GET | /api/v1/sanctions/user/{userId} | Por usuario |
| GET | /api/v1/sanctions/team/{teamId} | Por equipo |
| POST | /api/v1/sanctions | Crear sanción |
| PATCH | /api/v1/sanctions/{id}/status?status= | Actualizar estado |
| DELETE | /api/v1/sanctions/{id} | Eliminar |

### prize-service (8009)
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | /api/v1/prizes | Listar todos |
| GET | /api/v1/prizes/{id} | Buscar por ID |
| GET | /api/v1/prizes/tournament/{tournamentId} | Por torneo |
| GET | /api/v1/prizes/team/{teamId} | Por equipo |
| POST | /api/v1/prizes | Crear premio |
| POST | /api/v1/prizes/tournament/{tournamentId}/distribute | Distribuir premios |

### notification-service (8010)
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | /api/v1/notifications | Listar todas |
| GET | /api/v1/notifications/{id} | Buscar por ID |
| GET | /api/v1/notifications/user/{userId} | Por usuario |
| GET | /api/v1/notifications/team/{teamId} | Por equipo |
| GET | /api/v1/notifications/tournament/{tournamentId} | Por torneo |
| POST | /api/v1/notifications/send | Enviar notificación |

Tablero de Trello del Proyecto:
https://trello.com/b/LwwCThWS/proyecto-esports
