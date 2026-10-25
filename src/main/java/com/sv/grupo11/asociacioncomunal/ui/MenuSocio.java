package com.sv.grupo11.asociacioncomunal.ui;

import com.sv.grupo11.asociacioncomunal.controller.*;
import com.sv.grupo11.asociacioncomunal.model.Usuario;

import java.util.Scanner;

/** Solo consulta de su propia informacion. */
public class MenuSocio extends MenuBase {

    public MenuSocio(Scanner sc, Usuario u, AuthController a, SocioController s, CuotaController c, SistemaController sis) {
        super(sc, u, a, s, c, sis);
    }

    @Override
    protected String titulo() { return "MENU SOCIO"; }

    @Override
    protected void mostrarOpciones() {
        System.out.println("1. Ver mis datos");
        System.out.println("2. Mis cuotas pendientes");
        System.out.println("3. Mi historial de cuotas");
    }

    @Override
    protected void procesarOpcion(int op) {
        int miId = usuario.getIdSocio(); // el socio solo puede ver lo suyo
        switch (op) {
            case 1 -> System.out.println(socioController.obtener(miId).describir());
            case 2 -> cuotasPendientesUI(miId);
            case 3 -> historialUI(miId);
            default -> System.out.println("[!] Opcion no valida.");
        }
    }
}
