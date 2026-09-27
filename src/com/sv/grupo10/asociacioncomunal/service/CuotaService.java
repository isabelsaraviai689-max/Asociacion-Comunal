package com.sv.grupo10.asociacioncomunal.service;

import com.sv.grupo10.asociacioncomunal.dao.CuotaDAO;
import com.sv.grupo10.asociacioncomunal.dao.SocioDAO;
import com.sv.grupo10.asociacioncomunal.model.Cuota;
import com.sv.grupo10.asociacioncomunal.model.Socio;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/** Logica de negocio de cuotas y pagos (HU-003, HU-004, HU-007). */
public class CuotaService {

    private final CuotaDAO cuotaDAO;
    private final SocioDAO socioDAO;

    public CuotaService(CuotaDAO cuotaDAO, SocioDAO socioDAO) {
        this.cuotaDAO = cuotaDAO;
        this.socioDAO = socioDAO;
    }

    /**
     * Genera la cuota de un socio para un periodo (YYYY-MM). El monto se resuelve por
     * POLIMORFISMO: un MiembroDirectiva devuelve una cuota distinta a la de un Socio.
     */
    public Cuota generarCuota(Socio socio, String periodo) {
        if (cuotaDAO.existeCuota(socio.getId(), periodo)) {
            throw new IllegalStateException("El socio #" + socio.getId() + " ya tiene cuota para " + periodo);
        }
        LocalDate vencimiento = YearMonth.parse(periodo).atEndOfMonth();
        Cuota cuota = new Cuota(cuotaDAO.siguienteId(), socio.getId(),
                socio.calcularCuotaMensual(), periodo, vencimiento);
        cuotaDAO.guardar(cuota);
        return cuota;
    }

    public Cuota registrarPago(int idCuota, LocalDate fechaPago) {
        Cuota cuota = cuotaDAO.buscarPorId(idCuota)
                .orElseThrow(() -> new IllegalArgumentException("No existe la cuota #" + idCuota));
        cuota.registrarPago(fechaPago);
        cuotaDAO.actualizar(cuota);
        return cuota;
    }

    public List<Cuota> listarPendientesPorSocio(int idSocio) {
        validarSocio(idSocio);
        return cuotaDAO.listarPendientesPorSocio(idSocio);
    }

    public List<Cuota> historialPorSocio(int idSocio) {
        validarSocio(idSocio);
        return cuotaDAO.listarPorSocio(idSocio);
    }

    public List<Cuota> listarTodas() {
        return cuotaDAO.listarTodos();
    }

    private void validarSocio(int idSocio) {
        if (socioDAO.buscarPorId(idSocio).isEmpty()) {
            throw new IllegalArgumentException("No existe el socio #" + idSocio);
        }
    }
}
