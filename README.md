# Backend de eSports Arena Manager EP3

Proyecto Maven independiente con Java 21 y Spring Boot. En el modo de módulos separados, el frontend consume únicamente
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

## Despliegue de producción en Render Free

El Dockerfile compila por defecto `arena-web`, que ejecuta los módulos de
negocio y autenticación en un proceso. Conserva los endpoints `/api/v1` y
`/api/v2` de los módulos y publica una sola interfaz Swagger.

1. En Supabase abrir **Connect → Session pooler** y guardar host, usuario
   completo y contraseña de PostgreSQL.
2. En Render crear un **Web Service** desde la rama `main` con Docker y el plan
   **Free**, o aplicar `render.yaml` como Blueprint. Usar el Dockerfile de
   la raíz y `/actuator/health` como ruta de salud.
3. Definir `SPRING_PROFILES_ACTIVE=prod`, `DB_URL`, `DB_USUARIO`, `DB_CLAVE`,
   `JWT_SECRETO` y `CORS_ORIGENES`. Ejemplo de URL JDBC:
   `jdbc:postgresql://HOST_DEL_POOLER:5432/postgres?sslmode=require`.
   El perfil crea el esquema `arena_ep3` y conserva los datos. Si ya existen
   esquemas por módulo, migrar esos datos explícitamente antes de cambiar.
4. Para crear el administrador inicial, definir `ADMIN_USERNAME`,
   `ADMIN_PASSWORD` (mínimo 12 caracteres), `ADMIN_CORREO` y, opcionalmente,
   `ADMIN_NOMBRE`. Tras comprobar el acceso, retirar esas variables.
   Crear un juego, una cuenta organizadora y un torneo desde el panel de administración.
5. En Vercel, configurar `VITE_API_URL` con la URL HTTPS del servicio de Render
   y desplegar la rama del frontend que contiene EP3.

Para probar el ejecutable unificado con H2 en la máquina local:

```sh
./mvnw -pl arena-web -am package
java -jar arena-web/target/arena-web-1.0-SNAPSHOT.jar
```

El backend responde en `http://localhost:8080`, con las cuentas ficticias del
README. Docker Compose permite ejecutar los módulos en procesos separados
para el flujo de desarrollo.

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

Las partidas admiten pares de equipos o pares de jugadores. Los resultados usan
winnerTeamId o winnerUserId según la modalidad y validan el marcador. La clasificación
mantiene una contribución por partida: reenviar reemplaza ese aporte, corregir el
ganador retira el anterior y anular elimina sus puntos. La actualización se completa
dentro de una transacción; PostgreSQL agrega un bloqueo por torneo. H2/MySQL deben
usar una sola instancia de ranking-service. React permite programar partidas y
registrar resultados como ADMIN, y el perfil incluye estadísticas individuales.
Los premios conservan su contrato de asignación por equipos.

## Documentación y salud

- Swagger: `http://localhost:8080/docs/swagger-ui.html`.
- Salud por servicio: `/actuator/health`, sin detalles internos.
- Los errores de validación incluyen `error` y `campos`. Los errores remotos
  conservan códigos relevantes y los fallos internos no devuelven la excepción.
- Feign usa tiempos de conexión y respuesta acotados. Las escrituras no se reintentan
  automáticamente.

## Estado de comprobación

Verificación del 23 de noviembre de 2026 con Java 21:

- Suite completa `./mvnw.cmd test`: 153 pruebas aprobadas, sin fallos ni omisiones.
- Los 14 servicios se levantaron localmente y respondieron en `/actuator/health`.
- Desde el frontend separado se aprobaron 14 recorridos Playwright contra la API
  real: cuentas, JWT, permisos, CRUD, inscripciones y cupos simultáneos.
- Se corrigió la edición de juegos para guardar nombre y género, con una prueba
  de regresión y validación de nombres duplicados.

Los recursos de `src/test/resources/application.properties` de cada servicio aíslan
las pruebas en H2 en memoria y desactivan Eureka. No comparten los archivos de base
local ni necesitan que el ecosistema esté iniciado. Las pruebas de integración del
frontend sí necesitan los servicios reales y datos de demostración; crean registros.
Consultar el README del frontend para repetirlas.

La verificación corresponde al modo local H2. El perfil PostgreSQL/MySQL, el
arranque con Docker y el despliegue en la nube aún requieren validación propia.
El despliegue externo y las pruebas con base persistente siguen pendientes.

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

## Arranque local reproducible en Windows

Compilar primero con Java 21 y `./mvnw.cmd package`. Después:

```powershell
./scripts/Start-Local.ps1 -InMemory
```

Ese modo carga los datos ficticios en bases temporales y no abre los archivos de
base de desarrollo. Esperar a que el gateway resuelva el catálogo; Eureka puede
necesitar tiempo adicional después de que la salud responda. Los logs y los
procesos registrados quedan en `tmp/`, excluido de Git. Para detener exclusivamente
los servicios iniciados por este script:

```powershell
./scripts/Stop-Local.ps1
```

La guía de despliegue y los documentos de entrega están en `docs/ep3/` del repositorio
frontend. Los perfiles de producción requieren configuración y validación propias.
