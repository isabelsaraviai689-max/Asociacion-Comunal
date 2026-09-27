package com.sv.grupo10.asociacioncomunal.ui;

import com.sv.grupo10.asociacioncomunal.controller.*;
import com.sv.grupo10.asociacioncomunal.model.Usuario;

import java.util.Scanner;

/** Solo modulo de cuotas, reportes y cierre mensual. */
public class MenuTesorero extends MenuBase {

    public MenuTesorero(Scanner sc, Usuario u, AuthController a, SocioController s, CuotaController c) {
        super(sc, u, a, s, c);
    }

    @Override
    protected String titulo() { return "MENU TESORERO"; }

    @Override
    protected void mostrarOpciones() {
        System.out.println("1. Registrar pago de cuota");
        System.out.println("2. Cuotas pendientes por socio");
        System.out.println("3. Historial de cuotas por socio");
        System.out.println("4. Reporte de morosos");
        System.out.println("5. Ejecutar cierre mensual (hilos)");
        System.out.println("6. Ver estado del cierre");
        System.out.println("7. Listar socios");
    }

    @Override
    protected void procesarOpcion(int op) {
        switch (op) {
            case 1 -> registrarPagoUI();
            case 2 -> cuotasPendientesUI(leerEntero("ID del socio: "));
            case 3 -> historialUI(leerEntero("ID del socio: "));
            case 4 -> reporteMorososUI();
            case 5 -> cierreMensualUI();
            case 6 -> estadoCierreUI();
            case 7 -> listarSociosUI();
            default -> System.out.println("[!] Opcion no valida.");
        }
    }
}
