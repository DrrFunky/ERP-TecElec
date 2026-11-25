package l2madevs.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import l2madevs.modelo.Estado;
import l2madevs.modelo.RegistroSolicitud;
import l2madevs.modelo.TipoPrioridad;
import l2madevs.modelo.TipoSolicitud;
import l2madevs.modelo.Usuario;
import l2madevs.util.Constantes;

public class RegistroSolicitudDAO {

    private static final String BASE_SELECT_SQL
            = "SELECT s.id_solicitud, s.descripcion_solicitud, s.ubicacion, "
            + "s.fecha_creacion, s.fecha_fin, s.fecha_realizar_trabajo, "
            + "c.id_usuario AS id_cliente, c.nombre AS nombre_cliente, "
            + "c.a_paterno AS apaterno_cliente, c.a_materno AS amaterno_cliente, "
            + "p.id_tipo_prioridad, p.descripcion_tipo_prioridad, "
            + "t.id_tipo_solicitud, t.descripcion_tipo_solicitud, "
            + "e.id_estado, e.descripcion_estado "
            + "FROM registro_de_solicitud s "
            + "JOIN usuario c ON s.id_usuario = c.id_usuario "
            + "JOIN tipo_de_prioridad p ON s.id_tipo_prioridad = p.id_tipo_prioridad "
            + "JOIN tipo_de_solicitud t ON s.id_tipo_solicitud = t.id_tipo_solicitud "
            + "JOIN estado e ON s.id_estado = e.id_estado";

