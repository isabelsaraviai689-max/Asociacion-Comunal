package com.sv.grupo11.asociacioncomunal.model;

import com.sv.grupo11.asociacioncomunal.exception.ReglaNegocioException;
import com.sv.grupo11.asociacioncomunal.exception.ValidacionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CuotaTest {

    private Cuota cuota;

    @BeforeEach
    void setUp() {
        cuota = new Cuota(1, 10, 5.00, "2026-08", LocalDate.of(2026, 8, 31));
    }

    @Test
    void nuevaCuota_quedaPendiente() {
        assertEquals(EstadoCuota.PENDIENTE, cuota.getEstado());
        assertTrue(cuota.estaPendiente());
        assertNull(cuota.getFechaPago());
    }

    @Test
    void montoCeroONegativo_lanzaValidacion() {
        assertThrows(ValidacionException.class, () -> new Cuota(1, 10, 0, "2026-08", LocalDate.now()));
        assertThrows(ValidacionException.class, () -> new Cuota(1, 10, -3, "2026-08", LocalDate.now()));
    }

    @Test
    @DisplayName("registrarPago cambia el estado a PAGADA y guarda la fecha")
    void registrarPago_marcaPagada() {
        cuota.registrarPago(LocalDate.of(2026, 8, 10));
        assertEquals(EstadoCuota.PAGADA, cuota.getEstado());
        assertEquals(LocalDate.of(2026, 8, 10), cuota.getFechaPago());
        assertFalse(cuota.estaPendiente());
    }

    @Test
    @DisplayName("Pagar dos veces la misma cuota viola una regla de negocio")
    void registrarPagoDosVeces_lanzaReglaNegocio() {
        cuota.registrarPago(LocalDate.now());
        assertThrows(ReglaNegocioException.class, () -> cuota.registrarPago(LocalDate.now()));
    }

    @Test
    @DisplayName("verificarVencimiento marca VENCIDA solo si paso la fecha limite y sigue pendiente")
    void verificarVencimiento() {
        assertFalse(cuota.verificarVencimiento(LocalDate.of(2026, 8, 31)), "el dia del vencimiento aun no vence");
        assertTrue(cuota.verificarVencimiento(LocalDate.of(2026, 9, 1)));
        assertEquals(EstadoCuota.VENCIDA, cuota.getEstado());
        assertFalse(cuota.verificarVencimiento(LocalDate.of(2026, 9, 2)), "una cuota ya vencida no se vuelve a marcar");
    }
}
