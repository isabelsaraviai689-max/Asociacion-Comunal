package com.sv.grupo11.asociacioncomunal.util;

import com.sv.grupo11.asociacioncomunal.exception.ValidacionException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * Clase de utilidad para proteger contrasenas con PBKDF2-HMAC-SHA256 (compromiso del Avance 2).
 * Formato almacenado: pbkdf2$iteraciones$saltBase64$hashBase64
 * Nunca se guarda la contrasena en claro; en el login se recalcula el hash y se compara.
 */
public final class PasswordUtil {

    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final String PREFIJO = "pbkdf2";
    private static final int ITERACIONES = 100_000;
    private static final int LONGITUD_SALT = 16;
    private static final int LONGITUD_HASH_BITS = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {}

    public static String hash(String contrasenaPlana) {
        if (contrasenaPlana == null || contrasenaPlana.isBlank()) {
            throw new ValidacionException("La contrasena no puede estar vacia.");
        }
        byte[] salt = new byte[LONGITUD_SALT];
        RANDOM.nextBytes(salt);
        byte[] hash = derivar(contrasenaPlana, salt, ITERACIONES);
        return PREFIJO + "$" + ITERACIONES + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    /**
     * Compara una contrasena en claro contra el valor almacenado.
     * Si el valor almacenado no tiene el formato pbkdf2 (cuentas creadas antes de este avance),
     * se compara en claro para permitir la migracion; esas cuentas deben regenerar su contrasena.
     */
    public static boolean verificar(String contrasenaPlana, String almacenada) {
        if (contrasenaPlana == null || almacenada == null) {
            return false;
        }
        if (!esHash(almacenada)) {
            return almacenada.equals(contrasenaPlana); // compatibilidad con datos antiguos
        }
        String[] partes = almacenada.split("\\$");
        int iteraciones = Integer.parseInt(partes[1]);
        byte[] salt = Base64.getDecoder().decode(partes[2]);
        byte[] esperado = Base64.getDecoder().decode(partes[3]);
        byte[] calculado = derivar(contrasenaPlana, salt, iteraciones);
        return MessageDigest.isEqual(esperado, calculado); // comparacion en tiempo constante
    }

    public static boolean esHash(String valor) {
        return valor != null && valor.startsWith(PREFIJO + "$") && valor.split("\\$").length == 4;
    }

    private static byte[] derivar(String contrasena, byte[] salt, int iteraciones) {
        try {
            PBEKeySpec spec = new PBEKeySpec(contrasena.toCharArray(), salt, iteraciones, LONGITUD_HASH_BITS);
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("El entorno no soporta " + ALGORITMO, e);
        }
    }
}
