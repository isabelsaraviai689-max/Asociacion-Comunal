package com.sv.grupo10.asociacioncomunal.dao;

import com.sv.grupo10.asociacioncomunal.model.Socio;

import java.util.Optional;

/** HERENCIA: hereda toda la persistencia .dat de ArchivoDAO y solo agrega consultas propias. */
public class SocioDAO extends ArchivoDAO<Socio> {

    public SocioDAO() {
        super("socios.dat");
    }

    @Override
    protected int obtenerId(Socio socio) {
        return socio.getId();
    }

    public synchronized Optional<Socio> buscarPorDui(String dui) {
        return registros.stream()
                .filter(s -> s.getDui().equalsIgnoreCase(dui.trim()))
                .findFirst();
    }
}
