-- 1. Ranking para "Los Tigres FC" (team_id = 1) en el torneo (tournament_id = 1)
-- Como ganaron su partido, tienen 3 puntos, 1 partido jugado, 1 victoria y 0 derrotas.
INSERT INTO rankings (tournament_id, team_id, points, matches_played, wins, losses)
VALUES (1, 1, 3, 1, 1, 0);

-- 2. Ranking para "Los Leones de la Plaza" (team_id = 2) en el torneo (tournament_id = 1)
-- Como perdieron, tienen 0 puntos, 1 partido jugado, 0 victorias y 1 derrota.
INSERT INTO rankings (tournament_id, team_id, points, matches_played, wins, losses)
VALUES (1, 2, 0, 1, 0, 1);

-- 3. Un equipo nuevo inscrito que aún no ha jugado ningún partido (Estadísticas en 0)
-- Digamos que es un "Equipo 3" fantasma para mostrar cómo se ve un ranking inicial.
INSERT INTO rankings (tournament_id, team_id, points, matches_played, wins, losses)
VALUES (1, 3, 0, 0, 0, 0);