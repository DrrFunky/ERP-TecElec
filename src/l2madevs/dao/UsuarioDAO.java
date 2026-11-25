package l2madevs.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import l2madevs.modelo.Rol;
import l2madevs.modelo.Usuario;
import l2madevs.seguridad.PasswordHasher;
import l2madevs.util.Constantes;

public class UsuarioDAO {

    /**
     * Valida las credenciales. Acepta contraseñas ya hasheadas y, durante la
     * migración, contraseñas en texto plano: si la comparación funciona, la
     * contraseña se actualiza a hash automáticamente.
     */
    public Usuario validarLogin(String usuarioLogin, String contrasena) {
        String sql = "SELECT u.*, r.nombre_rol FROM usuario u "
                + "JOIN rol r ON u.id_rol = r.id_rol "
                + "WHERE u.usuario_login = ?";

        try (Connection conn = ConexionBD.getConexion();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, usuarioLogin);
            try (ResultSet rs = pst.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                String almacenada = rs.getString("contraseña");
                boolean valido = PasswordHasher.esHash(almacenada)
                        ? PasswordHasher.verificar(contrasena, almacenada)
                        : contrasena != null && contrasena.equals(almacenada);

                if (!valido) {
                    return null;
                }
                if (!PasswordHasher.esHash(almacenada)) {
                    migrarContrasena(rs.getInt("id_usuario"), contrasena);
                }
                return mapearResultSetAUsuario(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al validar login: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private void migrarContrasena(int idUsuario, String contrasena) {
        try {
            actualizarContrasena(idUsuario, PasswordHasher.hash(contrasena));
        } catch (SQLException e) {
            System.err.println("No se pudo migrar la contraseña del usuario " + idUsuario);
        }
    }

    public void actualizarContrasena(int idUsuario, String hash) throws SQLException {
        String sql = "UPDATE usuario SET contraseña = ? WHERE id_usuario = ?";
        try (Connection conn = ConexionBD.getConexion();
                PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, hash);
            pst.setInt(2, idUsuario);
            pst.executeUpdate();
        }
    }

    public List<Usuario> obtenerTodosLosTecnicos() {
        List<Usuario> tecnicos = new ArrayList<>();
        String sql = "SELECT u.*, r.nombre_rol FROM usuario u "
                + "JOIN rol r ON u.id_rol = r.id_rol "
                + "WHERE r.nombre_rol = 'Tecnico'";

        try (Connection conn = ConexionBD.getConexion();
                PreparedStatement pst = conn.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                tecnicos.add(mapearResultSetAUsuario(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener técnicos: " + e.getMessage());
            e.printStackTrace();
        }
        return tecnicos;
    }

    public Usuario crearCliente(Usuario cliente) {
        try (Connection conn = ConexionBD.getConexion()) {
            return crearCliente(conn, cliente);
        } catch (SQLException e) {
            System.err.println("Error al crear cliente: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /** Inserta un cliente usando una conexión ya existente (transacción). */
    public Usuario crearCliente(Connection conn, Usuario cliente) throws SQLException {
        String sql = "INSERT INTO usuario "
                + "(id_rol, nombre, a_paterno, a_materno, correo, "
                + "fecha_creacion_usuario, direccion, telefono, contraseña, usuario_login) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NULL, NULL)";

        try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, Constantes.ROL_CLIENTE);
            pst.setString(2, cliente.getNombre());
            pst.setString(3, cliente.getAPaterno());
            pst.setString(4, cliente.getAMaterno());
            pst.setString(5, cliente.getCorreo());
            pst.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            pst.setString(7, cliente.getDireccion());
            pst.setString(8, cliente.getTelefono());

            if (pst.executeUpdate() == 0) {
                return null;
            }
            try (ResultSet rsKeys = pst.getGeneratedKeys()) {
                if (rsKeys.next()) {
                    cliente.setIdUsuario(rsKeys.getInt(1));
                    Rol rol = new Rol();
                    rol.setIdRol(Constantes.ROL_CLIENTE);
                    cliente.setRol(rol);
                    return cliente;
                }
            }
            return null;
        }
    }

    private Usuario mapearResultSetAUsuario(ResultSet rs) throws SQLException {
        Rol rol = new Rol();
        rol.setIdRol(rs.getInt("id_rol"));
        rol.setNombreRol(rs.getString("nombre_rol"));

        Usuario user = new Usuario();
        user.setIdUsuario(rs.getInt("id_usuario"));
        user.setUsuarioLogin(rs.getString("usuario_login"));
        user.setNombre(rs.getString("nombre"));
        user.setAPaterno(rs.getString("a_paterno"));
        user.setAMaterno(rs.getString("a_materno"));
        user.setCorreo(rs.getString("correo"));
        user.setDireccion(rs.getString("direccion"));
        user.setTelefono(rs.getString("telefono"));

        Timestamp fecha = rs.getTimestamp("fecha_creacion_usuario");
        if (fecha != null) {
            user.setFechaCreacionUsuario(fecha.toLocalDateTime());
        }
        user.setRol(rol);
        return user;
    }
}
