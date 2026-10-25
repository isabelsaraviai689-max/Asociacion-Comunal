package com.sv.grupo11.asociacioncomunal.config;

import com.sv.grupo11.asociacioncomunal.exception.ConexionException;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * CONFIGURACION DEL POOL DE CONEXIONES (Tomcat JDBC Pool).
 *
 * Abrir una conexion a MySQL cuesta decenas de milisegundos (handshake TCP, autenticacion).
 * El pool mantiene conexiones ya abiertas y las presta; cada DAO pide una, la usa y la devuelve
 * (close() en try-with-resources no la cierra, la regresa al pool).
 *
 * Los valores se cargan desde db.properties y se justifican para esta aplicacion:
 *  - Un solo usuario en consola + el cierre mensual con 3 hilos (orquestador + 2 tareas):
 *    con maxActive = 5 nunca hay espera y queda margen; initialSize/minIdle = 2 evita abrir
 *    conexiones durante el login.
 *  - validationQuery "SELECT 1" + testOnBorrow: cada conexion prestada se prueba antes de usarse,
 *    asi una conexion que MySQL cerro por inactividad no llega a un DAO.
 *  - validationInterval = 30 s: no se vuelve a validar una conexion probada hace menos de 30 s
 *    (evita un SELECT 1 por cada consulta).
 *  - testWhileIdle + timeBetweenEvictionRunsMillis = 30 s + minEvictableIdleTimeMillis = 60 s:
 *    un hilo del pool revisa cada 30 s y cierra conexiones ociosas mas de 1 minuto, manteniendo
 *    minIdle; MySQL por defecto cierra conexiones inactivas a las 8 h, asi que 60 s es seguro.
 *  - removeAbandoned + removeAbandonedTimeout = 60 s: si un DAO olvidara cerrar una conexion
 *    (fuga), el pool la recupera tras 60 s en vez de agotarse.
 *  - maxWait = 5000 ms: si el pool esta lleno, el hilo espera a lo sumo 5 s y luego falla con
 *    una excepcion clara en vez de colgarse.
 *  - testOnReturn = false: validar al devolver duplicaria el trabajo de testOnBorrow.
 */
public class DataSourceConfig {

    private final DataSource dataSource;

    public DataSourceConfig(ConfiguracionBD config) {
        PoolProperties p = new PoolProperties();
        p.setDriverClassName("com.mysql.cj.jdbc.Driver");
        p.setUrl(config.getUrl());
        p.setUsername(config.getUsuario());
        p.setPassword(config.getClave());

        p.setValidationQuery(config.texto("pool.validationQuery", "SELECT 1"));
        p.setInitialSize(config.entero("pool.initialSize", 2));
        p.setMinIdle(config.entero("pool.minIdle", 2));
        p.setMaxIdle(config.entero("pool.maxIdle", 5));
        p.setMaxActive(config.entero("pool.maxActive", 5));
        p.setMaxWait(config.entero("pool.maxWait", 5000));
        p.setTestOnBorrow(config.logico("pool.testOnBorrow", true));
        p.setTestOnReturn(config.logico("pool.testOnReturn", false));
        p.setTestWhileIdle(config.logico("pool.testWhileIdle", true));
        p.setTestOnConnect(config.logico("pool.testOnConnect", true));
        p.setTimeBetweenEvictionRunsMillis(config.entero("pool.timeBetweenEvictionRunsMillis", 30_000));
        p.setMinEvictableIdleTimeMillis(config.entero("pool.minEvictableIdleTimeMillis", 60_000));
        p.setRemoveAbandoned(config.logico("pool.removeAbandoned", true));
        p.setRemoveAbandonedTimeout(config.entero("pool.removeAbandonedTimeout", 60));
        p.setValidationInterval(config.entero("pool.validationInterval", 30_000));

        this.dataSource = new DataSource(p);
    }

    public javax.sql.DataSource getDataSource() {
        return dataSource;
    }

    /** Prueba una conexion al arrancar: si la BD no responde, el sistema avisa y no inicia. */
    public void verificarConexion() {
        try (Connection con = dataSource.getConnection()) {
            if (!con.isValid(3)) {
                throw new ConexionException("La base de datos no respondio a la validacion.", null);
            }
        } catch (SQLException e) {
            throw new ConexionException("No se pudo conectar a la base de datos: " + e.getMessage(), e);
        }
    }

    /** Estado actual del pool (util para demostrar que las conexiones se reutilizan). */
    public String resumenPool() {
        return String.format("Pool: activas=%d, inactivas=%d, total=%d, max=%d",
                dataSource.getActive(), dataSource.getIdle(), dataSource.getSize(), dataSource.getMaxActive());
    }

    public void cerrar() {
        dataSource.close();
    }
}
