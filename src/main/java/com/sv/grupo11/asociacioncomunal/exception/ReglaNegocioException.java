package com.sv.grupo11.asociacioncomunal.exception;

/** Violacion de una regla de negocio (cuota ya pagada, DUI ya registrado, ultimo administrador). */
public class ReglaNegocioException extends AsociacionException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
