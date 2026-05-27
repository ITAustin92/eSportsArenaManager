-- 1. Inscribir a "Los Tigres FC" (team_id = 1) en la "Copa de Verano Valorant" (tournament_id = 1)
-- El pago ya fue procesado y están confirmados.
INSERT INTO registrations (team_id, tournament_id, registration_date, status)
VALUES (1, 1, '2026-05-10', 'CONFIRMED');

-- 2. Inscribir a "Los Leones de la Plaza" (team_id = 2) en la "Copa de Verano Valorant" (tournament_id = 1)
-- También confirmados para que puedan enfrentarse a Los Tigres.
INSERT INTO registrations (team_id, tournament_id, registration_date, status)
VALUES (2, 1, '2026-05-12', 'CONFIRMED');

-- 3. Inscribir a "Los Leones de la Plaza" (team_id = 2) en la "Liga Master LoL 2026" (tournament_id = 2)
-- Aún están esperando la revisión de sus documentos, así que quedan en estado pendiente.
INSERT INTO registrations (team_id, tournament_id, registration_date, status)
VALUES (2, 2, '2026-05-20', 'PENDING');