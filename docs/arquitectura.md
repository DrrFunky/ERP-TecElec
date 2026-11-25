# Arquitectura

El proyecto conserva una arquitectura en capas sencilla, típica de un trabajo universitario de Java Swing: **vista → controlador/servicio → DAO → base de datos**. Las mejoras se hicieron sin cambiar ese modelo.

## Vista general

```mermaid
flowchart TD
    Login[VentanaLogin] -->|UsuarioDAO.validarLogin| UsuarioDAO
    UsuarioDAO --> BD[(MySQL / MariaDB<br/>adte)]
    Login -->|rol Gestor| VentanaGestor
    Login -->|rol Técnico| VentanaTecnico

    subgraph Gestor["Rol Gestor"]
        VentanaGestor --> Crear[PanelCrearSolicitud]
        VentanaGestor --> Ver[PanelVerSolicitudesGestor]
        VentanaGestor --> Asignar[PanelAsignarTecnico]
        Crear --> G[Controlador Gestor]
        G --> Servicio[SolicitudService]
        Asignar --> Servicio
        Ver --> SolicitudDAO
    end

    subgraph Tecnico["Rol Técnico"]
        VentanaTecnico --> Tareas[PanelTareasAsignadas]
        VentanaTecnico --> Historial[PanelHistorialTrabajos]
        Tareas --> TecnicoC[Controlador Tecnico]
        TecnicoC --> SolicitudDAO
        Historial --> SolicitudDAO
    end

    Servicio --> SolicitudDAO
    Servicio --> UsuarioDAO
    SolicitudDAO --> BD
    EstadoDAO[EstadoDAO] --> BD
    TipoDAO[TipoPrioridadDAO / TipoSolicitudDAO] --> BD
    Tareas --> EstadoDAO
    Crear --> TipoDAO
```

## Responsabilidades por paquete

| Paquete | Responsabilidad |
| --- | --- |
| `l2madevs.config` | Leer URL, usuario y contraseña desde propiedades del sistema o variables de entorno. |
| `l2madevs.modelo` | POJOs (`Usuario`, `Rol`, `RegistroSolicitud`, `Estado`, `TipoSolicitud`, `TipoPrioridad`). |
| `l2madevs.dao` | Sentencias SQL, mapeo `ResultSet` → modelo y manejo de `Connection`. |
| `l2madevs.seguridad` | Hash PBKDF2-HMAC-SHA256 y verificación de contraseñas. |
| `l2madevs.util` | Constantes de catálogos y validaciones puras sin dependencia de Swing. |
| `l2madevs.servicio` | Operaciones que necesitan transacción (crear solicitud, asignar técnico). |
| `l2madevs.controlador` | Coordinación entre vista y datos; diálogos de confirmación. |
| `l2madevs.vista` | Ventanas, paneles y componentes Swing. Las consultas se hacen en `SwingWorker`. |
| `l2madevs.vista.base` | Panel abstracto compartido por las listas de gestor y técnico. |

## Modelo de datos

```mermaid
erDiagram
    ROL ||--o{ USUARIO : tiene
    USUARIO ||--o{ REGISTRO_DE_SOLICITUD : crea
    ESTADO ||--o{ REGISTRO_DE_SOLICITUD : clasifica
    TIPO_DE_SOLICITUD ||--o{ REGISTRO_DE_SOLICITUD : clasifica
    TIPO_DE_PRIORIDAD ||--o{ REGISTRO_DE_SOLICITUD : prioriza
    REGISTRO_DE_SOLICITUD ||--o{ HISTORIAL_DE_SOLICITUD : registra
    USUARIO ||--o{ HISTORIAL_DE_SOLICITUD : trabaja

    USUARIO {
        int id_usuario PK
        int id_rol FK
        string usuario_login
        string contrasena "PBKDF2 o legacy"
    }
    REGISTRO_DE_SOLICITUD {
        int id_solicitud PK
        int id_usuario FK
        int id_estado FK
        datetime fecha_fin "nullable"
    }
    HISTORIAL_DE_SOLICITUD {
        int id_historial PK
        int id_usuario FK
        int id_solicitud FK
    }
```

## Transacción: crear cliente y solicitud

Antes se insertaban el cliente y la solicitud con dos conexiones: si fallaba la segunda, quedaba un cliente huérfano. Ahora `SolicitudService` abre una sola conexión, desactiva `autoCommit` y hace rollback ante cualquier fallo.

```mermaid
sequenceDiagram
    actor U as Gestor
    participant P as PanelCrearSolicitud
    participant G as Gestor
    participant S as SolicitudService
    participant UD as UsuarioDAO
    participant RD as RegistroSolicitudDAO
    participant BD as Base de datos

    U->>P: Completa formulario y guarda
    P->>G: guardarSolicitud(...)
    G->>G: Validaciones.validarSolicitud(...)
    G->>S: crearClienteYSolicitud(cliente, solicitud)
    S->>BD: getConexion() + autoCommit=false
    S->>UD: crearCliente(conn, cliente)
    UD->>BD: INSERT usuario
    BD-->>UD: id_usuario generado
    S->>RD: crearSolicitud(conn, solicitud)
    RD->>BD: INSERT registro_de_solicitud
    alt Todo correcto
        S->>BD: commit()
        S-->>G: true
        G-->>P: Mensaje de éxito y limpieza del formulario
    else Fallo
        S->>BD: rollback()
        S-->>G: false / excepción
        G-->>P: Mensaje de error, sin cambios parciales
    end
```

## Transacción: asignar técnico

```mermaid
sequenceDiagram
    actor U as Gestor
    participant A as PanelAsignarTecnico
    participant S as SolicitudService
    participant RD as RegistroSolicitudDAO
    participant BD as Base de datos

    U->>A: Selecciona técnico y confirma
    A->>S: asignarTecnico(idSolicitud, idTecnico, cambiarEstado)
    S->>BD: autoCommit=false
    S->>RD: asignarSolicitud(conn, ...)
    RD->>BD: INSERT historial_de_solicitud
    S->>RD: actualizarEstadoSolicitud(conn, ..., EN_PROGRESO)
    RD->>BD: UPDATE registro_de_solicitud
    S->>BD: commit() o rollback()
    S-->>A: Resultado
```

## Concurrencia e interfaz

- Las consultas a BD se envuelven con `VistaUtils.enSegundoPlano`, que usa `SwingWorker<Void>`:
  - `doInBackground()`: ejecuta la consulta fuera del EDT.
  - `done()`: actualiza los componentes de Swing en el EDT y muestra errores con `JOptionPane`.
- Las listas de solicitudes comparten `PanelMostrarSolicitudes`; cada rol implementa `obtenerDatosFuente()` y `crearPanelAccionesEstado()`.
- El historial ya no depende de una lista en memoria: se consulta por `id_usuario` en `historial_de_solicitud` y se filtra por `id_estado = 3` (Finalizado).

## Seguridad

- `PasswordHasher` guarda `PBKDF2$iteraciones$salt$hash` en base64, con 120.000 iteraciones HMAC-SHA256 y salt aleatorio de 16 bytes.
- `UsuarioDAO.validarLogin` detecta valores `PBKDF2$...` y, si encuentra texto plano, verifica la contraseña antigua y la reemplaza por un hash.
- Las credenciales de conexión a BD no están en el código; se leen desde `Configuracion`.

## Pruebas

Las pruebas JUnit 4 se centran en lógica pura y seguridad, sin requerir base de datos: validaciones, hash de contraseñas e igualdad de entidades. `scripts\probar.bat` compila y ejecuta las tres clases de test.
