package com.sv.grupo11.asociacioncomunal.dao;

import com.sv.grupo11.asociacioncomunal.exception.ConexionException;
import com.sv.grupo11.asociacioncomunal.exception.PersistenciaException;
import com.sv.grupo11.asociacioncomunal.exception.RegistroDuplicadoException;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * CLASE ABSTRACTA base de los DAO JDBC. Concentra lo que antes estaba triplicado en
 * SocioDAO, CuotaDAO y UsuarioDAO: obtener la conexion del pool, asignar parametros,
 * ejecutar consultas y actualizaciones, manejar transacciones y traducir SQLException
 * a las excepciones propias del sistema.
 *
 * GENERICOS: consultar(...) devuelve List<T> para cualquier entidad, a partir de un
 * Mapeador<T> que cada DAO concreto aporta (como obtenerId() en el antiguo ArchivoDAO<T>).
 *
 * CONCURRENCIA: no hay estado compartido ni metodos synchronized; cada llamada toma su
 * propia conexion del pool y la devuelve al terminar, por lo que varios hilos pueden
 * trabajar a la vez y la consistencia la garantizan la BD (UNIQUE, FK) y las transacciones.
 */
public abstract class JdbcSupport {

    protected final DataSource dataSource;

    protected JdbcSupport(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "Se requiere un DataSource");
    }

    /** Convierte una fila del ResultSet en una entidad. Cada DAO concreto define el suyo. */
    @FunctionalInterface
    protected interface Mapeador<T> {
        T mapear(ResultSet rs) throws SQLException;
    }

    /** Unidad de trabajo que se ejecuta dentro de una transaccion. */
    @FunctionalInterface
    protected interface OperacionTransaccional<T> {
        T ejecutar(Connection con) throws SQLException;
    }

    protected Connection abrirConexion() {
        try {
            return dataSource.getConnection(); // prestada por el pool
        } catch (SQLException e) {
            throw new ConexionException("No se pudo obtener una conexion del pool: " + e.getMessage(), e);
        }
    }

    /** SELECT generico: executeQuery() + ResultSet mapeado a List<T>. */
    protected <T> List<T> consultar(String sql, Mapeador<T> mapeador, Object... parametros) {
        List<T> resultado = new ArrayList<>();
        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            asignarParametros(ps, parametros);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapeador.mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw traducir("consultar", e);
        }
        return resultado;
    }

    /** SELECT de un unico valor entero (COUNT, MAX...). */
    protected int consultarEntero(String sql, Object... parametros) {
        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            asignarParametros(ps, parametros);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw traducir("consultar", e);
        }
    }

    /** INSERT/UPDATE/DELETE con executeUpdate(); devuelve las filas afectadas. */
    protected int ejecutarActualizacion(String sql, Object... parametros) {
        try (Connection con = abrirConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            asignarParametros(ps, parametros);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw traducir("actualizar", e);
        }
    }

    /** INSERT que devuelve el id generado por AUTO_INCREMENT. */
    protected int insertarYObtenerId(Connection con, String sql, Object... parametros) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignarParametros(ps, parametros);
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (!claves.next()) {
                    throw new SQLException("La base de datos no devolvio el id generado.");
                }
                return claves.getInt(1);
            }
        }
    }

    /**
     * Ejecuta varias sentencias como una sola transaccion: si algo falla se hace rollback.
     * El finally garantiza que la conexion vuelva al pool con autocommit restaurado,
     * aun cuando la operacion lance una excepcion.
     */
    protected <T> T ejecutarEnTransaccion(OperacionTransaccional<T> operacion) {
        Connection con = abrirConexion();
        try {
            con.setAutoCommit(false);
            T resultado = operacion.ejecutar(con);
            con.commit();
            return resultado;
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ignorada) {
                // el rollback fallo porque la conexion ya no sirve; se reporta la causa original
            }
            throw traducir("guardar", e);
        } finally {
            try {
                con.setAutoCommit(true);
                con.close(); // regresa al pool
            } catch (SQLException e) {
                System.err.println("[dao] No se pudo devolver la conexion al pool: " + e.getMessage());
            }
        }
    }

    /** Asigna parametros convirtiendo los tipos de Java a los de JDBC. */
    protected void asignarParametros(PreparedStatement ps, Object... parametros) throws SQLException {
        for (int i = 0; i < parametros.length; i++) {
            Object valor = parametros[i];
            int indice = i + 1;
            if (valor == null) {
                ps.setNull(indice, Types.NULL);
            } else if (valor instanceof LocalDate) {
                ps.setDate(indice, Date.valueOf((LocalDate) valor));
            } else if (valor instanceof Enum) {
                ps.setString(indice, ((Enum<?>) valor).name());
            } else {
                ps.setObject(indice, valor);
            }
        }
    }

    /** Traduce la SQLException a la excepcion propia que corresponde. */
    protected PersistenciaException traducir(String accion, SQLException e) {
        if (e instanceof SQLIntegrityConstraintViolationException) {
            return new RegistroDuplicadoException("Registro rechazado por la base de datos: " + e.getMessage(), e);
        }
        return new PersistenciaException("Error al " + accion + " en la base de datos: " + e.getMessage(), e);
    }

    protected static LocalDate fecha(ResultSet rs, String columna) throws SQLException {
        Date d = rs.getDate(columna);
        return d != null ? d.toLocalDate() : null;
    }
}
