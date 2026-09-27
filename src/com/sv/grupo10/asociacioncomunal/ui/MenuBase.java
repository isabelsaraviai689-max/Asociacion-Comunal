package com.sv.grupo10.asociacioncomunal.ui;

import com.sv.grupo10.asociacioncomunal.controller.AuthController;
import com.sv.grupo10.asociacioncomunal.controller.CuotaController;
import com.sv.grupo10.asociacioncomunal.controller.SocioController;
import com.sv.grupo10.asociacioncomunal.dto.ResumenCierreDTO;
import com.sv.grupo10.asociacioncomunal.dto.SocioMorosoDTO;
import com.sv.grupo10.asociacioncomunal.model.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Future;

/**
 * CLASE ABSTRACTA de la capa de presentacion. Contiene el ciclo del menu y todas las
 * acciones de consola; cada menu concreto (por ROL) solo decide cuales expone.
 * POLIMORFISMO: el Main trabaja con MenuBase sin saber que menu concreto es.
 */
public abstract class MenuBase {

    protected final Scanner sc;
    protected final Usuario usuario;
    protected final AuthController authController;
    protected final SocioController socioController;
    protected final CuotaController cuotaController;

    // Future del ultimo cierre mensual lanzado en segundo plano
    private Future<ResumenCierreDTO> cierreEnCurso;

    protected MenuBase(Scanner sc, Usuario usuario, AuthController authController,
                       SocioController socioController, CuotaController cuotaController) {
        this.sc = sc;
        this.usuario = usuario;
        this.authController = authController;
        this.socioController = socioController;
        this.cuotaController = cuotaController;
    }

    // Cada rol define su propio menu (metodos abstractos)
    protected abstract String titulo();
    protected abstract void mostrarOpciones();
    protected abstract void procesarOpcion(int opcion);

    /** Ciclo comun a todos los menus. */
    public void ejecutar() {
        while (true) {
            System.out.println();
            System.out.println("==============================================");
            System.out.println("  " + titulo() + "  |  " + usuario.getNombreUsuario());
            System.out.println("==============================================");
            mostrarOpciones();
            System.out.println("0. Cerrar sesion");
            int opcion = leerEntero("Seleccione una opcion: ");
            if (opcion == 0) return;
            try {
                procesarOpcion(opcion);
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("[!] " + e.getMessage());
            }
        }
    }

    // ---------- Acciones reutilizables (las subclases eligen cuales ofrecer) ----------

    protected void registrarSocioUI() {
        System.out.println("--- Registrar socio ---");
        Socio s = socioController.registrar(leerTexto("Nombre completo: "), leerTexto("DUI (########-#): "),
                leerTexto("Telefono (####-####): "), leerTexto("Direccion: "),
                leerFecha("Fecha de ingreso (AAAA-MM-DD, vacio = hoy): "));
        System.out.println("[OK] Registrado -> " + s.describir());
    }

    protected void registrarDirectivaUI() {
        System.out.println("--- Registrar miembro de junta directiva ---");
        Socio s = socioController.registrarDirectiva(leerTexto("Nombre completo: "), leerTexto("DUI (########-#): "),
                leerTexto("Telefono (####-####): "), leerTexto("Direccion: "),
                leerFecha("Fecha de ingreso (AAAA-MM-DD, vacio = hoy): "),
                leerTexto("Cargo (Presidente/Secretario/Tesorero/Vocal): "), leerTexto("Periodo (ej. 2026-2028): "));
        System.out.println("[OK] Registrado -> " + s.describir());
    }

    protected void actualizarSocioUI() {
        System.out.println("--- Actualizar socio (vacio = no cambiar) ---");
        int id = leerEntero("ID del socio: ");
        Socio s = socioController.actualizar(id, leerTexto("Nuevo telefono: "), leerTexto("Nueva direccion: "));
        System.out.println("[OK] Actualizado -> " + s.describir());
    }

    protected void cambiarEstadoUI() {
        int id = leerEntero("ID del socio: ");
        String op = leerTexto("Nuevo estado (A = activo, I = inactivo): ").toUpperCase();
        EstadoMembresia estado = op.startsWith("A") ? EstadoMembresia.ACTIVO : EstadoMembresia.INACTIVO;
        System.out.println("[OK] " + socioController.cambiarEstado(id, estado).describir());
    }

    protected void buscarSocioUI() {
        String dui = leerTexto("DUI a buscar: ");
        socioController.buscarPorDui(dui).ifPresentOrElse(
                s -> System.out.println("[OK] " + s.describir()),
                () -> System.out.println("[!] No existe socio con DUI " + dui));
    }

    protected void listarSociosUI() {
        List<Socio> socios = socioController.listar();
        System.out.println("--- Padron de socios (" + socios.size() + ") ---");
        // POLIMORFISMO: describir() imprime distinto para Socio y MiembroDirectiva
        for (Socio s : socios) System.out.println(s.describir());
    }

