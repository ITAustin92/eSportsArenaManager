-- 1. Administrador principal
INSERT INTO usuario (nombre, nickname, correo, rol, estado)
VALUES ('Carlos Perez', 'cperez_admin', 'carlos.perez@torneo.com', 'ADMIN', 'ACTIVO');

-- 2. Jugador Activo
INSERT INTO usuario (nombre, nickname, correo, rol, estado)
VALUES ('Maria Gonzalez', 'mariag_10', 'maria.gonzalez@torneo.com', 'PLAYER', 'ACTIVO');

-- 3. Jugador Sancionado
INSERT INTO usuario (nombre, nickname, correo, rol, estado)
VALUES ('Luis Rojas', 'lrojas_destroyer', 'luis.rojas@torneo.com', 'PLAYER', 'SANCIONADO');

-- 4. Árbitro
INSERT INTO usuario (nombre, nickname, correo, rol, estado)
VALUES ('Ana Silva', 'asilva_ref', 'ana.silva@torneo.com', 'REFEREE', 'ACTIVO');

-- 5. Jugador Inactivo
INSERT INTO usuario (nombre, nickname, correo, rol, estado)
VALUES ('Pedro Munoz', 'pmunoz_22', 'pedro.munoz@torneo.com', 'PLAYER', 'INACTIVO');

-- 6. Entrenador
INSERT INTO usuario (nombre, nickname, correo, rol, estado)
VALUES ('Laura Soto', 'lsoto_coach', 'laura.soto@torneo.com', 'COACH', 'ACTIVO');

-- 7. Jugador Activo
INSERT INTO usuario (nombre, nickname, correo, rol, estado)
VALUES ('Diego Tapia', 'dtapia_gol', 'diego.tapia@torneo.com', 'PLAYER', 'ACTIVO');

-- 8. Administrador Inactivo
INSERT INTO usuario (nombre, nickname, correo, rol, estado)
VALUES ('Sofia Castro', 'scastro_sys', 'sofia.castro@torneo.com', 'ADMIN', 'INACTIVO');

-- 9. Árbitro Sancionado
INSERT INTO usuario (nombre, nickname, correo, rol, estado)
VALUES ('Javier Diaz', 'jdiaz_var', 'javier.diaz@torneo.com', 'REFEREE', 'SANCIONADO');

-- 10. Jugador Activo
INSERT INTO usuario (nombre, nickname, correo, rol, estado)
VALUES ('Camila Vargas', 'cvargas_11', 'camila.vargas@torneo.com', 'PLAYER', 'ACTIVO');