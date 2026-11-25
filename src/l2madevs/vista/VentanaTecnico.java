package l2madevs.vista;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import l2madevs.dao.EstadoDAO;
import l2madevs.dao.RegistroSolicitudDAO;
import l2madevs.modelo.Usuario;

/** Ventana principal del rol técnico. */
public class VentanaTecnico extends JFrame {

    private static final Color COLOR_FONDO_NAV = new Color(10, 38, 40);
    private static final Color COLOR_FONDO_PANEL = new Color(220, 220, 220);
    private static final Color COLOR_BOTON_NAV = new Color(26, 179, 200);

    private JPanel panelPrincipal;
    private CardLayout cardLayout;

    private final PanelTareasAsignadas panelTareasAsignadas;
    private final PanelHistorialTrabajos panelHistorialTrabajos;

    private final Usuario tecnico;
    private final RegistroSolicitudDAO solicitudDAO = new RegistroSolicitudDAO();
    private final EstadoDAO estadoDAO = new EstadoDAO();

    public VentanaTecnico(Usuario tecnico) {
        this.tecnico = tecnico;
        configurarVentana();

        JPanel panelNavegacion = crearPanelNavegacion();
        add(panelNavegacion, BorderLayout.WEST);

        cardLayout = new CardLayout();
        panelPrincipal = new JPanel(cardLayout);
        panelPrincipal.setBackground(COLOR_FONDO_PANEL);
        add(panelPrincipal, BorderLayout.CENTER);

        panelTareasAsignadas = new PanelTareasAsignadas(tecnico, solicitudDAO, estadoDAO);
        panelHistorialTrabajos = new PanelHistorialTrabajos(
                tecnico, this, solicitudDAO, estadoDAO);
        panelTareasAsignadas.inicializarVista();

        panelPrincipal.add(panelTareasAsignadas, "TAREAS_ACTIVAS");
        panelPrincipal.add(panelHistorialTrabajos, "HISTORIAL");
        cardLayout.show(panelPrincipal, "TAREAS_ACTIVAS");
    }

    private void configurarVentana() {
        setTitle("Sistema de Gestión de Solicitudes - TÉCNICO: " + tecnico.getNombre());
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private JPanel crearPanelNavegacion() {
        JPanel panelNav = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        panelNav.setBackground(COLOR_FONDO_NAV);
        panelNav.setPreferredSize(new Dimension(220, 0));

        JLabel lblBienvenida = new JLabel(
                "<html><div style='text-align: center;'>Bienvenido<br>Técnico</div></html>");
        lblBienvenida.setForeground(Color.WHITE);
        lblBienvenida.setFont(new Font("Arial", Font.BOLD, 18));
        lblBienvenida.setPreferredSize(new Dimension(200, 50));
        panelNav.add(lblBienvenida);

        JButton btnTareasAsignadas = new JButton("Mis Tareas");
        JButton btnHistorialTrabajos = new JButton("Historial Trabajos");
        estilizarBoton(btnTareasAsignadas);
        estilizarBoton(btnHistorialTrabajos);
        panelNav.add(btnTareasAsignadas);
        panelNav.add(btnHistorialTrabajos);

        btnTareasAsignadas.addActionListener(e -> {
            recargarPanelTareasActivas();
            cardLayout.show(panelPrincipal, "TAREAS_ACTIVAS");
        });
        btnHistorialTrabajos.addActionListener(e -> {
            panelHistorialTrabajos.recargar();
            cardLayout.show(panelPrincipal, "HISTORIAL");
        });
        return panelNav;
    }

    private void estilizarBoton(JButton boton) {
        boton.setFont(new Font("Arial", Font.BOLD, 16));
        boton.setBackground(COLOR_BOTON_NAV);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setPreferredSize(new Dimension(180, 50));
        boton.setBorderPainted(false);
        boton.setOpaque(true);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    public void recargarPanelTareasActivas() {
        if (panelTareasAsignadas != null) {
            panelTareasAsignadas.recargar();
        }
    }
}
