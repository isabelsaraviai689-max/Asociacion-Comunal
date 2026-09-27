package com.sv.grupo10.asociacioncomunal.ui;

import com.sv.grupo10.asociacioncomunal.controller.*;
import com.sv.grupo10.asociacioncomunal.model.Usuario;

import java.util.Scanner;

/** Solo modulo de socios (padron). */
public class MenuSecretario extends MenuBase {

    public MenuSecretario(Scanner sc, Usuario u, AuthController a, SocioController s, CuotaController c) {
        super(sc, u, a, s, c);
    }

    @Override
    protected String titulo() { return "MENU SECRETARIO"; }

    @Override
    protected void mostrarOpciones() {
        System.out.println("1. Registrar socio");
        System.out.println("2. Registrar junta directiva");
        System.out.println("3. Actualizar socio");
        System.out.println("4. Buscar socio por DUI");
        System.out.println("5. Listar socios");
    }

    @Override
    protected void procesarOpcion(int op) {
        switch (op) {
            case 1 -> registrarSocioUI();
            case 2 -> registrarDirectivaUI();
            case 3 -> actualizarSocioUI();
            case 4 -> buscarSocioUI();
            case 5 -> listarSociosUI();
            default -> System.out.println("[!] Opcion no valida.");
        }
    }
}
