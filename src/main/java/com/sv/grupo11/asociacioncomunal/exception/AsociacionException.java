package com.sv.grupo11.asociacioncomunal.exception;

/**
 * Excepcion base del sistema. Todas las excepciones propias heredan de esta,
 * lo que permite a la capa de presentacion capturar "cualquier error del dominio"
 * con un solo catch sin recurrir a catch (Exception e).
 */
public class AsociacionException extends RuntimeException {

    public AsociacionException(String mensaje) {
        super(mensaje);
    }

    public AsociacionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
