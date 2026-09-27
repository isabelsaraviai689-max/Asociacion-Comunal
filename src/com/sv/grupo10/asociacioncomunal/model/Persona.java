package com.sv.grupo10.asociacioncomunal.model;

import java.io.Serializable;

/**
 * CLASE ABSTRACTA base de la jerarquia de personas del sistema.
 * No se puede instanciar directamente: en la asociacion nunca existe una
 * "persona" generica, siempre es un Socio o un MiembroDirectiva.
 * Implementa Serializable para poder persistirse en archivos .dat.
 */
public abstract class Persona implements Serializable {

    private static final long serialVersionUID = 1L;

    // ENCAPSULAMIENTO: atributos privados, acceso solo por getters/setters con validacion
    private int id;
    private String nombre;
    private String dui;
    private String telefono;

    protected Persona(int id, String nombre, String dui, String telefono) {
        this.id = id;
        setNombre(nombre);
        setDui(dui);
        setTelefono(telefono);
    }

    public int getId() { return id; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        this.nombre = nombre.trim();
    }

    public String getDui() { return dui; }

    public void setDui(String dui) {
        // Formato de DUI salvadoreno: 8 digitos, guion, 1 digito (########-#)
        if (dui == null || !dui.matches("\\d{8}-\\d")) {
            throw new IllegalArgumentException("El DUI debe tener el formato ########-#");
        }
        this.dui = dui;
    }

    public String getTelefono() { return telefono; }

    public void setTelefono(String telefono) {
        if (telefono == null || !telefono.matches("\\d{4}-\\d{4}")) {
            throw new IllegalArgumentException("El telefono debe tener el formato ####-####");
        }
        this.telefono = telefono;
    }

    // METODOS ABSTRACTOS: cada subclase define su propia version (POLIMORFISMO)
    public abstract String describir();

    public abstract double calcularCuotaMensual();
}
