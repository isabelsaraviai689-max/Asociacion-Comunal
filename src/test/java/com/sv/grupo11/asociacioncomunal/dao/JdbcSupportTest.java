package com.sv.grupo11.asociacioncomunal.dao;

import com.sv.grupo11.asociacioncomunal.exception.PersistenciaException;
import com.sv.grupo11.asociacioncomunal.exception.RegistroDuplicadoException;
import com.sv.grupo11.asociacioncomunal.model.EstadoCuota;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Types;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

/** Clase de utilidad JDBC: conversion de parametros y traduccion de errores, sin base de datos real. */
@ExtendWith(MockitoExtension.class)
class JdbcSupportTest {

    @Mock
    private DataSource dataSource;
    @Mock
    private PreparedStatement ps;

    /** Subclase minima para poder probar los metodos protected. */
    private static class DaoDePrueba extends JdbcSupport {
        DaoDePrueba(DataSource ds) { super(ds); }
        void parametros(PreparedStatement ps, Object... p) throws SQLException { asignarParametros(ps, p); }
        PersistenciaException traducirError(SQLException e) { return traducir("probar", e); }
    }

    @Test
    @DisplayName("asignarParametros convierte LocalDate a java.sql.Date, enums a texto y null a setNull")
    void asignarParametros_convierteTipos() throws SQLException {
        new DaoDePrueba(dataSource).parametros(ps, 7, LocalDate.of(2026, 10, 31), EstadoCuota.PAGADA, null, "texto");

        verify(ps).setObject(1, 7);
        verify(ps).setDate(2, Date.valueOf(LocalDate.of(2026, 10, 31)));
        verify(ps).setString(3, "PAGADA");
        verify(ps).setNull(4, Types.NULL);
        verify(ps).setObject(5, "texto");
    }

    @Test
    @DisplayName("Una violacion de UNIQUE se traduce a RegistroDuplicadoException conservando la causa")
    void traducir_violacionDeIntegridad() {
        SQLException causa = new SQLIntegrityConstraintViolationException("Duplicate entry");
        PersistenciaException ex = new DaoDePrueba(dataSource).traducirError(causa);
        assertTrue(ex instanceof RegistroDuplicadoException);
        assertSame(causa, ex.getCause());
    }

    @Test
    void traducir_otroErrorSQL_esPersistenciaGenerica() {
        PersistenciaException ex = new DaoDePrueba(dataSource).traducirError(new SQLException("Connection reset"));
        assertFalse(ex instanceof RegistroDuplicadoException);
        assertTrue(ex.getMessage().contains("Connection reset"));
    }

    @Test
    void constructor_sinDataSource_falla() {
        assertThrows(NullPointerException.class, () -> new DaoDePrueba(null));
    }
}
