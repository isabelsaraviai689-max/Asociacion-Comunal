package com.sv.grupo11.asociacioncomunal.concurrent;

import com.sv.grupo11.asociacioncomunal.exception.RegistroDuplicadoException;
import com.sv.grupo11.asociacioncomunal.exception.ReglaNegocioException;
import com.sv.grupo11.asociacioncomunal.model.Socio;
import com.sv.grupo11.asociacioncomunal.service.CuotaService;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * TAREA CONCURRENTE 1: genera en la base de datos la cuota del periodo para cada socio activo.
 * Implementa Callable porque devuelve un resultado (cuantas cuotas genero) que la UI
 * recupera a traves de un Future. Cada INSERT toma su propia conexion del pool.
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
        int omitidas = 0;
        for (Socio socio : socios) {
            if (!socio.estaActivo()) continue;
            try {
                cuotaService.generarCuota(socio, periodo); // monto por POLIMORFISMO
                generadas++;
            } catch (ReglaNegocioException | RegistroDuplicadoException e) {
                // Ya existia la cuota (verificada por el servicio o rechazada por la restriccion UNIQUE
                // de la BD si otro hilo la inserto primero): no es un error del proceso, se omite.
                omitidas++;
            }
            // Cualquier PersistenciaException (BD caida, pool agotado) se propaga: el Future la
            // reportara como fallo del cierre en lugar de ocultarla.
        }
        System.out.println("  [" + hilo + "] Generacion finalizada: " + generadas + " cuota(s), " + omitidas + " omitida(s)");
        return generadas;
    }
}
