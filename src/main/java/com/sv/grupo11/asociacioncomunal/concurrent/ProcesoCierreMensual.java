package com.sv.grupo11.asociacioncomunal.concurrent;

import com.sv.grupo11.asociacioncomunal.dto.ResumenCierreDTO;
import com.sv.grupo11.asociacioncomunal.repository.CuotaRepository;
import com.sv.grupo11.asociacioncomunal.repository.SocioRepository;
import com.sv.grupo11.asociacioncomunal.service.CuotaService;

import java.time.LocalDate;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * PROGRAMACION CONCURRENTE integrada con la base de datos.
 *
 * QUE: el cierre mensual genera la cuota del periodo para todos los socios activos y, en paralelo,
 * audita todas las cuotas pendientes para marcar las vencidas.
 * POR QUE: son dos recorridos independientes sobre datos distintos (INSERT en cuotas nuevas vs
 * UPDATE en cuotas viejas) y en un padron grande tardan; ejecutarlos en paralelo y en segundo
 * plano deja al tesorero seguir registrando pagos mientras el cierre termina.
 * MECANISMO: ExecutorService de 3 hilos (1 orquestador + 2 tareas Callable) y un Future con el resumen.
 * BASE DE DATOS: cada tarea obtiene sus conexiones del pool (DataSource) a traves de los DAO y las
 * devuelve al terminar; nunca comparten una Connection entre hilos.
 * CONTROL DE CONCURRENCIA: los ids los asigna MySQL (AUTO_INCREMENT), la restriccion UNIQUE
 * (id_socio, periodo) impide cuotas duplicadas aunque dos hilos inserten a la vez, y cada
 * escritura va en su propia transaccion. Una PersistenciaException dentro de una tarea se
 * propaga al Future y la UI la reporta en vez de quedar oculta.
 */
public class ProcesoCierreMensual {

    private static final AtomicInteger CONTADOR_HILOS = new AtomicInteger();

    private final ExecutorService executor;
    private final SocioRepository socios;
    private final CuotaRepository cuotas;
    private final CuotaService cuotaService;

    public ProcesoCierreMensual(SocioRepository socios, CuotaRepository cuotas, CuotaService cuotaService) {
        this.socios = socios;
        this.cuotas = cuotas;
        this.cuotaService = cuotaService;
        // 3 hilos: 1 orquestador + 2 tareas que corren en paralelo (el pool tiene maxActive = 5)
        this.executor = Executors.newFixedThreadPool(3, r -> {
            Thread t = new Thread(r);
            t.setName("cierre-" + CONTADOR_HILOS.incrementAndGet());
            t.setDaemon(true); // no impide que el programa termine
            return t;
        });
    }

    /** Lanza el cierre en segundo plano y devuelve de inmediato un Future con el resumen. */
    public Future<ResumenCierreDTO> ejecutarAsync(String periodo, LocalDate fechaCorte) {
        return executor.submit(() -> {
            long inicio = System.currentTimeMillis();
            Future<Integer> generadas = executor.submit(
                    new GeneracionCuotasTask(socios.listarTodos(), periodo, cuotaService));
            Future<Integer> vencidas = executor.submit(
                    new AuditoriaMorosidadTask(cuotas, fechaCorte));
            // get() bloquea solo a este hilo orquestador, nunca a la UI
            ResumenCierreDTO resumen = new ResumenCierreDTO(periodo, generadas.get(), vencidas.get(),
                    System.currentTimeMillis() - inicio);
            System.out.println("  [cierre] " + resumen);
            return resumen;
        });
    }

    public void apagar() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
