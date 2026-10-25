-- =====================================================================
-- Sistema para Administracion de Asociacion Comunal - Grupo 11
-- Esquema de base de datos (MySQL 8.0+). Ejecutar completo en MySQL Workbench
-- o con: mysql -u root -p < database/schema.sql
-- =====================================================================

CREATE DATABASE IF NOT EXISTS asociacion_comunal
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE asociacion_comunal;

-- Socios registrados en la asociacion (entidad Socio; la jerarquia Persona -> Socio se aplana aqui)
CREATE TABLE IF NOT EXISTS socios (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(120) NOT NULL,
    dui           CHAR(10)     NOT NULL UNIQUE,
    telefono      CHAR(9)      NOT NULL,
    direccion     VARCHAR(200) NOT NULL,
    fecha_ingreso DATE         NOT NULL,
    estado        ENUM('ACTIVO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO',
    creado_en     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Miembros de la junta directiva: especializacion de Socio (MiembroDirectiva), tabla 1 a 1 con socios
CREATE TABLE IF NOT EXISTS miembros_directiva (
    id_directiva INT AUTO_INCREMENT PRIMARY KEY,
    id_socio     INT NOT NULL UNIQUE,
    cargo        ENUM('PRESIDENTE', 'VICEPRESIDENTE', 'SECRETARIO', 'TESORERO', 'VOCAL') NOT NULL,
    periodo      VARCHAR(20) NOT NULL COMMENT 'Ejemplo: 2026-2028',
    CONSTRAINT fk_directiva_socio FOREIGN KEY (id_socio) REFERENCES socios(id)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

-- Cuotas mensuales de cada socio
CREATE TABLE IF NOT EXISTS cuotas (
    id_cuota          INT AUTO_INCREMENT PRIMARY KEY,
    id_socio          INT           NOT NULL,
    monto             DECIMAL(10,2) NOT NULL,
    periodo           CHAR(7)       NOT NULL COMMENT 'Formato YYYY-MM, por ejemplo 2026-10',
    fecha_vencimiento DATE          NOT NULL,
    fecha_pago        DATE          NULL,
    estado            ENUM('PENDIENTE', 'PAGADA', 'VENCIDA') NOT NULL DEFAULT 'PENDIENTE',
    creado_en         TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_cuotas_monto CHECK (monto > 0),
    CONSTRAINT fk_cuotas_socio FOREIGN KEY (id_socio) REFERENCES socios(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    -- Control de concurrencia: aunque dos hilos intenten generar la misma cuota, solo una entra
    CONSTRAINT uq_cuota_socio_periodo UNIQUE (id_socio, periodo)
);

-- Cuentas de acceso. La contrasena se guarda como hash PBKDF2 (nunca en claro)
CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario     INT AUTO_INCREMENT PRIMARY KEY,
    nombre_usuario VARCHAR(50)  NOT NULL UNIQUE,
    contrasena     VARCHAR(255) NOT NULL COMMENT 'pbkdf2$iteraciones$salt$hash',
    rol            ENUM('ADMINISTRADOR', 'SECRETARIO', 'TESORERO', 'SOCIO') NOT NULL,
    id_socio       INT NULL,
    creado_en      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_usuarios_socio FOREIGN KEY (id_socio) REFERENCES socios(id)
        ON UPDATE CASCADE ON DELETE SET NULL
);

-- Indices para las consultas habituales
CREATE INDEX idx_socios_nombre ON socios(nombre);
CREATE INDEX idx_cuotas_estado ON cuotas(estado);
CREATE INDEX idx_cuotas_periodo ON cuotas(periodo);
