-- =====================================================================
-- Datos de demostracion - Grupo 11. Ejecutar DESPUES de schema.sql
-- Usuarios de prueba: admin/admin123, secretario/secre123, tesorero/tesoro123, jramirez/socio123
-- Las contrasenas estan protegidas con PBKDF2-HMAC-SHA256 (util/PasswordUtil)
-- =====================================================================
USE asociacion_comunal;

INSERT INTO socios (id, nombre, dui, telefono, direccion, fecha_ingreso, estado) VALUES
(1, 'Maria Elena Perez',    '04512378-9', '7412-3698', 'Colonia Las Flores, Santo Tomas', '2024-03-15', 'ACTIVO'),
(2, 'Jose Antonio Ramirez', '06987541-2', '7896-1234', 'Barrio San Jose, Santo Tomas',    '2025-01-10', 'ACTIVO'),
(3, 'Ana Lucia Gomez',      '03214567-8', '7555-8899', 'Pasaje 3, Santo Tomas',           '2025-06-20', 'ACTIVO'),
(4, 'Carlos Mendez Rivas',  '02345678-1', '7777-1010', 'Calle Principal, Santo Tomas',    '2023-08-01', 'ACTIVO'),
(5, 'Rosa Hernandez',       '01234567-0', '7222-3344', 'Colonia El Carmen, Santo Tomas',  '2026-02-05', 'ACTIVO');

INSERT INTO miembros_directiva (id_socio, cargo, periodo) VALUES
(1, 'PRESIDENTE', '2026-2028'),
(4, 'TESORERO',   '2026-2028');

-- Julio y agosto: la directiva paga $7.50 (polimorfismo), el resto $5.00. Varias sin pagar -> morosos
INSERT INTO cuotas (id_socio, monto, periodo, fecha_vencimiento, fecha_pago, estado) VALUES
(1, 7.50, '2026-07', '2026-07-31', '2026-07-20', 'PAGADA'),
(1, 7.50, '2026-08', '2026-08-31', '2026-08-18', 'PAGADA'),
(2, 5.00, '2026-07', '2026-07-31', '2026-07-25', 'PAGADA'),
(2, 5.00, '2026-08', '2026-08-31', NULL,         'VENCIDA'),
(3, 5.00, '2026-07', '2026-07-31', NULL,         'VENCIDA'),
(3, 5.00, '2026-08', '2026-08-31', NULL,         'VENCIDA'),
(4, 7.50, '2026-07', '2026-07-31', '2026-07-30', 'PAGADA'),
(4, 7.50, '2026-08', '2026-08-31', '2026-08-28', 'PAGADA'),
(5, 5.00, '2026-07', '2026-07-31', NULL,         'VENCIDA'),
(5, 5.00, '2026-08', '2026-08-31', NULL,         'VENCIDA');

INSERT INTO usuarios (nombre_usuario, contrasena, rol, id_socio) VALUES
('admin',      'pbkdf2$100000$qMANlvDczv8ctn6B9EwmKw==$Mk9AyDT79ytpSQ2lZngQ/LDV/nDDRhlpSdbm0YCLMr0=', 'ADMINISTRADOR', NULL),
('secretario', 'pbkdf2$100000$QqxklBgTsv3Yjh7vEbMNcQ==$DSk4K0FqMtp0YqFu+q7YP0MSw09DCnHnY8CpJ5+oXSc=', 'SECRETARIO', NULL),
('tesorero',   'pbkdf2$100000$j5xq9iCwhEgViIrw2ckysA==$J0RC9G/i4Tdek3AknSQM52cCKTJx4Mz7tj7XaIRphKE=', 'TESORERO', NULL),
('jramirez',   'pbkdf2$100000$F3MUHttuWv9Eh2jIUmji4g==$4hkqJa8TtTvIAVg9D6UCXCC096Nc9NzjGt/H6WHibSg=', 'SOCIO', 2);
