package com.sv.grupo10.asociacioncomunal.dao;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * CLASE ABSTRACTA + GENERICOS: DAO base que resuelve la PERSISTENCIA EN ARCHIVOS .dat
 * una sola vez para todas las entidades. Cada DAO concreto solo indica el nombre
 * del archivo y como obtener el id de su entidad.
 *
 * COLECCIONES: los registros viven en un ArrayList en memoria (lista de trabajo)
 * y cada cambio se serializa completo al archivo .dat (fuente de datos).
 *
 * CONCURRENCIA: todos los metodos que tocan la lista o el archivo son synchronized,
 * porque el cierre mensual corre en hilos aparte mientras la UI sigue registrando.
 */
public abstract class ArchivoDAO<T extends Serializable> {

    private static final String CARPETA_DATOS = "data";

    private final Path rutaArchivo;
    protected final List<T> registros = new ArrayList<>();

    protected ArchivoDAO(String nombreArchivo) {
        this.rutaArchivo = Paths.get(CARPETA_DATOS, nombreArchivo);
        cargar();
    }

    // Cada subclase sabe cual es el identificador de su entidad
    protected abstract int obtenerId(T entidad);

    @SuppressWarnings("unchecked")
    private synchronized void cargar() {
        if (!Files.exists(rutaArchivo)) {
            return; // primera ejecucion: el archivo se crea al guardar
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(rutaArchivo.toFile()))) {
            registros.addAll((List<T>) in.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("No se pudo leer " + rutaArchivo + ": " + e.getMessage(), e);
        }
    }

    protected synchronized void persistir() {
        try {
            Files.createDirectories(rutaArchivo.getParent());
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(rutaArchivo.toFile()))) {
                out.writeObject(new ArrayList<>(registros));
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo escribir " + rutaArchivo + ": " + e.getMessage(), e);
        }
    }

    public synchronized void guardar(T entidad) {
        registros.add(entidad);
        persistir();
    }

    public synchronized boolean actualizar(T entidad) {
        int id = obtenerId(entidad);
        for (int i = 0; i < registros.size(); i++) {
            if (obtenerId(registros.get(i)) == id) {
                registros.set(i, entidad);
                persistir();
                return true;
            }
        }
        return false;
    }

    public synchronized Optional<T> buscarPorId(int id) {
        return registros.stream().filter(r -> obtenerId(r) == id).findFirst();
    }

    // Se devuelve una copia: la lista interna nunca sale del DAO (ENCAPSULAMIENTO)
    public synchronized List<T> listarTodos() {
        return new ArrayList<>(registros);
    }

    public synchronized int siguienteId() {
        return registros.stream().mapToInt(this::obtenerId).max().orElse(0) + 1;
    }

    public synchronized int contar() {
        return registros.size();
    }

    public Path getRutaArchivo() {
        return rutaArchivo;
    }
}
