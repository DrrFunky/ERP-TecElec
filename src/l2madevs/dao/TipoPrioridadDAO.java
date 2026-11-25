package l2madevs.dao;

import l2madevs.modelo.TipoPrioridad;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TipoPrioridadDAO {

    public List<TipoPrioridad> obtenerTodos() throws SQLException {
        List<TipoPrioridad> lista = new ArrayList<>();
        String sql = "SELECT * FROM tipo_de_prioridad";
        
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            
            while (rs.next()) {
                TipoPrioridad p = new TipoPrioridad();
                p.setIdTipoPrioridad(rs.getInt("id_tipo_prioridad"));
                p.setDescripcionTipoPrioridad(rs.getString("descripcion_tipo_prioridad"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener tipos de prioridad: " + e.getMessage());
            e.printStackTrace();
            throw e; //Relanzamos la excepción
        }
        return lista;
    }
}
