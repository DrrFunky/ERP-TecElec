package l2madevs.vista.base;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import l2madevs.dao.EstadoDAO;
import l2madevs.dao.RegistroSolicitudDAO;
import l2madevs.modelo.RegistroSolicitud;
import l2madevs.modelo.Usuario;
import l2madevs.vista.componentes.TareaVisualPanel;
import l2madevs.vista.util.VistaUtils;

/** Base común para las listas de solicitudes del gestor y del técnico. */
public abstract class PanelMostrarSolicitudes extends JPanel {

    protected final Color COLOR_FONDO_CLARO = new Color(245, 245, 245);
    protected final Color COLOR_PRIORIDAD_ALTA = new Color(220, 53, 69);
    protected final Color COLOR_PRIORIDAD_MEDIA = new Color(255, 193, 7);
    protected final Color COLOR_PRIORIDAD_BAJA = new Color(40, 167, 69);

    protected final RegistroSolicitudDAO solicitudDAO;
    protected final EstadoDAO estadoDAO;
    protected final Usuario usuarioFiltro;

    protected JPanel panelContenedorTareas;
    protected JScrollPane scrollPane;
    protected List<RegistroSolicitud> listaCompletaSolicitudes = new ArrayList<>();

    public PanelMostrarSolicitudes(Usuario usuario, RegistroSolicitudDAO rsDAO, EstadoDAO eDAO) {
        this.usuarioFiltro = usuario;
        this.solicitudDAO = rsDAO;
        this.estadoDAO = eDAO;

        setLayout(new BorderLayout());
        setBackground(COLOR_FONDO_CLARO);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitulo = new JLabel(getTituloPanel());
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel panelNorth = new JPanel(new BorderLayout());
        panelNorth.add(lblTitulo, BorderLayout.NORTH);
        panelNorth.add(crearPanelFiltros(), BorderLayout.CENTER);
        panelNorth.setBackground(COLOR_FONDO_CLARO);
        add(panelNorth, BorderLayout.NORTH);

        panelContenedorTareas = new JPanel();
        panelContenedorTareas.setLayout(new BoxLayout(panelContenedorTareas, BoxLayout.Y_AXIS));
        panelContenedorTareas.setBackground(Color.WHITE);

        scrollPane = new JScrollPane(panelContenedorTareas);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
    }

    public void inicializarVista() {
        recargar();
    }

    /** Recarga los datos en segundo plano y repinta la lista en el EDT. */
    public void recargar() {
        VistaUtils.enSegundoPlano(this, this::obtenerDatosFuente, lista -> {
            this.listaCompletaSolicitudes = lista == null ? new ArrayList<>() : lista;
            filtrarYMostrarTareas(null);
        });
    }

    protected abstract String getTituloPanel();

    protected abstract List<RegistroSolicitud> obtenerDatosFuente();

    protected abstract JPanel crearPanelAccionesEstado(RegistroSolicitud tarea);

    protected JPanel crearPanelFiltros() {
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelFiltros.setBackground(COLOR_FONDO_CLARO);

        JLabel lblFiltrar = new JLabel("FILTRAR POR PRIORIDAD:");
        lblFiltrar.setFont(new Font("Arial", Font.BOLD, 12));
        panelFiltros.add(lblFiltrar);
        panelFiltros.add(crearBotonFiltroPrioridad("Todas", null, Color.DARK_GRAY));
        panelFiltros.add(crearBotonFiltroPrioridad("Alta", 1, COLOR_PRIORIDAD_ALTA));
        panelFiltros.add(crearBotonFiltroPrioridad("Media", 2, COLOR_PRIORIDAD_MEDIA));
        panelFiltros.add(crearBotonFiltroPrioridad("Baja", 3, COLOR_PRIORIDAD_BAJA));
        return panelFiltros;
    }

    protected JButton crearBotonFiltroPrioridad(String texto, Integer idPrioridad, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground((color == COLOR_PRIORIDAD_MEDIA || color == COLOR_PRIORIDAD_BAJA)
                ? Color.BLACK : Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setActionCommand(idPrioridad == null ? "TODAS" : String.valueOf(idPrioridad));
        btn.addActionListener(e -> {
            String command = e.getActionCommand();
            filtrarYMostrarTareas("TODAS".equals(command) ? null : Integer.parseInt(command));
        });
        return btn;
    }

    public void filtrarYMostrarTareas(Integer idPrioridadFiltro) {
        panelContenedorTareas.removeAll();

        List<RegistroSolicitud> tareasFiltradas = (idPrioridadFiltro == null)
                ? listaCompletaSolicitudes
                : listaCompletaSolicitudes.stream()
                        .filter(t -> t.getTipoPrioridad() != null
                                && t.getTipoPrioridad().getIdTipoPrioridad() == idPrioridadFiltro)
                        .collect(Collectors.toList());

        if (tareasFiltradas.isEmpty()) {
            panelContenedorTareas.add(new JLabel("No hay solicitudes que coincidan con el filtro."));
        } else {
            for (RegistroSolicitud tarea : tareasFiltradas) {
                panelContenedorTareas.add(crearPanelTarea(tarea));
                panelContenedorTareas.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        panelContenedorTareas.revalidate();
        panelContenedorTareas.repaint();
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
    }

    protected JPanel crearPanelTarea(RegistroSolicitud tarea) {
        JPanel panelItemCompleto = new JPanel(new BorderLayout(10, 0));
        panelItemCompleto.setBackground(Color.WHITE);
        panelItemCompleto.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        panelItemCompleto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        panelItemCompleto.setPreferredSize(new Dimension(600, 150));

        panelItemCompleto.add(new TareaVisualPanel(tarea), BorderLayout.CENTER);

        JPanel panelAccionesDerecha = new JPanel(new BorderLayout());
        panelAccionesDerecha.setOpaque(false);

        JButton btnVerDetalles = new JButton("Ver Detalles");
        btnVerDetalles.setFont(new Font("Arial", Font.BOLD, 12));
        btnVerDetalles.setBackground(new Color(40, 167, 69));
        btnVerDetalles.setForeground(Color.WHITE);
        btnVerDetalles.setFocusPainted(false);
        btnVerDetalles.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnVerDetalles.addActionListener(e -> VistaUtils.mostrarDetallesSolicitud(this, tarea));

        JPanel panelDetalleBoton = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panelDetalleBoton.setOpaque(false);
        panelDetalleBoton.setBorder(new EmptyBorder(5, 5, 5, 5));
        panelDetalleBoton.add(btnVerDetalles);

        JPanel panelAccionesRol = crearPanelAccionesEstado(tarea);
        panelAccionesRol.setBorder(new EmptyBorder(10, 10, 10, 10));

        panelAccionesDerecha.add(panelDetalleBoton, BorderLayout.NORTH);
        panelAccionesDerecha.add(panelAccionesRol, BorderLayout.CENTER);
        panelItemCompleto.add(panelAccionesDerecha, BorderLayout.EAST);
        return panelItemCompleto;
    }
}
