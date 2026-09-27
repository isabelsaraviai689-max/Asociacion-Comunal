package com.sv.grupo10.asociacioncomunal.dao;

import com.sv.grupo10.asociacioncomunal.model.Usuario;

import java.util.Optional;

public class UsuarioDAO extends ArchivoDAO<Usuario> {

    public UsuarioDAO() {
        super("usuarios.dat");
    }

    @Override
    protected int obtenerId(Usuario usuario) {
        return usuario.getIdUsuario();
    }

    public synchronized Optional<Usuario> buscarPorNombre(String nombreUsuario) {
        return registros.stream()
                .filter(u -> u.getNombreUsuario().equalsIgnoreCase(nombreUsuario.trim()))
                .findFirst();
    }
}
