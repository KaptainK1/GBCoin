package com.uwgb.GBCoin.Model;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.security.*;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.ArrayList;
import java.util.Collections;

/**
 * The wallet class will hold both private and public keys
 * the private key needs to be kept safe, as it is used to sign transactions
 * the public key can be distributed as it is the form of address
 */
@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ID;

    //@Type(type = "com.uwgb.GBCoin.MSQLHelpers.Key")
    @Lob
    private final PublicKey publicKey;

    @Lob
    private final PrivateKey privateKey;

    public Wallet() throws Exception {

        //generate keyPair
        KeyPair keyPair;
        try {

            //create an instance of the ken pair generator using RSA
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");

            //create a new secure random with the SHA1PRNG algorithm provided by sun
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");

            //Use a RSA spec since we are using RSA for key generations
            RSAKeyGenParameterSpec rsaKeyGenParameterSpec = new RSAKeyGenParameterSpec(2048, RSAKeyGenParameterSpec.F4);

            //initialize our key pair generator and create the key pair
            keyGen.initialize(rsaKeyGenParameterSpec, random);
            keyPair = keyGen.genKeyPair();

            //set the private and public keys from the key pair
            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();

        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new Exception("Error with generating key");
        }

    }

    /**
     * Get the public key from when the wallet object was instantiated
     * @return return the public key
     */
    public PublicKey getPublicKey() {
        return publicKey;
    }

    /**
     * Get the private key from when the wallet object was instantiated
     * @return return the private key
     */
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    /**
     * method to get a wallet's total number of coins
     * @param utxoPool the entire utxo, where the spendable coins reside
     * @return a double value of all the utxo's for which this wallet can spend
     */
    public double getTotalCoins(UTXOPool utxoPool){
        assert utxoPool != null;
        double amount = 0.0d;
        ArrayList<UTXO> utxoSet = utxoPool.getUTXOs();
        for (UTXO utxo: utxoSet) {
            Transaction.Output output = utxoPool.getTxOutput(utxo);
            if (output.getPublicKey() == this.publicKey){
                amount += output.getValue();
            }
        }
        return amount;
    }

    /**
     * Static method to get the total coin worth of any wallet provided a public key
     * @param utxoPool the entire utxo, where the spendable coins reside
     * @param publicKey the address of the wallet we want to check
     * @return a double value of all the utxo's for which the public key can spend
     */
    public static double getTotalCoins(UTXOPool utxoPool, PublicKey publicKey){
        assert utxoPool != null;
        double amount = 0.0d;
        ArrayList<UTXO> utxoSet = utxoPool.getUTXOs();
        for (UTXO utxo: utxoSet) {
            Transaction.Output output = utxoPool.getTxOutput(utxo);
            if (output.getPublicKey() == publicKey){
                amount += output.getValue();
            }
        }
        return amount;
    }

    public void spendNewTransaction(double amount, PublicKey key, UTXOPool utxoPool){

    }

    public ArrayList<UTXO> getUTXOSetToSpend(UTXOPool utxoPool, double amount){
        ArrayList<UTXO> myUTXOs = new ArrayList<>();
        ArrayList<UTXO> publicUTXOs = utxoPool.getUTXOs();

        for (UTXO utxo: publicUTXOs) {
            Transaction.Output output = utxoPool.getTxOutput(utxo);
            if (output.getPublicKey() == publicKey){
                myUTXOs.add(utxo);
            }
        }

        return myUTXOs;
    }

    private UTXO[] pickCoinsToSpend (UTXOPool utxoPool, ArrayList<UTXO> utxos, double amount){

        double currentAmount = amount;
        assert utxoPool != null;
        ArrayList<UTXO> sortedUTXOs = new ArrayList<>(utxos);
        ArrayList<Transaction.Output> outputs = new ArrayList<>();
        ArrayList<Transaction.Output> selectedOutputs = new ArrayList<>();
        ArrayList<UTXO> selectedUTXOs = new ArrayList<>();

        for (UTXO utxo: sortedUTXOs) {
            outputs.add(utxoPool.getTxOutput(utxo));
        }

        Collections.sort(outputs);
        int i = 0;
        while(currentAmount < amount){
            selectedOutputs.add(outputs.get(0));
            currentAmount+=outputs.get(0).getValue();
            i++;
        }

        for (Transaction.Output output: selectedOutputs){
            for (UTXO utxo: utxoPool.getUtxoPool().keySet()){
                if (output == utxoPool.getTxOutput(utxo)){
                    selectedUTXOs.add(utxo);
                }
            }
        }
        UTXO[] utxoArray = new UTXO[selectedUTXOs.size()];
        selectedUTXOs.toArray(utxoArray);
        return utxoArray;
    }

}
