package com.example.scrumpoker.helpers;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/*

    This class encrypts the password using AES algorithm
    Source: https://stackoverflow.com/questions/40123319/easy-way-to-encrypt-decrypt-string-in-android

 */

public class Encrypt {

    /*
        This function generates a secret key
        returns:
            generated secret key
     */
    public static SecretKey generateKey()
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        String password = "1234567891011121";
        return new SecretKeySpec(password.getBytes(), "AES");
    }

    /*
        This function encrypts the given password
        params:
            pass - the password that should be encrypted
            secret - a secret key used at the encryption
        returns:
            encrypted password
     */
    public static byte[] encryptPass(String pass, SecretKey secret)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException
    {
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        byte[] cipherText = cipher.doFinal(pass.getBytes("UTF-8"));
        return cipherText;
    }
}
