package com.sv.grupo11.asociacioncomunal.exception;

/**
 * Error de acceso a datos (JDBC). Envuelve la SQLException original para no perder
 * la causa ni el stack trace, pero evita que la capa de servicio dependa de java.sql.
 */
public class PersistenciaException extends AsociacionException {

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
