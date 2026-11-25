# L2MADevs — Gestión de solicitudes técnicas

Proyecto universitario de **Java Swing** para administrar solicitudes de soporte/proyectos eléctricos: alta de clientes y solicitudes, asignación de técnicos, cambio de estados e historial persistido.

Este repositorio es una versión preparada para **portafolio**: conserva la arquitectura original (Swing + DAO + NetBeans/Ant) pero corrige errores funcionales, externaliza la configuración, añade seguridad básica, transacciones, pruebas automatizadas y documentación.

## Tecnologías

| Capa | Tecnología |
| --- | --- |
| Interfaz | Java Swing (cards, `JTable`-style panels, `SwingWorker`) |
| Negocio | Controladores `Gestor` y `Tecnico`, servicio `SolicitudService` |
| Persistencia | DAO + JDBC (`mysql-connector-j-9.4.0.jar`) |
| Base de datos | MySQL / MariaDB (`adte`) |
| Build | NetBeans/Ant (`build.xml`, `nbproject/`) + scripts `.bat` |
| Pruebas | JUnit 4.13.2 + Hamcrest 1.3 |
| Lenguaje | Java 17+ (probado con JDK 25) |

## Estructura

```text
L2MADevs-Portfolio/
├── database/adte.sql          # Esquema corregido + datos de ejemplo
├── docs/
│   ├── arquitectura.md        # Diagramas Mermaid y responsabilidades
│   └── configuracion.md       # Instalación de BD y variables de entorno
├── lib/
│   ├── mysql-connector-j-9.4.0.jar
│   └── test/                  # JUnit y Hamcrest
├── scripts/
│   ├── compilar.bat
│   ├── ejecutar.bat
│   └── probar.bat
├── src/l2madevs/
│   ├── config/                # Configuración desde propiedades/env
│   ├── controlador/           # Gestor, Tecnico, Login
│   ├── dao/                   # Acceso a datos
│   ├── modelo/                # Entidades POJO
│   ├── seguridad/             # PasswordHasher (PBKDF2)
│   ├── servicio/              # Operaciones transaccionales
│   ├── util/                  # Constantes y validaciones puras
│   └── vista/                 # Ventanas, paneles y utilidades Swing
├── test/tests/                # Pruebas unitarias JUnit 4
├── build.xml
└── README.md
```

## Puesta en marcha

### 1. Requisitos

- JDK 17 o superior.
- MySQL o MariaDB en ejecución.
- NetBeans 27 (opcional; también se puede compilar con los scripts).
- Git (solo si quieres clonar/subir el repositorio).

### 2. Crear la base de datos

```bat
mysql -u root -p < database\adte.sql
```

El script crea la base `adte`, las seis tablas y datos de ejemplo. Consulta [docs/configuracion.md](docs/configuracion.md) si tu usuario/contraseña o puerto son distintos.

### 3. Configurar la conexión

Por defecto se usa:

- URL: `jdbc:mysql://localhost:3306/adte?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8`
- Usuario: `root`
- Contraseña: `root`

Puedes cambiarlo sin tocar el código con variables de entorno:

```bat
set DB_URL=jdbc:mysql://localhost:3306/adte?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
set DB_USER=root
set DB_PASSWORD=root
```

O con propiedades del sistema:

```bat
scripts\ejecutar.bat -Ddb.url=... -Ddb.usuario=... -Ddb.contrasena=...
```

### 4. Compilar y ejecutar

Desde la raíz del repositorio:

```bat
scripts\compilar.bat
scripts\ejecutar.bat
```

También puedes abrir la carpeta como proyecto en NetBeans y usar **Run Project** (`main.class=l2madevs.L2MADevs`).

### 5. Usuarios de prueba

| Usuario | Contraseña | Rol |
| --- | --- | --- |
| `cgomez` | `pass123` | Gestor |
| `lperez` | `tech456` | Técnico |

Las contraseñas del script SQL están hasheadas con PBKDF2-HMAC-SHA256. Si tu base antigua todavía tiene contraseñas en texto plano, el login las migrará automáticamente al primer inicio de sesión correcto.

## Pruebas

```bat
scripts\probar.bat
```

Ejecuta 11 pruebas JUnit 4 sin necesidad de base de datos:

- `PasswordHasherTest`: generación, verificación y valores mal formateados.
- `ValidacionesTest`: campos obligatorios, correo y catálogos.
- `RegistroSolicitudTest`: igualdad y `hashCode` por identificador.

En NetBeans también funciona **Test Project** porque `nbproject/project.properties` incluye JUnit y Hamcrest en el classpath de pruebas.

## Mejoras respecto al proyecto original

| Problema original | Solución en este repo |
| --- | --- |
| Credenciales y URL de BD escritas en el código | `Configuracion` lee propiedades `-D` o variables `DB_*` |
| Contraseñas en texto plano | PBKDF2 con salt aleatorio + migración automática desde texto plano |
| Dump llamado `tecelecerp`, código usando `adte` | `database/adte.sql` unificado y corregido |
| `fecha_fin` / `fecha_realizar_trabajo` `NOT NULL` impedían crear solicitudes | Columnas nullable y timestamps nulos en el INSERT |
| Cliente y solicitud se guardaban con conexiones separadas | `SolicitudService` usa una sola transacción con rollback |
| Asignar técnico podía quedar a medias | Asignación + cambio de estado en la misma transacción |
| IDs de catálogos hardcodeados | Centralizados en `Constantes` |
| Consultas pesadas congelaban la UI | Consultas movidas a `SwingWorker` mediante `VistaUtils.enSegundoPlano` |
| Historial solo en memoria | Historial leído desde `historial_de_solicitud` en la BD |
| `estadoDAO` sin inicializar y `EXIT_ON_CLOSE` en ventanas hijas | DAOs inyectados y `DISPOSE_ON_CLOSE` |
| Componentes Swing añadidos dos veces | Formularios y paneles reescritos |
| Sin pruebas ni documentación | JUnit 4, README y documentación técnica |

## Decisiones de diseño

- **No se migró a web ni a Maven**: el objetivo es mantener el proyecto universitario original y que abra directamente en NetBeans/Ant.
- **POJOs y DAO se conservan**: las mejoras se hicieron alrededor de esa arquitectura para no romper la esencia del trabajo.
- **Seguridad sin dependencias nuevas**: `PasswordHasher` usa solo el JDK (`javax.crypto`).
- **Configuración por entorno**: el repo no contiene secretos ni rutas absolutas de la máquina del autor.

Consulta [docs/arquitectura.md](docs/arquitectura.md) para ver los diagramas y el flujo de las transacciones.

## Roadmap

- [ ] Añadir `pom.xml` opcional sin abandonar `build.xml`.
- [ ] Pruebas de integración con una base de datos efímera (Testcontainers/H2).
- [ ] Validación de teléfono y longitud de campos en `Validaciones`.
- [ ] Internacionalización y tema visual configurable.
- [ ] Exportar solicitudes a PDF/CSV.

## Licencia

Proyecto de uso académico y demostrativo. El código propio del repositorio puede reutilizarse citando este proyecto; el driver MySQL incluido mantiene su licencia original.
