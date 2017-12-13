package com.blogspot.anindyabhandari.filelocker;

/**
 * Created by Anindya Bhandari on 12/13/2017.
 */

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/**
 * Created by Anindya Bhandari on 11/11/2017.
 */

public class Keychain {
    public static final String TAG="KeyChainClass";
    public static String readFileAsString(String filePath) {
        String result = "";
        File file = new File(filePath);
        if ( file.exists() ) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                char current;
                while (fis.available() > 0) {
                    current = (char) fis.read();
                    result = result + String.valueOf(current);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fis != null)
                    try {
                        fis.close();
                    } catch (IOException ignored) {
                    }
            }
        }
        return result;
    }
    public static byte[] readFileAsBytes(String filepath)
    {
        File file = new File(filepath);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            byte [] error = {1,2,3};
            e.printStackTrace();
            return error;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bytes;
    }
    public static String decryptFile(byte [] encrypted, SecretKey secretKey, byte[] iv){
        try {
            Log.v(TAG, "ct= " + encrypted + " iv= "+iv);
            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            final GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
            final byte[] dec = cipher.doFinal(encrypted);
            Log.v(TAG, "dec= "+dec);
            String decrypted = new String(dec, "UTF-8");
            return decrypted;
        }
        catch(Exception e)
        {
            return "Wrong password!";
        }
    }
    public static byte[][] returnEncryptedBlobWithIV(String data, SecretKey secretKey)
    {
        try {
            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] iv = cipher.getIV();
            byte[] encryption = cipher.doFinal(data.getBytes("UTF-8"));
            byte [][] res = {encryption,iv};//writing to file needs to be done in an activity for application context, hence return iv as well
            return res;
        }
        catch(Exception e){
            return null;
        }
    }
}