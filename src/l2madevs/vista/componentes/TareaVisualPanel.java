package l2madevs.vista.componentes;

import l2madevs.modelo.RegistroSolicitud;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TareaVisualPanel extends JPanel {
    //Colores
    private final Color COLOR_PRIORIDAD_ALTA = new Color(220, 53, 69);
    private final Color COLOR_PRIORIDAD_MEDIA = new Color(255, 193, 7);
    private final Color COLOR_PRIORIDAD_BAJA = new Color(40, 167, 69);
    private final Font FONT_ETIQUETA_PLAIN = new Font("Arial", Font.PLAIN, 14);

    public TareaVisualPanel(RegistroSolicitud tarea) {
        //Panel Principal
        setLayout(new BorderLayout(10, 0));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        //Indicador de Prioridad
        String prioridadDesc = (tarea.getTipoPrioridad() != null) ? tarea.getTipoPrioridad().getDescripcionTipoPrioridad() : "N/A";
        JPanel indicador = new JPanel();
        indicador.setPreferredSize(new Dimension(5, 0));
        indicador.setBackground(getColorPorPrioridad(prioridadDesc));
        add(indicador, BorderLayout.WEST);

        //Contenido (CENTER)
        JPanel panelContenido = new JPanel(new BorderLayout(20, 0));
        panelContenido.setBackground(Color.WHITE);

        //Detalles
        JPanel panelDetalles = new JPanel();
        panelDetalles.setLayout(new BoxLayout(panelDetalles, BoxLayout.Y_AXIS));
        panelDetalles.setBackground(Color.WHITE);
        panelDetalles.setAlignmentX(Component.LEFT_ALIGNMENT);

        //Fila 0: ID Solicitud
        JLabel lblID = new JLabel("ID Solicitud: " + tarea.getIdSolicitud());
        lblID.setFont(new Font("Arial", Font.BOLD, 14));
        lblID.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDetalles.add(lblID);
        panelDetalles.add(Box.createRigidArea(new Dimension(0, 3)));

        //Fila 1: Descripción breve
        String descTexto = (tarea.getDescripcionSolicitud() != null) ? tarea.getDescripcionSolicitud() : "";
        JTextArea areaDesc = new JTextArea("Descripción Breve: " + descTexto);
        areaDesc.setFont(FONT_ETIQUETA_PLAIN);
        areaDesc.setEditable(false);
        areaDesc.setLineWrap(true);
        areaDesc.setWrapStyleWord(true);
        areaDesc.setOpaque(false);
        areaDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDetalles.add(areaDesc);
        panelDetalles.add(Box.createRigidArea(new Dimension(0, 3)));

        //Fila 2: Prioridad
        JLabel lblPrioridad = new JLabel("Prioridad: " + prioridadDesc.toUpperCase());
        lblPrioridad.setFont(FONT_ETIQUETA_PLAIN);
        lblPrioridad.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDetalles.add(lblPrioridad);
        panelDetalles.add(Box.createRigidArea(new Dimension(0, 3)));

        //Fila 3: Ubicación
        String ubicacionOFecha = (tarea.getFechaRealizarTrabajo() != null)
                                   ? "Fecha Límite: " + tarea.getFechaRealizarTrabajo().toLocalDate().toString()
                                   : "Ubicación: " + tarea.getUbicacion();
        JLabel lblUbicacion = new JLabel(ubicacionOFecha);
        lblUbicacion.setFont(FONT_ETIQUETA_PLAIN);
        lblUbicacion.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDetalles.add(lblUbicacion);
        panelDetalles.add(Box.createRigidArea(new Dimension(0, 3)));

         //Fila 4: Estado (Solo mostrar)
        String estadoDesc = (tarea.getEstado() != null) ? tarea.getEstado().getDescripcionEstado() : "N/A";
        JLabel lblEstado = new JLabel("Estado Actual: " + estadoDesc);
        lblEstado.setFont(FONT_ETIQUETA_PLAIN);
        lblEstado.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDetalles.add(lblEstado);

        panelContenido.add(panelDetalles, BorderLayout.CENTER);
        add(panelContenido, BorderLayout.CENTER);
    }

    private Color getColorPorPrioridad(String prioridad) {
        if (prioridad == null) return Color.GRAY;
        switch (prioridad.toLowerCase()) {
            case "alta": return COLOR_PRIORIDAD_ALTA;
            case "media": return COLOR_PRIORIDAD_MEDIA;
            case "baja": return COLOR_PRIORIDAD_BAJA;
            default: return Color.GRAY;
        }
    }
}