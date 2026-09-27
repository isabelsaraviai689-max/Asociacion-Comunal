package com.sv.grupo10.asociacioncomunal.concurrent;

import com.sv.grupo10.asociacioncomunal.model.Socio;
import com.sv.grupo10.asociacioncomunal.service.CuotaService;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * TAREA CONCURRENTE 1: genera la cuota del periodo para cada socio activo.
 * Implementa Callable porque devuelve un resultado (cuantas cuotas genero)
 * que la UI recupera a traves de un Future.
 */
public class GeneracionCuotasTask implements Callable<Integer> {

    private final List<Socio> socios;
    private final String periodo;
    private final CuotaService cuotaService;

    public GeneracionCuotasTask(List<Socio> socios, String periodo, CuotaService cuotaService) {
        this.socios = socios;
        this.periodo = periodo;
        this.cuotaService = cuotaService;
    }

    @Override
    public Integer call() {
        String hilo = Thread.currentThread().getName();
        System.out.println("  [" + hilo + "] Generacion de cuotas " + periodo + " iniciada (" + socios.size() + " socios)");
        int generadas = 0;
        for (Socio socio : socios) {
            if (!socio.estaActivo()) continue;
            try {
                cuotaService.generarCuota(socio, periodo); // monto por POLIMORFISMO
                generadas++;
            } catch (IllegalStateException e) {
                // ya tenia cuota para el periodo: se omite, no es error del proceso
            }
        }
        System.out.println("  [" + hilo + "] Generacion finalizada: " + generadas + " cuota(s)");
        return generadas;
    }
}
