package com.uwgb.GBCoin.Model;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TransactionPool {

    private List<Transaction> transactionList;
    private TransactionHandler transactionHandler;
    UTXOPool utxoPool;

    public TransactionPool(ArrayList<Transaction> transactions, UTXOPool pool){
        transactionList = new ArrayList<>();
        utxoPool = pool;

        if (transactions != null && pool != null){
            transactionList.addAll(transactions);
            utxoPool = new UTXOPool(pool);
            transactionHandler= new TransactionHandler(pool);
        }

    }

    public void addTransaction(Transaction t){
        transactionList.add(t);

        //for each transaction output, add a new utxo as these are now available to be spent
        for (int i = 0; i < t.getInputs().size(); i++) {
            System.out.println(t.getInputs().get(i));
            addUTXO(t.getInputs().get(i));
        }

    }

    /**
     * function to get all the utxos for a given public key
     * which means that public key can spend these coins
     * @param publicKey the public key of the person we want to check
     * @return the full list of utxos to which the person can spend
     */
    public Set<UTXO> myUTXOs(PublicKey publicKey){
        Set<UTXO> utxos = new HashSet<>();
        assert publicKey != null;
        for (UTXO utxo: utxoPool.getUTXOs()) {
            Transaction.Output output = utxoPool.getTxOutput(utxo);
            if (output.getPublicKey() == publicKey){
                utxos.add(utxo);
            }
        }
        return utxos;
    }

    /**
     * function to convert all of the utxos to transaction outputs, for which a specific address can spend
     * @param publicKey the address of the person
     * @return a full set of transactions outputs, to which they are able to spend
     */
    public Set<Transaction.Output> mySpendableOutputs(PublicKey publicKey){
        Set<UTXO> utxos = myUTXOs(publicKey);
        Set<Transaction.Output> outputs = new HashSet<>();
        for (UTXO utxo: utxos) {
            outputs.add(utxoPool.getTxOutput(utxo));
        }
        return outputs;
    }

    /**
     * function to add a new utxo given a Transaction input
     * @param input the input for which we are now spending
     */
    public void addUTXO(Transaction.Input input){
        UTXO utxo = new UTXO(input);
        Transaction.Output output = utxoPool.getTxOutput(utxo);
        utxoPool.addUTXO(utxo, output);
    }

    /**
     * function to remove the given utxo from the utxo set
     * which means it was spent
     * @param hash the hash of the transaction from which we are able to be spending
     * @param outputIndex the output index of said transaction
     */
    public void removeUTXO(byte[] hash, int outputIndex){
        UTXO utxo = new UTXO(hash, outputIndex);
        if (utxoPool.contains(utxo)){
            utxoPool.removeUTXO(utxo);
            System.out.println("UTXO removed");
        } else {
            System.out.println("UTXO doesnt exist, so nothing was removed");
        }
    }

    public boolean validateTransactions() {
        for (Transaction tx: this.transactionList){
            if (!transactionHandler.isValidTX(tx)){
                return false;
            }
        }
        return true;
    }

    public void clearTransactions(){
        this.transactionList.clear();
    }

    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    public TransactionHandler getTransactionHandler() {
        return transactionHandler;
    }

    public void setTransactionHandler(TransactionHandler transactionHandler) {
        this.transactionHandler = transactionHandler;
    }

    public UTXOPool getUtxoPool() {
        return utxoPool;
    }

}
