package com.sv.grupo11.asociacioncomunal.repository;

import com.sv.grupo11.asociacioncomunal.model.Cuota;
import com.sv.grupo11.asociacioncomunal.model.EstadoCuota;

import java.util.List;
import java.util.Optional;

public interface CuotaRepository {

    /** Inserta y devuelve la cuota con el id asignado por la base de datos. */
    Cuota guardar(Cuota cuota);

    boolean actualizar(Cuota cuota);

    Optional<Cuota> buscarPorId(int id);

    List<Cuota> listarTodos();

    List<Cuota> listarPorSocio(int idSocio);

    List<Cuota> listarPendientesPorSocio(int idSocio);

    List<Cuota> listarPorEstado(EstadoCuota estado);

    boolean existeCuota(int idSocio, String periodo);

    int contar();
}
