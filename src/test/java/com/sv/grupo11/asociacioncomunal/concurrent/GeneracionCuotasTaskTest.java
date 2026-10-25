package com.sv.grupo11.asociacioncomunal.concurrent;

import com.sv.grupo11.asociacioncomunal.exception.RegistroDuplicadoException;
import com.sv.grupo11.asociacioncomunal.exception.ReglaNegocioException;
import com.sv.grupo11.asociacioncomunal.model.Cuota;
import com.sv.grupo11.asociacioncomunal.model.EstadoMembresia;
import com.sv.grupo11.asociacioncomunal.model.Socio;
import com.sv.grupo11.asociacioncomunal.service.CuotaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/** La tarea concurrente se prueba de forma aislada: el servicio (y por tanto la BD) es un mock. */
@ExtendWith(MockitoExtension.class)
class GeneracionCuotasTaskTest {

    @Mock
    private CuotaService cuotaService;

    private Socio socio(int id, boolean activo) {
        Socio s = new Socio(id, "Socio " + id, "0000000" + id + "-0", "7000-000" + id, "x", LocalDate.now());
        s.setEstado(activo ? EstadoMembresia.ACTIVO : EstadoMembresia.INACTIVO);
        return s;
    }

    @Test
    @DisplayName("Genera cuota solo a los activos y omite las que ya existen o la BD rechaza por duplicado")
    void call_cuentaSoloLasGeneradas() throws Exception {
        Socio activo = socio(1, true);
        Socio inactivo = socio(2, false);
        Socio yaTenia = socio(3, true);
        Socio duplicadoEnBD = socio(4, true);
        when(cuotaService.generarCuota(eq(activo), any())).thenReturn(new Cuota(1, 1, 5.00, "2026-10", LocalDate.of(2026, 10, 31)));
        when(cuotaService.generarCuota(eq(yaTenia), any())).thenThrow(new ReglaNegocioException("ya tiene cuota"));
        when(cuotaService.generarCuota(eq(duplicadoEnBD), any())).thenThrow(
                new RegistroDuplicadoException("UNIQUE", new SQLIntegrityConstraintViolationException()));

        Integer generadas = new GeneracionCuotasTask(List.of(activo, inactivo, yaTenia, duplicadoEnBD), "2026-10", cuotaService).call();

        assertEquals(1, generadas.intValue());
        verify(cuotaService, times(1)).generarCuota(activo, "2026-10");
        verify(cuotaService, never()).generarCuota(eq(inactivo), any());
    }

    @Test
    void call_sinSocios_devuelveCero() throws Exception {
        assertEquals(0, new GeneracionCuotasTask(List.of(), "2026-10", cuotaService).call().intValue());
        verifyNoInteractions(cuotaService);
    }
}
