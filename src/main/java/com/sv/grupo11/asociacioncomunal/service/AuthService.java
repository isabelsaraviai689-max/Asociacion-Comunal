package com.sv.grupo11.asociacioncomunal.service;

import com.sv.grupo11.asociacioncomunal.exception.RecursoNoEncontradoException;
import com.sv.grupo11.asociacioncomunal.exception.ReglaNegocioException;
import com.sv.grupo11.asociacioncomunal.exception.ValidacionException;
import com.sv.grupo11.asociacioncomunal.model.Rol;
import com.sv.grupo11.asociacioncomunal.model.Usuario;
import com.sv.grupo11.asociacioncomunal.repository.UsuarioRepository;
import com.sv.grupo11.asociacioncomunal.util.PasswordUtil;

import java.util.List;
import java.util.Optional;

/** Autenticacion y administracion de usuarios. Depende de la INTERFAZ UsuarioRepository, no de JDBC. */
public class AuthService {

    private static final int LONGITUD_MINIMA_CONTRASENA = 6;

    private final UsuarioRepository usuarios;

    public AuthService(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    public Optional<Usuario> login(String nombreUsuario, String contrasena) {
        if (nombreUsuario == null || nombreUsuario.isBlank() || contrasena == null) {
            return Optional.empty();
        }
        return usuarios.buscarPorNombre(nombreUsuario).filter(u -> u.validarContrasena(contrasena));
    }

    public Usuario crearUsuario(String nombreUsuario, String contrasena, Rol rol, Integer idSocio) {
        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            throw new ValidacionException("El nombre de usuario es obligatorio.");
        }
        if (contrasena == null || contrasena.length() < LONGITUD_MINIMA_CONTRASENA) {
            throw new ValidacionException("La contrasena debe tener al menos " + LONGITUD_MINIMA_CONTRASENA + " caracteres.");
        }
        if (rol == null) {
            throw new ValidacionException("El rol es obligatorio.");
        }
        if (rol == Rol.SOCIO && idSocio == null) {
            throw new ValidacionException("Un usuario de rol SOCIO debe asociarse a un socio.");
        }
        if (usuarios.buscarPorNombre(nombreUsuario).isPresent()) {
            throw new ReglaNegocioException("Ya existe el usuario " + nombreUsuario.trim());
        }
        Usuario nuevo = new Usuario(0, nombreUsuario.trim(), PasswordUtil.hash(contrasena), rol, idSocio);
        return usuarios.guardar(nuevo);
    }

    /** Regla: nunca puede eliminarse el ultimo administrador del sistema. */
    public void eliminarUsuario(int idUsuario) {
        Usuario usuario = usuarios.buscarPorId(idUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("el usuario", idUsuario));
        if (usuario.getRol() == Rol.ADMINISTRADOR && usuarios.contarPorRol(Rol.ADMINISTRADOR) <= 1) {
            throw new ReglaNegocioException("No se puede eliminar al unico administrador del sistema.");
        }
        usuarios.eliminar(idUsuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarios.listarTodos();
    }
}
