package com.sv.grupo10.asociacioncomunal.dto;

/**
 * DTO (Data Transfer Object): objeto plano para transportar el resultado del
 * reporte de morosos hacia la capa de presentacion, sin exponer las entidades.
 */
public class SocioMorosoDTO {

    private final int idSocio;
    private final String nombre;
    private final String dui;
    private final int cuotasPendientes;
    private final double montoPendiente;

    public SocioMorosoDTO(int idSocio, String nombre, String dui, int cuotasPendientes, double montoPendiente) {
        this.idSocio = idSocio;
        this.nombre = nombre;
        this.dui = dui;
        this.cuotasPendientes = cuotasPendientes;
        this.montoPendiente = montoPendiente;
    }

    public int getIdSocio() { return idSocio; }
    public String getNombre() { return nombre; }
    public String getDui() { return dui; }
    public int getCuotasPendientes() { return cuotasPendientes; }
    public double getMontoPendiente() { return montoPendiente; }

    @Override
    public String toString() {
        return String.format("%-4d %-30s %-11s %3d cuota(s)  $%8.2f",
                idSocio, nombre, dui, cuotasPendientes, montoPendiente);
    }
}
