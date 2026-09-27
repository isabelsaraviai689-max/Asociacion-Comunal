package com.sv.grupo10.asociacioncomunal.concurrent;

import com.sv.grupo10.asociacioncomunal.dao.CuotaDAO;
import com.sv.grupo10.asociacioncomunal.model.Cuota;
import com.sv.grupo10.asociacioncomunal.model.EstadoCuota;

import java.time.LocalDate;
import java.util.concurrent.Callable;

/**
 * TAREA CONCURRENTE 2: recorre todas las cuotas pendientes y marca como VENCIDAS
 * las que superaron su fecha limite. Es independiente de la generacion, por eso
 * ambas pueden correr en paralelo dentro del cierre mensual.
 */
public class AuditoriaMorosidadTask implements Callable<Integer> {

    private final CuotaDAO cuotaDAO;
    private final LocalDate fechaCorte;

    public AuditoriaMorosidadTask(CuotaDAO cuotaDAO, LocalDate fechaCorte) {
        this.cuotaDAO = cuotaDAO;
        this.fechaCorte = fechaCorte;
    }

    @Override
    public Integer call() {
        String hilo = Thread.currentThread().getName();
        System.out.println("  [" + hilo + "] Auditoria de morosidad iniciada (corte " + fechaCorte + ")");
        int vencidas = 0;
        for (Cuota cuota : cuotaDAO.listarPorEstado(EstadoCuota.PENDIENTE)) {
            if (cuota.verificarVencimiento(fechaCorte)) {
                cuotaDAO.actualizar(cuota); // escritura protegida por synchronized en el DAO
                vencidas++;
            }
        }
        System.out.println("  [" + hilo + "] Auditoria finalizada: " + vencidas + " cuota(s) vencida(s)");
        return vencidas;
    }
}
