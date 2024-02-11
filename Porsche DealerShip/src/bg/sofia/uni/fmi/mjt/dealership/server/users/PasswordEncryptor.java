package bg.sofia.uni.fmi.mjt.dealership.server.users;

import bg.sofia.uni.fmi.mjt.dealership.server.exception.EncryptionException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.SecureRandom;

public class PasswordEncryptor {
    private static final int KILOBYTE = 1024;
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String SECURE_RANDOM_ALGORITHM = "SHA1PRNG";
    private static final int KEY_SIZE_IN_BITS = 128;
    private static final byte[] SEED = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };

    public static String encryptData(String password) throws EncryptionException {
        try {
            SecureRandom secureRandom = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM);
            secureRandom.setSeed(SEED);

            KeyGenerator keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM);
            keyGenerator.init(KEY_SIZE_IN_BITS, secureRandom);
            SecretKey secretKey = keyGenerator.generateKey();

            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] encryptedData = cipher.doFinal(password.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : encryptedData) {
                hexString.append(String.format("%02X", b));
            }

            return hexString.toString();
        } catch (Exception e) {
            throw new EncryptionException("Exception caught during the execution of the encryption.", e);
        }
    }
}
