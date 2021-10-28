package com.uwgb.GBCoin.Model;

import com.uwgb.GBCoin.Utils.Crypto;

import java.security.PublicKey;
import java.util.HashSet;
import java.util.Set;

public class TransactionHandler {

    private UTXOPool utxoPool;
    private double transactionFees;

    public TransactionHandler(UTXOPool utxoPool){
        this.utxoPool = new UTXOPool(utxoPool);
        this.transactionFees = 0.0d;
    }


    public boolean isValidTX(Transaction transaction){
        return true;
    }


    /**
     * Method to check to see if a coin has been double spent
     * @param claimedUTXO a set of all claimed utxo's
     * @param input the corresponding input transaction that lines up with the utxo
     * @return false if the utxo based off of the input param is already in the set
     */
    private boolean isCoinDoubleSpent(Set<UTXO> claimedUTXO, Transaction.Input input){

        //create the utxo with the input that was provided
        UTXO utxo = new UTXO(input);

        //if the utxo is already in the set of all claimed utxo's, then this is a double spend
        return !claimedUTXO.add(utxo);

    }

    /**
     * Method to check if the signature of the transaction matches with the utxo
     * @param transaction the transaction to check
     * @param index the index of the input transaction
     * @param input the input transaction that needs to be verified against
     * @return true if the signatures match up
     */
    private boolean isSignatureVerified(Transaction transaction, int index, Transaction.Input input){
        UTXO utxo = new UTXO(input);
        Transaction.Output correspondingOutput = utxoPool.getTxOutput(utxo);
        PublicKey publicKey = correspondingOutput.getPublicKey();
        return Crypto.verifySignature(publicKey, transaction.getDataToSign(index), input.getSignature());
    }

    /**
     * Method to check if the consumed coin is available to spend
     * if so, then it will exist in the utxo pool of transactions
     * @param input the Transaction.Input that is available
     * @return true if the utxo pool contains this utxo transaction
     * false if it doesn't exist
     */
    private boolean isConsumedCoinAvailable(Transaction.Input input){
        UTXO utxo = new UTXO(input);
        return utxoPool.contains(utxo);
    }

    public UTXOPool getUtxoPool() {
        return utxoPool;
    }

    public void setUtxoPool(UTXOPool utxoPool) {
        this.utxoPool = utxoPool;
    }

    public double getTransactionFees() {
        return transactionFees;
    }

    public void setTransactionFees(double transactionFees) {
        this.transactionFees = transactionFees;
    }
}
