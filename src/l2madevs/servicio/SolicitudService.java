package l2madevs.servicio;

import java.sql.Connection;
import java.sql.SQLException;
import l2madevs.dao.ConexionBD;
import l2madevs.dao.RegistroSolicitudDAO;
import l2madevs.dao.UsuarioDAO;
import l2madevs.modelo.RegistroSolicitud;
import l2madevs.modelo.Usuario;

/**
 * Operaciones de negocio que deben ejecutarse como una sola transacción.
 *
 * <p>Antes estas operaciones vivían en la vista y usaban dos conexiones
 * independientes: si fallaba la segunda, quedaban datos huérfanos.</p>
 */
public class SolicitudService {

    private final UsuarioDAO usuarioDAO;
    private final RegistroSolicitudDAO solicitudDAO;

    public SolicitudService(UsuarioDAO usuarioDAO, RegistroSolicitudDAO solicitudDAO) {
        this.usuarioDAO = usuarioDAO;
        this.solicitudDAO = solicitudDAO;
    }

    /** Crea el cliente y su solicitud en una única transacción. */
    public boolean crearClienteYSolicitud(Usuario cliente, RegistroSolicitud solicitud)
            throws SQLException {
        try (Connection conn = ConexionBD.getConexion()) {
            conn.setAutoCommit(false);
            try {
                Usuario guardado = usuarioDAO.crearCliente(conn, cliente);
                if (guardado == null) {
                    conn.rollback();
                    return false;
                }
                solicitud.setCliente(guardado);
                if (!solicitudDAO.crearSolicitud(conn, solicitud)) {
                    conn.rollback();
                    return false;
                }
                conn.commit();
                return true;
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    /** Asigna el técnico y cambia el estado en una única transacción. */
    public boolean asignarTecnico(int idSolicitud, int idTecnico, boolean cambiarEstado)
            throws SQLException {
        try (Connection conn = ConexionBD.getConexion()) {
            conn.setAutoCommit(false);
            try {
                if (!solicitudDAO.asignarSolicitud(conn, idSolicitud, idTecnico)) {
                    conn.rollback();
                    return false;
                }
                if (cambiarEstado && !solicitudDAO.actualizarEstadoSolicitud(
                        conn, idSolicitud, l2madevs.util.Constantes.ESTADO_EN_PROGRESO)) {
                    conn.rollback();
                    return false;
                }
                conn.commit();
                return true;
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}
