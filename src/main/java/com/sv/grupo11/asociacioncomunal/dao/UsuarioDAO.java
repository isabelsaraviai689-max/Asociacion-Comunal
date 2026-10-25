package com.sv.grupo11.asociacioncomunal.dao;

import com.sv.grupo11.asociacioncomunal.exception.PersistenciaException;
import com.sv.grupo11.asociacioncomunal.model.Rol;
import com.sv.grupo11.asociacioncomunal.model.Usuario;
import com.sv.grupo11.asociacioncomunal.repository.UsuarioRepository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** Implementacion JDBC (MySQL) de UsuarioRepository. La contrasena se guarda ya hasheada. */
public class UsuarioDAO extends JdbcSupport implements UsuarioRepository {

    private static final String SELECT_BASE =
            "SELECT id_usuario, nombre_usuario, contrasena, rol, id_socio FROM usuarios";

    public UsuarioDAO(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        int id = ejecutarEnTransaccion(con -> insertarYObtenerId(con,
                "INSERT INTO usuarios (nombre_usuario, contrasena, rol, id_socio) VALUES (?, ?, ?, ?)",
                usuario.getNombreUsuario(), usuario.getContrasenaHash(), usuario.getRol(), usuario.getIdSocio()));
        return buscarPorId(id)
                .orElseThrow(() -> new PersistenciaException("El usuario se inserto pero no pudo releerse.", null));
    }

    @Override
    public boolean actualizar(Usuario usuario) {
        return ejecutarActualizacion(
                "UPDATE usuarios SET nombre_usuario = ?, contrasena = ?, rol = ?, id_socio = ? WHERE id_usuario = ?",
                usuario.getNombreUsuario(), usuario.getContrasenaHash(), usuario.getRol(),
                usuario.getIdSocio(), usuario.getIdUsuario()) > 0;
    }

    @Override
    public boolean eliminar(int idUsuario) {
        return ejecutarActualizacion("DELETE FROM usuarios WHERE id_usuario = ?", idUsuario) > 0;
    }

    @Override
    public Optional<Usuario> buscarPorId(int id) {
        return consultar(SELECT_BASE + " WHERE id_usuario = ?", this::mapear, id).stream().findFirst();
    }

    @Override
    public Optional<Usuario> buscarPorNombre(String nombreUsuario) {
        return consultar(SELECT_BASE + " WHERE nombre_usuario = ?", this::mapear, nombreUsuario.trim()).stream().findFirst();
    }

    @Override
    public List<Usuario> listarTodos() {
        return consultar(SELECT_BASE + " ORDER BY id_usuario", this::mapear);
    }

    @Override
    public int contarPorRol(Rol rol) {
        return consultarEntero("SELECT COUNT(*) FROM usuarios WHERE rol = ?", rol);
    }

    @Override
    public int contar() {
        return consultarEntero("SELECT COUNT(*) FROM usuarios");
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        int idSocio = rs.getInt("id_socio");
        return new Usuario(
                rs.getInt("id_usuario"),
                rs.getString("nombre_usuario"),
                rs.getString("contrasena"),
                Rol.valueOf(rs.getString("rol")),
                rs.wasNull() ? null : idSocio);
    }
}
