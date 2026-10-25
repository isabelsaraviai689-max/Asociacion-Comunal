package com.sv.grupo11.asociacioncomunal.service;

import com.sv.grupo11.asociacioncomunal.dto.SocioMorosoDTO;
import com.sv.grupo11.asociacioncomunal.model.Cuota;
import com.sv.grupo11.asociacioncomunal.model.EstadoCuota;
import com.sv.grupo11.asociacioncomunal.model.Socio;
import com.sv.grupo11.asociacioncomunal.repository.CuotaRepository;
import com.sv.grupo11.asociacioncomunal.repository.SocioRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Reportes (HU-005, HU-006). Devuelve DTOs, nunca entidades crudas. */
public class ReporteService {

    private final SocioRepository socios;
    private final CuotaRepository cuotas;

    public ReporteService(SocioRepository socios, CuotaRepository cuotas) {
        this.socios = socios;
        this.cuotas = cuotas;
    }

    /**
     * COLECCIONES: se usa un HashMap<idSocio, lista de cuotas vencidas> para agrupar
     * en una sola pasada, en lugar de recorrer todas las cuotas por cada socio (O(n) vs O(n*m)).
     */
    public List<SocioMorosoDTO> reporteMorosos() {
        Map<Integer, List<Cuota>> vencidasPorSocio = new HashMap<>();
        for (Cuota cuota : cuotas.listarPorEstado(EstadoCuota.VENCIDA)) {
            vencidasPorSocio.computeIfAbsent(cuota.getIdSocio(), k -> new ArrayList<>()).add(cuota);
        }

        List<SocioMorosoDTO> morosos = new ArrayList<>();
        for (Socio socio : socios.listarTodos()) {
            List<Cuota> vencidas = vencidasPorSocio.get(socio.getId());
            if (vencidas == null || vencidas.isEmpty()) continue;
            double monto = vencidas.stream().mapToDouble(Cuota::getMonto).sum();
            morosos.add(new SocioMorosoDTO(socio.getId(), socio.getNombre(), socio.getDui(),
                    vencidas.size(), monto));
        }
        morosos.sort((a, b) -> Double.compare(b.getMontoPendiente(), a.getMontoPendiente()));
        return morosos;
    }
}
