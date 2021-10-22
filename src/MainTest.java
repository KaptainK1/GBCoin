import Interfaces.Beautify;
import Model.Transaction;
import Model.Wallet;
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

    public static void dylanTestStuff() throws Exception {

        //Create some test wallets
        Wallet dylanWallet = new Wallet();
        Wallet zachWallet = new Wallet();
        Wallet jonWallet = new Wallet();

        System.out.println("----Private and Public Keys----");
        System.out.println((dylanWallet.getPrivateKey()));
        System.out.println((dylanWallet.getPublicKey()));

        System.out.println((zachWallet.getPrivateKey()));
        System.out.println((zachWallet.getPublicKey()));

        System.out.println((jonWallet.getPrivateKey()));
        System.out.println((jonWallet.getPublicKey()));


        Transaction tx = new Transaction();
        tx.addOutput(10.2, zachWallet.getPublicKey());
        tx.finalize();

        System.out.println(tx.getOutputs().get(0));

        //Order of operations:
        /*
        1. Create a new blank transaction
        2. Create a new transaction.Input Where
            the first parameter is the hash of a previous output
            and the output lines up with said previous output
        3. Add a transaction.Output to the new transaction
             specific the value and the address to whom the coins should go to
        4. Create the signature (as a byte array) by calling Crypto.signMessage
            with the parameters of
                1. Private key of the person who is spending the coins
                2. the data to be signed (i.e. the entire transaction

        5. add the new signature to the transaction
        6. Call finalize to hash the transaction
        7. To verify run the Crypto.verifySignature function
            with the following parameters:
                1. the public key of the person who spent the coins
                2. the transaction data that we want to sign. i.e. all the data for the entire transaction
                3. the signature of the transaction
         7.1 IF everything lines up, then the signature is valid so return true, else false
         */

        Transaction zachToJon = new Transaction();
        zachToJon.addInput(tx.getHash(), 0);
        zachToJon.addOutput(10.1,jonWallet.getPublicKey());
        byte[] data = Crypto.signMessage(zachWallet.getPrivateKey(), zachToJon.getDataToSign(0));
        zachToJon.addSignature(data,0);
        System.out.println(data.length);
        zachToJon.finalize();

        zachToJon.addSignature(data, 0);
        System.out.println(zachToJon.getInputs().get(0).getSignature().length);

       System.out.println(Crypto.verifySignature(zachWallet.getPublicKey(), zachToJon.getDataToSign(0), zachToJon.getInputs().get(0).getSignature()));


    }



}