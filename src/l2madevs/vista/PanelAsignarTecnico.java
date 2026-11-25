package l2madevs.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import l2madevs.dao.RegistroSolicitudDAO;
import l2madevs.dao.UsuarioDAO;
import l2madevs.modelo.RegistroSolicitud;
import l2madevs.modelo.Usuario;
import l2madevs.servicio.SolicitudService;
import l2madevs.util.Constantes;
import l2madevs.vista.componentes.TareaVisualPanel;
import l2madevs.vista.util.VistaUtils;

/** Formulario para asignar un técnico a una solicitud. */
public class PanelAsignarTecnico extends JPanel {

    private static final Font FONT_TITULO = new Font("Arial", Font.BOLD, 28);
    private static final Font FONT_ETIQUETA_BOLD = new Font("Arial", Font.BOLD, 16);
    private static final Font FONT_ETIQUETA_PLAIN = new Font("Arial", Font.PLAIN, 16);
    private static final Font FONT_BOTON = new Font("Arial", Font.BOLD, 14);

    private final UsuarioDAO usuarioDAO;
    private final SolicitudService solicitudService;

    private final JPanel panelDetalles = new JPanel(new BorderLayout());
    private final JLabel lblTituloSolicitud = new JLabel("Asignar Técnico a Solicitud ID: ");
    private final DefaultListModel<Usuario> listModelTecnicos = new DefaultListModel<>();
    private final JList<Usuario> listaTecnicosDisponibles = new JList<>(listModelTecnicos);
    private final JButton btnAsignarSeleccionado = new JButton("Asignar Técnico Seleccionado");
    private final JButton btnCancelar = new JButton("Cancelar / Volver");
    private final JButton btnVerDetalles = new JButton("Ver Detalles");

    private RegistroSolicitud solicitudActual;

    public PanelAsignarTecnico(UsuarioDAO uDAO, RegistroSolicitudDAO rsDAO) {
        this.usuarioDAO = uDAO;
        this.solicitudService = new SolicitudService(uDAO, rsDAO);

        setOpaque(false);
        setLayout(new BorderLayout(10, 15));
        setBorder(new EmptyBorder(20, 40, 20, 40));
        initComponents();
    }

    private void initComponents() {
        JPanel panelSuperior = new JPanel(new BorderLayout(5, 10));
        panelSuperior.setOpaque(false);

        lblTituloSolicitud.setFont(FONT_TITULO);
        panelSuperior.add(lblTituloSolicitud, BorderLayout.NORTH);
        panelDetalles.setOpaque(false);
        panelSuperior.add(panelDetalles, BorderLayout.CENTER);

        btnVerDetalles.setFont(new Font("Arial", Font.BOLD, 12));
        btnVerDetalles.setBackground(new Color(40, 167, 69));
        btnVerDetalles.setForeground(Color.WHITE);
        btnVerDetalles.setFocusPainted(false);
        btnVerDetalles.addActionListener(e -> {
            if (solicitudActual != null) {
                VistaUtils.mostrarDetallesSolicitud(this, solicitudActual);
            }
        });

        JPanel panelBotonWrapper = new JPanel(new GridBagLayout());
        panelBotonWrapper.setOpaque(false);
        panelBotonWrapper.setBorder(new EmptyBorder(0, 10, 0, 10));
        panelBotonWrapper.add(btnVerDetalles);
        panelSuperior.add(panelBotonWrapper, BorderLayout.EAST);
        add(panelSuperior, BorderLayout.NORTH);

        JPanel panelCentral = new JPanel(new BorderLayout(5, 5));
        panelCentral.setOpaque(false);
        JLabel lblTecnicos = new JLabel("Seleccione un Técnico Disponible:");
        lblTecnicos.setFont(FONT_ETIQUETA_BOLD);
        panelCentral.add(lblTecnicos, BorderLayout.NORTH);

        listaTecnicosDisponibles.setFont(FONT_ETIQUETA_PLAIN);
        listaTecnicosDisponibles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaTecnicosDisponibles.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Usuario) {
                    setText(((Usuario) value).getNombreCompleto());
                }
                return this;
            }
        });
        JScrollPane scrollTecnicos = new JScrollPane(listaTecnicosDisponibles);
        scrollTecnicos.setPreferredSize(new Dimension(400, 220));
        panelCentral.add(scrollTecnicos, BorderLayout.CENTER);
        add(panelCentral, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);
        estilizarBoton(btnAsignarSeleccionado, new Color(23, 162, 184));
        estilizarBoton(btnCancelar, new Color(23, 162, 184));
        btnAsignarSeleccionado.addActionListener(e -> asignarTecnico());
        panelBotones.add(btnAsignarSeleccionado);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void estilizarBoton(JButton boton, Color color) {
        boton.setFont(FONT_BOTON);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setOpaque(true);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
    }

    public void cargarDatosAsignacion(RegistroSolicitud solicitud) {
        this.solicitudActual = solicitud;
        lblTituloSolicitud.setText("Asignar Técnico a Solicitud ID: " + solicitud.getIdSolicitud());

        panelDetalles.removeAll();
        panelDetalles.add(new TareaVisualPanel(solicitud), BorderLayout.CENTER);
        panelDetalles.revalidate();
        panelDetalles.repaint();

        cargarTecnicosDisponibles();
    }

    private void cargarTecnicosDisponibles() {
        listModelTecnicos.clear();
        listaTecnicosDisponibles.setEnabled(false);
        VistaUtils.enSegundoPlano(this, usuarioDAO::obtenerTodosLosTecnicos, tecnicos -> {
            listModelTecnicos.clear();
            for (Usuario tecnico : tecnicos) {
                listModelTecnicos.addElement(tecnico);
            }
            listaTecnicosDisponibles.setEnabled(true);
            if (listModelTecnicos.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No hay técnicos registrados en la base de datos.",
                        "Sin técnicos", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    private void asignarTecnico() {
        Usuario tecnicoSeleccionado = listaTecnicosDisponibles.getSelectedValue();
        if (tecnicoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un técnico de la lista.",
                    "Selección Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (solicitudActual == null) {
            JOptionPane.showMessageDialog(this, "No hay una solicitud cargada para asignar.",
                    "Error Interno", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea asignar la solicitud ID "
                        + solicitudActual.getIdSolicitud() + " al técnico "
                        + tecnicoSeleccionado.getNombreCompleto() + "?",
                "Confirmar Asignación", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        boolean cambiarEstado = solicitudActual.getEstado() != null
                && solicitudActual.getEstado().getIdEstado() == Constantes.ESTADO_INICIAR;
        try {
            boolean exito = solicitudService.asignarTecnico(
                    solicitudActual.getIdSolicitud(), tecnicoSeleccionado.getIdUsuario(), cambiarEstado);
            if (!exito) {
                JOptionPane.showMessageDialog(this,
                        "No se pudo guardar la asignación. No se realizaron cambios.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (cambiarEstado) {
                solicitudActual.getEstado().setIdEstado(Constantes.ESTADO_EN_PROGRESO);
            }
            JOptionPane.showMessageDialog(this,
                    cambiarEstado
                            ? "Técnico asignado y estado actualizado a 'En progreso'."
                            : "Técnico asignado correctamente.",
                    "Asignación Completa", JOptionPane.INFORMATION_MESSAGE);
            btnCancelar.doClick();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error durante la asignación: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void addCancelarListener(java.awt.event.ActionListener listener) {
        btnCancelar.addActionListener(listener);
    }
}
