package com.sv.grupo10.asociacioncomunal.dto;

/**
 * DTO con el resultado del cierre mensual ejecutado de forma concurrente.
 * Viaja de vuelta a la UI dentro de un Future.
 */
public class ResumenCierreDTO {

    private final String periodo;
    private final int cuotasGeneradas;
    private final int cuotasVencidas;
    private final long duracionMs;

    public ResumenCierreDTO(String periodo, int cuotasGeneradas, int cuotasVencidas, long duracionMs) {
        this.periodo = periodo;
        this.cuotasGeneradas = cuotasGeneradas;
        this.cuotasVencidas = cuotasVencidas;
        this.duracionMs = duracionMs;
    }

    public String getPeriodo() { return periodo; }
    public int getCuotasGeneradas() { return cuotasGeneradas; }
    public int getCuotasVencidas() { return cuotasVencidas; }
    public long getDuracionMs() { return duracionMs; }

    @Override
    public String toString() {
        return String.format("Cierre %s: %d cuota(s) generada(s), %d cuota(s) marcada(s) como vencida(s) en %d ms",
                periodo, cuotasGeneradas, cuotasVencidas, duracionMs);
    }
}
