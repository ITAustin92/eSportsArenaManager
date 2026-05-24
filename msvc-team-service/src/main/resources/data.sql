-- ========================================================
-- STEP 1: CREAR LOS EQUIPOS BASE
-- ========================================================
-- Al insertarlos, H2 les asignará automáticamente equipo_id = 1 y equipo_id = 2

-- Equipo 1: Capitana Maria (Id 2), Juego Valorant (Id 1)
INSERT INTO equipos (nombre, capitan_id, juego_principal_id, estado)
VALUES ('Los Tigres FC', 2, 1, 'ACTIVO');

-- Equipo 2: Capitana Camila (Id 10), Juego League of Legends (Id 2)
INSERT INTO equipos (nombre, capitan_id, juego_principal_id, estado)
VALUES ('Los Leones de la Plaza', 10, 2, 'ACTIVO');


-- ========================================================
-- STEP 2: POBLAR LOS MIEMBROS DE CADA EQUIPO
-- ========================================================
-- Nota la columna oculta 'equipo_id' que Spring crea gracias a tu @JoinColumn

-- Miembros para Los Tigres FC (equipo_id = 1)
INSERT INTO miembros_equipo (usuario_id, rol_dentro_equipo, equipo_id)
VALUES (2, 'Estratega / IGL', 1); -- Maria Gonzalez

INSERT INTO miembros_equipo (usuario_id, rol_dentro_equipo, equipo_id)
VALUES (7, 'Tirador Principal', 1); -- Diego Tapia


-- Miembros para Los Leones de la Plaza (equipo_id = 2)
INSERT INTO miembros_equipo (usuario_id, rol_dentro_equipo, equipo_id)
VALUES (10, 'Soporte / Utilidad', 2); -- Camila Vargas

INSERT INTO miembros_equipo (usuario_id, rol_dentro_equipo, equipo_id)
VALUES (3, 'Tirador / Carry', 2); -- Luis Rojas