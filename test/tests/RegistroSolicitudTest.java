package tests;

import l2madevs.modelo.RegistroSolicitud;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.Test;

public class RegistroSolicitudTest {

    @Test
    public void igualdadEstaBasadaEnElId() {
        RegistroSolicitud uno = new RegistroSolicitud();
        RegistroSolicitud dos = new RegistroSolicitud();
        uno.setIdSolicitud(7);
        dos.setIdSolicitud(7);
        dos.setDescripcionSolicitud("Otra descripción");

        assertEquals(uno, dos);
        assertEquals(uno.hashCode(), dos.hashCode());
    }

    @Test
    public void solicitudesConDistintoIdNoSonIguales() {
        RegistroSolicitud uno = new RegistroSolicitud();
        RegistroSolicitud dos = new RegistroSolicitud();
        uno.setIdSolicitud(7);
        dos.setIdSolicitud(8);

        assertNotEquals(uno, dos);
    }
}
