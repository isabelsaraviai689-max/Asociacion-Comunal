package com.sv.grupo11.asociacioncomunal.service;

import com.sv.grupo11.asociacioncomunal.exception.RecursoNoEncontradoException;
import com.sv.grupo11.asociacioncomunal.exception.ReglaNegocioException;
import com.sv.grupo11.asociacioncomunal.model.EstadoMembresia;
import com.sv.grupo11.asociacioncomunal.model.MiembroDirectiva;
import com.sv.grupo11.asociacioncomunal.model.Socio;
import com.sv.grupo11.asociacioncomunal.repository.SocioRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Logica de negocio del padron de socios (HU-001, HU-002, HU-005, HU-009). */
public class SocioService {

    private final SocioRepository socios;

    public SocioService(SocioRepository socios) {
        this.socios = socios;
    }

    public Socio registrarSocio(String nombre, String dui, String telefono, String direccion, LocalDate fechaIngreso) {
        // El constructor valida formato (ValidacionException); el id lo asigna la base de datos
        Socio socio = new Socio(0, nombre, dui, telefono, direccion, fechaIngreso);
        validarDuiUnico(socio.getDui());
        return socios.guardar(socio);
    }

    public MiembroDirectiva registrarMiembroDirectiva(String nombre, String dui, String telefono, String direccion,
                                                      LocalDate fechaIngreso, String cargo, String periodo) {
        MiembroDirectiva miembro = new MiembroDirectiva(0, nombre, dui, telefono, direccion, fechaIngreso, cargo, periodo);
        validarDuiUnico(miembro.getDui());
        return (MiembroDirectiva) socios.guardar(miembro); // el DAO devuelve el mismo tipo que recibio
    }

    public Socio actualizarSocio(int idSocio, String telefono, String direccion) {
        Socio socio = obtenerSocio(idSocio);
        if (telefono != null && !telefono.isBlank()) socio.setTelefono(telefono);
        if (direccion != null && !direccion.isBlank()) socio.setDireccion(direccion);
        socios.actualizar(socio);
        return socio;
    }

    public Socio cambiarEstado(int idSocio, EstadoMembresia estado) {
        Socio socio = obtenerSocio(idSocio);
        socio.setEstado(estado);
        socios.actualizar(socio);
        return socio;
    }

    public Socio obtenerSocio(int idSocio) {
        return socios.buscarPorId(idSocio)
                .orElseThrow(() -> new RecursoNoEncontradoException("el socio", idSocio));
    }

    public Optional<Socio> buscarPorDui(String dui) {
        return socios.buscarPorDui(dui);
    }

    public List<Socio> listarSocios() {
        return socios.listarTodos();
    }

    private void validarDuiUnico(String dui) {
        if (socios.buscarPorDui(dui).isPresent()) {
            throw new ReglaNegocioException("Ya existe un socio registrado con el DUI " + dui);
        }
    }
}
