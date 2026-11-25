package l2madevs.controlador;

import java.time.LocalDateTime;
import javax.swing.JOptionPane;
import l2madevs.dao.RegistroSolicitudDAO;
import l2madevs.dao.UsuarioDAO;
import l2madevs.modelo.Estado;
import l2madevs.modelo.RegistroSolicitud;
import l2madevs.modelo.TipoPrioridad;
import l2madevs.modelo.TipoSolicitud;
import l2madevs.modelo.Usuario;
import l2madevs.servicio.SolicitudService;
import l2madevs.util.Constantes;
import l2madevs.util.Validaciones;
import l2madevs.vista.PanelCrearSolicitud;

public class Gestor {

    private final SolicitudService solicitudService;

    public Gestor(UsuarioDAO usuarioDAO, RegistroSolicitudDAO solicitudDAO) {
        this.solicitudService = new SolicitudService(usuarioDAO, solicitudDAO);
    }

    /**
     * @return {@code true} sólo si cliente y solicitud se guardaron juntos.
     */
    public boolean guardarSolicitud(PanelCrearSolicitud panel, String nombreCliente,
            String apCliente, String amCliente, String correoCliente, String telCliente,
            String dirCliente, TipoSolicitud tipoSel, TipoPrioridad prioSel,
            String dirSolicitud, String descripcion) {

        String error = Validaciones.validarSolicitud(nombreCliente, apCliente, correoCliente,
                telCliente, dirCliente, dirSolicitud, descripcion, tipoSel, prioSel);
        if (error != null) {
            JOptionPane.showMessageDialog(panel, error, "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        Usuario nuevoCliente = new Usuario();
        nuevoCliente.setNombre(nombreCliente);
        nuevoCliente.setAPaterno(apCliente);
        nuevoCliente.setAMaterno(amCliente);
        nuevoCliente.setCorreo(correoCliente);
        nuevoCliente.setTelefono(telCliente);
        nuevoCliente.setDireccion(dirCliente);

        RegistroSolicitud nuevaSol = new RegistroSolicitud();
        nuevaSol.setTipoSolicitud(tipoSel);
        nuevaSol.setTipoPrioridad(prioSel);
        nuevaSol.setUbicacion(dirSolicitud);
        nuevaSol.setDescripcionSolicitud(descripcion);
        nuevaSol.setFechaCreacion(LocalDateTime.now());

        Estado estadoInicial = new Estado();
        estadoInicial.setIdEstado(Constantes.ESTADO_INICIAR);
        nuevaSol.setEstado(estadoInicial);

        try {
            boolean exito = solicitudService.crearClienteYSolicitud(nuevoCliente, nuevaSol);
            if (exito) {
                JOptionPane.showMessageDialog(panel,
                        "Cliente y solicitud creados exitosamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(panel,
                        "No se pudo guardar la solicitud. No se realizaron cambios.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
            return exito;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(panel, "Error fatal: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
