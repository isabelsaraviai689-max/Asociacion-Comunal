package com.sv.grupo11.asociacioncomunal.service;

import com.sv.grupo11.asociacioncomunal.dto.SocioMorosoDTO;
import com.sv.grupo11.asociacioncomunal.model.Cuota;
import com.sv.grupo11.asociacioncomunal.model.EstadoCuota;
import com.sv.grupo11.asociacioncomunal.model.Socio;
import com.sv.grupo11.asociacioncomunal.repository.CuotaRepository;
import com.sv.grupo11.asociacioncomunal.repository.SocioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock
    private SocioRepository socioRepository;
    @Mock
    private CuotaRepository cuotaRepository;

    private Cuota vencida(int id, int idSocio, double monto) {
        Cuota c = new Cuota(id, idSocio, monto, "2026-08", LocalDate.of(2026, 8, 31));
        c.verificarVencimiento(LocalDate.of(2026, 9, 30));
        return c;
    }

    @Test
    @DisplayName("El reporte agrupa las cuotas vencidas por socio, suma montos y ordena de mayor a menor")
    void reporteMorosos_agrupaYOrdena() {
        Socio rosa = new Socio(5, "Rosa", "01234567-0", "7222-3344", "x", LocalDate.now());
        Socio jose = new Socio(2, "Jose", "06987541-2", "7896-1234", "x", LocalDate.now());
        Socio maria = new Socio(1, "Maria", "04512378-9", "7412-3698", "x", LocalDate.now());
        when(socioRepository.listarTodos()).thenReturn(List.of(maria, jose, rosa));
        when(cuotaRepository.listarPorEstado(EstadoCuota.VENCIDA)).thenReturn(List.of(
                vencida(4, 2, 5.00), vencida(9, 5, 5.00), vencida(10, 5, 5.00)));

        List<SocioMorosoDTO> morosos = new ReporteService(socioRepository, cuotaRepository).reporteMorosos();

        assertAll(
                () -> assertEquals(2, morosos.size(), "Maria no tiene vencidas y no aparece"),
                () -> assertEquals(5, morosos.get(0).getIdSocio(), "Rosa debe mas, va primero"),
                () -> assertEquals(2, morosos.get(0).getCuotasPendientes()),
                () -> assertEquals(10.00, morosos.get(0).getMontoPendiente(), 0.001),
                () -> assertEquals(2, morosos.get(1).getIdSocio()),
                () -> assertEquals(5.00, morosos.get(1).getMontoPendiente(), 0.001));
    }

    @Test
    void reporteMorosos_sinVencidas_devuelveListaVacia() {
        when(socioRepository.listarTodos()).thenReturn(List.of());
        when(cuotaRepository.listarPorEstado(EstadoCuota.VENCIDA)).thenReturn(List.of());
        assertTrue(new ReporteService(socioRepository, cuotaRepository).reporteMorosos().isEmpty());
    }
}
