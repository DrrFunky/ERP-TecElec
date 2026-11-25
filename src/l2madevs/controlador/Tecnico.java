package l2madevs.controlador;

import java.awt.Component;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import l2madevs.dao.EstadoDAO;
import l2madevs.dao.RegistroSolicitudDAO;
import l2madevs.modelo.Estado;
import l2madevs.modelo.RegistroSolicitud;
import l2madevs.util.Constantes;

public class Tecnico {

    private final RegistroSolicitudDAO solicitudDAO;
    private final EstadoDAO estadoDAO;

    public Tecnico() {
        this(new RegistroSolicitudDAO(), new EstadoDAO());
    }

    public Tecnico(RegistroSolicitudDAO solicitudDAO, EstadoDAO estadoDAO) {
        this.solicitudDAO = solicitudDAO;
        this.estadoDAO = estadoDAO;
    }

    public RegistroSolicitudDAO getSolicitudDAO() {
        return solicitudDAO;
    }

    public boolean actualizarEstado(Component parent, RegistroSolicitud solicitud,
            Estado nuevoEstado, int estadoAnteriorId, JComboBox<Estado> cmbEstado) {
        if (nuevoEstado == null || nuevoEstado.getIdEstado() == estadoAnteriorId) {
            return false;
        }

        String mensaje = String.format(
                "¿Está seguro de que desea cambiar el estado de la Solicitud ID %d a '%s'?",
                solicitud.getIdSolicitud(), nuevoEstado.getDescripcionEstado());

        int confirmacion = JOptionPane.showConfirmDialog(parent, mensaje,
                "Confirmar Cambio de Estado",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirmacion != JOptionPane.YES_OPTION) {
            restaurarSeleccion(cmbEstado, estadoAnteriorId);
            return false;
        }

        boolean exito = solicitudDAO.actualizarEstadoSolicitud(
                solicitud.getIdSolicitud(), nuevoEstado.getIdEstado());
        if (!exito) {
            return false;
        }

        if (Constantes.ESTADO_FINALIZADO == nuevoEstado.getIdEstado()) {
            boolean exitoFecha = solicitudDAO.actualizarFechaFin(
                    solicitud.getIdSolicitud(), java.time.LocalDateTime.now());
            JOptionPane.showMessageDialog(parent,
                    exitoFecha
                            ? "Estado actualizado y fecha de finalización registrada."
                            : "Estado actualizado, pero hubo un error al registrar la fecha de finalización.",
                    exitoFecha ? "Actualización Completa" : "Actualización Parcial",
                    exitoFecha ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(parent,
                    "Estado actualizado a: " + nuevoEstado.getDescripcionEstado(),
                    "Actualización Exitosa", JOptionPane.INFORMATION_MESSAGE);
        }
        return true;
    }

    public boolean reactivarSolicitud(Component parent, RegistroSolicitud solicitud) {
        Estado enProgreso = estadoDAO.obtenerPorId(Constantes.ESTADO_EN_PROGRESO);
        if (enProgreso == null) {
            JOptionPane.showMessageDialog(parent,
                    "Error de configuración: no se encontró el estado 'En progreso' en la BD.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        int confirmacion = JOptionPane.showConfirmDialog(parent,
                "¿Está seguro de que desea REABRIR la Solicitud ID "
                        + solicitud.getIdSolicitud() + " y regresarla a 'En progreso'?",
                "Confirmar Reactivación", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirmacion != JOptionPane.YES_OPTION) {
            return false;
        }

        if (!solicitudDAO.reactivarSolicitud(solicitud.getIdSolicitud())) {
            JOptionPane.showMessageDialog(parent,
                    "No se pudo reactivar la solicitud.", "Error de Base de Datos",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        solicitud.setEstado(enProgreso);
        solicitud.setFechaFin(null);
        JOptionPane.showMessageDialog(parent,
                "Solicitud ID " + solicitud.getIdSolicitud() + " reactivada correctamente.",
                "Reactivación Exitosa", JOptionPane.INFORMATION_MESSAGE);
        return true;
    }

    private void restaurarSeleccion(JComboBox<Estado> cmbEstado, int estadoAnteriorId) {
        for (int i = 0; i < cmbEstado.getItemCount(); i++) {
            if (cmbEstado.getItemAt(i).getIdEstado() == estadoAnteriorId) {
                cmbEstado.setSelectedItem(cmbEstado.getItemAt(i));
                return;
            }
        }
    }
}
