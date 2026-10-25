package com.sv.grupo11.asociacioncomunal.main;

import com.sv.grupo11.asociacioncomunal.concurrent.ProcesoCierreMensual;
import com.sv.grupo11.asociacioncomunal.config.ConfiguracionBD;
import com.sv.grupo11.asociacioncomunal.config.DataSourceConfig;
import com.sv.grupo11.asociacioncomunal.controller.AuthController;
import com.sv.grupo11.asociacioncomunal.controller.CuotaController;
import com.sv.grupo11.asociacioncomunal.controller.SistemaController;
import com.sv.grupo11.asociacioncomunal.controller.SocioController;
import com.sv.grupo11.asociacioncomunal.dao.CuotaDAO;
import com.sv.grupo11.asociacioncomunal.dao.SocioDAO;
import com.sv.grupo11.asociacioncomunal.dao.UsuarioDAO;
import com.sv.grupo11.asociacioncomunal.exception.ConexionException;
import com.sv.grupo11.asociacioncomunal.model.Usuario;
import com.sv.grupo11.asociacioncomunal.repository.CuotaRepository;
import com.sv.grupo11.asociacioncomunal.repository.SocioRepository;
import com.sv.grupo11.asociacioncomunal.repository.UsuarioRepository;
import com.sv.grupo11.asociacioncomunal.service.AuthService;
import com.sv.grupo11.asociacioncomunal.service.CuotaService;
import com.sv.grupo11.asociacioncomunal.service.ReporteService;
import com.sv.grupo11.asociacioncomunal.service.SocioService;
import com.sv.grupo11.asociacioncomunal.ui.MenuBase;
import com.sv.grupo11.asociacioncomunal.ui.MenuFactory;
import com.sv.grupo11.asociacioncomunal.util.DatosDemo;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.Scanner;

/**
 * Punto de entrada. Aqui se "cablea" la ARQUITECTURA POR CAPAS:
 *   UI (menus) -> Controller -> Service -> Repository (interfaz) -> DAO JDBC -> DataSource (pool) -> MySQL
 * Es el unico lugar que conoce las implementaciones concretas (SocioDAO, CuotaDAO, UsuarioDAO).
 */
public class Main {

    public static void main(String[] args) {
        // 1. Configuracion y pool de conexiones. Si la BD no responde, se informa y no se arranca.
        DataSourceConfig dataSourceConfig;
        try {
            dataSourceConfig = new DataSourceConfig(ConfiguracionBD.cargar());
            dataSourceConfig.verificarConexion();
        } catch (ConexionException e) {
            System.out.println("[!] " + e.getMessage());
            System.out.println("    Revise que MySQL este iniciado, que exista la base 'asociacion_comunal'");
            System.out.println("    (database/schema.sql) y las credenciales en src/main/resources/db.properties.");
            return;
        }
        DataSource dataSource = dataSourceConfig.getDataSource();

        // 2. Capa de acceso a datos: los servicios solo conocen las interfaces
        SocioRepository socios = new SocioDAO(dataSource);
        CuotaRepository cuotas = new CuotaDAO(dataSource);
        UsuarioRepository usuarios = new UsuarioDAO(dataSource);

        // 3. Capa de logica de negocio
        AuthService authService = new AuthService(usuarios);
        SocioService socioService = new SocioService(socios);
        CuotaService cuotaService = new CuotaService(cuotas, socios);
        ReporteService reporteService = new ReporteService(socios, cuotas);
        ProcesoCierreMensual cierreMensual = new ProcesoCierreMensual(socios, cuotas, cuotaService);

        // 4. Capa de control
        AuthController authController = new AuthController(authService);
        SocioController socioController = new SocioController(socioService);
        CuotaController cuotaController = new CuotaController(cuotaService, reporteService, cierreMensual);
        SistemaController sistemaController = new SistemaController(dataSourceConfig);

        try {
            DatosDemo.cargarSiEsNecesario(socios, cuotas, usuarios);

            // 5. Capa de presentacion
            Scanner sc = new Scanner(System.in);
            System.out.println("==============================================");
            System.out.println("   ASOCIACION COMUNAL - SISTEMA DE ADMINISTRACION");
            System.out.println("   Grupo 11 - Programacion II - Entrega final");
            System.out.println("==============================================");
            System.out.println("[i] " + dataSourceConfig.resumenPool());

            while (true) {
                System.out.print("\nUsuario (vacio para salir): ");
                String nombre = sc.nextLine().trim();
                if (nombre.isEmpty()) break;
                System.out.print("Contrasena: ");
                String clave = sc.nextLine().trim();

                Optional<Usuario> sesion = authController.login(nombre, clave);
                if (sesion.isEmpty()) {
                    System.out.println("[!] Usuario o contrasena incorrectos.");
                    continue;
                }
                // POLIMORFISMO: MenuFactory devuelve el menu del rol; aqui solo se conoce MenuBase
                MenuBase menu = MenuFactory.crear(sc, sesion.get(), authController, socioController,
                        cuotaController, sistemaController);
                menu.ejecutar();
            }
        } finally {
            // Pase lo que pase, se detienen los hilos y se cierran las conexiones del pool
            cierreMensual.apagar();
            dataSourceConfig.cerrar();
            System.out.println("Sesion finalizada. Conexiones cerradas.");
        }
    }
}
