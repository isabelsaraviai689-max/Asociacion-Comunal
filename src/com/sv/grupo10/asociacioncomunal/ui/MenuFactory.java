package com.sv.grupo10.asociacioncomunal.ui;

import com.sv.grupo10.asociacioncomunal.controller.*;
import com.sv.grupo10.asociacioncomunal.model.Usuario;

import java.util.Scanner;

/** Devuelve el menu concreto segun el rol; el Main solo conoce MenuBase (POLIMORFISMO). */
public class MenuFactory {

    private MenuFactory() {}

    public static MenuBase crear(Scanner sc, Usuario u, AuthController a, SocioController s, CuotaController c) {
        return switch (u.getRol()) {
            case ADMINISTRADOR -> new MenuAdministrador(sc, u, a, s, c);
            case SECRETARIO -> new MenuSecretario(sc, u, a, s, c);
            case TESORERO -> new MenuTesorero(sc, u, a, s, c);
            case SOCIO -> new MenuSocio(sc, u, a, s, c);
        };
    }
}
