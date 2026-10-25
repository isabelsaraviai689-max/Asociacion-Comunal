package com.sv.grupo11.asociacioncomunal.repository;

import com.sv.grupo11.asociacioncomunal.model.Socio;

import java.util.List;
import java.util.Optional;

/**
 * INTERFAZ (contrato) de acceso a datos de socios. Los servicios dependen de esta
 * abstraccion y no de SocioDAO (JDBC), lo que permite sustituir la implementacion
 * (MySQL, archivos, un mock en pruebas) sin tocar la logica de negocio.
 */
public interface SocioRepository {

    /** Inserta y devuelve el socio con el id asignado por la base de datos. */
    Socio guardar(Socio socio);

    boolean actualizar(Socio socio);

    Optional<Socio> buscarPorId(int id);

    Optional<Socio> buscarPorDui(String dui);

    List<Socio> listarTodos();

    int contar();
}
