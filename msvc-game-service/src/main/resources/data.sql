-- 1. Shooter en equipos
INSERT INTO juegos (nombre, genero, modalidad, jugadores_por_equipo, estado)
VALUES ('Valorant', 'Shooter', '5v5', 5, 'ACTIVO');

-- 2. El clásico MOBA
INSERT INTO juegos (nombre, genero, modalidad, jugadores_por_equipo, estado)
VALUES ('League of Legends', 'MOBA', '5v5', 5, 'ACTIVO');

-- 3. Shooter táctico tradicional
INSERT INTO juegos (nombre, genero, modalidad, jugadores_por_equipo, estado)
VALUES ('Counter-Strike 2', 'Shooter', '5v5', 5, 'ACTIVO');

-- 4. Deporte en autos
INSERT INTO juegos (nombre, genero, modalidad, jugadores_por_equipo, estado)
VALUES ('Rocket League', 'Deportes', '3v3', 3, 'ACTIVO');

-- 5. Juego de fútbol individual
INSERT INTO juegos (nombre, genero, modalidad, jugadores_por_equipo, estado)
VALUES ('EA Sports FC 24', 'Deportes', '1v1', 1, 'ACTIVO');

-- 6. Juego de pelea uno contra uno (Inactivo por ahora para pruebas)
INSERT INTO juegos (nombre, genero, modalidad, jugadores_por_equipo, estado)
VALUES ('Street Fighter 6', 'Lucha', '1v1', 1, 'INACTIVO');