-- 1. El gran premio en efectivo para la Copa Valorant (tournament_id = 1)
-- El team_id está en NULL porque estamos esperando que termine el torneo para entregarlo.
INSERT INTO prizes (tournament_id, team_id, description, type, amount, status)
VALUES (1, NULL, 'Gran Premio Campeón de Oro', 'MONEY', 7500.0, 'PENDING');

-- 2. El trofeo físico para el mismo torneo (tournament_id = 1)
-- Fíjate que el monto (amount) es 0 porque es un objeto, no efectivo.
INSERT INTO prizes (tournament_id, team_id, description, type, amount, status)
VALUES (1, NULL, 'Copa Oficial Verano 2026', 'TROPHY', 0.0, 'PENDING');

-- 3. Un premio del torneo pasado (tournament_id = 5) que ya fue cobrado
-- Aquí el team_id ya tiene el número 1 (Los Tigres FC) y su estado es entregado.
INSERT INTO prizes (tournament_id, team_id, description, type, amount, status)
VALUES (5, 1, 'Medallas de Campeón Torneo Relámpago', 'MEDALS', 500.0, 'DELIVERED');