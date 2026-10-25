package com.sv.grupo11.asociacioncomunal.config;

import com.sv.grupo11.asociacioncomunal.exception.ConexionException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Lee la configuracion de la base de datos y del pool desde db.properties (classpath).
 * Las credenciales pueden sobreescribirse con variables de entorno (DB_URL, DB_USER, DB_PASSWORD)
 * para no tener contrasenas escritas en el codigo fuente.
 */
public final class ConfiguracionBD {

    private static final String ARCHIVO = "db.properties";

    private final Properties propiedades;

    private ConfiguracionBD(Properties propiedades) {
        this.propiedades = propiedades;
    }

    public static ConfiguracionBD cargar() {
        Properties props = new Properties();
        try (InputStream in = ConfiguracionBD.class.getClassLoader().getResourceAsStream(ARCHIVO)) {
            if (in == null) {
                throw new ConexionException("No se encontro " + ARCHIVO + " en el classpath.", null);
            }
            props.load(in);
        } catch (IOException e) {
            throw new ConexionException("No se pudo leer " + ARCHIVO + ": " + e.getMessage(), e);
        }
        return new ConfiguracionBD(props);
    }

    public static ConfiguracionBD desde(Properties propiedades) {
        return new ConfiguracionBD(propiedades);
    }

    public String getUrl() { return obtener("db.url", "DB_URL"); }

    public String getUsuario() { return obtener("db.usuario", "DB_USER"); }

    public String getClave() { return obtener("db.clave", "DB_PASSWORD"); }

    public String texto(String clave, String porDefecto) {
        return propiedades.getProperty(clave, porDefecto);
    }

    public int entero(String clave, int porDefecto) {
        try {
            return Integer.parseInt(propiedades.getProperty(clave, String.valueOf(porDefecto)).trim());
        } catch (NumberFormatException e) {
            return porDefecto;
        }
    }

    public boolean logico(String clave, boolean porDefecto) {
        return Boolean.parseBoolean(propiedades.getProperty(clave, String.valueOf(porDefecto)).trim());
    }

    private String obtener(String clave, String variableEntorno) {
        String valorEntorno = System.getenv(variableEntorno);
        return (valorEntorno != null && !valorEntorno.isBlank()) ? valorEntorno : propiedades.getProperty(clave, "");
    }
}
