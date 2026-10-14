-- Datos de prueba - Asociacion Comunal Grupo 11
INSERT INTO socio (tipo, nombre, dui, telefono, direccion, fecha_ingreso, estado, cargo, periodo_directiva) VALUES
 ('DIRECTIVA','Maria Elena Perez','04512378-9','7412-3698','Colonia Las Flores','2024-03-15','ACTIVO','Presidente','2026-2028'),
 ('SOCIO','Jose Antonio Ramirez','06987541-2','7896-1234','Barrio San Jose','2025-01-10','ACTIVO',NULL,NULL),
 ('SOCIO','Ana Lucia Gomez','03214567-8','7555-8899','Pasaje 3','2025-06-20','ACTIVO',NULL,NULL),
 ('DIRECTIVA','Carlos Mendez Rivas','02345678-1','7777-1010','Calle Principal','2023-08-01','ACTIVO','Tesorero','2026-2028'),
 ('SOCIO','Rosa Hernandez','01234567-0','7222-3344','Colonia El Carmen','2026-02-05','ACTIVO',NULL,NULL);

INSERT INTO cuota (id_socio, monto, periodo, fecha_vencimiento, fecha_pago, estado) VALUES
 ((SELECT id FROM socio WHERE dui='04512378-9'), 7.50, '2026-07', '2026-07-31', '2026-07-20', 'PAGADA'),
 ((SELECT id FROM socio WHERE dui='04512378-9'), 7.50, '2026-08', '2026-08-31', '2026-08-18', 'PAGADA'),
 ((SELECT id FROM socio WHERE dui='06987541-2'), 5.00, '2026-07', '2026-07-31', '2026-07-25', 'PAGADA'),
 ((SELECT id FROM socio WHERE dui='06987541-2'), 5.00, '2026-08', '2026-08-31', NULL, 'PENDIENTE'),
 ((SELECT id FROM socio WHERE dui='03214567-8'), 5.00, '2026-07', '2026-07-31', NULL, 'PENDIENTE'),
 ((SELECT id FROM socio WHERE dui='03214567-8'), 5.00, '2026-08', '2026-08-31', NULL, 'PENDIENTE'),
 ((SELECT id FROM socio WHERE dui='02345678-1'), 7.50, '2026-07', '2026-07-31', '2026-07-30', 'PAGADA'),
 ((SELECT id FROM socio WHERE dui='02345678-1'), 7.50, '2026-08', '2026-08-31', '2026-08-28', 'PAGADA'),
 ((SELECT id FROM socio WHERE dui='01234567-0'), 5.00, '2026-07', '2026-07-31', NULL, 'PENDIENTE'),
 ((SELECT id FROM socio WHERE dui='01234567-0'), 5.00, '2026-08', '2026-08-31', NULL, 'PENDIENTE');

-- Usuarios de prueba (claves en README). Hash PBKDF2-HMAC-SHA256.
INSERT INTO usuario (nombre_usuario, clave_hash, rol, id_socio) VALUES
 ('admin','pbkdf2$65000$211NM0CDz/4V8zvAo0q78A==$JZ2fAv/R8skOOuJ2B3jfiNbIVJ1/qQO6ppZW9IraTQ8=','ADMINISTRADOR',NULL),
 ('secretario','pbkdf2$65000$FZ9qcC1icC7pnXbbXwF39g==$Ub/bxtZxWGGaYd3Tr483sGoMzVKeM7wIbD8wDCOSSFo=','SECRETARIO',NULL),
 ('tesorero','pbkdf2$65000$EJnK2BI/UeCvSa7QjCPRag==$ngy/V1VGGLoXZECPP0Owkd4YytRGhEmZkyE8Z6aNtpA=','TESORERO',NULL),
 ('jramirez','pbkdf2$65000$0gzNRjV4WRQhjIDc4Fh5CQ==$STe1lnLSE3yXFgEbwVRF+JDs19k7pdQuwM8vWZRQy5E=','SOCIO',(SELECT id FROM socio WHERE dui='06987541-2'));
