package com.sv.grupo11.asociacioncomunal.exception;

/** No fue posible obtener una conexion de la base de datos (servidor caido, credenciales, red). */
public class ConexionException extends PersistenciaException {

    public ConexionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
