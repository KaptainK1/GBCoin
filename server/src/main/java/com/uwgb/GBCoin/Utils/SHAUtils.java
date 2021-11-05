package com.uwgb.GBCoin.Utils;


import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


public class SHAUtils {

    public static final Charset UTF_8 = StandardCharsets.UTF_8;

    public static byte[] digest(byte[] data, String algorithm){
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
//            e.printStackTrace();
        }
        return md.digest(data);
    }

    public static String bytesToHex(byte[] data){
        StringBuilder hexString = new StringBuilder(2 * data.length);
        for (int i = 0; i < data.length ; i++) {
            String hex = Integer.toHexString(0xff & data[i]);
            if (hex.length() == 1){
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();

    }

    public static String encodeBytes(byte[] data){
        return Base64.getEncoder().encodeToString(data);
    }

}
