package com.sv.grupo11.asociacioncomunal.service;

import com.sv.grupo11.asociacioncomunal.exception.RecursoNoEncontradoException;
import com.sv.grupo11.asociacioncomunal.exception.ReglaNegocioException;
import com.sv.grupo11.asociacioncomunal.model.Cuota;
import com.sv.grupo11.asociacioncomunal.model.Socio;
import com.sv.grupo11.asociacioncomunal.repository.CuotaRepository;
import com.sv.grupo11.asociacioncomunal.repository.SocioRepository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/** Logica de negocio de cuotas y pagos (HU-003, HU-004, HU-007, HU-010). */
public class CuotaService {

    private final CuotaRepository cuotas;
    private final SocioRepository socios;

    public CuotaService(CuotaRepository cuotas, SocioRepository socios) {
        this.cuotas = cuotas;
        this.socios = socios;
    }

    /**
     * Genera la cuota de un socio para un periodo (YYYY-MM). El monto se resuelve por
     * POLIMORFISMO: un MiembroDirectiva devuelve una cuota distinta a la de un Socio.
     */
    public Cuota generarCuota(Socio socio, String periodo) {
        if (cuotas.existeCuota(socio.getId(), periodo)) {
            throw new ReglaNegocioException("El socio #" + socio.getId() + " ya tiene cuota para " + periodo);
        }
        LocalDate vencimiento = YearMonth.parse(periodo).atEndOfMonth();
        Cuota cuota = new Cuota(0, socio.getId(), socio.calcularCuotaMensual(), periodo, vencimiento);
        return cuotas.guardar(cuota);
    }

    public Cuota registrarPago(int idCuota, LocalDate fechaPago) {
        Cuota cuota = cuotas.buscarPorId(idCuota)
                .orElseThrow(() -> new RecursoNoEncontradoException("la cuota", idCuota));
        cuota.registrarPago(fechaPago); // lanza ReglaNegocioException si ya estaba pagada
        cuotas.actualizar(cuota);
        return cuota;
    }

    public List<Cuota> listarPendientesPorSocio(int idSocio) {
        validarSocio(idSocio);
        return cuotas.listarPendientesPorSocio(idSocio);
    }

    public List<Cuota> historialPorSocio(int idSocio) {
        validarSocio(idSocio);
        return cuotas.listarPorSocio(idSocio);
    }

    public List<Cuota> listarTodas() {
        return cuotas.listarTodos();
    }

    private void validarSocio(int idSocio) {
        if (socios.buscarPorId(idSocio).isEmpty()) {
            throw new RecursoNoEncontradoException("el socio", idSocio);
        }
    }
}
