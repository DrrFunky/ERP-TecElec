package l2madevs.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import l2madevs.controlador.Tecnico;
import l2madevs.dao.EstadoDAO;
import l2madevs.dao.RegistroSolicitudDAO;
import l2madevs.modelo.RegistroSolicitud;
import l2madevs.modelo.Usuario;
import l2madevs.vista.util.VistaUtils;

/**
 * Historial de trabajos finalizados. Ahora se lee de la base de datos en lugar
 * de una lista temporal en memoria: sobrevive al cierre de la aplicación.
 */
public class PanelHistorialTrabajos extends JPanel {

    private final Color COLOR_FONDO_CLARO = new Color(245, 245, 245);
    private final JPanel panelContenedorHistorial = new JPanel();
    private final Tecnico controladorTecnico;
    private final int idTecnico;
    private final Frame ventanaPadre;
    private List<RegistroSolicitud> listaHistorialActual = new ArrayList<>();

    public PanelHistorialTrabajos(Usuario tecnico, Frame padre,
            RegistroSolicitudDAO solicitudDAO, EstadoDAO estadoDAO) {
        this.idTecnico = tecnico.getIdUsuario();
        this.ventanaPadre = padre;
        this.controladorTecnico = new Tecnico(solicitudDAO, estadoDAO);

        setLayout(new BorderLayout());
        setBackground(COLOR_FONDO_CLARO);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitulo = new JLabel("HISTORIAL DE TRABAJOS FINALIZADOS");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(lblTitulo, BorderLayout.NORTH);

        panelContenedorHistorial.setLayout(new BoxLayout(panelContenedorHistorial, BoxLayout.Y_AXIS));
        panelContenedorHistorial.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(panelContenedorHistorial);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        recargar();
    }

    public void recargar() {
        RegistroSolicitudDAO dao = controladorTecnico.getSolicitudDAO();
        VistaUtils.enSegundoPlano(this,
                () -> dao.obtenerSolicitudesFinalizadasPorTecnico(idTecnico),
                this::actualizarHistorial);
    }

    public void actualizarHistorial(List<RegistroSolicitud> historial) {
        panelContenedorHistorial.removeAll();
        this.listaHistorialActual = historial == null ? new ArrayList<>() : historial;

        if (listaHistorialActual.isEmpty()) {
            JLabel lblVacio = new JLabel("No hay trabajos finalizados registrados.");
            lblVacio.setFont(new Font("Arial", Font.ITALIC, 16));
            lblVacio.setBorder(new EmptyBorder(10, 10, 10, 10));
            panelContenedorHistorial.add(lblVacio);
        } else {
            for (RegistroSolicitud tarea : listaHistorialActual) {
                panelContenedorHistorial.add(crearPanelTareaHistorial(tarea));
                panelContenedorHistorial.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        panelContenedorHistorial.revalidate();
        panelContenedorHistorial.repaint();
    }

    private JPanel crearPanelTareaHistorial(RegistroSolicitud tarea) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(new Color(230, 230, 230));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                new EmptyBorder(10, 10, 10, 10)));

        JLabel lblInfo = new JLabel("ID " + tarea.getIdSolicitud()
                + " | Cliente: " + tarea.getCliente().getNombreCompleto()
                + " | Estado: " + tarea.getEstado().getDescripcionEstado());
        lblInfo.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(lblInfo, BorderLayout.NORTH);

        JLabel lblDesc = new JLabel("Descripción: " + tarea.getDescripcionSolicitud());
        panel.add(lblDesc, BorderLayout.CENTER);

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBoton.setBackground(new Color(230, 230, 230));

        JButton btnReactivar = new JButton("Reactivar Tarea");
        btnReactivar.setFont(new Font("Arial", Font.BOLD, 10));
        btnReactivar.setBackground(new Color(255, 99, 71));
        btnReactivar.setForeground(Color.WHITE);
        btnReactivar.addActionListener(e -> {
            if (controladorTecnico.reactivarSolicitud(this, tarea)) {
                recargar();
                if (ventanaPadre instanceof VentanaTecnico) {
                    ((VentanaTecnico) ventanaPadre).recargarPanelTareasActivas();
                }
            }
        });

        JButton btnVerDetalles = new JButton("Ver Detalles");
        btnVerDetalles.setFont(new Font("Arial", Font.BOLD, 10));
        btnVerDetalles.setBackground(Color.GRAY);
        btnVerDetalles.setForeground(Color.WHITE);
        btnVerDetalles.addActionListener(e -> VistaUtils.mostrarDetallesSolicitud(this, tarea));

        panelBoton.add(btnReactivar);
        panelBoton.add(btnVerDetalles);
        panel.add(panelBoton, BorderLayout.SOUTH);
        return panel;
    }
}
