package l2madevs.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import l2madevs.controlador.Tecnico;
import l2madevs.dao.EstadoDAO;
import l2madevs.dao.RegistroSolicitudDAO;
import l2madevs.modelo.Estado;
import l2madevs.modelo.RegistroSolicitud;
import l2madevs.modelo.Usuario;
import l2madevs.vista.base.PanelMostrarSolicitudes;

public class PanelTareasAsignadas extends PanelMostrarSolicitudes {

    private final Tecnico controladorTecnico;

    public PanelTareasAsignadas(Usuario tecnico, RegistroSolicitudDAO rsDAO, EstadoDAO eDAO) {
        super(tecnico, rsDAO, eDAO);
        this.controladorTecnico = new Tecnico(rsDAO, eDAO);
    }

    @Override
    protected String getTituloPanel() {
        return "MIS TAREAS ASIGNADAS";
    }

    @Override
    protected List<RegistroSolicitud> obtenerDatosFuente() {
        return solicitudDAO.obtenerSolicitudesActivasPorTecnico(usuarioFiltro.getIdUsuario());
    }

    @Override
    protected JPanel crearPanelAccionesEstado(RegistroSolicitud tarea) {
        JPanel panelAcciones = new JPanel();
        panelAcciones.setLayout(new BoxLayout(panelAcciones, BoxLayout.Y_AXIS));
        panelAcciones.setBackground(Color.WHITE);
        panelAcciones.setBorder(new EmptyBorder(0, 10, 0, 0));
        panelAcciones.setPreferredSize(new Dimension(200, 100));

        JLabel lblEstadoActual = new JLabel("Estado Actual:");
        lblEstadoActual.setFont(new Font("Arial", Font.PLAIN, 12));
        lblEstadoActual.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelAcciones.add(lblEstadoActual);
        panelAcciones.add(Box.createRigidArea(new Dimension(0, 5)));

        List<Estado> estados = estadoDAO.obtenerTodosLosEstados();
        JComboBox<Estado> cmbEstado = new JComboBox<>();
        Estado estadoAnterior = tarea.getEstado();

        for (Estado e : estados) {
            cmbEstado.addItem(e);
            if (e.getIdEstado() == estadoAnterior.getIdEstado()) {
                cmbEstado.setSelectedItem(e);
            }
        }
        cmbEstado.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Estado) {
                    setText(((Estado) value).getDescripcionEstado());
                }
                return this;
            }
        });

        cmbEstado.addActionListener(e -> {
            boolean debeRecargar = controladorTecnico.actualizarEstado(this, tarea,
                    (Estado) cmbEstado.getSelectedItem(), estadoAnterior.getIdEstado(), cmbEstado);
            if (debeRecargar) {
                recargar();
            }
        });
        panelAcciones.add(cmbEstado);
        return panelAcciones;
    }
}
