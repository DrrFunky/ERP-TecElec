package l2madevs.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import l2madevs.modelo.Estado;

public class EstadoDAO {

    public List<Estado> obtenerTodosLosEstados() {
        List<Estado> estados = new ArrayList<>();
        String sql = "SELECT id_estado, descripcion_estado FROM estado ORDER BY id_estado";

        try (Connection conn = ConexionBD.getConexion();
                PreparedStatement pst = conn.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                estados.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los estados: " + e.getMessage());
        }
        return estados;
    }

    public Estado obtenerPorDescripcion(String descripcion) {
        String sql = "SELECT id_estado, descripcion_estado FROM estado WHERE descripcion_estado = ?";
        try (Connection conn = ConexionBD.getConexion();
                PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, descripcion);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener Estado por descripción: " + e.getMessage());
        }
        return null;
    }

    public Estado obtenerPorId(int idEstado) {
        String sql = "SELECT id_estado, descripcion_estado FROM estado WHERE id_estado = ?";
        try (Connection conn = ConexionBD.getConexion();
                PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idEstado);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener Estado por id: " + e.getMessage());
        }
        return null;
    }

    private Estado mapear(ResultSet rs) throws SQLException {
        Estado e = new Estado();
        e.setIdEstado(rs.getInt("id_estado"));
        e.setDescripcionEstado(rs.getString("descripcion_estado"));
        return e;
    }
}
