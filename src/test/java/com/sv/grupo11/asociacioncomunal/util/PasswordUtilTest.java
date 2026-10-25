package com.sv.grupo11.asociacioncomunal.util;

import com.sv.grupo11.asociacioncomunal.exception.ValidacionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Clase de utilidad: proteccion de contrasenas. */
class PasswordUtilTest {

    @Test
    @DisplayName("El hash nunca contiene la contrasena en claro y tiene el formato pbkdf2$iter$salt$hash")
    void hash_tieneFormatoYNoExponeClave() {
        String hash = PasswordUtil.hash("admin123");
        assertAll(
                () -> assertFalse(hash.contains("admin123")),
                () -> assertTrue(PasswordUtil.esHash(hash)),
                () -> assertEquals(4, hash.split("\\$").length));
    }

    @Test
    @DisplayName("Dos hashes de la misma contrasena son distintos (salt aleatorio) pero ambos verifican")
    void hash_usaSaltAleatorio() {
        String h1 = PasswordUtil.hash("secreto1");
        String h2 = PasswordUtil.hash("secreto1");
        assertNotEquals(h1, h2);
        assertTrue(PasswordUtil.verificar("secreto1", h1));
        assertTrue(PasswordUtil.verificar("secreto1", h2));
    }

    @Test
    void verificar_rechazaContrasenaIncorrecta() {
        String hash = PasswordUtil.hash("correcta");
        assertFalse(PasswordUtil.verificar("incorrecta", hash));
        assertFalse(PasswordUtil.verificar(null, hash));
    }

    @Test
    @DisplayName("Compatibilidad: un valor almacenado sin hash se compara en claro")
    void verificar_valorLegadoEnClaro() {
        assertTrue(PasswordUtil.verificar("admin123", "admin123"));
        assertFalse(PasswordUtil.verificar("otra", "admin123"));
    }

    @Test
    void hash_contrasenaVacia_lanzaValidacion() {
        assertThrows(ValidacionException.class, () -> PasswordUtil.hash("  "));
    }
}
