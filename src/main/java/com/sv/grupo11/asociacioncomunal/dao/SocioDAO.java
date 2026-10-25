package com.sv.grupo11.asociacioncomunal.dao;

import com.sv.grupo11.asociacioncomunal.exception.PersistenciaException;
import com.sv.grupo11.asociacioncomunal.model.EstadoMembresia;
import com.sv.grupo11.asociacioncomunal.model.MiembroDirectiva;
import com.sv.grupo11.asociacioncomunal.model.Socio;
import com.sv.grupo11.asociacioncomunal.repository.SocioRepository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Implementacion JDBC (MySQL) de SocioRepository.
 * Un socio vive en la tabla "socios"; un MiembroDirectiva ademas tiene una fila en
 * "miembros_directiva" (cargo y periodo). Las dos escrituras van en una transaccion.
 */
public class SocioDAO extends JdbcSupport implements SocioRepository {

    private static final String SELECT_BASE =
            "SELECT s.id, s.nombre, s.dui, s.telefono, s.direccion, s.fecha_ingreso, s.estado, "
          + "m.cargo, m.periodo "
          + "FROM socios s LEFT JOIN miembros_directiva m ON m.id_socio = s.id";

    public SocioDAO(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Socio guardar(Socio socio) {
        int idGenerado = ejecutarEnTransaccion(con -> {
            int id = insertarYObtenerId(con,
                    "INSERT INTO socios (nombre, dui, telefono, direccion, fecha_ingreso, estado) VALUES (?, ?, ?, ?, ?, ?)",
                    socio.getNombre(), socio.getDui(), socio.getTelefono(), socio.getDireccion(),
                    socio.getFechaIngreso(), socio.getEstado());
            if (socio instanceof MiembroDirectiva) {
                MiembroDirectiva m = (MiembroDirectiva) socio;
                insertarYObtenerId(con,
                        "INSERT INTO miembros_directiva (id_socio, cargo, periodo) VALUES (?, ?, ?)",
                        id, m.getCargo().trim().toUpperCase(), m.getPeriodo());
            }
            return id;
        });
        return buscarPorId(idGenerado)
                .orElseThrow(() -> new PersistenciaException("El socio se inserto pero no pudo releerse.", null));
    }

    @Override
    public boolean actualizar(Socio socio) {
        return ejecutarEnTransaccion(con -> {
            int filas;
            try (var ps = con.prepareStatement(
                    "UPDATE socios SET nombre = ?, dui = ?, telefono = ?, direccion = ?, estado = ? WHERE id = ?")) {
                asignarParametros(ps, socio.getNombre(), socio.getDui(), socio.getTelefono(),
                        socio.getDireccion(), socio.getEstado(), socio.getId());
                filas = ps.executeUpdate();
            }
            if (filas > 0 && socio instanceof MiembroDirectiva) {
                MiembroDirectiva m = (MiembroDirectiva) socio;
                try (var ps = con.prepareStatement(
                        "UPDATE miembros_directiva SET cargo = ?, periodo = ? WHERE id_socio = ?")) {
                    asignarParametros(ps, m.getCargo().trim().toUpperCase(), m.getPeriodo(), m.getId());
                    ps.executeUpdate();
                }
            }
            return filas > 0;
        });
    }

    @Override
    public Optional<Socio> buscarPorId(int id) {
        return consultar(SELECT_BASE + " WHERE s.id = ?", this::mapear, id).stream().findFirst();
    }

    @Override
    public Optional<Socio> buscarPorDui(String dui) {
        return consultar(SELECT_BASE + " WHERE s.dui = ?", this::mapear, dui.trim()).stream().findFirst();
    }

    @Override
    public List<Socio> listarTodos() {
        return consultar(SELECT_BASE + " ORDER BY s.id", this::mapear);
    }

    @Override
    public int contar() {
        return consultarEntero("SELECT COUNT(*) FROM socios");
    }

    // Convierte una fila en Socio o MiembroDirectiva segun tenga cargo en la directiva
    private Socio mapear(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nombre = rs.getString("nombre");
        String dui = rs.getString("dui");
        String telefono = rs.getString("telefono");
        String direccion = rs.getString("direccion");
        String cargo = rs.getString("cargo");

        Socio socio = cargo != null
                ? new MiembroDirectiva(id, nombre, dui, telefono, direccion, fecha(rs, "fecha_ingreso"),
                        formatearCargo(cargo), rs.getString("periodo"))
                : new Socio(id, nombre, dui, telefono, direccion, fecha(rs, "fecha_ingreso"));
        socio.setEstado(EstadoMembresia.valueOf(rs.getString("estado")));
        return socio;
    }

    // "PRESIDENTE" -> "Presidente" (como lo muestra el resto del programa)
    private static String formatearCargo(String cargo) {
        return cargo.substring(0, 1).toUpperCase() + cargo.substring(1).toLowerCase();
    }
}
