package com.sv.grupo10.asociacioncomunal.service;

import com.sv.grupo10.asociacioncomunal.dao.UsuarioDAO;
import com.sv.grupo10.asociacioncomunal.model.Rol;
import com.sv.grupo10.asociacioncomunal.model.Usuario;

import java.util.List;
import java.util.Optional;

/** Logica de autenticacion y administracion de usuarios. */
public class AuthService {

    private final UsuarioDAO usuarioDAO;

    public AuthService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public Optional<Usuario> login(String nombreUsuario, String contrasena) {
        return usuarioDAO.buscarPorNombre(nombreUsuario)
                .filter(u -> u.validarContrasena(contrasena));
    }

    public Usuario crearUsuario(String nombreUsuario, String contrasena, Rol rol, Integer idSocio) {
        if (nombreUsuario == null || nombreUsuario.isBlank() || contrasena == null || contrasena.length() < 6) {
            throw new IllegalArgumentException("Usuario obligatorio y contrasena de al menos 6 caracteres.");
        }
        if (usuarioDAO.buscarPorNombre(nombreUsuario).isPresent()) {
            throw new IllegalArgumentException("Ya existe el usuario " + nombreUsuario);
        }
        if (rol == Rol.SOCIO && idSocio == null) {
            throw new IllegalArgumentException("Un usuario de rol SOCIO debe asociarse a un socio.");
        }
        Usuario nuevo = new Usuario(usuarioDAO.siguienteId(), nombreUsuario.trim(), contrasena, rol, idSocio);
        usuarioDAO.guardar(nuevo);
        return nuevo;
    }

    public List<Usuario> listarUsuarios() {
        return usuarioDAO.listarTodos();
    }
}
