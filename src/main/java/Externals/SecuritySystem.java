package Externals;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class SecuritySystem {
    //secure random number generator (RNG) implementing the default random number algorithm
    private static final SecureRandom RAND = new SecureRandom();
    //amount of iteration to repeat on hashing
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 512;
    //the following algorithm create hash password in length of 512 bits
    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";
    /**
     * against dictionary attacks
     * generates and returns a salt for strengthening the security of the hashed password
     * @param length
     * @return salt
     */
    public static Optional<String> generateSalt (final int length) {

        if (length < 1) {
            System.err.println("error in generateSalt: length must be > 0");
            return Optional.empty();
        }

        byte[] salt = new byte[length];
        RAND.nextBytes(salt);

        return Optional.of(Base64.getEncoder().encodeToString(salt));
    }

    /**
     * hashing the user password using the generated salt
     * @param password
     * @param salt
     * @return the hashed password by the selected ALGORITHM
     */
    public static Optional<String> hashPassword (String password, String salt) {

        char[] chars = password.toCharArray();
        byte[] bytes = salt.getBytes();
        //spec holds the encryption selected method
        PBEKeySpec spec = new PBEKeySpec(chars, bytes, ITERATIONS, KEY_LENGTH);
        //hide the original password
        Arrays.fill(chars, Character.MIN_VALUE);

        try {
            SecretKeyFactory fac = SecretKeyFactory.getInstance(ALGORITHM); //get the algo
            //holds the hashed password as bytes
            byte[] securePassword = fac.generateSecret(spec).getEncoded(); //hash the plaintext
            return Optional.of(Base64.getEncoder().encodeToString(securePassword));

        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            System.err.println("Exception encountered in hashPassword()");
            return Optional.empty();

        } finally {
            spec.clearPassword(); //clear the password from the algo information
        }
    }

    /**
     * authentication of password
     * @param password plaintext to compare to the key
     * @param key password hash text to compare for validation of password
     * @param salt pseudo random object to generate the hash with
     * @return true if password authenticated successfully
     * otherwise false
     */
    public static boolean verifyPassword (String password, String key, String salt) {
        Optional<String> optEncrypted = hashPassword(password, salt);
        if (!optEncrypted.isPresent()) return false;
        return optEncrypted.get().equals(key);
    }



}
