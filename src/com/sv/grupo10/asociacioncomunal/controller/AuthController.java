package com.sv.grupo10.asociacioncomunal.controller;

import com.sv.grupo10.asociacioncomunal.model.Rol;
import com.sv.grupo10.asociacioncomunal.model.Usuario;
import com.sv.grupo10.asociacioncomunal.service.AuthService;

import java.util.List;
import java.util.Optional;

/** CAPA CONTROLLER: recibe las peticiones de la UI y las delega al servicio. */
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public Optional<Usuario> login(String usuario, String contrasena) {
        return authService.login(usuario, contrasena);
    }

    public Usuario crearUsuario(String usuario, String contrasena, Rol rol, Integer idSocio) {
        return authService.crearUsuario(usuario, contrasena, rol, idSocio);
    }

    public List<Usuario> listarUsuarios() {
        return authService.listarUsuarios();
    }
}
