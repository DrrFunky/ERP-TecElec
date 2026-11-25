package l2madevs.seguridad;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Hash de contraseñas con PBKDF2-HMAC-SHA256 usando únicamente el JDK.
 *
 * <p>Formato almacenado: {@code PBKDF2$iteraciones$saltEnBase64$hashEnBase64}.
 * Esto evita guardar contraseñas en texto plano y no añade dependencias
 * externas al proyecto.</p>
 */
public final class PasswordHasher {

    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final String PREFIJO = "PBKDF2";
    private static final int ITERACIONES = 120_000;
    private static final int TAMANO_SALT = 16;
    private static final int TAMANO_HASH = 256;
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private PasswordHasher() {
    }

    /** Genera un hash nuevo con un salt aleatorio. */
    public static String hash(String contrasena) {
        byte[] salt = new byte[TAMANO_SALT];
        ALEATORIO.nextBytes(salt);
        byte[] hash = derivar(contrasena.toCharArray(), salt, ITERACIONES);
        return PREFIJO + "$" + ITERACIONES + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    /** Comprueba una contraseña contra un valor almacenado. */
    public static boolean verificar(String contrasena, String almacenado) {
        if (contrasena == null || !esHash(almacenado)) {
            return false;
        }
        try {
            String[] partes = almacenado.split("\\$", -1);
            if (partes.length != 4) {
                return false;
            }
            int iteraciones = Integer.parseInt(partes[1]);
            byte[] salt = Base64.getDecoder().decode(partes[2]);
            byte[] esperado = Base64.getDecoder().decode(partes[3]);
            if (iteraciones <= 0 || salt.length == 0 || esperado.length == 0) {
                return false;
            }
            byte[] calculado = derivar(contrasena.toCharArray(), salt, iteraciones);
            return MessageDigest.isEqual(esperado, calculado);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return false;
        }
    }

    /** Indica si el valor ya tiene el formato de hash de esta clase. */
    public static boolean esHash(String valor) {
        return valor != null && valor.startsWith(PREFIJO + "$");
    }

    private static byte[] derivar(char[] contrasena, byte[] salt, int iteraciones) {
        try {
            PBEKeySpec especificacion = new PBEKeySpec(
                    contrasena, salt, iteraciones, TAMANO_HASH);
            try {
                SecretKeyFactory factoria = SecretKeyFactory.getInstance(ALGORITMO);
                return factoria.generateSecret(especificacion).getEncoded();
            } finally {
                especificacion.clearPassword();
            }
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo derivar la contraseña", e);
        }
    }
}
