package l2madevs.util;

import l2madevs.modelo.TipoPrioridad;
import l2madevs.modelo.TipoSolicitud;

/** Validaciones de negocio independientes de Swing, para poder probarlas. */
public final class Validaciones {

    private Validaciones() {
    }

    public static boolean estaVacio(String valor) {
        return valor == null || valor.isBlank();
    }

    public static boolean correoValido(String correo) {
        return correo != null && correo.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }

    /**
     * @return {@code null} si todo es válido; si no, el mensaje a mostrar.
     */
    public static String validarSolicitud(String nombre, String apaterno, String correo,
            String telefono, String direccionCliente, String direccionTrabajo,
            String descripcion, TipoSolicitud tipo, TipoPrioridad prioridad) {

        if (estaVacio(nombre) || estaVacio(apaterno) || estaVacio(correo)
                || estaVacio(telefono) || estaVacio(direccionCliente)
                || estaVacio(direccionTrabajo) || estaVacio(descripcion)) {
            return "Por favor, complete todos los campos obligatorios.";
        }
        if (!correoValido(correo)) {
            return "El correo electrónico no es válido.";
        }
        if (tipo == null) {
            return "Seleccione un tipo de solicitud.";
        }
        if (prioridad == null) {
            return "Seleccione una prioridad.";
        }
        return null;
    }
}
