package l2madevs.vista;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import l2madevs.dao.EstadoDAO;
import l2madevs.dao.RegistroSolicitudDAO;
import l2madevs.dao.TipoPrioridadDAO;
import l2madevs.dao.TipoSolicitudDAO;
import l2madevs.dao.UsuarioDAO;
import l2madevs.modelo.RegistroSolicitud;
import l2madevs.modelo.Usuario;

/** Ventana principal del rol gestor. */
public class VentanaGestor extends JFrame {

    private static final Color COLOR_FONDO_NAV = new Color(10, 38, 40);
    private static final Color COLOR_FONDO_PANEL = new Color(220, 220, 220);

    private JPanel panelPrincipal;
    private CardLayout cardLayout;

    private PanelCrearSolicitud panelCrearSolicitud;
    private PanelVerSolicitudesGestor panelVerSolicitudes;
    private PanelAsignarTecnico panelAsignarTecnico;

    private final Usuario gestorLogueado;
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final EstadoDAO estadoDAO = new EstadoDAO();
    private final RegistroSolicitudDAO solicitudDAO = new RegistroSolicitudDAO();
    private final TipoPrioridadDAO tipoPrioridadDAO = new TipoPrioridadDAO();
    private final TipoSolicitudDAO tipoSolicitudDAO = new TipoSolicitudDAO();

    public VentanaGestor(Usuario gestor) {
        this.gestorLogueado = gestor;

        setTitle("Panel de Gestión - " + gestor.getNombreCompleto());
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelNavegacion = crearPanelNavegacion();
        add(panelNavegacion, BorderLayout.WEST);

        inicializarComponentes();
        add(panelPrincipal, BorderLayout.CENTER);
    }

    private JPanel crearPanelNavegacion() {
        JPanel panelNavegacion = new JPanel(new GridBagLayout());
        panelNavegacion.setBackground(COLOR_FONDO_NAV);
        panelNavegacion.setPreferredSize(new Dimension(250, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 20, 10, 20);

        JButton btnCrear = new JButton("Crear Solicitud");
        JButton btnVer = new JButton("Ver Solicitudes");
        estilizarBoton(btnCrear);
        estilizarBoton(btnVer);
        panelNavegacion.add(btnCrear, gbc);
        panelNavegacion.add(btnVer, gbc);

        btnCrear.addActionListener(e -> cardLayout.show(panelPrincipal, "CREAR"));
        btnVer.addActionListener(e -> {
            panelVerSolicitudes.recargar();
            cardLayout.show(panelPrincipal, "VER");
        });
        return panelNavegacion;
    }

    private void inicializarComponentes() {
        cardLayout = new CardLayout();
        panelPrincipal = new JPanel(cardLayout);
        panelPrincipal.setBackground(COLOR_FONDO_PANEL);

        panelCrearSolicitud = new PanelCrearSolicitud(
                usuarioDAO, tipoPrioridadDAO, tipoSolicitudDAO, solicitudDAO);
        panelVerSolicitudes = new PanelVerSolicitudesGestor(
                gestorLogueado, solicitudDAO, estadoDAO);
        panelAsignarTecnico = new PanelAsignarTecnico(usuarioDAO, solicitudDAO);

        panelAsignarTecnico.addCancelarListener(e -> {
            panelVerSolicitudes.recargar();
            cardLayout.show(panelPrincipal, "VER");
        });

        panelPrincipal.add(panelCrearSolicitud, "CREAR");
        panelPrincipal.add(panelVerSolicitudes, "VER");
        panelPrincipal.add(panelAsignarTecnico, "ASIGNAR_FORM");

        panelVerSolicitudes.inicializarVista();
        cardLayout.show(panelPrincipal, "VER");
    }

    public void mostrarPanelAsignacion(RegistroSolicitud solicitud) {
        panelAsignarTecnico.cargarDatosAsignacion(solicitud);
        cardLayout.show(panelPrincipal, "ASIGNAR_FORM");
    }

    private void estilizarBoton(JButton boton) {
        boton.setFont(new Font("Arial", Font.BOLD, 16));
        boton.setBackground(Color.WHITE);
        boton.setForeground(Color.BLACK);
        boton.setFocusPainted(false);
        boton.setPreferredSize(new Dimension(180, 50));
        boton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
}
