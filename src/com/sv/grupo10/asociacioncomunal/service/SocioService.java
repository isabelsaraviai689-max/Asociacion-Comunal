package com.sv.grupo10.asociacioncomunal.service;

import com.sv.grupo10.asociacioncomunal.dao.SocioDAO;
import com.sv.grupo10.asociacioncomunal.model.EstadoMembresia;
import com.sv.grupo10.asociacioncomunal.model.MiembroDirectiva;
import com.sv.grupo10.asociacioncomunal.model.Socio;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Logica de negocio del padron de socios (HU-001, HU-002, HU-005, HU-009). */
public class SocioService {

    private final SocioDAO socioDAO;

    public SocioService(SocioDAO socioDAO) {
        this.socioDAO = socioDAO;
    }

    public Socio registrarSocio(String nombre, String dui, String telefono,
                                String direccion, LocalDate fechaIngreso) {
        validarDuiUnico(dui);
        Socio socio = new Socio(socioDAO.siguienteId(), nombre, dui, telefono, direccion, fechaIngreso);
        socioDAO.guardar(socio);
        return socio;
    }

    public MiembroDirectiva registrarMiembroDirectiva(String nombre, String dui, String telefono,
                                                      String direccion, LocalDate fechaIngreso,
                                                      String cargo, String periodo) {
        validarDuiUnico(dui);
        MiembroDirectiva miembro = new MiembroDirectiva(socioDAO.siguienteId(), nombre, dui,
                telefono, direccion, fechaIngreso, cargo, periodo);
        socioDAO.guardar(miembro); // se guarda en la misma lista: POLIMORFISMO en la coleccion
        return miembro;
    }

    public Socio actualizarSocio(int idSocio, String telefono, String direccion) {
        Socio socio = obtenerSocio(idSocio);
        if (telefono != null && !telefono.isBlank()) socio.setTelefono(telefono);
        if (direccion != null && !direccion.isBlank()) socio.setDireccion(direccion);
        socioDAO.actualizar(socio);
        return socio;
    }

    public Socio cambiarEstado(int idSocio, EstadoMembresia estado) {
        Socio socio = obtenerSocio(idSocio);
        socio.setEstado(estado);
        socioDAO.actualizar(socio);
        return socio;
    }

    public Socio obtenerSocio(int idSocio) {
        return socioDAO.buscarPorId(idSocio)
                .orElseThrow(() -> new IllegalArgumentException("No existe el socio #" + idSocio));
    }

    public Optional<Socio> buscarPorDui(String dui) {
        return socioDAO.buscarPorDui(dui);
    }

    public List<Socio> listarSocios() {
        return socioDAO.listarTodos();
    }

    private void validarDuiUnico(String dui) {
        if (dui != null && socioDAO.buscarPorDui(dui).isPresent()) {
            throw new IllegalArgumentException("Ya existe un socio registrado con el DUI " + dui);
        }
    }
}
