# eSports Arena Manager

Plataforma backend distribuida para la gestión integral de torneos eSports desarrollada con arquitectura de microservicios utilizando Spring Boot.

Permite administrar usuarios, juegos, equipos, torneos, inscripciones, partidas, resultados, rankings, sanciones, premios y notificaciones, incorporando además autenticación JWT, documentación OpenAPI y descubrimiento de servicios.

---

# Integrantes — Grupo 18

* Luis Ahumada
* Cristian Villavicencio
* Ángel Quintero

---

# Tecnologías Utilizadas

## Backend

* Java 21
* Spring Boot 4.x
* Spring Security
* Spring Data JPA
* OpenFeign
* Spring HATEOAS

## Arquitectura Distribuida

* Eureka Server
* Spring Cloud Gateway
* JWT Authentication

## Persistencia

* H2 Database

## Testing

* JUnit 5
* Mockito
* DataFaker

## Documentación

* Swagger / OpenAPI 3

## Gestión

* Maven
* Trello

---

# Arquitectura General

El sistema implementa una arquitectura distribuida basada en microservicios.

La solución está compuesta por:

| Servicio             | Puerto |
| -------------------- | ------ |
| Eureka Server        | 8761   |
| API Gateway          | 8080   |
| Auth Service         | 8099   |
| User Service         | 8000   |
| Game Service         | 8001   |
| Team Service         | 8002   |
| Tournament Service   | 8003   |
| Registration Service | 8004   |
| Match Service        | 8005   |
| Result Service       | 8006   |
| Ranking Service      | 8007   |
| Sanction Service     | 8008   |
| Prize Service        | 8009   |
| Notification Service | 8010   |

---

# Componentes de Infraestructura

## Eureka Server

Responsable del descubrimiento automático de servicios.

Todos los microservicios se registran automáticamente en Eureka al iniciar.

Acceso:

http://localhost:8761

---

## API Gateway

Punto único de entrada para todos los clientes.

Responsabilidades:

* Enrutamiento dinámico
* Validación JWT
* Integración con Eureka
* Centralización del acceso a APIs

Acceso:

http://localhost:8080

---

# Autenticación y Seguridad

El sistema implementa autenticación basada en JWT mediante auth-service.

## Endpoints de autenticación

### Registro

POST

/api/v1/auth/register

Permite crear nuevas cuentas.

---

### Login

POST

/api/v1/auth/login

Retorna:

* JWT Token
* Email
* Roles asignados

---

## Roles del Sistema

### ROLE_ADMIN

Permisos administrativos completos.

### ROLE_ORGANIZADOR

Gestión de torneos y operaciones asociadas.

---

## JWT

Los tokens contienen:

* Subject (email)
* Roles
* Fecha de expiración

Los microservicios validan el token localmente utilizando una clave secreta compartida.

No es necesario consultar auth-service en cada solicitud.

---

# Arquitectura Interna

Todos los microservicios implementan el patrón CSR:

## Controller

Expone endpoints REST.

## Service

Contiene lógica de negocio.

## Repository

Gestiona persistencia mediante JPA.

---

# Comunicación entre Microservicios

La comunicación se realiza mediante OpenFeign.

Ejemplos:

* Registration → Tournament
* Registration → Sanction
* Match → Team
* Result → Match
* Prize → Ranking

Esto permite mantener servicios desacoplados y especializados.

---

# Reglas de Negocio Principales

## Registration Service

* Evita inscripciones duplicadas
* Verifica cupos disponibles
* Valida sanciones activas
* Verifica estado del torneo

## Ranking Service

* Calcula posiciones automáticamente
* Actualiza puntajes
* Mantiene tabla de posiciones

## Prize Service

* Distribuye premios según ranking final
* Verifica finalización del torneo

---

# Validaciones y Manejo de Excepciones

El sistema implementa:

* @Valid
* @NotBlank
* @NotNull
* @Positive

Además utiliza:

* @RestControllerAdvice
* @ExceptionHandler
* ResponseEntity

para garantizar respuestas HTTP consistentes.

---

# Testing

Se implementaron pruebas unitarias en todos los microservicios de negocio.

Tecnologías utilizadas:

* JUnit 5
* Mockito
* DataFaker

Las pruebas cubren:

* Casos exitosos
* Reglas de negocio
* Validaciones
* Excepciones
* Clientes Feign simulados

Ejemplos de escenarios probados:

* Inscripción duplicada
* Estado inválido
* Recurso inexistente
* Ganador inválido
* Torneo no finalizado

---

# Documentación API

Cada microservicio expone documentación Swagger/OpenAPI.

Acceso:

http://localhost:8080/docs/swagger-ui.html

---

# Consola H2

Cada servicio expone una consola H2.

URL:

[http://localhost:{puerto}/h2-console](http://localhost:{puerto}/h2-console)

Ejemplo:

jdbc:h2:file:./data/game

Credenciales:

Usuario: sa

Contraseña: sa

---

# Ejecución del Proyecto

## Orden de inicio recomendado

1. Eureka Server
2. Auth Service
3. User Service
4. Game Service
5. Team Service
6. Tournament Service
7. Sanction Service
8. Registration Service
9. Match Service
10. Result Service
11. Ranking Service
12. Prize Service
13. Notification Service
14. Gateway Service

---

# Tablero Trello

https://trello.com/b/LwwCThWS/proyecto-esports
