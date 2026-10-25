package com.sv.grupo11.asociacioncomunal.repository;

import com.sv.grupo11.asociacioncomunal.model.Rol;
import com.sv.grupo11.asociacioncomunal.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {

    /** Inserta y devuelve el usuario con el id asignado por la base de datos. */
    Usuario guardar(Usuario usuario);

    boolean actualizar(Usuario usuario);

    boolean eliminar(int idUsuario);

    Optional<Usuario> buscarPorId(int id);

    Optional<Usuario> buscarPorNombre(String nombreUsuario);

    List<Usuario> listarTodos();

    int contarPorRol(Rol rol);

    int contar();
}
