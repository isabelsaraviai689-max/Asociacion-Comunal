# Sistema para Administración de Asociación Comunal — Grupo 11

Proyecto final de **Programación II** · Universidad Evangélica de El Salvador · Facilitador: Ing. Daniel Enrique Guevara Gómez

Aplicación Java de consola, con arquitectura por capas, persistencia en MySQL mediante JDBC con pool de conexiones
(DataSource), proceso concurrente de cierre mensual integrado con la base de datos y pruebas unitarias con JUnit 5 y Mockito.

## Integrantes

| Integrante | Rol Scrum | Rol técnico |
|---|---|---|
| Isabel Uvaldina Ramos Saravia | Scrum Master / Product Owner | Desarrolladora — Git, documento, migración JDBC |
| Luis Alonso Alvarenga Meza | Architect | Backend — capas, pool, concurrencia, excepciones, pruebas |
| Camila Nicole Salvador Samayoa | Developer | Base de datos — modelo, esquema, DAO |
| Perla Esmeralda López Barrientos | Business Analyst / Developer | Fullstack — historias de usuario, controladores, menús |
| Diego Emanuel Escobar Hernández | QA | Pruebas funcionales, evidencias |

## Tecnologías

- Java 17+ (compila con JDK 17 o 21)
- MySQL 8.0 (driver `mysql-connector-j` 8.4.0)
- Apache Tomcat JDBC Pool 10.1 (DataSource / pool de conexiones)
- JUnit 5.10 y Mockito 5.11 (pruebas unitarias)
- Maven 3 (gestión de dependencias y construcción)

## Requisitos para ejecutar

1. JDK 17 o superior ([Temurin](https://adoptium.net)).
2. MySQL Server 8 en `localhost:3306` (MySQL Workbench es opcional).
3. Maven 3.8+ **o** IntelliJ IDEA (que trae Maven integrado; no hace falta instalarlo aparte).

## Configuración de la base de datos

1. Ejecutar `database/schema.sql` (crea la base `asociacion_comunal` y sus cuatro tablas).
2. Ejecutar `database/data.sql` (5 socios, 10 cuotas y 4 usuarios de prueba con contraseñas hasheadas).
   Desde Workbench: *File → Open SQL Script* y ejecutar; desde terminal:
   ```bash
   mysql -u root -p < database/schema.sql
   mysql -u root -p < database/data.sql
   ```
3. Credenciales: editar `src/main/resources/db.properties` (`db.usuario`, `db.clave`) **o** definir las variables
   de entorno `DB_URL`, `DB_USER`, `DB_PASSWORD`, que tienen prioridad sobre el archivo.

Si la base queda vacía (solo `schema.sql`), el sistema carga los mismos datos de demostración en el primer arranque.

## Cómo ejecutar

**Con el script** (compila, empaqueta y arranca):
```bash
./run.sh          # Linux / macOS
run.bat           # Windows
```
**Con Maven directamente:**
```bash
mvn -q clean package -DskipTests
java -jar target/asociacion-comunal.jar
```
**Desde IntelliJ IDEA:** *File → Open* sobre la carpeta (reconoce el `pom.xml`), esperar a que descargue dependencias
y ejecutar `com.sv.grupo11.asociacioncomunal.main.Main`.

## Cómo ejecutar las pruebas

```bash
mvn test
```
55 pruebas en 12 clases (modelo, utilidades, servicios, controladores, tarea concurrente y soporte JDBC). Las dependencias
externas (repositorios JDBC, servicios) se aíslan con Mockito, por lo que las pruebas **no necesitan MySQL**.
En IntelliJ: clic derecho sobre `src/test/java` → *Run 'All Tests'*. El reporte queda en `target/surefire-reports/`.

## Usuarios de prueba

| Usuario | Contraseña | Rol | Qué puede hacer |
|---|---|---|---|
| admin | admin123 | Administrador | Todo, incluido crear/eliminar usuarios y ver el estado del pool |
| secretario | secre123 | Secretario | Padrón de socios y junta directiva |
| tesorero | tesoro123 | Tesorero | Pagos, pendientes, morosos y cierre mensual |
| jramirez | socio123 | Socio | Solo su propia información |

Formatos: DUI `########-#`, teléfono `####-####`, fechas `AAAA-MM-DD`, periodo `AAAA-MM`.

## Descripción de la concurrencia

El **cierre mensual** (menú de Tesorero/Administrador) genera la cuota del periodo a todos los socios activos y, en
paralelo, audita las cuotas pendientes para marcar como vencidas las que superaron su fecha límite. Son dos recorridos
independientes (INSERT sobre cuotas nuevas vs UPDATE sobre cuotas viejas), por lo que `ProcesoCierreMensual` los envía como
dos `Callable` a un `ExecutorService` de 3 hilos (1 orquestador + 2 tareas) y devuelve de inmediato un
`Future<ResumenCierreDTO>`: el usuario sigue trabajando y consulta el resultado con "Ver estado del cierre".

Integración con la base de datos: cada tarea obtiene sus conexiones del **pool** (`DataSourceConfig`) a través de los DAO y
las devuelve al terminar; nunca comparten una `Connection`. Control de problemas de concurrencia: los ids los asigna MySQL
(`AUTO_INCREMENT` + `RETURN_GENERATED_KEYS`), la restricción `UNIQUE (id_socio, periodo)` impide cuotas duplicadas aunque
dos hilos inserten a la vez (`RegistroDuplicadoException`, que la tarea trata como "omitida"), cada escritura va en su
propia transacción, y cualquier `PersistenciaException` dentro de un hilo se propaga al `Future` y la UI la reporta.

## Estructura del proyecto

```
├── src/main/java/com/sv/grupo11/asociacioncomunal/
│   ├── config/       ConfiguracionBD, DataSourceConfig (pool Tomcat JDBC)
│   ├── controller/   AuthController, SocioController, CuotaController, SistemaController
│   ├── service/      AuthService, SocioService, CuotaService, ReporteService
│   ├── concurrent/   ProcesoCierreMensual, GeneracionCuotasTask, AuditoriaMorosidadTask
│   ├── repository/   SocioRepository, CuotaRepository, UsuarioRepository (interfaces)
│   ├── dao/          JdbcSupport (abstracta, genérica), SocioDAO, CuotaDAO, UsuarioDAO
│   ├── model/        Persona (abstracta) → Socio → MiembroDirectiva; Cuota; Usuario; enums
│   ├── dto/          SocioMorosoDTO, ResumenCierreDTO
│   ├── exception/    AsociacionException y sus 6 especializaciones
│   ├── util/         PasswordUtil (PBKDF2), DatosDemo
│   ├── ui/           MenuBase (abstracta) y un menú por rol, MenuFactory
│   └── main/         Main (cableado de capas)
├── src/main/resources/db.properties
├── src/test/java/...            55 pruebas JUnit 5 + Mockito
├── database/                    schema.sql, data.sql, reset.sql
├── docs/                        documento-final.pdf, uml.png, arquitectura.png
├── pom.xml · run.sh · run.bat
```

## Repositorio

https://github.com/isabelsaraviai689-max/Asociacion-Comunal
