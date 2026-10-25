package com.sv.grupo11.asociacioncomunal.exception;

/**
 * La base de datos rechazo el registro por una restriccion UNIQUE
 * (DUI repetido, cuota repetida para el mismo socio y periodo, nombre de usuario en uso).
 * Es la forma en que la BD protege la consistencia cuando dos hilos intentan insertar lo mismo.
 */
public class RegistroDuplicadoException extends PersistenciaException {

    public RegistroDuplicadoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
