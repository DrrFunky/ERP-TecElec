package l2madevs.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import l2madevs.config.Configuracion;

/** Fábrica de conexiones; la configuración no está escrita en el código. */
public final class ConexionBD {

    private ConexionBD() {
    }

    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(
                Configuracion.getUrl(),
                Configuracion.getUsuario(),
                Configuracion.getContrasena());
    }
}
