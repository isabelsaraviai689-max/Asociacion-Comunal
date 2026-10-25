package com.sv.grupo11.asociacioncomunal.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/** Herencia y polimorfismo: MiembroDirectiva especializa a Socio. */
class MiembroDirectivaTest {

    @Test
    @DisplayName("La cuota de un socio regular es la base de $5.00")
    void socioRegular_cuotaBase() {
        Persona socio = new Socio(1, "Jose", "01234567-8", "7000-1234", "x", LocalDate.now());
        assertEquals(5.00, socio.calcularCuotaMensual(), 0.001);
    }

    @Test
    @DisplayName("La cuota de la directiva incluye el aporte adicional: $7.50")
    void directiva_cuotaDiferenciada() {
        Persona directiva = new MiembroDirectiva(2, "Maria", "04512378-9", "7412-3698", "x",
                LocalDate.now(), "Presidente", "2026-2028");
        assertEquals(7.50, directiva.calcularCuotaMensual(), 0.001);
    }

    @Test
    @DisplayName("describir() de la directiva reutiliza la del padre y agrega el cargo")
    void directiva_describirIncluyeCargo() {
        MiembroDirectiva directiva = new MiembroDirectiva(2, "Maria", "04512378-9", "7412-3698", "x",
                LocalDate.of(2024, 3, 15), "Presidente", "2026-2028");
        String texto = directiva.describir();
        assertAll(
                () -> assertTrue(texto.contains("Maria")),
                () -> assertTrue(texto.contains("DIRECTIVA: Presidente")),
                () -> assertTrue(directiva instanceof Socio, "un miembro de directiva ES un socio"));
    }
}
