package com.sv.grupo11.asociacioncomunal.exception;

/** El registro solicitado no existe en la base de datos. */
public class RecursoNoEncontradoException extends AsociacionException {

    public RecursoNoEncontradoException(String recurso, Object id) {
        super("No existe " + recurso + " con identificador " + id);
    }
}
