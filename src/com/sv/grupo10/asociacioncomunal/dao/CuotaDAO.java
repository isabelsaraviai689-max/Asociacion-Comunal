package com.sv.grupo10.asociacioncomunal.dao;

import com.sv.grupo10.asociacioncomunal.model.Cuota;
import com.sv.grupo10.asociacioncomunal.model.EstadoCuota;

import java.util.List;
import java.util.stream.Collectors;

public class CuotaDAO extends ArchivoDAO<Cuota> {

    public CuotaDAO() {
        super("cuotas.dat");
    }

    @Override
    protected int obtenerId(Cuota cuota) {
        return cuota.getIdCuota();
    }

    public synchronized List<Cuota> listarPorSocio(int idSocio) {
        return registros.stream()
                .filter(c -> c.getIdSocio() == idSocio)
                .sorted((a, b) -> a.getPeriodo().compareTo(b.getPeriodo()))
                .collect(Collectors.toList());
    }

    public synchronized List<Cuota> listarPendientesPorSocio(int idSocio) {
        return registros.stream()
                .filter(c -> c.getIdSocio() == idSocio && c.estaPendiente())
                .collect(Collectors.toList());
    }

    public synchronized boolean existeCuota(int idSocio, String periodo) {
        return registros.stream()
                .anyMatch(c -> c.getIdSocio() == idSocio && c.getPeriodo().equals(periodo));
    }

    public synchronized List<Cuota> listarPorEstado(EstadoCuota estado) {
        return registros.stream()
                .filter(c -> c.getEstado() == estado)
                .collect(Collectors.toList());
    }
}
