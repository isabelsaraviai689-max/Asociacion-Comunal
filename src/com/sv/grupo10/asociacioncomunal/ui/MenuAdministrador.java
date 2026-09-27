package com.sv.grupo10.asociacioncomunal.ui;

import com.sv.grupo10.asociacioncomunal.controller.*;
import com.sv.grupo10.asociacioncomunal.model.Usuario;

import java.util.Scanner;

/** Acceso a todas las funciones del sistema. */
public class MenuAdministrador extends MenuBase {

    public MenuAdministrador(Scanner sc, Usuario u, AuthController a, SocioController s, CuotaController c) {
        super(sc, u, a, s, c);
    }

    @Override
    protected String titulo() { return "MENU ADMINISTRADOR"; }

    @Override
    protected void mostrarOpciones() {
        System.out.println("1. Registrar socio            6. Registrar pago de cuota");
        System.out.println("2. Registrar junta directiva  7. Cuotas pendientes por socio");
        System.out.println("3. Actualizar socio           8. Reporte de morosos");
        System.out.println("4. Buscar socio por DUI       9. Ejecutar cierre mensual (hilos)");
        System.out.println("5. Listar socios             10. Ver estado del cierre");
        System.out.println("11. Cambiar estado de socio  12. Crear usuario   13. Listar usuarios");
    }

    @Override
    protected void procesarOpcion(int op) {
        switch (op) {
            case 1 -> registrarSocioUI();
            case 2 -> registrarDirectivaUI();
            case 3 -> actualizarSocioUI();
            case 4 -> buscarSocioUI();
            case 5 -> listarSociosUI();
            case 6 -> registrarPagoUI();
            case 7 -> cuotasPendientesUI(leerEntero("ID del socio: "));
            case 8 -> reporteMorososUI();
            case 9 -> cierreMensualUI();
            case 10 -> estadoCierreUI();
            case 11 -> cambiarEstadoUI();
            case 12 -> crearUsuarioUI();
            case 13 -> listarUsuariosUI();
            default -> System.out.println("[!] Opcion no valida.");
        }
    }
}
