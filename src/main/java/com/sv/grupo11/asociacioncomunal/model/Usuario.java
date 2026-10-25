package com.sv.grupo11.asociacioncomunal.model;

import com.sv.grupo11.asociacioncomunal.util.PasswordUtil;

import java.io.Serializable;

/**
 * Cuenta de acceso al sistema. El rol determina el menu y las funciones disponibles.
 */
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idUsuario;
    private String nombreUsuario;
    private String contrasenaHash; // nunca se guarda la contrasena en claro
    private Rol rol;
    private Integer idSocio; // solo aplica cuando rol == SOCIO

    /** @param contrasenaHash valor ya protegido con PasswordUtil.hash(...) */
    public Usuario(int idUsuario, String nombreUsuario, String contrasenaHash, Rol rol, Integer idSocio) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.contrasenaHash = contrasenaHash;
        this.rol = rol;
        this.idSocio = idSocio;
    }

    public int getIdUsuario() { return idUsuario; }

    public String getNombreUsuario() { return nombreUsuario; }

    public Rol getRol() { return rol; }

    public Integer getIdSocio() { return idSocio; }

    // Uso exclusivo de la capa DAO para persistir la cuenta; es el hash, no la contrasena
    public String getContrasenaHash() { return contrasenaHash; }

    // La contrasena nunca se expone: solo se valida contra el hash (ENCAPSULAMIENTO)
    public boolean validarContrasena(String intento) {
        return PasswordUtil.verificar(intento, contrasenaHash);
    }

    @Override
    public String toString() {
        return String.format("Usuario #%d | %s | %s%s", idUsuario, nombreUsuario, rol,
                idSocio != null ? " | socio " + idSocio : "");
    }
}
