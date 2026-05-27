-- 1. Notificación Directa a un Usuario (Sanción de Luis Rojas)
-- userId = 3 (Luis), teamId es NULL. Ya fue enviada al correo.
INSERT INTO notifications (user_id, team_id, tournament_id, type, subject, message, status)
VALUES (3, NULL, 1, 'EMAIL', 'Aviso Oficial de Suspensión', 'Estimado jugador, le informamos que ha sido suspendido por 2 partidos debido a conducta antideportiva. Puede apelar esta decisión en las próximas 24 horas.', 'SENT');

-- 2. Notificación Grupal a un Equipo (Inscripción de Los Tigres)
-- userId es NULL, teamId = 1 (Los Tigres). Es una alerta push a la app móvil.
INSERT INTO notifications (user_id, team_id, tournament_id, type, subject, message, status)
VALUES (NULL, 1, 1, 'PUSH', '¡Inscripción Confirmada!', 'Su equipo ha sido registrado exitosamente en la Copa de Verano Valorant. Revisen el calendario para su primer encuentro.', 'SENT');

-- 3. Notificación General de Torneo en Cola
-- Una alerta SMS general que aún no se envía (PENDING) sobre un cambio de horario.
INSERT INTO notifications (user_id, team_id, tournament_id, type, subject, message, status)
VALUES (NULL, 2, 1, 'SMS', 'Cambio de Horario Jornada 2', 'Atención equipo: El partido de la Jornada 2 ha sido retrasado 30 minutos por problemas en el servidor del juego.', 'PENDING');