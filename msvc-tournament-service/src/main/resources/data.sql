-- 1. Un torneo en curso de Valorant (game_id = 1) organizado por Carlos (organizer_id = 1)
INSERT INTO tournaments (name, game_id, organizer_id, start_date, end_date, state)
VALUES ('Copa de Verano Valorant', 1, 1, '2026-11-01', '2026-12-15', 'IN_PROGRESS');

-- 2. Una liga futura de League of Legends (game_id = 2), abierta a inscripciones
INSERT INTO tournaments (name, game_id, organizer_id, start_date, end_date, state)
VALUES ('Liga Master LoL 2027', 2, 1, '2027-01-15', '2027-03-20', 'UPCOMING');

-- 3. Un torneo relámpago de fútbol que ya terminó (game_id = 5)
INSERT INTO tournaments (name, game_id, organizer_id, start_date, end_date, state)
VALUES ('Torneo Relampago EA FC', 5, 1, '2026-05-01', '2026-05-05', 'FINISHED');

-- 4. Torneo futuro de Valorant (game_id = 1): aquí puede inscribirse "Los Tigres FC" (capitana jugador1)
INSERT INTO tournaments (name, game_id, organizer_id, start_date, end_date, state)
VALUES ('Copa Invierno Valorant', 1, 11, '2027-02-01', '2027-02-28', 'UPCOMING');

-- 5. Torneo ya finalizado de Rocket League (game_id = 4); tiene un premio entregado en prize-service
INSERT INTO tournaments (name, game_id, organizer_id, start_date, end_date, state)
VALUES ('Clasificatoria Rocket League', 4, 11, '2026-04-01', '2026-04-03', 'FINISHED');
