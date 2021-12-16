package com.uwgb.GBCoin.Model;

import com.uwgb.GBCoin.Utils.Crypto;
import com.uwgb.GBCoin.Utils.SHAUtils;

import java.io.IOException;
import java.lang.invoke.LambdaMetafactory;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.*;

/**
 * The TransactionPool is responsible for managing transactions
 * It can add transactions to the pool, get total coin amount and spend new transactions
 */
public class TransactionPool {

    private List<Transaction> transactionList;
//    private TransactionHandler transactionHandler;
    private UTXOPool utxoPool;

    public TransactionPool(ArrayList<Transaction> transactions, UTXOPool pool){
        transactionList = new ArrayList<>(transactions);
        utxoPool = pool;
//        transactionHandler= new TransactionHandler(pool);
    }

    public TransactionPool(UTXOPool pool){
        transactionList = new ArrayList<>();
        utxoPool = pool;
//        transactionHandler= new TransactionHandler(pool);
    }

    public void addTransaction(Transaction t, byte[] hash){
        transactionList.add(t);

    }

    /**
     * method to get a wallet's total number of coins
     * @param publicKey the amount of spendable tokens the publickey we want to check
     * @return a double value of all the utxo's for which this wallet can spend
     */
    public double getTotalCoins(PublicKey publicKey){
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

    /**
     * Method to spend a new transaction(s)
     * @param amount the amount the spender needs to spend
     * @param spender the full wallet of the spender
     * @param receiverKeys hashmap with the receiver keys and values
     */
    public void spendNewTransaction(double amount, Wallet spender, HashMap<PublicKey, Double> receiverKeys) throws Exception {
        double utxoAmount = 0.0d;

        ArrayList<UTXO> utxos = getUTXOSetToSpend((amount), spender.getPublicKey());
        UTXO[] pickedUTXOs = pickCoinsToSpend(utxos, (amount));

        //verify that our picked utxo's is bigger than 1
        //if not then we dont have enough coins to spend this transaction
        //so throw a transaction exception
        if (pickedUTXOs.length < 1){
            System.out.println("payment cant proceed, no utxo's were round under this wallet");
            throw new Exception("Error with submitting new transaction");
        }

        //create a new transaction object
        Transaction transaction = new Transaction();

        // loop through each picked utxo and add that as an input
        for (int i = 0; i < pickedUTXOs.length; i++) {
            System.out.println("UTXO's for this Transaction: " + pickedUTXOs.length);
            Transaction.Output output = utxoPool.getTxOutput(pickedUTXOs[i]);
            utxoAmount += output.getValue();
            transaction.addInput(pickedUTXOs[i].getTxhash(), pickedUTXOs[i].getIndex());
        }

        //here we need to check to see if the total amount in unspent transaction outputs
        //is bigger than the amount we are trying to spend ( which is most likely the case)
        //this means we need to create a new transaction and send it to ourselves as change.
        //because in bitcoin, a coin is consumed fully
        //so the "change" needs to be a new transaction
        if (utxoAmount > amount){
            transaction.addOutput(utxoAmount - amount, spender.getPublicKey());
        }

        //add a new output for each key in the hashmap
        for (Map.Entry<PublicKey, Double> entry : receiverKeys.entrySet()){
            transaction.addOutput(entry.getValue(), entry.getKey());
        }

        //for each input, we need to add the signatures
        for (int i = 0; i < transaction.getInputs().size(); i++) {

            //create a byte array for the data associated with the ith transaction input
            byte[] data = transaction.getDataToSign(i);

            //create the signature for the ith input with the spender's private key
            byte[] sig = Crypto.signMessage(spender.getPrivateKey(), data);

            //finally add that signature byte array to the ith transaction.input
            transaction.addSignature(sig, i);
        }

        //now that each transaction.input is offically signed, and outputs added, we are ready to fully
        //hash the transaction
        transaction.hashObject();
        this.addTransaction(transaction, transaction.getHash());

    }

    /**
     * Method to get all of the individual UTXO's for which a certain address can spend
     * @param amount the amount we are trying to spend
     * @param publicKey the address of where the coins need to come from
     * @return return all of the utxo's that satisfy the amount requirement
     */
    public ArrayList<UTXO> getUTXOSetToSpend(double amount, PublicKey publicKey){
        double totalCoins = getTotalCoins(publicKey);
        //first check to see if this address has the amount of coins available
        if (totalCoins < amount){
            System.out.println(publicKey + "does not have the available balance to spend: " + amount);
            System.out.println("currently, " + publicKey + " only has " + totalCoins );
            return null;
        }

        System.out.println(SHAUtils.encodeBytes(publicKey.getEncoded()) + "\n" + " is spending " + amount + " coins!");

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

    private UTXO[] pickCoinsToSpend (ArrayList<UTXO> utxos, double amount){

        double currentAmount = 0;
        assert utxoPool != null;
        ArrayList<UTXO> sortedUTXOs = new ArrayList<>(utxos);
        ArrayList<Transaction.Output> outputs = new ArrayList<>();
        ArrayList<Transaction.Output> selectedOutputs = new ArrayList<>();
        ArrayList<UTXO> selectedUTXOs = new ArrayList<>();

        for (UTXO utxo: sortedUTXOs) {
            outputs.add(utxoPool.getTxOutput(utxo));
        }

        //TODO improve sorting alg
        Collections.sort(outputs, Collections.reverseOrder());
        int i = 0;
        while(currentAmount < amount){
            selectedOutputs.add(outputs.get(i));
            currentAmount+=outputs.get(i).getValue();
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

    public void clearTransactions(){
        this.transactionList.clear();
    }

//    public void removeConsumedCoins(List<Transaction> transactions){
//        for (Transaction tx: transactions) {
//            this.transactionHandler.removeConsumedCoins(tx);
//        }
//    }

    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

//    public TransactionHandler getTransactionHandler() {
//        return transactionHandler;
//    }
//
//    public void setTransactionHandler(TransactionHandler transactionHandler) {
//        this.transactionHandler = transactionHandler;
//    }

    public UTXOPool getUtxoPool() {
        return utxoPool;
    }

}
