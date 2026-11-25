package l2madevs.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import l2madevs.dao.EstadoDAO;
import l2madevs.dao.RegistroSolicitudDAO;
import l2madevs.modelo.RegistroSolicitud;
import l2madevs.modelo.Usuario;
import l2madevs.util.Constantes;
import l2madevs.vista.base.PanelMostrarSolicitudes;

public class PanelVerSolicitudesGestor extends PanelMostrarSolicitudes {

    public PanelVerSolicitudesGestor(Usuario usuario, RegistroSolicitudDAO rsDAO, EstadoDAO eDAO) {
        super(usuario, rsDAO, eDAO);
    }

    @Override
    protected String getTituloPanel() {
        return "VER SOLICITUDES (Gestor)";
    }

    @Override
    protected List<RegistroSolicitud> obtenerDatosFuente() {
        return solicitudDAO.obtenerTodasLasSolicitudes();
    }

    @Override
    protected JPanel crearPanelAccionesEstado(RegistroSolicitud tarea) {
        JPanel panelEstado = new JPanel();
        panelEstado.setLayout(new BoxLayout(panelEstado, BoxLayout.Y_AXIS));
        panelEstado.setBackground(Color.WHITE);
        panelEstado.setBorder(new EmptyBorder(0, 10, 0, 0));

        JLabel lblEstadoActual = new JLabel("Estado Actual:");
        lblEstadoActual.setFont(new Font("Arial", Font.PLAIN, 12));
        lblEstadoActual.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelEstado.add(lblEstadoActual);
        panelEstado.add(Box.createRigidArea(new Dimension(0, 5)));

        String estadoDesc = tarea.getEstado() == null
                ? "N/A" : tarea.getEstado().getDescripcionEstado();
        JLabel lblEstadoTexto = new JLabel(estadoDesc);
        lblEstadoTexto.setFont(new Font("Arial", Font.BOLD, 14));
        lblEstadoTexto.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelEstado.add(lblEstadoTexto);

        boolean estaIniciada = tarea.getEstado() != null
                && tarea.getEstado().getIdEstado() == Constantes.ESTADO_INICIAR;
        if (estaIniciada) {
            panelEstado.add(Box.createRigidArea(new Dimension(0, 10)));
            JButton btnAsignar = new JButton("Asignar Técnico");
            btnAsignar.setFont(new Font("Arial", Font.BOLD, 12));
            btnAsignar.setBackground(new Color(23, 162, 184));
            btnAsignar.setForeground(Color.WHITE);
            btnAsignar.setFocusPainted(false);
            btnAsignar.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnAsignar.addActionListener(e -> {
                Window ventanaPadre = SwingUtilities.getWindowAncestor(this);
                if (ventanaPadre instanceof VentanaGestor) {
                    ((VentanaGestor) ventanaPadre).mostrarPanelAsignacion(tarea);
                } else {
                    JOptionPane.showMessageDialog(this, "Error al intentar cambiar de panel.",
                            "Error de Navegación", JOptionPane.ERROR_MESSAGE);
                }
            });
            panelEstado.add(btnAsignar);
        }
        return panelEstado;
    }
}
