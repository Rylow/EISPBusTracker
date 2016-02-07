package com.rylow.eispbustracker.network;

/**
 * Created by bakht on 06.02.2016.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import gnu.crypto.cipher.Twofish;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.lang.reflect.Array;
import android.util.Base64;
/**
 *
 * @author bakht
 */
public class TwoFish {


    public static String encrypt (String cookieValue, String key) throws InvalidKeyException, UnsupportedEncodingException {

        byte[] plainText;
        byte[] encryptedText;
        Twofish twofish = new Twofish();
        // create a key
        byte[] keyBytes = key.getBytes();
        Object keyObject = twofish.makeKey(keyBytes, 16);
        //make the length of the text a multiple of the block size
        if ((cookieValue.length() % 16) != 0) {
            while ((cookieValue.length() % 16) != 0)
            {
                cookieValue += " ";
            }
        }
        // initialize byte arrays for plain/encrypted text
        plainText = cookieValue.getBytes("UTF8");
        encryptedText = new byte[cookieValue.length()];
        // encrypt text in 8-byte chunks
        for (int i=0; i < Array.getLength(plainText); i+=16)
        {
            twofish.encrypt(plainText, i, encryptedText, i, keyObject, 16);
        }
        String encryptedString = Base64.encodeToString(encryptedText, Base64.DEFAULT);
        //String encryptedString = Base64Coder.encodeLines(encryptedText);
        return encryptedString;
    }

    public static String decrypt (String cookieValue, String key) throws InvalidKeyException, UnsupportedEncodingException {
        byte[] encryptedText;
        byte[] decryptedText;
        Twofish twofish = new Twofish();
        //create the key
        byte[] keyBytes = key.getBytes();
        Object keyObject = twofish.makeKey(keyBytes, 16);
        //make the length of the string a multiple of
        //the block size
        if ((cookieValue.length() % 16) != 0) {
            while ((cookieValue.length() % 16) != 0) {
                cookieValue += " ";
            }
        }
        //initialize byte arrays that will hold encrypted/decrypted
        //text
        encryptedText = Base64.decode(cookieValue, Base64.DEFAULT);
        //encryptedText = Base64Coder.decodeLines(cookieValue);
        decryptedText = new byte[cookieValue.length()];
        //Iterate over the byte arrays by 16-byte blocks and decrypt.
        for (int i=0; i < Array.getLength(encryptedText); i+=16) {
            twofish.decrypt(encryptedText, i, decryptedText, i, keyObject, 16);
        }
        String decryptedString = new String(decryptedText, "UTF8");
        return decryptedString;
    }
}
