package com.sv.grupo10.asociacioncomunal.concurrent;

import com.sv.grupo10.asociacioncomunal.dao.CuotaDAO;
import com.sv.grupo10.asociacioncomunal.dao.SocioDAO;
import com.sv.grupo10.asociacioncomunal.dto.ResumenCierreDTO;
import com.sv.grupo10.asociacioncomunal.service.CuotaService;

import java.time.LocalDate;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * PROGRAMACION CONCURRENTE aplicada a la problematica del sistema.
 *
 * JUSTIFICACION: el cierre mensual es la operacion mas pesada de la asociacion:
 * hay que generar la cuota de TODOS los socios activos y, ademas, revisar TODAS las
 * cuotas pendientes para marcar las vencidas. Son dos recorridos independientes sobre
 * datos distintos, asi que se ejecutan en paralelo dentro de un ExecutorService,
 * y ademas se lanzan en segundo plano: el tesorero recibe un Future y puede seguir
 * registrando pagos mientras el cierre termina. La consistencia de los datos la
 * garantizan los metodos synchronized de ArchivoDAO, que serializan el acceso al .dat.
 */
public class ProcesoCierreMensual {

    private static final AtomicInteger CONTADOR_HILOS = new AtomicInteger();

    private final ExecutorService executor;
    private final SocioDAO socioDAO;
    private final CuotaDAO cuotaDAO;
    private final CuotaService cuotaService;

    public ProcesoCierreMensual(SocioDAO socioDAO, CuotaDAO cuotaDAO, CuotaService cuotaService) {
        this.socioDAO = socioDAO;
        this.cuotaDAO = cuotaDAO;
        this.cuotaService = cuotaService;
        // 3 hilos: 1 orquestador + 2 tareas que corren en paralelo
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

            // Las dos tareas se envian al pool y corren EN PARALELO
            Future<Integer> generadas = executor.submit(
                    new GeneracionCuotasTask(socioDAO.listarTodos(), periodo, cuotaService));
            Future<Integer> vencidas = executor.submit(
                    new AuditoriaMorosidadTask(cuotaDAO, fechaCorte));

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
