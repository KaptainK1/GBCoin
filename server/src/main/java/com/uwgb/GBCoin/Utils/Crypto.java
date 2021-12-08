package com.uwgb.GBCoin.Utils;

import java.security.*;
import java.util.Base64;

public class Crypto {
    public static boolean verifySignature(PublicKey publicKey, byte[] message, byte[] signature){
        Signature sig = null;
        try{
            sig = Signature.getInstance("SHA256withRSA");
        } catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        try{
            sig.initVerify(publicKey);
        } catch (InvalidKeyException e){
            e.printStackTrace();
        }
        try{
            //System.out.println("Here!");
            sig.update(message);
            return sig.verify(signature);
        } catch (SignatureException e){
            e.printStackTrace();
        }

        return false;
    }

    public static byte[] signMessage(PrivateKey privateKey, byte[] messageToSign) throws NoSuchAlgorithmException,
            InvalidKeyException, SignatureException {

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(messageToSign);
        return signature.sign();

    }


    public static String getStringFromKey(Key key){
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }


}
