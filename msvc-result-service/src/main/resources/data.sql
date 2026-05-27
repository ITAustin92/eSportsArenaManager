-- 1. Resultado del Partido 1 (Jornada 1)
-- Ganan Los Tigres (Equipo 1) por 3 a 1. El resultado ya fue verificado por el árbitro.
INSERT INTO results (match_id, winner_team_id, home_score, away_score, status)
VALUES (1, 1, 3, 1, 'CONFIRMED');

-- 2. Resultado del Partido 2 (Jornada 2)
-- En este caso asumiendo un partido anterior donde los Leones (Equipo 2) ganaron 2 a 0,
-- pero está pendiente de que el administrador lo confirme porque hubo una queja.
INSERT INTO results (match_id, winner_team_id, home_score, away_score, status)
VALUES (2, 2, 0, 2, 'PENDING_VERIFICATION');

-- 3. Resultado de la Gran Final (Partido 5 en el calendario teórico)
-- Final de infarto donde Los Tigres ganan por la mínima.
INSERT INTO results (match_id, winner_team_id, home_score, away_score, status)
VALUES (5, 1, 1, 0, 'CONFIRMED');