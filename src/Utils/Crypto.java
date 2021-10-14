package Utils;

import java.security.*;

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
            sig.update(message);
            return sig.verify(signature);
        } catch (SignatureException e){
            e.printStackTrace();
        }

        return false;
    }
}
