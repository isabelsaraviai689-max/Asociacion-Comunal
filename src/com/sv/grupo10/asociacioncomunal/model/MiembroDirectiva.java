package com.sv.grupo10.asociacioncomunal.model;

import java.time.LocalDate;

/**
 * HERENCIA de segundo nivel: un miembro de la junta directiva ES un socio
 * que ademas ocupa un cargo durante un periodo.
 * POLIMORFISMO: sobrescribe describir() y calcularCuotaMensual() porque,
 * segun estatutos, la directiva realiza un aporte mensual adicional.
 */
public class MiembroDirectiva extends Socio {

    private static final long serialVersionUID = 1L;

    public static final double APORTE_DIRECTIVA = 2.50;

    private String cargo;     // Presidente, Vicepresidente, Secretario, Tesorero, Vocal
    private String periodo;   // Ej: "2026-2028"

    public MiembroDirectiva(int id, String nombre, String dui, String telefono,
                            String direccion, LocalDate fechaIngreso,
                            String cargo, String periodo) {
        super(id, nombre, dui, telefono, direccion, fechaIngreso);
        this.cargo = cargo;
        this.periodo = periodo;
    }

    public String getCargo() { return cargo; }

    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getPeriodo() { return periodo; }

    @Override
    public String describir() {
        // Reutiliza la descripcion del padre y la extiende (super.describir())
        return super.describir() + String.format(" | DIRECTIVA: %s (%s)", cargo, periodo);
    }

    @Override
    public double calcularCuotaMensual() {
        return super.calcularCuotaMensual() + APORTE_DIRECTIVA;
    }
}
