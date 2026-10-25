package com.sv.grupo11.asociacioncomunal.controller;

import com.sv.grupo11.asociacioncomunal.concurrent.ProcesoCierreMensual;
import com.sv.grupo11.asociacioncomunal.dto.SocioMorosoDTO;
import com.sv.grupo11.asociacioncomunal.model.Cuota;
import com.sv.grupo11.asociacioncomunal.service.CuotaService;
import com.sv.grupo11.asociacioncomunal.service.ReporteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** El controlador solo delega: se verifica que llame al servicio correcto y devuelva su resultado. */
@ExtendWith(MockitoExtension.class)
class CuotaControllerTest {

    @Mock
    private CuotaService cuotaService;
    @Mock
    private ReporteService reporteService;
    @Mock
    private ProcesoCierreMensual cierreMensual;

    private CuotaController controller;

    @BeforeEach
    void setUp() {
        controller = new CuotaController(cuotaService, reporteService, cierreMensual);
    }

    @Test
    void registrarPago_delegaEnElServicio() {
        Cuota pagada = new Cuota(4, 2, 5.00, "2026-08", LocalDate.of(2026, 8, 31));
        when(cuotaService.registrarPago(4, LocalDate.of(2026, 9, 1))).thenReturn(pagada);

        Cuota resultado = controller.registrarPago(4, LocalDate.of(2026, 9, 1));

        assertSame(pagada, resultado);
        verify(cuotaService, times(1)).registrarPago(4, LocalDate.of(2026, 9, 1));
    }

    @Test
    void reporteMorosos_delegaEnReporteService() {
        List<SocioMorosoDTO> esperado = List.of(new SocioMorosoDTO(5, "Rosa", "01234567-0", 2, 10.0));
        when(reporteService.reporteMorosos()).thenReturn(esperado);

        assertEquals(esperado, controller.reporteMorosos());
        verifyNoInteractions(cuotaService);
    }

    @Test
    @DisplayName("ejecutarCierreMensual lanza el proceso concurrente con periodo y fecha de corte")
    void ejecutarCierreMensual_delegaEnProceso() {
        controller.ejecutarCierreMensual("2026-10", LocalDate.of(2026, 10, 31));
        verify(cierreMensual).ejecutarAsync("2026-10", LocalDate.of(2026, 10, 31));
    }
}
