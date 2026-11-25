package l2madevs.dao;

import l2madevs.modelo.TipoSolicitud;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TipoSolicitudDAO {

    public List<TipoSolicitud> obtenerTodos() throws SQLException {
        List<TipoSolicitud> lista = new ArrayList<>();
        String sql = "SELECT * FROM tipo_de_solicitud";
        
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            
            while (rs.next()) {
                TipoSolicitud t = new TipoSolicitud();
                t.setIdTipoSolicitud(rs.getInt("id_tipo_solicitud"));
                t.setDescripcionTipoSolicitud(rs.getString("descripcion_tipo_solicitud"));
                lista.add(t);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener tipos de solicitud: " + e.getMessage());
            e.printStackTrace();
            throw e; //Relanzamos la excepción
        }
        return lista;
    }
}
