package tests;

import l2madevs.modelo.TipoPrioridad;
import l2madevs.modelo.TipoSolicitud;
import l2madevs.util.Validaciones;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class ValidacionesTest {

    private TipoSolicitud tipo() {
        TipoSolicitud tipo = new TipoSolicitud();
        tipo.setDescripcionTipoSolicitud("Soporte");
        return tipo;
    }

    private TipoPrioridad prioridad() {
        TipoPrioridad prioridad = new TipoPrioridad();
        prioridad.setDescripcionTipoPrioridad("Alta");
        return prioridad;
    }

    @Test
    public void datosCompletosSonValidos() {
        String error = Validaciones.validarSolicitud(
                "Ana", "Perez", "ana@example.com", "123456789",
                "Calle 1", "Calle 2", "Revisar cableado", tipo(), prioridad());

        assertNull(error);
    }

    @Test
    public void campoVacioDevuelveMensaje() {
        String error = Validaciones.validarSolicitud(
                "", "Perez", "ana@example.com", "123456789",
                "Calle 1", "Calle 2", "Revisar cableado", tipo(), prioridad());

        assertEquals("Por favor, complete todos los campos obligatorios.", error);
    }

    @Test
    public void correoInvalidoDevuelveMensaje() {
        String error = Validaciones.validarSolicitud(
                "Ana", "Perez", "ana@@example", "123456789",
                "Calle 1", "Calle 2", "Revisar cableado", tipo(), prioridad());

        assertEquals("El correo electrónico no es válido.", error);
    }

    @Test
    public void catalogosObligatoriosDevuelvenMensaje() {
        String sinTipo = Validaciones.validarSolicitud(
                "Ana", "Perez", "ana@example.com", "123456789",
                "Calle 1", "Calle 2", "Revisar cableado", null, prioridad());
        String sinPrioridad = Validaciones.validarSolicitud(
                "Ana", "Perez", "ana@example.com", "123456789",
                "Calle 1", "Calle 2", "Revisar cableado", tipo(), null);

        assertEquals("Seleccione un tipo de solicitud.", sinTipo);
        assertEquals("Seleccione una prioridad.", sinPrioridad);
    }

    @Test
    public void estaVacioDetectaNulosYEspacios() {
        assertTrue(Validaciones.estaVacio(null));
        assertTrue(Validaciones.estaVacio("   "));
        assertFalse(Validaciones.estaVacio(" dato "));
    }
}
