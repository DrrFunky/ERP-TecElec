# Configuración

Esta guía explica cómo dejar la aplicación lista en una máquina nueva, sin editar código fuente.

## 1. Requisitos

- **JDK 17+**. Verificado con JDK 25.0.1.
- **MySQL 8+ o MariaDB 10+**.
- **NetBeans 27** si quieres usar el IDE (opcional).
- El driver JDBC ya está incluido en `lib/mysql-connector-j-9.4.0.jar`.

## 2. Base de datos

El script `database/adte.sql` crea la base `adte`, las tablas y los datos de ejemplo.

Desde la raíz del repositorio, en **CMD**:

```bat
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p < database\adte.sql
```

En **PowerShell** (que no admite `<`) usa `Get-Content` con UTF-8:

```powershell
Get-Content -Raw -Encoding UTF8 'database\adte.sql' |
    & 'C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe' -u root -p
```

También puedes entrar al cliente y usar `SOURCE`:

```sql
SOURCE C:/Users/Alonso/Documents/L2MADevs/L2MADevs-Portfolio/database/adte.sql
```

> El script usa el nombre de columna ASCII `contrasena` (sin `ñ`) a propósito: así evita problemas de codificación entre PowerShell, CMD y MySQL. Si importaste una versión anterior con `contraseña`, vuelve a importar el script actual.

### Usuarios incluidos en el script

| login | contraseña | rol | id_rol |
| --- | --- | --- | --- |
| `cgomez` | `pass123` | Gestor | 1 |
| `lperez` | `tech456` | Técnico | 2 |

Los clientes (`María`, `José`) no inician sesión: se crean desde el formulario de solicitudes.

## 3. Conexión JDBC

`l2madevs.config.Configuracion` busca los valores en este orden:

1. Propiedades del sistema: `-Ddb.url`, `-Ddb.usuario`, `-Ddb.contrasena`.
2. Variables de entorno: `DB_URL`, `DB_USER`, `DB_PASSWORD`.
3. Valores por defecto del repositorio.

| Concepto | Propiedad | Variable de entorno | Valor por defecto |
| --- | --- | --- | --- |
| URL JDBC | `db.url` | `DB_URL` | `jdbc:mysql://localhost:3306/adte?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8` |
| Usuario | `db.usuario` | `DB_USER` | `root` |
| Contraseña | `db.contrasena` | `DB_PASSWORD` | `root` |

### Opción A: variables de entorno (CMD)

```bat
set DB_URL=jdbc:mysql://localhost:3306/adte?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
set DB_USER=root
set DB_PASSWORD=root
scripts\ejecutar.bat
```

### Opción B: propiedades del sistema

`scripts\ejecutar.bat` reenvía sus argumentos a la JVM:

```bat
scripts\ejecutar.bat -Ddb.url=jdbc:mysql://localhost:3307/adte?useSSL=false -Ddb.usuario=root -Ddb.contrasena=secret
```

### Opción C: NetBeans

1. Abre la carpeta `L2MADevs-Portfolio` como proyecto.
2. Clic derecho en el proyecto → **Properties** → **Run**.
3. En **VM Options** escribe, por ejemplo:

```text
-Ddb.url=jdbc:mysql://localhost:3306/adte?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8 -Ddb.usuario=root -Ddb.contrasena=root
```

4. Ejecuta **Run Project**. `main.class` ya apunta a `l2madevs.L2MADevs`.

## 4. Compilación y pruebas

```bat
scripts\compilar.bat   % Solo compila en build\classes
scripts\ejecutar.bat   % Compila y lanza la interfaz
scripts\probar.bat     % Compila y ejecuta los 11 tests JUnit
```

Con NetBeans, **Clean and Build** y **Test Project** usan `build.xml` y `nbproject/project.properties`. Todas las rutas de classpath son relativas al repositorio.

## 5. Solución de problemas

| Síntoma | Causa probable | Acción |
| --- | --- | --- |
| `Communications link failure` | MySQL apagado o puerto distinto | Arranca el servicio y ajusta `DB_URL`. |
| `Unknown database 'adte'` | No se importó el script | Ejecuta `mysql -u root -p < database\adte.sql`. |
| `Access denied for user 'root'` | Contraseña distinta a `root` | Define `DB_USER` / `DB_PASSWORD`. |
| `Public Key Retrieval is not allowed` | MySQL 8 con caching_sha2_password | La URL por defecto ya incluye `allowPublicKeyRetrieval=true`; no la quites. |
| `The server time zone value ... is unrecognized` | Zona horaria del servidor | La URL por defecto incluye `serverTimezone=UTC`. |
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | Driver fuera del classpath | Usa los scripts o verifica `lib/mysql-connector-j-9.4.0.jar`. |
| La UI se queda cargando | Consulta lenta o BD inaccesible | Las consultas usan `SwingWorker`; revisa la consola y los diálogos de error. |
| Login con contraseña antigua en texto plano | Base migrada desde el proyecto original | Inicia sesión una vez: `UsuarioDAO` guarda el hash automáticamente. |

## 6. Cambiar las contraseñas de ejemplo

El valor almacenado sigue el formato:

```text
PBKDF2$120000$<salt-en-base64>$<hash-en-base64>
```

Para generar un hash nuevo puedes usar `l2madevs.seguridad.PasswordHasher.hash(String)` desde un pequeño programa Java o `jshell` con `build/classes` en el classpath, y reemplazar el valor en la tabla `usuario`.

> No subas contraseñas reales ni cadenas de conexión de producción al repositorio. Usa siempre `DB_*` o `-D...`.
