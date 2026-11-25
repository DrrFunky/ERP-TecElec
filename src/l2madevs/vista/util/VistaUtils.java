package l2madevs.vista.util;

import java.awt.Component;
import java.awt.Dimension;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import l2madevs.modelo.RegistroSolicitud;

public final class VistaUtils {

    private VistaUtils() {
    }

    /**
     * Ejecuta una consulta fuera del hilo de interfaz y entrega el resultado
     * en el EDT. Así la ventana no se congela mientras se accede a la BD.
     */
    public static <T> void enSegundoPlano(Component parent, Callable<T> tarea,
            Consumer<T> alTerminar) {
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return tarea.call();
            }

            @Override
            protected void done() {
                try {
                    alTerminar.accept(get());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    Throwable causa = e.getCause() == null ? e : e.getCause();
                    JOptionPane.showMessageDialog(parent,
                            "No se pudieron cargar los datos: " + causa.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    public static void mostrarDetallesSolicitud(Component parent, RegistroSolicitud tarea) {
        StringBuilder detalles = new StringBuilder();
        detalles.append("ID Solicitud: ").append(tarea.getIdSolicitud()).append("\n");
        detalles.append("Cliente: ").append(tarea.getCliente().getNombreCompleto()).append("\n");
        detalles.append("------------------------------------------\n");
        detalles.append("Tipo: ").append(tarea.getTipoSolicitud().getDescripcionTipoSolicitud()).append("\n");
        detalles.append("Prioridad: ").append(tarea.getTipoPrioridad().getDescripcionTipoPrioridad()).append("\n");
        detalles.append("Ubicación: ").append(tarea.getUbicacion()).append("\n");
        detalles.append("Estado Actual: ").append(tarea.getEstado().getDescripcionEstado()).append("\n");
        detalles.append("------------------------------------------\n");
        detalles.append("DESCRIPCIÓN COMPLETA:\n").append(tarea.getDescripcionSolicitud()).append("\n");
        detalles.append("------------------------------------------\n");

        appendFecha(detalles, "Fecha Creación", tarea.getFechaCreacion());
        appendFecha(detalles, "Fecha Fin", tarea.getFechaFin());
        appendFecha(detalles, "Fecha Límite Tarea", tarea.getFechaRealizarTrabajo());

        JTextArea textArea = new JTextArea(detalles.toString());
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        JOptionPane.showMessageDialog(parent, scrollPane,
                "Detalles de Solicitud ID: " + tarea.getIdSolicitud(),
                JOptionPane.INFORMATION_MESSAGE);
    }

    private static void appendFecha(StringBuilder sb, String etiqueta, java.time.LocalDateTime fecha) {
        sb.append(etiqueta).append(": ")
                .append(fecha == null ? "N/A" : fecha.toLocalDate()).append("\n");
    }
}
