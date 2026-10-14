-- Esquema de la base de datos - Asociacion Comunal Grupo 11
-- Compatible con H2 (MODE=MySQL) y MySQL 8.

CREATE TABLE IF NOT EXISTS socio (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo               VARCHAR(10)  NOT NULL,
    nombre             VARCHAR(120) NOT NULL,
    dui                VARCHAR(10)  NOT NULL,
    telefono           VARCHAR(9)   NOT NULL,
    direccion          VARCHAR(200),
    fecha_ingreso      DATE         NOT NULL,
    estado             VARCHAR(10)  NOT NULL,
    cargo              VARCHAR(40),
    periodo_directiva  VARCHAR(20),
    CONSTRAINT uq_socio_dui UNIQUE (dui)
);

CREATE TABLE IF NOT EXISTS usuario (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_usuario  VARCHAR(40)  NOT NULL,
    clave_hash      VARCHAR(200) NOT NULL,
    rol             VARCHAR(20)  NOT NULL,
    id_socio        BIGINT,
    CONSTRAINT uq_usuario_nombre UNIQUE (nombre_usuario),
    CONSTRAINT fk_usuario_socio FOREIGN KEY (id_socio) REFERENCES socio (id)
);

CREATE TABLE IF NOT EXISTS cuota (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_socio           BIGINT        NOT NULL,
    monto              DECIMAL(10,2) NOT NULL,
    periodo            VARCHAR(7)    NOT NULL,
    fecha_vencimiento  DATE          NOT NULL,
    fecha_pago         DATE,
    estado             VARCHAR(10)   NOT NULL,
    CONSTRAINT fk_cuota_socio FOREIGN KEY (id_socio) REFERENCES socio (id),
    -- Evita cuotas duplicadas para el mismo socio/periodo aunque haya concurrencia
    CONSTRAINT uq_cuota_socio_periodo UNIQUE (id_socio, periodo)
);

CREATE TABLE IF NOT EXISTS cierre_mensual (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    periodo           VARCHAR(7)  NOT NULL,
    cuotas_generadas  INT         NOT NULL,
    cuotas_vencidas   INT         NOT NULL,
    duracion_ms       BIGINT      NOT NULL,
    fecha_corte       DATE        NOT NULL,
    fecha_ejecucion   DATE        NOT NULL,
    ejecutado_por     VARCHAR(40) NOT NULL
);
