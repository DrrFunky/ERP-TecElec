package l2madevs.vista;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import l2madevs.controlador.Gestor;
import l2madevs.dao.RegistroSolicitudDAO;
import l2madevs.dao.TipoPrioridadDAO;
import l2madevs.dao.TipoSolicitudDAO;
import l2madevs.dao.UsuarioDAO;
import l2madevs.modelo.TipoPrioridad;
import l2madevs.modelo.TipoSolicitud;
import l2madevs.vista.util.VistaUtils;

/** Formulario de alta de cliente + solicitud. */
public class PanelCrearSolicitud extends JPanel {

    private static final Color COLOR_BOTON = new Color(26, 179, 200);
    private static final Color COLOR_BLANCO = Color.WHITE;
    private static final Color COLOR_FONDO_PANEL = new Color(12, 48, 51);

    private final TipoPrioridadDAO tipoPrioridadDAO;
    private final TipoSolicitudDAO tipoSolicitudDAO;
    private final Gestor gestor;

    private final JTextField txtClienteNombre = new JTextField(15);
    private final JTextField txtClienteAPaterno = new JTextField(15);
    private final JTextField txtClienteAMaterno = new JTextField(15);
    private final JTextField txtClienteCorreo = new JTextField(15);
    private final JTextField txtClienteTelefono = new JTextField(15);
    private final JTextField txtClienteDireccion = new JTextField(15);
    private final JComboBox<TipoSolicitud> comboTipoSolicitud = new JComboBox<>();
    private final JComboBox<TipoPrioridad> comboPrioridad = new JComboBox<>();
    private final JTextField txtDireccion = new JTextField();
    private final JTextArea txtDescripcion = new JTextArea(5, 50);
    private final JButton btnGuardarSolicitud = new JButton("Guardar Solicitud");

    public PanelCrearSolicitud(UsuarioDAO uDAO, TipoPrioridadDAO pDAO,
            TipoSolicitudDAO tsDAO, RegistroSolicitudDAO rsDAO) {
        this.tipoPrioridadDAO = pDAO;
        this.tipoSolicitudDAO = tsDAO;
        this.gestor = new Gestor(uDAO, rsDAO);

        setBackground(COLOR_FONDO_PANEL);
        setBorder(new EmptyBorder(20, 20, 20, 40));
        setLayout(new GridBagLayout());
        initComponents();
        cargarDatosCombos();
    }