    public List<RegistroSolicitud> obtenerTodasLasSolicitudes() {
        List<RegistroSolicitud> solicitudes = new ArrayList<>();
        String sql = BASE_SELECT_SQL + " ORDER BY s.fecha_creacion DESC";

        try (Connection conn = ConexionBD.getConexion();
                PreparedStatement pst = conn.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                solicitudes.add(mapearResultSetASolicitud(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todas las solicitudes: " + e.getMessage());
            e.printStackTrace();
        }
        return solicitudes;
    }

    /** Todas las solicitudes asignadas al técnico, incluidas las finalizadas. */
    public List<RegistroSolicitud> obtenerSolicitudesPorTecnico(int idTecnico) {
        return obtenerPorTecnico(idTecnico, "", "s.fecha_realizar_trabajo ASC");
    }

    public List<RegistroSolicitud> obtenerSolicitudesActivasPorTecnico(int idTecnico) {
        return obtenerPorTecnico(idTecnico,
                " AND e.id_estado <> " + Constantes.ESTADO_FINALIZADO,
                "s.fecha_realizar_trabajo ASC");
    }

    public List<RegistroSolicitud> obtenerSolicitudesFinalizadasPorTecnico(int idTecnico) {
        return obtenerPorTecnico(idTecnico,
                " AND e.id_estado = " + Constantes.ESTADO_FINALIZADO,
                "s.fecha_fin DESC");
    }

    private List<RegistroSolicitud> obtenerPorTecnico(
            int idTecnico, String filtroEstado, String orden) {
        List<RegistroSolicitud> solicitudes = new ArrayList<>();
        String sql = BASE_SELECT_SQL
                + " JOIN historial_de_solicitud h ON s.id_solicitud = h.id_solicitud"
                + " WHERE h.id_usuario = ?" + filtroEstado
                + " ORDER BY " + orden;

        try (Connection conn = ConexionBD.getConexion();
                PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idTecnico);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    solicitudes.add(mapearResultSetASolicitud(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener solicitudes por técnico: " + e.getMessage());
            e.printStackTrace();
        }
        return solicitudes;
    }

    public List<Integer> obtenerIdsTecnicosAsignados(int idSolicitud) {
        List<Integer> idsTecnicos = new ArrayList<>();
        String sql = "SELECT DISTINCT id_usuario FROM historial_de_solicitud WHERE id_solicitud = ?";
        try (Connection conn = ConexionBD.getConexion();
                PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idSolicitud);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    idsTecnicos.add(rs.getInt("id_usuario"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener IDs de técnicos asignados: " + e.getMessage());
            e.printStackTrace();
        }
        return idsTecnicos;
    }

    public boolean crearSolicitud(RegistroSolicitud solicitud) {
        try (Connection conn = ConexionBD.getConexion()) {
            return crearSolicitud(conn, solicitud);
        } catch (SQLException e) {
            System.err.println("Error al crear la solicitud: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean crearSolicitud(Connection conn, RegistroSolicitud solicitud)
            throws SQLException {
        String sql = "INSERT INTO registro_de_solicitud "
                + "(id_usuario, id_tipo_solicitud, id_tipo_prioridad, id_estado, "
                + "descripcion_solicitud, ubicacion, fecha_creacion, "
                + "fecha_fin, fecha_realizar_trabajo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, solicitud.getCliente().getIdUsuario());
            pst.setInt(2, solicitud.getTipoSolicitud().getIdTipoSolicitud());
            pst.setInt(3, solicitud.getTipoPrioridad().getIdTipoPrioridad());
            pst.setInt(4, solicitud.getEstado().getIdEstado());
            pst.setString(5, solicitud.getDescripcionSolicitud());
            pst.setString(6, solicitud.getUbicacion());
            pst.setTimestamp(7, Timestamp.valueOf(solicitud.getFechaCreacion()));
            pst.setNull(8, java.sql.Types.TIMESTAMP);
            pst.setNull(9, java.sql.Types.TIMESTAMP);
            return pst.executeUpdate() > 0;
        }
    }

    public boolean asignarSolicitud(int idSolicitud, int idTecnico) {
        try (Connection conn = ConexionBD.getConexion()) {
            return asignarSolicitud(conn, idSolicitud, idTecnico);
        } catch (SQLException e) {
            System.err.println("Error al asignar solicitud: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean asignarSolicitud(Connection conn, int idSolicitud, int idTecnico)
            throws SQLException {
        String sql = "INSERT INTO historial_de_solicitud (id_usuario, id_solicitud) VALUES (?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idTecnico);
            pst.setInt(2, idSolicitud);
            return pst.executeUpdate() > 0;
        }
    }

    public boolean actualizarEstadoSolicitud(int idSolicitud, int idNuevoEstado) {
        try (Connection conn = ConexionBD.getConexion()) {
            return actualizarEstadoSolicitud(conn, idSolicitud, idNuevoEstado);
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarEstadoSolicitud(
            Connection conn, int idSolicitud, int idNuevoEstado) throws SQLException {
        String sql = "UPDATE registro_de_solicitud SET id_estado = ? WHERE id_solicitud = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idNuevoEstado);
            pst.setInt(2, idSolicitud);
            return pst.executeUpdate() > 0;
        }
    }

    public boolean actualizarFechaFin(int idSolicitud, LocalDateTime fechaFin) {
        String sql = "UPDATE registro_de_solicitud SET fecha_fin = ? WHERE id_solicitud = ?";
        try (Connection conn = ConexionBD.getConexion();
                PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setTimestamp(1, Timestamp.valueOf(fechaFin));
            pst.setInt(2, idSolicitud);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar la fecha de finalización: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /** Reabre una solicitud y limpia la fecha de finalización. */
    public boolean reactivarSolicitud(int idSolicitud) {
        String sql = "UPDATE registro_de_solicitud "
                + "SET id_estado = ?, fecha_fin = NULL WHERE id_solicitud = ?";
        try (Connection conn = ConexionBD.getConexion();
                PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, Constantes.ESTADO_EN_PROGRESO);
            pst.setInt(2, idSolicitud);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al reactivar la solicitud: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private RegistroSolicitud mapearResultSetASolicitud(ResultSet rs) throws SQLException {
        Usuario cliente = new Usuario();
        cliente.setIdUsuario(rs.getInt("id_cliente"));
        cliente.setNombre(rs.getString("nombre_cliente"));
        cliente.setAPaterno(rs.getString("apaterno_cliente"));
        cliente.setAMaterno(rs.getString("amaterno_cliente"));

        TipoPrioridad prioridad = new TipoPrioridad();
        prioridad.setIdTipoPrioridad(rs.getInt("id_tipo_prioridad"));
        prioridad.setDescripcionTipoPrioridad(rs.getString("descripcion_tipo_prioridad"));

        TipoSolicitud tipoSol = new TipoSolicitud();
        tipoSol.setIdTipoSolicitud(rs.getInt("id_tipo_solicitud"));
        tipoSol.setDescripcionTipoSolicitud(rs.getString("descripcion_tipo_solicitud"));

        Estado estado = new Estado();
        estado.setIdEstado(rs.getInt("id_estado"));
        estado.setDescripcionEstado(rs.getString("descripcion_estado"));

        RegistroSolicitud sol = new RegistroSolicitud();
        sol.setIdSolicitud(rs.getInt("id_solicitud"));
        sol.setDescripcionSolicitud(rs.getString("descripcion_solicitud"));
        sol.setUbicacion(rs.getString("ubicacion"));

        Timestamp fechaCreacion = rs.getTimestamp("fecha_creacion");
        if (fechaCreacion != null) {
            sol.setFechaCreacion(fechaCreacion.toLocalDateTime());
        }
        Timestamp fechaFin = rs.getTimestamp("fecha_fin");
        if (fechaFin != null) {
            sol.setFechaFin(fechaFin.toLocalDateTime());
        }
        Timestamp fechaLimite = rs.getTimestamp("fecha_realizar_trabajo");
        if (fechaLimite != null) {
            sol.setFechaRealizarTrabajo(fechaLimite.toLocalDateTime());
        }

        sol.setCliente(cliente);
        sol.setTipoPrioridad(prioridad);
        sol.setTipoSolicitud(tipoSol);
        sol.setEstado(estado);
        return sol;
    }
}
