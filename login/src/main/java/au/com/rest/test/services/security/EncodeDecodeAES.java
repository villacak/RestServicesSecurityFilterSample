package au.com.rest.test.services.security;

//import java.security.Key;
//
//import javax.crypto.Cipher;
//import javax.crypto.spec.SecretKeySpec;
//
//import sun.misc.BASE64Decoder;
//import sun.misc.BASE64Encoder;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 * EncodeDecodeAES
 * This class encrypt and decript using AES 128bits
 * that's the maximum allowed without use the JCEKS
 * It will be used to generate an read the token values.
 *
 * mvn = Java Cryptography Extension KeyStore (JCEK is needed)
 *
 * @author kvillaca
 *
 */
public class EncodeDecodeAES {

    private static final String ALGO = "AES";


    /**
     * Encrypt the data using the password
     *
     * @param Data
     * @param password
     * @return
     * @throws Exception
     */
    public static String encrypt(final String Data, final String password) throws Exception {
        final Key key = generateKey(password);
        final Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        final byte[] encVal = c.doFinal(Data.getBytes());
        final String encryptedValue = new BASE64Encoder().encode(encVal);
        return encryptedValue;
    }


    /**
     * Decript the data with the password
     *
     * @param encryptedData
     * @param password
     * @return
     * @throws Exception
     */
    public static String decrypt(final String encryptedData, final String password) throws Exception {
        final Key key = generateKey(password);
        final Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        final byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
        final byte[] decValue = c.doFinal(decordedValue);
        final String decryptedValue = new String(decValue);
        return decryptedValue;
    }


    /**
     * Generate the key from the password
     *
     * @param pass
     * @return
     * @throws Exception
     */
    private static Key generateKey(final String pass) throws Exception {
        final byte[] keyValue = pass.getBytes();
        final Key key = new SecretKeySpec(keyValue, ALGO);
        return key;
    }

}
