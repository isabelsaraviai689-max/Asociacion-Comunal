# Sistema para Administración de Asociación Comunal — Grupo 10 (Avance 2)

Programación II · UEES · Facilitador: Ing. Daniel Enrique Guevara Gómez

## Tecnología
Java 21 (Opción 0 de la tabla): aplicación de consola, arquitectura por capas
y persistencia en archivos `.dat` (sin base de datos en este avance).

## Compilar y ejecutar
```bash
javac -d out $(find src -name "*.java")          # Linux/macOS
java -cp out com.sv.grupo10.asociacioncomunal.main.Main
```
En Windows usar `run.bat` (compila y ejecuta). En IntelliJ: abrir la carpeta, marcar `src` como
Sources Root y ejecutar `Main`.

La primera ejecución crea `data/` con datos de demostración. Usuarios de prueba:

| Usuario     | Contraseña | Rol           |
|-------------|------------|---------------|
| admin       | admin123   | Administrador |
| secretario  | secre123   | Secretario    |
| tesorero    | tesoro123  | Tesorero      |
| jramirez    | socio123   | Socio         |

## Estructura (paquete `com.sv.grupo10.asociacioncomunal`)
| Paquete      | Contenido |
|--------------|-----------|
| `model`      | `Persona` (abstracta) → `Socio` → `MiembroDirectiva`; `Cuota`; `Usuario`; enums |
| `dto`        | `SocioMorosoDTO`, `ResumenCierreDTO` |
| `dao`        | `ArchivoDAO<T>` (abstracta, persistencia .dat) → `SocioDAO`, `CuotaDAO`, `UsuarioDAO` |
| `service`    | `AuthService`, `SocioService`, `CuotaService`, `ReporteService` |
| `concurrent` | `GeneracionCuotasTask`, `AuditoriaMorosidadTask`, `ProcesoCierreMensual` |
| `controller` | `AuthController`, `SocioController`, `CuotaController` |
| `ui`         | `MenuBase` (abstracta) → un menú por rol; `MenuFactory` |
| `util`       | `DatosDemo` (carga inicial) |
| `main`       | `Main` (cableado de capas) |

## Mapa de la rúbrica
| Requisito | Dónde |
|---|---|
| Encapsulamiento | atributos privados + setters con validación en `model/` |
| Herencia | `Persona→Socio→MiembroDirectiva`, `ArchivoDAO→DAOs`, `MenuBase→menús` |
| Polimorfismo | `calcularCuotaMensual()`, `describir()`, `MenuBase.ejecutar()` |
| Concurrencia | `concurrent/ProcesoCierreMensual` (Callable + Future + ExecutorService) |
| Clases abstractas | `Persona`, `ArchivoDAO<T>`, `MenuBase` |
| Colecciones | `ArrayList` en DAO, `HashMap` en `ReporteService`, `List<Socio>` polimórfica |
| Capas | `ui → controller → service → dao → .dat` |
| Persistencia .dat | `dao/ArchivoDAO` (ObjectOutputStream / ObjectInputStream) |
| Git | commits por integrante según funcionalidad; documento en la raíz |

## Documento del avance
`AVANCE2_DOCUMENTO_GRUPO_10.docx` / `.pdf` (versionados en la raíz del repositorio).
