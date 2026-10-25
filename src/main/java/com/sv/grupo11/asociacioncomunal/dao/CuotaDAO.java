package com.sv.grupo11.asociacioncomunal.dao;

import com.sv.grupo11.asociacioncomunal.exception.PersistenciaException;
import com.sv.grupo11.asociacioncomunal.model.Cuota;
import com.sv.grupo11.asociacioncomunal.model.EstadoCuota;
import com.sv.grupo11.asociacioncomunal.repository.CuotaRepository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** Implementacion JDBC (MySQL) de CuotaRepository. */
public class CuotaDAO extends JdbcSupport implements CuotaRepository {

    private static final String SELECT_BASE =
            "SELECT id_cuota, id_socio, monto, periodo, fecha_vencimiento, fecha_pago, estado FROM cuotas";

    public CuotaDAO(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Cuota guardar(Cuota cuota) {
        // La restriccion UNIQUE (id_socio, periodo) rechaza duplicados aunque dos hilos inserten a la vez
        int id = ejecutarEnTransaccion(con -> insertarYObtenerId(con,
                "INSERT INTO cuotas (id_socio, monto, periodo, fecha_vencimiento, fecha_pago, estado) VALUES (?, ?, ?, ?, ?, ?)",
                cuota.getIdSocio(), cuota.getMonto(), cuota.getPeriodo(), cuota.getFechaVencimiento(),
                cuota.getFechaPago(), cuota.getEstado()));
        return buscarPorId(id)
                .orElseThrow(() -> new PersistenciaException("La cuota se inserto pero no pudo releerse.", null));
    }

    @Override
    public boolean actualizar(Cuota cuota) {
        return ejecutarActualizacion(
                "UPDATE cuotas SET id_socio = ?, monto = ?, periodo = ?, fecha_vencimiento = ?, fecha_pago = ?, estado = ? WHERE id_cuota = ?",
                cuota.getIdSocio(), cuota.getMonto(), cuota.getPeriodo(), cuota.getFechaVencimiento(),
                cuota.getFechaPago(), cuota.getEstado(), cuota.getIdCuota()) > 0;
    }

    @Override
    public Optional<Cuota> buscarPorId(int id) {
        return consultar(SELECT_BASE + " WHERE id_cuota = ?", this::mapear, id).stream().findFirst();
    }

    @Override
    public List<Cuota> listarTodos() {
        return consultar(SELECT_BASE + " ORDER BY id_cuota", this::mapear);
    }

    @Override
    public List<Cuota> listarPorSocio(int idSocio) {
        return consultar(SELECT_BASE + " WHERE id_socio = ? ORDER BY periodo", this::mapear, idSocio);
    }

    // Pendiente = cualquier cuota que no este pagada (PENDIENTE o VENCIDA)
    @Override
    public List<Cuota> listarPendientesPorSocio(int idSocio) {
        return consultar(SELECT_BASE + " WHERE id_socio = ? AND estado <> 'PAGADA' ORDER BY periodo", this::mapear, idSocio);
    }

    @Override
    public List<Cuota> listarPorEstado(EstadoCuota estado) {
        return consultar(SELECT_BASE + " WHERE estado = ? ORDER BY id_cuota", this::mapear, estado);
    }

    @Override
    public boolean existeCuota(int idSocio, String periodo) {
        return consultarEntero("SELECT COUNT(*) FROM cuotas WHERE id_socio = ? AND periodo = ?", idSocio, periodo) > 0;
    }

    @Override
    public int contar() {
        return consultarEntero("SELECT COUNT(*) FROM cuotas");
    }

    private Cuota mapear(ResultSet rs) throws SQLException {
        return new Cuota(
                rs.getInt("id_cuota"),
                rs.getInt("id_socio"),
                rs.getDouble("monto"),
                rs.getString("periodo"),
                fecha(rs, "fecha_vencimiento"),
                fecha(rs, "fecha_pago"),
                EstadoCuota.valueOf(rs.getString("estado")));
    }
}
