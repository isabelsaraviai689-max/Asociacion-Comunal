package com.sv.grupo10.asociacioncomunal.util;

import com.sv.grupo10.asociacioncomunal.dao.CuotaDAO;
import com.sv.grupo10.asociacioncomunal.dao.SocioDAO;
import com.sv.grupo10.asociacioncomunal.dao.UsuarioDAO;
import com.sv.grupo10.asociacioncomunal.model.Rol;
import com.sv.grupo10.asociacioncomunal.model.Socio;
import com.sv.grupo10.asociacioncomunal.service.AuthService;
import com.sv.grupo10.asociacioncomunal.service.CuotaService;
import com.sv.grupo10.asociacioncomunal.service.SocioService;

import java.time.LocalDate;

/**
 * Carga datos de demostracion SOLO la primera vez (cuando los .dat no existen).
 * En las siguientes ejecuciones el sistema arranca desde los archivos .dat.
 */
public final class DatosDemo {

    private DatosDemo() {}

    public static void cargarSiEsNecesario(SocioDAO socioDAO, CuotaDAO cuotaDAO, UsuarioDAO usuarioDAO) {
        if (usuarioDAO.contar() > 0) return;

        AuthService auth = new AuthService(usuarioDAO);
        SocioService socios = new SocioService(socioDAO);
        CuotaService cuotas = new CuotaService(cuotaDAO, socioDAO);

        Socio s1 = socios.registrarMiembroDirectiva("Maria Elena Perez", "04512378-9", "7412-3698",
                "Colonia Las Flores, Santo Tomas", LocalDate.of(2024, 3, 15), "Presidente", "2026-2028");
        Socio s2 = socios.registrarSocio("Jose Antonio Ramirez", "06987541-2", "7896-1234",
                "Barrio San Jose, Santo Tomas", LocalDate.of(2025, 1, 10));
        Socio s3 = socios.registrarSocio("Ana Lucia Gomez", "03214567-8", "7555-8899",
                "Pasaje 3, Santo Tomas", LocalDate.of(2025, 6, 20));
        Socio s4 = socios.registrarMiembroDirectiva("Carlos Mendez Rivas", "02345678-1", "7777-1010",
                "Calle Principal, Santo Tomas", LocalDate.of(2023, 8, 1), "Tesorero", "2026-2028");
        Socio s5 = socios.registrarSocio("Rosa Hernandez", "01234567-0", "7222-3344",
                "Colonia El Carmen, Santo Tomas", LocalDate.of(2026, 2, 5));

        // Cuotas de meses anteriores: algunas pagadas, otras sin pagar (serviran al reporte de morosos)
        for (Socio s : new Socio[]{s1, s2, s3, s4, s5}) {
            cuotas.generarCuota(s, "2026-07");
            cuotas.generarCuota(s, "2026-08");
        }
        cuotas.registrarPago(1, LocalDate.of(2026, 7, 20));   // s1 julio
        cuotas.registrarPago(2, LocalDate.of(2026, 8, 18));   // s1 agosto
        cuotas.registrarPago(3, LocalDate.of(2026, 7, 25));   // s2 julio
        cuotas.registrarPago(7, LocalDate.of(2026, 7, 30));   // s4 julio
        cuotas.registrarPago(8, LocalDate.of(2026, 8, 28));   // s4 agosto

        auth.crearUsuario("admin", "admin123", Rol.ADMINISTRADOR, null);
        auth.crearUsuario("secretario", "secre123", Rol.SECRETARIO, null);
        auth.crearUsuario("tesorero", "tesoro123", Rol.TESORERO, null);
        auth.crearUsuario("jramirez", "socio123", Rol.SOCIO, s2.getId());

        System.out.println("[i] Datos de demostracion cargados en la carpeta data/");
    }
}
