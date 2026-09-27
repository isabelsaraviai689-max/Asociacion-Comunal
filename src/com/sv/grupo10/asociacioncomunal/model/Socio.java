package com.sv.grupo10.asociacioncomunal.model;

import java.time.LocalDate;

/**
 * HERENCIA: Socio extiende de la clase abstracta Persona y agrega lo propio
 * de un miembro de la asociacion (direccion, fecha de ingreso, estado).
 */
public class Socio extends Persona {

    private static final long serialVersionUID = 1L;

    // Cuota base mensual definida por la asociacion
    public static final double CUOTA_BASE = 5.00;

    private String direccion;
    private LocalDate fechaIngreso;
    private EstadoMembresia estado;

    public Socio(int id, String nombre, String dui, String telefono,
                 String direccion, LocalDate fechaIngreso) {
        super(id, nombre, dui, telefono); // constructor en cadena con super
        this.direccion = direccion;
        this.fechaIngreso = fechaIngreso != null ? fechaIngreso : LocalDate.now();
        this.estado = EstadoMembresia.ACTIVO;
    }

    public String getDireccion() { return direccion; }

    public void setDireccion(String direccion) { this.direccion = direccion; }

    public LocalDate getFechaIngreso() { return fechaIngreso; }

    public EstadoMembresia getEstado() { return estado; }

    public void setEstado(EstadoMembresia estado) { this.estado = estado; }

    public boolean estaActivo() { return estado == EstadoMembresia.ACTIVO; }

    // POLIMORFISMO: implementacion concreta de los metodos abstractos del padre
    @Override
    public String describir() {
        return String.format("Socio #%d | %s | DUI %s | Tel %s | %s | Ingreso %s",
                getId(), getNombre(), getDui(), getTelefono(), estado, fechaIngreso);
    }

    @Override
    public double calcularCuotaMensual() {
        return CUOTA_BASE;
    }
}
