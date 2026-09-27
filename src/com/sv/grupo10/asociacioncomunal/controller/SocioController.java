package com.sv.grupo10.asociacioncomunal.controller;

import com.sv.grupo10.asociacioncomunal.model.EstadoMembresia;
import com.sv.grupo10.asociacioncomunal.model.Socio;
import com.sv.grupo10.asociacioncomunal.service.SocioService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class SocioController {

    private final SocioService socioService;

    public SocioController(SocioService socioService) {
        this.socioService = socioService;
    }

    public Socio registrar(String nombre, String dui, String telefono, String direccion, LocalDate ingreso) {
        return socioService.registrarSocio(nombre, dui, telefono, direccion, ingreso);
    }

    public Socio registrarDirectiva(String nombre, String dui, String telefono, String direccion,
                                    LocalDate ingreso, String cargo, String periodo) {
        return socioService.registrarMiembroDirectiva(nombre, dui, telefono, direccion, ingreso, cargo, periodo);
    }

    public Socio actualizar(int idSocio, String telefono, String direccion) {
        return socioService.actualizarSocio(idSocio, telefono, direccion);
    }

    public Socio cambiarEstado(int idSocio, EstadoMembresia estado) {
        return socioService.cambiarEstado(idSocio, estado);
    }

    public Socio obtener(int idSocio) {
        return socioService.obtenerSocio(idSocio);
    }

    public Optional<Socio> buscarPorDui(String dui) {
        return socioService.buscarPorDui(dui);
    }

    public List<Socio> listar() {
        return socioService.listarSocios();
    }
}
