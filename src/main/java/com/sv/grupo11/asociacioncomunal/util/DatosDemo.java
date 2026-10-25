package com.sv.grupo11.asociacioncomunal.util;

import com.sv.grupo11.asociacioncomunal.model.Cuota;
import com.sv.grupo11.asociacioncomunal.model.Rol;
import com.sv.grupo11.asociacioncomunal.model.Socio;
import com.sv.grupo11.asociacioncomunal.repository.CuotaRepository;
import com.sv.grupo11.asociacioncomunal.repository.SocioRepository;
import com.sv.grupo11.asociacioncomunal.repository.UsuarioRepository;
import com.sv.grupo11.asociacioncomunal.service.AuthService;
import com.sv.grupo11.asociacioncomunal.service.CuotaService;
import com.sv.grupo11.asociacioncomunal.service.SocioService;

import java.time.LocalDate;

/**
 * Carga datos de demostracion SOLO si la base de datos esta vacia (sin usuarios).
 * Alternativa a ejecutar database/data.sql; util cuando se crea el esquema a mano.
 */
public final class DatosDemo {

    private DatosDemo() {}

    public static void cargarSiEsNecesario(SocioRepository socioRepo, CuotaRepository cuotaRepo, UsuarioRepository usuarioRepo) {
        if (usuarioRepo.contar() > 0) return;

        AuthService auth = new AuthService(usuarioRepo);
        SocioService socios = new SocioService(socioRepo);
        CuotaService cuotas = new CuotaService(cuotaRepo, socioRepo);

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

        // Cuotas de meses anteriores: algunas pagadas, otras sin pagar (alimentan el reporte de morosos)
        for (Socio s : new Socio[]{s1, s2, s3, s4, s5}) {
            Cuota julio = cuotas.generarCuota(s, "2026-07");
            Cuota agosto = cuotas.generarCuota(s, "2026-08");
            if (s == s1 || s == s4) {
                cuotas.registrarPago(julio.getIdCuota(), LocalDate.of(2026, 7, 20));
                cuotas.registrarPago(agosto.getIdCuota(), LocalDate.of(2026, 8, 18));
            } else if (s == s2) {
                cuotas.registrarPago(julio.getIdCuota(), LocalDate.of(2026, 7, 25));
            }
        }

        auth.crearUsuario("admin", "admin123", Rol.ADMINISTRADOR, null);
        auth.crearUsuario("secretario", "secre123", Rol.SECRETARIO, null);
        auth.crearUsuario("tesorero", "tesoro123", Rol.TESORERO, null);
        auth.crearUsuario("jramirez", "socio123", Rol.SOCIO, s2.getId());

        System.out.println("[i] Datos de demostracion cargados en la base de datos.");
    }
}
