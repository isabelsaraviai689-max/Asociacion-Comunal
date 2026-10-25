package com.sv.grupo11.asociacioncomunal.model;

import com.sv.grupo11.asociacioncomunal.exception.ValidacionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/** Validaciones del modelo (encapsulamiento): un objeto nunca queda en estado invalido. */
class PersonaTest {

    private Socio socioValido() {
        return new Socio(1, "Ana Gomez", "01234567-8", "7000-1234", "Santo Tomas", LocalDate.of(2026, 1, 10));
    }

    @Test
    @DisplayName("Un socio con datos validos se construye y queda ACTIVO")
    void socioValido_seConstruye() {
        Socio socio = socioValido();
        assertAll(
                () -> assertEquals("Ana Gomez", socio.getNombre()),
                () -> assertEquals("01234567-8", socio.getDui()),
                () -> assertEquals(EstadoMembresia.ACTIVO, socio.getEstado()),
                () -> assertTrue(socio.estaActivo()));
    }

    @Test
    @DisplayName("DUI sin el formato ########-# lanza ValidacionException")
    void duiInvalido_lanzaExcepcion() {
        ValidacionException ex = assertThrows(ValidacionException.class,
                () -> new Socio(1, "Ana", "1234", "7000-1234", "x", LocalDate.now()));
        assertTrue(ex.getMessage().contains("DUI"));
    }

    @Test
    @DisplayName("Telefono sin el formato ####-#### lanza ValidacionException")
    void telefonoInvalido_lanzaExcepcion() {
        assertThrows(ValidacionException.class,
                () -> new Socio(1, "Ana", "01234567-8", "70001234", "x", LocalDate.now()));
    }

    @Test
    @DisplayName("Nombre vacio lanza ValidacionException")
    void nombreVacio_lanzaExcepcion() {
        assertThrows(ValidacionException.class,
                () -> new Socio(1, "   ", "01234567-8", "7000-1234", "x", LocalDate.now()));
    }

    @Test
    @DisplayName("El setter de telefono tambien valida (no solo el constructor)")
    void setTelefono_valida() {
        Socio socio = socioValido();
        assertThrows(ValidacionException.class, () -> socio.setTelefono("abc"));
        assertEquals("7000-1234", socio.getTelefono(), "el valor anterior debe conservarse");
    }
}
