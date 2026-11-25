package l2madevs.config;

/**
 * Configuración de la aplicación.
 *
 * <p>Permite ejecutar el proyecto en cualquier máquina sin editar el código:
 * primero se intenta leer una propiedad del sistema (-Ddb.url=...), después
 * una variable de entorno (DB_URL) y, si no hay nada, se usa un valor por
 * defecto pensado para el entorno local de desarrollo.</p>
 */
public final class Configuracion {

    private static final String URL_POR_DEFECTO =
            "jdbc:mysql://localhost:3306/adte?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
    private static final String USUARIO_POR_DEFECTO = "root";
    private static final String CONTRASENA_POR_DEFECTO = "root";

    private Configuracion() {
    }

    public static String getUrl() {
        return leer("db.url", "DB_URL", URL_POR_DEFECTO);
    }

    public static String getUsuario() {
        return leer("db.usuario", "DB_USER", USUARIO_POR_DEFECTO);
    }

    public static String getContrasena() {
        return leer("db.contrasena", "DB_PASSWORD", CONTRASENA_POR_DEFECTO);
    }

    private static String leer(String propiedad, String variableEntorno, String defecto) {
        String valor = System.getProperty(propiedad);
        if (esVacio(valor)) {
            valor = System.getenv(variableEntorno);
        }
        return esVacio(valor) ? defecto : valor;
    }

    private static boolean esVacio(String valor) {
        return valor == null || valor.isBlank();
    }
}