    protected void registrarPagoUI() {
        System.out.println("--- Registrar pago de cuota ---");
        int idSocio = leerEntero("ID del socio: ");
        List<Cuota> pendientes = cuotaController.pendientesDeSocio(idSocio);
        if (pendientes.isEmpty()) { System.out.println("[i] El socio no tiene cuotas pendientes."); return; }
        pendientes.forEach(c -> System.out.println("   " + c));
        int idCuota = leerEntero("ID de la cuota a pagar: ");
        Cuota pagada = cuotaController.registrarPago(idCuota, leerFecha("Fecha de pago (AAAA-MM-DD, vacio = hoy): "));
        System.out.println("[OK] Pago registrado -> " + pagada);
    }

    protected void cuotasPendientesUI(int idSocio) {
        List<Cuota> pendientes = cuotaController.pendientesDeSocio(idSocio);
        System.out.println("--- Cuotas pendientes del socio #" + idSocio + " (" + pendientes.size() + ") ---");
        pendientes.forEach(System.out::println);
        double total = pendientes.stream().mapToDouble(Cuota::getMonto).sum();
        System.out.printf("Total pendiente: $%.2f%n", total);
    }

    protected void historialUI(int idSocio) {
        List<Cuota> historial = cuotaController.historialDeSocio(idSocio);
        System.out.println("--- Historial de cuotas del socio #" + idSocio + " (" + historial.size() + ") ---");
        historial.forEach(System.out::println);
    }

    protected void reporteMorososUI() {
        List<SocioMorosoDTO> morosos = cuotaController.reporteMorosos();
        System.out.println("--- Reporte de morosos (" + morosos.size() + ") ---");
        System.out.printf("%-4s %-30s %-11s %-12s %s%n", "ID", "Nombre", "DUI", "Vencidas", "Monto");
        morosos.forEach(System.out::println);
        System.out.printf("Total en mora: $%.2f%n", morosos.stream().mapToDouble(SocioMorosoDTO::getMontoPendiente).sum());
    }

    protected void cierreMensualUI() {
        if (cierreEnCurso != null && !cierreEnCurso.isDone()) {
            System.out.println("[!] Ya hay un cierre en ejecucion. Consulte su estado.");
            return;
        }
        String periodo = leerTexto("Periodo a cerrar (AAAA-MM, vacio = mes actual): ");
        if (periodo.isBlank()) periodo = YearMonth.now().toString();
        YearMonth.parse(periodo); // valida formato
        LocalDate corte = leerFecha("Fecha de corte para morosidad (AAAA-MM-DD, vacio = hoy): ");
        if (corte == null) corte = LocalDate.now();
        cierreEnCurso = cuotaController.ejecutarCierreMensual(periodo, corte);
        System.out.println("[OK] Cierre " + periodo + " lanzado en segundo plano. Puede seguir trabajando.");
    }

    protected void estadoCierreUI() {
        if (cierreEnCurso == null) { System.out.println("[i] No se ha lanzado ningun cierre."); return; }
        if (!cierreEnCurso.isDone()) { System.out.println("[i] El cierre sigue en ejecucion..."); return; }
        try {
            System.out.println("[OK] " + cierreEnCurso.get());
        } catch (Exception e) {
            System.out.println("[!] El cierre fallo: " + e.getCause().getMessage());
        }
    }

    protected void crearUsuarioUI() {
        System.out.println("--- Crear usuario ---");
        String nombre = leerTexto("Nombre de usuario: ");
        String clave = leerTexto("Contrasena (min. 6): ");
        Rol rol = Rol.valueOf(leerTexto("Rol (ADMINISTRADOR/SECRETARIO/TESORERO/SOCIO): ").trim().toUpperCase());
        Integer idSocio = rol == Rol.SOCIO ? leerEntero("ID del socio asociado: ") : null;
        System.out.println("[OK] " + authController.crearUsuario(nombre, clave, rol, idSocio));
    }

    protected void listarUsuariosUI() {
        authController.listarUsuarios().forEach(System.out::println);
    }

    // ---------- Lectura validada de consola ----------

    protected String leerTexto(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    protected int leerEntero(String prompt) {
        while (true) {
            System.out.print(prompt);
            String linea = sc.nextLine().trim();
            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.println("[!] Ingrese un numero entero.");
            }
        }
    }

    protected LocalDate leerFecha(String prompt) {
        while (true) {
            String texto = leerTexto(prompt);
            if (texto.isBlank()) return null;
            try {
                return LocalDate.parse(texto);
            } catch (DateTimeParseException e) {
                System.out.println("[!] Formato de fecha invalido (AAAA-MM-DD).");
            }
        }
    }
}
