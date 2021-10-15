import Interfaces.Beautify;
import Model.Transaction;
import Utils.Crypto;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;

public class MainTest {

    public static void main(String[] args) throws Exception {

        dylanTestStuff();

    }

//    public byte[] signMessage(PrivateKey privateKey, byte[] messageToSign) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
//
//        Signature signature = Signature.getInstance("RSA");
//        signature.initSign(privateKey);
//        signature.update(messageToSign);
//        return signature.sign();
//
//    }

    public static void dylanTestStuff() throws Exception {
        //generate keyPair
        KeyPair keyPair;
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            System.out.println(keyGen);
            keyGen.initialize(2048);
            keyPair = keyGen.genKeyPair();
        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new Exception("Error with generating key");
        }


        PublicKey pubKey = keyPair.getPublic();

        Transaction genesisTx = new Transaction();
        genesisTx.addOutput(10, pubKey);
        System.out.println(Arrays.toString(genesisTx.getHash()));
        genesisTx.finalize();

        System.out.println(pubKey);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(keyPair.getPrivate());
        signature.update(genesisTx.getHash());

        Transaction tx = new Transaction();
        tx.addInput(genesisTx.getHash(), 0);
        tx.addOutput(10, pubKey);
        ArrayList<Transaction.Input> inputs = tx.getInputs();
        for (int i = 0; i < inputs.size(); i++) {
            System.out.println(inputs.get(i));

            inputs.get(i).addSignature(signature.sign());

        }

        tx.finalize();
    }



}