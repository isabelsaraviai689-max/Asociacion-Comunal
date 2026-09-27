package com.sv.grupo10.asociacioncomunal.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Cuota mensual asignada a un socio. Se genera en el cierre mensual y
 * cambia de estado cuando se paga o cuando vence sin pago.
 */
public class Cuota implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idCuota;
    private int idSocio;
    private double monto;
    private String periodo;             // Ej: "2026-09"
    private LocalDate fechaVencimiento;
    private LocalDate fechaPago;        // null mientras no se pague
    private EstadoCuota estado;

    public Cuota(int idCuota, int idSocio, double monto, String periodo, LocalDate fechaVencimiento) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto de la cuota debe ser mayor que cero.");
        }
        this.idCuota = idCuota;
        this.idSocio = idSocio;
        this.monto = monto;
        this.periodo = periodo;
        this.fechaVencimiento = fechaVencimiento;
        this.estado = EstadoCuota.PENDIENTE;
    }

    public int getIdCuota() { return idCuota; }

    public int getIdSocio() { return idSocio; }

    public double getMonto() { return monto; }

    public String getPeriodo() { return periodo; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }

    public LocalDate getFechaPago() { return fechaPago; }

    public EstadoCuota getEstado() { return estado; }

    public boolean estaPendiente() { return estado != EstadoCuota.PAGADA; }

    // Metodo de accion: registra el pago y cambia el estado
    public void registrarPago(LocalDate fecha) {
        if (estado == EstadoCuota.PAGADA) {
            throw new IllegalStateException("La cuota " + idCuota + " ya fue pagada.");
        }
        this.fechaPago = fecha != null ? fecha : LocalDate.now();
        this.estado = EstadoCuota.PAGADA;
    }

    // Metodo de accion: marca como vencida si paso la fecha limite sin pago
    public boolean verificarVencimiento(LocalDate hoy) {
        if (estado == EstadoCuota.PENDIENTE && hoy.isAfter(fechaVencimiento)) {
            estado = EstadoCuota.VENCIDA;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Cuota #%d | Socio %d | %s | $%.2f | vence %s | %s%s",
                idCuota, idSocio, periodo, monto, fechaVencimiento, estado,
                fechaPago != null ? " (pagada " + fechaPago + ")" : "");
    }
}
