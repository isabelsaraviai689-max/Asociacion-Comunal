package com.sv.grupo11.asociacioncomunal.controller;

import com.sv.grupo11.asociacioncomunal.model.EstadoMembresia;
import com.sv.grupo11.asociacioncomunal.model.Socio;
import com.sv.grupo11.asociacioncomunal.service.SocioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SocioControllerTest {

    @Mock
    private SocioService socioService;

    @Test
    void registrar_delegaYDevuelveElSocio() {
        Socio socio = new Socio(1, "Ana", "01234567-8", "7000-1234", "x", LocalDate.of(2026, 1, 1));
        when(socioService.registrarSocio("Ana", "01234567-8", "7000-1234", "x", LocalDate.of(2026, 1, 1))).thenReturn(socio);

        Socio resultado = new SocioController(socioService).registrar("Ana", "01234567-8", "7000-1234", "x", LocalDate.of(2026, 1, 1));

        assertSame(socio, resultado);
    }

    @Test
    void buscarPorDui_devuelveVacioSiNoExiste() {
        when(socioService.buscarPorDui("00000000-0")).thenReturn(Optional.empty());
        assertTrue(new SocioController(socioService).buscarPorDui("00000000-0").isEmpty());
    }

    @Test
    void cambiarEstado_delegaConElEstadoIndicado() {
        new SocioController(socioService).cambiarEstado(3, EstadoMembresia.INACTIVO);
        verify(socioService).cambiarEstado(3, EstadoMembresia.INACTIVO);
    }
}
