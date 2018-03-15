package sw806f18.server;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Security {
    private static final Random RANDOM = new SecureRandom();
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;

    /**
     * Returns a salted and hashed password using the provided hash.<br>
     * Note - side effect: the password is destroyed (the char[] is filled with zeros)
     *
     * @param password the password to be hashed
     * @param salt     a 16 bytes salt, ideally obtained with the getNextSalt method
     *
     * @return the hashed password with a pinch of salt
     */
    public static byte[] hash(String password, byte[] salt) {
        char[] charPass = password.toCharArray();
        PBEKeySpec spec = new PBEKeySpec(charPass, salt, ITERATIONS, KEY_LENGTH);
        Arrays.fill(charPass, Character.MIN_VALUE);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AssertionError("Error while hashing a password: "
                    + e.getMessage(), e);
        } finally {
            spec.clearPassword();
        }
    }

    /**
     * Convert a binary array into a string.
     * @param input A binary array
     * @return A Base64 encoded array in string form.
     */
    public static String convertByteArrayToString(byte[] input) {
        return Base64.getEncoder().encodeToString(input);
    }

    /**
     * Converts an encoded string back into a binary array.
     * @param string a Base64 encoded string to convert to byte[]
     * @return A Decoded byte[]
     */
    public static byte[] convertStringToByteArray(String string) {
        return Base64.getDecoder().decode(string);
    }

    /**
     * Returns a random salt to be used to hash a password.
     *
     * @return a 16 bytes random salt
     */
    public static byte[] getNextSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return salt;
    }
}