    private void initComponents() {
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel lblTitulo = new JLabel("Creación de Solicitud");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setForeground(COLOR_BLANCO);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 30, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        add(lblTitulo, gbc);

        JPanel panelCliente = new JPanel(new GridBagLayout());
        panelCliente.setBackground(COLOR_FONDO_PANEL);
        TitledBorder bordeCliente = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Datos del Nuevo Cliente",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 14),
                Color.WHITE);
        panelCliente.setBorder(bordeCliente);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 10);
        gc.anchor = GridBagConstraints.EAST;

        anadirCampo(panelCliente, gc, 0, 0, "Nombre:", txtClienteNombre);
        anadirCampo(panelCliente, gc, 2, 0, "A. Paterno:", txtClienteAPaterno);
        anadirCampo(panelCliente, gc, 0, 1, "A. Materno:", txtClienteAMaterno);
        anadirCampo(panelCliente, gc, 2, 1, "Correo:", txtClienteCorreo);
        anadirCampo(panelCliente, gc, 0, 2, "Teléfono:", txtClienteTelefono);
        anadirCampo(panelCliente, gc, 2, 2, "Dirección (cliente):", txtClienteDireccion);

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 5, 15, 5);
        add(panelCliente, gbc);

        comboTipoSolicitud.setFont(new Font("Arial", Font.PLAIN, 16));
        comboTipoSolicitud.setRenderer(renderer());
        comboPrioridad.setFont(new Font("Arial", Font.PLAIN, 16));
        comboPrioridad.setRenderer(renderer());

        anadirFila(gbc, 2, "Tipo solicitud:", comboTipoSolicitud);
        anadirFila(gbc, 3, "Prioridad:", comboPrioridad);

        txtDireccion.setFont(new Font("Arial", Font.PLAIN, 16));
        anadirFila(gbc, 4, "Dirección (trabajo):", txtDireccion);

        JLabel lblDesc = new JLabel("Descripción:");
        lblDesc.setForeground(Color.WHITE);
        lblDesc.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        add(lblDesc, gbc);

        txtDescripcion.setFont(new Font("Arial", Font.PLAIN, 16));
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        add(new JScrollPane(txtDescripcion), gbc);

        btnGuardarSolicitud.setFont(new Font("Arial", Font.BOLD, 16));
        btnGuardarSolicitud.setBackground(COLOR_BOTON);
        btnGuardarSolicitud.setForeground(COLOR_BLANCO);
        btnGuardarSolicitud.setOpaque(true);
        btnGuardarSolicitud.setBorderPainted(false);
        btnGuardarSolicitud.setFocusPainted(false);
        btnGuardarSolicitud.setPreferredSize(new Dimension(180, 45));
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(15, 0, 0, 10);
        add(btnGuardarSolicitud, gbc);

        btnGuardarSolicitud.addActionListener(e -> guardar());
    }

    private void anadirCampo(JPanel panel, GridBagConstraints gc,
            int gridx, int gridy, String etiqueta, JTextField campo) {
        JLabel lbl = new JLabel(etiqueta);
        lbl.setForeground(Color.WHITE);
        gc.gridx = gridx;
        gc.gridy = gridy;
        gc.fill = GridBagConstraints.NONE;
        gc.weightx = 0.0;
        panel.add(lbl, gc);

        gc.gridx = gridx + 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;
        panel.add(campo, gc);
    }

    private void anadirFila(GridBagConstraints gbc, int gridy,
            String etiqueta, Component campo) {
        JLabel lbl = new JLabel(etiqueta);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridy = gridy;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(5, 5, 5, 10);
        add(lbl, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        add(campo, gbc);
    }

    private DefaultListCellRenderer renderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof TipoSolicitud) {
                    setText(((TipoSolicitud) value).getDescripcionTipoSolicitud());
                } else if (value instanceof TipoPrioridad) {
                    setText(((TipoPrioridad) value).getDescripcionTipoPrioridad());
                }
                return this;
            }
        };
    }

    private void guardar() {
        btnGuardarSolicitud.setEnabled(false);
        try {
            boolean exito = gestor.guardarSolicitud(this,
                    txtClienteNombre.getText(),
                    txtClienteAPaterno.getText(),
                    txtClienteAMaterno.getText(),
                    txtClienteCorreo.getText(),
                    txtClienteTelefono.getText(),
                    txtClienteDireccion.getText(),
                    (TipoSolicitud) comboTipoSolicitud.getSelectedItem(),
                    (TipoPrioridad) comboPrioridad.getSelectedItem(),
                    txtDireccion.getText(),
                    txtDescripcion.getText());
            if (exito) {
                limpiar();
            }
        } finally {
            btnGuardarSolicitud.setEnabled(true);
        }
    }

    private void limpiar() {
        txtClienteNombre.setText("");
        txtClienteAPaterno.setText("");
        txtClienteAMaterno.setText("");
        txtClienteCorreo.setText("");
        txtClienteTelefono.setText("");
        txtClienteDireccion.setText("");
        txtDireccion.setText("");
        txtDescripcion.setText("");
        if (comboTipoSolicitud.getItemCount() > 0) {
            comboTipoSolicitud.setSelectedIndex(0);
        }
        if (comboPrioridad.getItemCount() > 0) {
            comboPrioridad.setSelectedIndex(0);
        }
    }

    private void cargarDatosCombos() {
        comboTipoSolicitud.setEnabled(false);
        VistaUtils.enSegundoPlano(this, tipoSolicitudDAO::obtenerTodos, tipos -> {
            comboTipoSolicitud.removeAllItems();
            for (TipoSolicitud tipo : tipos) {
                comboTipoSolicitud.addItem(tipo);
            }
            comboTipoSolicitud.setEnabled(true);
        });

        comboPrioridad.setEnabled(false);
        VistaUtils.enSegundoPlano(this, tipoPrioridadDAO::obtenerTodos, prioridades -> {
            comboPrioridad.removeAllItems();
            for (TipoPrioridad prioridad : prioridades) {
                comboPrioridad.addItem(prioridad);
            }
            comboPrioridad.setEnabled(true);
        });
    }
}
