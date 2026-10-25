package com.sv.grupo11.asociacioncomunal.service;

import com.sv.grupo11.asociacioncomunal.exception.RecursoNoEncontradoException;
import com.sv.grupo11.asociacioncomunal.exception.ReglaNegocioException;
import com.sv.grupo11.asociacioncomunal.model.Cuota;
import com.sv.grupo11.asociacioncomunal.model.EstadoCuota;
import com.sv.grupo11.asociacioncomunal.model.MiembroDirectiva;
import com.sv.grupo11.asociacioncomunal.model.Socio;
import com.sv.grupo11.asociacioncomunal.repository.CuotaRepository;
import com.sv.grupo11.asociacioncomunal.repository.SocioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CuotaServiceTest {

    @Mock
    private CuotaRepository cuotaRepository;
    @Mock
    private SocioRepository socioRepository;

    private CuotaService service;

    @BeforeEach
    void setUp() {
        service = new CuotaService(cuotaRepository, socioRepository);
    }

    @Test
    @DisplayName("Polimorfismo: la cuota generada a un miembro de directiva es de $7.50 y vence a fin de mes")
    void generarCuota_directiva_montoDiferenciado() {
        Socio directiva = new MiembroDirectiva(4, "Carlos", "02345678-1", "7777-1010", "x",
                LocalDate.now(), "Tesorero", "2026-2028");
        when(cuotaRepository.existeCuota(4, "2026-10")).thenReturn(false);
        when(cuotaRepository.guardar(any(Cuota.class))).thenReturn(new Cuota(15, 4, 7.50, "2026-10", LocalDate.of(2026, 10, 31)));

        Cuota generada = service.generarCuota(directiva, "2026-10");

        ArgumentCaptor<Cuota> captor = ArgumentCaptor.forClass(Cuota.class);
        verify(cuotaRepository).guardar(captor.capture());
        assertAll(
                () -> assertEquals(7.50, captor.getValue().getMonto(), 0.001),
                () -> assertEquals(LocalDate.of(2026, 10, 31), captor.getValue().getFechaVencimiento()),
                () -> assertEquals(15, generada.getIdCuota()));
    }

    @Test
    @DisplayName("Un socio regular recibe la cuota base de $5.00")
    void generarCuota_socioRegular_montoBase() {
        Socio socio = new Socio(2, "Jose", "06987541-2", "7896-1234", "x", LocalDate.now());
        when(cuotaRepository.existeCuota(anyInt(), anyString())).thenReturn(false);
        when(cuotaRepository.guardar(any(Cuota.class))).thenReturn(new Cuota(12, 2, 5.00, "2026-10", LocalDate.of(2026, 10, 31)));

        service.generarCuota(socio, "2026-10");

        ArgumentCaptor<Cuota> captor = ArgumentCaptor.forClass(Cuota.class);
        verify(cuotaRepository).guardar(captor.capture());
        assertEquals(5.00, captor.getValue().getMonto(), 0.001);
    }

    @Test
    @DisplayName("No se genera dos veces la cuota del mismo periodo para el mismo socio")
    void generarCuota_duplicada_lanzaReglaNegocio() {
        Socio socio = new Socio(2, "Jose", "06987541-2", "7896-1234", "x", LocalDate.now());
        when(cuotaRepository.existeCuota(2, "2026-10")).thenReturn(true);

        assertThrows(ReglaNegocioException.class, () -> service.generarCuota(socio, "2026-10"));
        verify(cuotaRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("Registrar pago: la cuota pasa a PAGADA y se actualiza en el repositorio")
    void registrarPago_exitoso() {
        Cuota cuota = new Cuota(4, 2, 5.00, "2026-08", LocalDate.of(2026, 8, 31));
        when(cuotaRepository.buscarPorId(4)).thenReturn(Optional.of(cuota));

        Cuota pagada = service.registrarPago(4, LocalDate.of(2026, 9, 25));

        assertEquals(EstadoCuota.PAGADA, pagada.getEstado());
        assertEquals(LocalDate.of(2026, 9, 25), pagada.getFechaPago());
        verify(cuotaRepository).actualizar(cuota);
    }

    @Test
    void registrarPago_cuotaInexistente_lanzaNoEncontrado() {
        when(cuotaRepository.buscarPorId(99)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.registrarPago(99, LocalDate.now()));
        verify(cuotaRepository, never()).actualizar(any());
    }

    @Test
    @DisplayName("Pagar una cuota ya pagada no modifica la base de datos")
    void registrarPago_yaPagada_noActualiza() {
        Cuota cuota = new Cuota(4, 2, 5.00, "2026-08", LocalDate.of(2026, 8, 31));
        cuota.registrarPago(LocalDate.of(2026, 8, 1));
        when(cuotaRepository.buscarPorId(4)).thenReturn(Optional.of(cuota));

        assertThrows(ReglaNegocioException.class, () -> service.registrarPago(4, LocalDate.now()));
        verify(cuotaRepository, never()).actualizar(any());
    }

    @Test
    void listarPendientes_socioInexistente_lanzaNoEncontrado() {
        when(socioRepository.buscarPorId(50)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.listarPendientesPorSocio(50));
        verifyNoInteractions(cuotaRepository);
    }
}
