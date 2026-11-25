package tests;

import l2madevs.seguridad.PasswordHasher;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class PasswordHasherTest {

    @Test
    public void hashGeneraValorVerificable() {
        String hash = PasswordHasher.hash("pass123");

        assertTrue(hash.startsWith("PBKDF2$"));
        assertTrue(PasswordHasher.esHash(hash));
        assertTrue(PasswordHasher.verificar("pass123", hash));
    }

    @Test
    public void hashRechazaContrasenaIncorrecta() {
        String hash = PasswordHasher.hash("tech456");

        assertFalse(PasswordHasher.verificar("tech456 ", hash));
        assertFalse(PasswordHasher.verificar("otra", hash));
    }

    @Test
    public void dosHashesDeLaMismaContrasenaSonDistintos() {
        String primero = PasswordHasher.hash("pass123");
        String segundo = PasswordHasher.hash("pass123");

        assertFalse(primero.equals(segundo));
        assertTrue(PasswordHasher.verificar("pass123", primero));
        assertTrue(PasswordHasher.verificar("pass123", segundo));
    }

    @Test
    public void valoresMalFormateadosNoSeAceptan() {
        assertFalse(PasswordHasher.esHash(null));
        assertFalse(PasswordHasher.esHash("pass123"));
        assertFalse(PasswordHasher.verificar("pass123", "PBKDF2$malformado"));
        assertFalse(PasswordHasher.verificar("pass123", "PBKDF2$120000$$"));
    }
}
