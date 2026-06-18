-- 1. Partido Inicial (Jornada 1) - Ya jugado y finalizado
-- Los Tigres (1) reciben a Los Leones (2). Resultado: 3 a 1.
INSERT INTO matches (tournament_id, home_team_id, away_team_id, home_score, away_score, match_date, status)
VALUES (1, 1, 2, 3, 1, '2026-06-05 15:30:00', 'FINISHED');

-- 2. La Revancha (Jornada 2) - Jugado y finalizado
-- Los Leones (2) reciben a Los Tigres (1). Resultado: 2 a 0.
INSERT INTO matches (tournament_id, home_team_id, away_team_id, home_score, away_score, match_date, status)
VALUES (1, 2, 1, 2, 0, '2026-06-12 15:30:00', 'FINISHED');

-- 3. Partido a futuro (Aún no se juega)
-- Los Leones (2) contra un Equipo 3. Fíjate que los puntajes (score) van en NULL.
INSERT INTO matches (tournament_id, home_team_id, away_team_id, home_score, away_score, match_date, status)
VALUES (2, 2, 3, NULL, NULL, '2026-08-15 18:00:00', 'SCHEDULED');

-- 4. Un partido en vivo en este preciso momento
-- Puntaje parcial 1-1, con estado IN_PROGRESS.
INSERT INTO matches (tournament_id, home_team_id, away_team_id, home_score, away_score, match_date, status)
VALUES (1, 1, 3, 1, 1, '2026-05-24 20:00:00', 'IN_PROGRESS');


INSERT INTO matches (tournament_id, home_team_id, away_team_id, home_score, away_score, match_date, status)
VALUES (1, 1, 2, 1, 0, '2026-06-20 20:00:00', 'FINISHED');