package com.sv.grupo11.asociacioncomunal.service;

import com.sv.grupo11.asociacioncomunal.exception.RecursoNoEncontradoException;
import com.sv.grupo11.asociacioncomunal.exception.ReglaNegocioException;
import com.sv.grupo11.asociacioncomunal.exception.ValidacionException;
import com.sv.grupo11.asociacioncomunal.model.EstadoMembresia;
import com.sv.grupo11.asociacioncomunal.model.Socio;
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
import static org.mockito.Mockito.*;

/** El repositorio (JDBC) se reemplaza por un mock: se prueba solo la logica de negocio. */
@ExtendWith(MockitoExtension.class)
class SocioServiceTest {

    @Mock
    private SocioRepository socioRepository;

    private SocioService service;

    @BeforeEach
    void setUp() {
        service = new SocioService(socioRepository);
    }

    @Test
    @DisplayName("Registrar un socio nuevo lo guarda en el repositorio y devuelve el persistido")
    void registrarSocio_exitoso() {
        Socio persistido = new Socio(7, "Ana Gomez", "01234567-8", "7000-1234", "Santo Tomas", LocalDate.of(2026, 1, 1));
        when(socioRepository.buscarPorDui("01234567-8")).thenReturn(Optional.empty());
        when(socioRepository.guardar(any(Socio.class))).thenReturn(persistido);

        Socio resultado = service.registrarSocio("Ana Gomez", "01234567-8", "7000-1234", "Santo Tomas", LocalDate.of(2026, 1, 1));

        assertEquals(7, resultado.getId(), "el id lo asigna la base de datos");
        ArgumentCaptor<Socio> captor = ArgumentCaptor.forClass(Socio.class);
        verify(socioRepository, times(1)).guardar(captor.capture());
        assertEquals("01234567-8", captor.getValue().getDui());
    }

    @Test
    @DisplayName("Un DUI ya registrado viola la regla de negocio y no se guarda nada")
    void registrarSocio_duiDuplicado() {
        Socio existente = new Socio(1, "Otro", "01234567-8", "7000-0000", "x", LocalDate.now());
        when(socioRepository.buscarPorDui("01234567-8")).thenReturn(Optional.of(existente));

        assertThrows(ReglaNegocioException.class,
                () -> service.registrarSocio("Ana", "01234567-8", "7000-1234", "x", LocalDate.now()));
        verify(socioRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("Datos con formato invalido se rechazan antes de consultar el repositorio")
    void registrarSocio_datosInvalidos_noTocaRepositorio() {
        assertThrows(ValidacionException.class,
                () -> service.registrarSocio("Ana", "DUI-MALO", "7000-1234", "x", LocalDate.now()));
        verifyNoInteractions(socioRepository);
    }

    @Test
    @DisplayName("Buscar un socio inexistente lanza RecursoNoEncontradoException")
    void obtenerSocio_inexistente() {
        when(socioRepository.buscarPorId(99)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerSocio(99));
    }

    @Test
    @DisplayName("Actualizar cambia telefono y direccion y persiste; los campos vacios no se tocan")
    void actualizarSocio_cambiaSoloLoIndicado() {
        Socio socio = new Socio(3, "Ana", "01234567-8", "7000-1234", "Direccion vieja", LocalDate.now());
        when(socioRepository.buscarPorId(3)).thenReturn(Optional.of(socio));

        Socio actualizado = service.actualizarSocio(3, "7999-8888", "   ");

        assertEquals("7999-8888", actualizado.getTelefono());
        assertEquals("Direccion vieja", actualizado.getDireccion());
        verify(socioRepository).actualizar(socio);
    }

    @Test
    void cambiarEstado_persisteElNuevoEstado() {
        Socio socio = new Socio(3, "Ana", "01234567-8", "7000-1234", "x", LocalDate.now());
        when(socioRepository.buscarPorId(anyInt())).thenReturn(Optional.of(socio));

        service.cambiarEstado(3, EstadoMembresia.INACTIVO);

        assertFalse(socio.estaActivo());
        verify(socioRepository).actualizar(socio);
    }
}
