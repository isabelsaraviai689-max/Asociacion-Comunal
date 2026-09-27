package com.sv.grupo10.asociacioncomunal.model;

import java.io.Serializable;

/**
 * Cuenta de acceso al sistema. El rol determina el menu y las funciones disponibles.
 */
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idUsuario;
    private String nombreUsuario;
    private String contrasena;
    private Rol rol;
    private Integer idSocio; // solo aplica cuando rol == SOCIO

    public Usuario(int idUsuario, String nombreUsuario, String contrasena, Rol rol, Integer idSocio) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.rol = rol;
        this.idSocio = idSocio;
    }

    public int getIdUsuario() { return idUsuario; }

    public String getNombreUsuario() { return nombreUsuario; }

    public Rol getRol() { return rol; }

    public Integer getIdSocio() { return idSocio; }

    // La contrasena nunca se expone: solo se valida (ENCAPSULAMIENTO)
    public boolean validarContrasena(String intento) {
        return contrasena != null && contrasena.equals(intento);
    }

    @Override
    public String toString() {
        return String.format("Usuario #%d | %s | %s%s", idUsuario, nombreUsuario, rol,
                idSocio != null ? " | socio " + idSocio : "");
    }
}
