-- 1. SANCIÓN A UN JUGADOR ESPECÍFICO EN UN PARTIDO
-- Castigamos a Luis Rojas (Id 3) del equipo Leones (Id 2) en el torneo de Valorant (Id 1)
-- durante el partido (Id 1).
INSERT INTO sanctions (tournament_id, team_id, match_id, user_id, type, reason, matches_suspended, fine_amount, status)
VALUES (1, 2, 1, 3, 'SUSPENSION', 'Uso de lenguaje tóxico y conducta antideportiva en el chat general.', 2, 0.0, 'ACTIVE');

-- 2. SANCIÓN ADMINISTRATIVA A UN EQUIPO COMPLETO (Fuera de un partido)
-- Multamos a Los Tigres FC (Id 1) en el torneo de Valorant (Id 1).
-- Fíjate que match_id y user_id quedan en NULL (vacíos) porque la culpa es de todo el club.
INSERT INTO sanctions (tournament_id, team_id, match_id, user_id, type, reason, matches_suspended, fine_amount, status)
VALUES (1, 1, NULL, NULL, 'FINE', 'Llegada 30 minutos tarde al lobby oficial del torneo.', 0, 150.50, 'ACTIVE');

-- 3. SANCIÓN YA CUMPLIDA (Historial)
-- Una advertencia antigua al equipo Leones (Id 2).
INSERT INTO sanctions (tournament_id, team_id, match_id, user_id, type, reason, matches_suspended, fine_amount, status)
VALUES (1, 2, NULL, NULL, 'WARNING', 'Desconexión injustificada durante la fase de picks y bans.', 0, 0.0, 'SERVED');