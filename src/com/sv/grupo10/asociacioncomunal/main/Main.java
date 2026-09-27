package com.sv.grupo10.asociacioncomunal.main;

import com.sv.grupo10.asociacioncomunal.concurrent.ProcesoCierreMensual;
import com.sv.grupo10.asociacioncomunal.controller.AuthController;
import com.sv.grupo10.asociacioncomunal.controller.CuotaController;
import com.sv.grupo10.asociacioncomunal.controller.SocioController;
import com.sv.grupo10.asociacioncomunal.dao.CuotaDAO;
import com.sv.grupo10.asociacioncomunal.dao.SocioDAO;
import com.sv.grupo10.asociacioncomunal.dao.UsuarioDAO;
import com.sv.grupo10.asociacioncomunal.model.Usuario;
import com.sv.grupo10.asociacioncomunal.service.AuthService;
import com.sv.grupo10.asociacioncomunal.service.CuotaService;
import com.sv.grupo10.asociacioncomunal.service.ReporteService;
import com.sv.grupo10.asociacioncomunal.service.SocioService;
import com.sv.grupo10.asociacioncomunal.ui.MenuBase;
import com.sv.grupo10.asociacioncomunal.ui.MenuFactory;
import com.sv.grupo10.asociacioncomunal.util.DatosDemo;

import java.util.Optional;
import java.util.Scanner;

/**
 * Punto de entrada. Aqui se "cablea" la ARQUITECTURA POR CAPAS:
 *   UI (menus) -> Controller -> Service -> DAO -> archivos .dat
 */
public class Main {

    public static void main(String[] args) {
        // Capa de acceso a datos (persistencia .dat)
        SocioDAO socioDAO = new SocioDAO();
        CuotaDAO cuotaDAO = new CuotaDAO();
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        DatosDemo.cargarSiEsNecesario(socioDAO, cuotaDAO, usuarioDAO);

        // Capa de logica de negocio
        AuthService authService = new AuthService(usuarioDAO);
        SocioService socioService = new SocioService(socioDAO);
        CuotaService cuotaService = new CuotaService(cuotaDAO, socioDAO);
        ReporteService reporteService = new ReporteService(socioDAO, cuotaDAO);
        ProcesoCierreMensual cierreMensual = new ProcesoCierreMensual(socioDAO, cuotaDAO, cuotaService);

        // Capa de control
        AuthController authController = new AuthController(authService);
        SocioController socioController = new SocioController(socioService);
        CuotaController cuotaController = new CuotaController(cuotaService, reporteService, cierreMensual);

        // Capa de presentacion
        Scanner sc = new Scanner(System.in);
        System.out.println("==============================================");
        System.out.println("   ASOCIACION COMUNAL - SISTEMA DE ADMINISTRACION");
        System.out.println("   Grupo 10 - Programacion II");
        System.out.println("==============================================");

        while (true) {
            System.out.print("\nUsuario (vacio para salir): ");
            String nombre = sc.nextLine().trim();
            if (nombre.isEmpty()) break;
            System.out.print("Contrasena: ");
            String clave = sc.nextLine().trim();
          
            // Valida las credenciales a traves del controlador de autenticacion
            Optional<Usuario> sesion = authController.login(nombre, clave);
            if (sesion.isEmpty()) {
                // Si los datos no coinciden, muestra el error y vuelve a pedirlos
                System.out.println("[!] Usuario o contrasena incorrectos.");
                continue;
            }
            // POLIMORFISMO: MenuFactory devuelve el menu del rol; aqui solo se conoce MenuBase
            MenuBase menu = MenuFactory.crear(sc, sesion.get(), authController, socioController, cuotaController);
            menu.ejecutar();
        }

        cierreMensual.apagar();
        System.out.println("Sesion finalizada. Datos guardados en data/*.dat");
    }
}
