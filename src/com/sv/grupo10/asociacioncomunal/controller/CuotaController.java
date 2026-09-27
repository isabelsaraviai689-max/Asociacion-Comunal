package com.sv.grupo10.asociacioncomunal.controller;

import com.sv.grupo10.asociacioncomunal.concurrent.ProcesoCierreMensual;
import com.sv.grupo10.asociacioncomunal.dto.ResumenCierreDTO;
import com.sv.grupo10.asociacioncomunal.dto.SocioMorosoDTO;
import com.sv.grupo10.asociacioncomunal.model.Cuota;
import com.sv.grupo10.asociacioncomunal.service.CuotaService;
import com.sv.grupo10.asociacioncomunal.service.ReporteService;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Future;

public class CuotaController {

    private final CuotaService cuotaService;
    private final ReporteService reporteService;
    private final ProcesoCierreMensual cierreMensual;

    public CuotaController(CuotaService cuotaService, ReporteService reporteService,
                           ProcesoCierreMensual cierreMensual) {
        this.cuotaService = cuotaService;
        this.reporteService = reporteService;
        this.cierreMensual = cierreMensual;
    }

    public Cuota registrarPago(int idCuota, LocalDate fecha) {
        return cuotaService.registrarPago(idCuota, fecha);
    }

    public List<Cuota> pendientesDeSocio(int idSocio) {
        return cuotaService.listarPendientesPorSocio(idSocio);
    }

    public List<Cuota> historialDeSocio(int idSocio) {
        return cuotaService.historialPorSocio(idSocio);
    }

    public List<SocioMorosoDTO> reporteMorosos() {
        return reporteService.reporteMorosos();
    }

    public Future<ResumenCierreDTO> ejecutarCierreMensual(String periodo, LocalDate fechaCorte) {
        return cierreMensual.ejecutarAsync(periodo, fechaCorte);
    }
}
