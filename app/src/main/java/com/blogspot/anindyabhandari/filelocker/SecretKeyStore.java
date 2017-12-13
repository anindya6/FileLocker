package com.blogspot.anindyabhandari.filelocker;

import android.security.keystore.KeyProperties;
import android.security.keystore.KeyProtection;

import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Anindya Bhandari on 11/10/2017.
 */

public class SecretKeyStore {
    public static UUID getUUID()
    {
        UUID id = UUID.randomUUID();
        return id;
    }
    public static String getStringUUID()
    {
        UUID id = getUUID();
        return id.toString();
    }
    public static SecretKey getSecretKeyWithAlias(String alias)
    {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            final KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(alias, null);
            final SecretKey secretKey = secretKeyEntry.getSecretKey();
            return secretKey;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    public static byte[] getSalt()
    {
        try{
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            byte[] salt = new byte[32];
            sr.nextBytes(salt);
            return salt;
        }
        catch(Exception e)
        {
            return null;
        }
    }
    public static SecretKey getNewKeyWithAlias(String alias, String password, byte[] salt)
    {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            final SecretKey secretKey1 = generateKey(password.toCharArray(), salt);
            byte[] temp = secretKey1.getEncoded();
            SecretKey secretKey = new SecretKeySpec(temp, 0, temp.length, "AES");
            KeyStore.SecretKeyEntry sk = new KeyStore.SecretKeyEntry(secretKey);
            keyStore.setEntry(alias, sk,
                    new KeyProtection.Builder(KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                            .build());
            return secretKey;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    public static int deleteSecretKeyWithAlias(String alias)
    {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            keyStore.deleteEntry(alias);
            return 1;
        }
        catch(Exception e)
        {
            return 0;
        }
    }
    public static SecretKey generateKey(char[] passphraseOrPin, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        final int iterations = 1000;
        final int outputKeyLength = 256;
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");//can also use SHA256 or SHA224
        KeySpec keySpec = new PBEKeySpec(passphraseOrPin, salt, iterations, outputKeyLength);
        SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
        return secretKey;
    }
}