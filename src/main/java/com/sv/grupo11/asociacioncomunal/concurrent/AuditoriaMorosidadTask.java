package com.sv.grupo11.asociacioncomunal.concurrent;

import com.sv.grupo11.asociacioncomunal.model.Cuota;
import com.sv.grupo11.asociacioncomunal.model.EstadoCuota;
import com.sv.grupo11.asociacioncomunal.repository.CuotaRepository;

import java.time.LocalDate;
import java.util.concurrent.Callable;

/**
 * TAREA CONCURRENTE 2: recorre las cuotas pendientes en la base de datos y marca como VENCIDAS
 * las que superaron su fecha limite. Es independiente de la generacion (toca filas distintas),
 * por eso ambas pueden correr en paralelo dentro del cierre mensual.
 */
public class AuditoriaMorosidadTask implements Callable<Integer> {

    private final CuotaRepository cuotas;
    private final LocalDate fechaCorte;

    public AuditoriaMorosidadTask(CuotaRepository cuotas, LocalDate fechaCorte) {
        this.cuotas = cuotas;
        this.fechaCorte = fechaCorte;
    }

    @Override
    public Integer call() {
        String hilo = Thread.currentThread().getName();
        System.out.println("  [" + hilo + "] Auditoria de morosidad iniciada (corte " + fechaCorte + ")");
        int vencidas = 0;
        for (Cuota cuota : cuotas.listarPorEstado(EstadoCuota.PENDIENTE)) {
            if (cuota.verificarVencimiento(fechaCorte)) {
                cuotas.actualizar(cuota); // UPDATE con su propia conexion del pool
                vencidas++;
            }
        }
        System.out.println("  [" + hilo + "] Auditoria finalizada: " + vencidas + " cuota(s) vencida(s)");
        return vencidas;
    }
}
