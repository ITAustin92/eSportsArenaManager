# Backend de eSports Arena Manager EP3

Proyecto Maven independiente con Java 21 y Spring Boot. El frontend consume únicamente
el gateway (8080). Eureka utiliza 8761; auth, 8099; los servicios de negocio, 8000–8010.

## Desarrollo local con datos ficticios

Este modo utiliza H2 y recrea los datos de demostración. No es un modo persistente.

En la raíz de este repositorio, copiar `.env.example` a `.env` y definir un `JWT_SECRETO` propio de al
menos 32 caracteres. Docker Compose lee las variables de `.env`; Spring ejecutado
manualmente necesita las variables en el entorno del proceso o del IDE.

```sh
docker compose up --build
```

Solo Eureka y el gateway publican puertos al host. Los demás servicios se comunican
por la red de Docker. El registro en Eureka puede tardar tras el primer arranque.

También se puede usar Java 21 y el wrapper Maven desde esta carpeta:

```sh
./mvnw compile
./mvnw -pl msvc-eureka spring-boot:run
```

Después iniciar user-service, los demás servicios y el gateway, cada uno en su
proceso. En Windows usar `mvnw.cmd` y configurar `JAVA_HOME` con el JDK 21.
El wrapper descargado de un módulo del ZIP se incorporó al proyecto padre.

## Cuentas ficticias del modo local

| Usuario | Contraseña | Rol |
| --- | --- | --- |
| admin | admin123 | ADMIN |
| organizador1 | org123 | ORGANIZADOR |
| jugador1 | jugador123 | JUGADOR |
| jugador2 | jugador123 | JUGADOR |

Las cuentas están asociadas a los perfiles de `data.sql`. Los equipos de muestra
pueden necesitar completar integrantes antes de satisfacer las nuevas reglas de
inscripción. El registro público crea exclusivamente jugadores.

## Persistencia

- `mysql`: usa `MYSQL_HOST`, `MYSQL_PORT`, `MYSQL_USER`, `MYSQL_PASSWORD`. Conserva
  tablas mediante `ddl-auto=update`, no ejecuta `data.sql` y desactiva consola H2.
- `prod`: PostgreSQL externo con `DB_URL`, `DB_USUARIO`, `DB_CLAVE`. Cada servicio
  utiliza un esquema `arena_<servicio>`, conserva las tablas y limita su pool a dos
  conexiones. No carga cuentas ficticias ni ejecuta `data.sql`.

Para una base existente hacer un respaldo antes de cambiar de versión. El uso de
`update` sirve para esta entrega; los cambios de esquema futuros deberían gestionarse
con migraciones versionadas. No activar producción sobre datos que se desee conservar
sin revisar antes el esquema y sus restricciones.

## Despliegue de producción

1. Publicar cada módulo con el Dockerfile y el argumento `MODULO=msvc-...`.
2. Definir `SPRING_PROFILES_ACTIVE=prod`, `PORT` y `JWT_SECRETO` en cada servicio.
3. En los servicios con base, definir la conexión PostgreSQL con TLS.
4. Configurar las variables `<DOMINIO>_SERVICE_URL` requeridas por cada
   `application-prod.yml`. En producción Eureka se desactiva; el gateway usa las
   direcciones fijas del cliente de descubrimiento simple de Spring Cloud.
5. En auth definir `USER_SERVICE_URL`. Para crear el primer administrador en una base
   nueva, definir `ADMIN_USERNAME`, `ADMIN_PASSWORD` (mínimo 12 caracteres),
   `ADMIN_NOMBRE` y `ADMIN_CORREO`, con user-service ya disponible. Tras la creación,
   retirar esas credenciales de bootstrap. No se recrea una cuenta existente.
6. En el gateway definir `CORS_ORIGENES` con el origen HTTPS exacto de Vercel, sin `/`
   final. Cada servicio debe compartir el mismo secreto JWT.
7. En Vercel configurar `VITE_API_URL` con la URL HTTPS del gateway y volver a compilar.

`.env.example` enumera las variables, sin incluir secretos reales. El Compose
incluido corresponde a demostración local y no sustituye los perfiles de producción.

## Contratos y reglas agregadas

- Torneos: `maxParticipants` (mínimo 2), `registrationDeadline` (instante UTC).
  Si no hay cierre explícito, la API expone el inicio del torneo en America/Santiago.
- Inscripciones: exactamente uno de `teamId` o `userId`; el jugador individual solo
  participa en juegos 1v1. `GET /api/v1/registrations/mine` devuelve su actividad individual.
- El capitán gestiona sus equipos e inscripciones; un jugador individual solo las suyas.
- La inscripción requiere estado UPCOMING, plazo vigente, cupos, juego compatible,
  equipo completo sin repetidos, usuarios activos y ausencia de sanciones ACTIVE
  correspondientes al torneo. PENDING, ACCEPTED y CONFIRMED ocupan cupo;
  REJECTED y CANCELLED lo liberan.
- Reinscribirse después de cancelar o rechazar reutiliza el registro, tras validar
  otra vez las reglas. La base impide duplicar participante y torneo.
- PostgreSQL serializa las escrituras de inscripción por torneo mediante un bloqueo
  transaccional; H2/MySQL usan el monitor del servicio, por lo que esos modos deben
  ejecutarse con una sola instancia de registration-service.
- Perfil: `avatar` opcional PNG/JPEG/WebP en base64, limitado en tamaño.
- Partidas: campo `round` opcional para agrupar rondas.
- `GET /api/v1/users/public` solo expone id y nickname; no publica correos.

La API de partidas y rankings heredada sigue identificando equipos. La inscripción
individual está implementada, pero el registro de resultados y clasificación
individual requiere extender esos contratos antes de anunciar un ciclo 1v1 completo.

## Documentación y salud

- Swagger: `http://localhost:8080/docs/swagger-ui.html`.
- Salud por servicio: `/actuator/health`, sin detalles internos.
- Los errores de validación incluyen `error` y `campos`. Los errores remotos
  conservan códigos relevantes y los fallos internos no devuelven la excepción.
- Feign usa tiempos de conexión y respuesta acotados. Las escrituras no se reintentan
  automáticamente.

## Estado de comprobación

Los 14 módulos compilaron con Java 21 mediante `mvnw compile`. No se ejecutaron las
pruebas ni se levantó el ecosistema completo. Las pruebas Java del ZIP deben adaptarse
al nuevo contrato de reglas y clientes antes de usar sus resultados como evidencia.
No se ha realizado ningún despliegue ni se han creado recursos externos.

## Repositorios separados

- Backend: https://github.com/ITAustin92/eSportsArenaManager
- Frontend: https://github.com/ITAustin92/eSportsArenaManager-ReactMig

Clonar ambos repositorios como carpetas hermanas. El frontend utiliza la URL del
gateway; no necesita contener el código ni los archivos de compilación del backend.

## Mantener el formato

Las herramientas de formato son independientes del frontend y solo se necesitan
para editar el código. Requieren Node.js y npm:

```sh
npm ci
npm run format
npm run format:check
```

Prettier y su plugin de Java mantienen las tabulaciones y los saltos de línea.
Java, Maven y Docker no dependen de estas herramientas para compilar los servicios.
