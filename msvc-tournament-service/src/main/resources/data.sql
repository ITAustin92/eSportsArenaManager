-- 1. Un torneo activo de Valorant (game_id = 1) organizado por Carlos (organizer_id = 1)
INSERT INTO tournaments (name, game_id, organizer_id, start_date, end_date, state)
VALUES ('Copa de Verano Valorant', 1, 1, '2026-06-01', '2026-06-30', 'IN_PROGRESS');

-- 2. Una liga futura de League of Legends (game_id = 2) organizada por Carlos
INSERT INTO tournaments (name, game_id, organizer_id, start_date, end_date, state)
VALUES ('Liga Master LoL 2026', 2, 1, '2026-08-15', '2026-11-20', 'UPCOMING');

-- 3. Un torneo relámpago de fútbol que ya terminó (game_id = 5)
INSERT INTO tournaments (name, game_id, organizer_id, start_date, end_date, state)
VALUES ('Torneo Relampago EA FC', 5, 1, '2026-05-01', '2026-05-05', 'FINISHED');