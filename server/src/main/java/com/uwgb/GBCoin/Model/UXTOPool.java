package com.uwgb.GBCoin.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class UXTOPool {

    /**
     * the UTXOPool is a data structure to hold all the transaction outputs
     * that are currently available to be spent.
     */
    private HashMap<UTXO, Transaction.Output> utxoPool;

    public UXTOPool(){
        utxoPool = new HashMap<>();
    }

    /**
     * add a new unspent transaction output to the list
     * @param utxo the new utxo that is available to spend
     * @param output the output that the utxo represents
     */
    public void addUTXO(UTXO utxo, Transaction.Output output){
        this.utxoPool.put(utxo, output);
    }

    /**
     * Remove the specified utxo
     * @param utxo the unspent transaction output to be removed
     *             i.e. it has been spent, so it doesn't need to be in the pool
     */
    public void removeUTXO(UTXO utxo){
        this.utxoPool.remove(utxo);
    }

    /**
     * method to check if an utxo is available to spend
     * if it is in the pool, then it is available, otherwise it may have been spent already
     * or simply does not exist. either way it could be a malicious spend attack
     * @param utxo the utxo we want to check
     * @return return true if the specified utxo is in the pool. false otherwise
     */
    public boolean contains(UTXO  utxo){
        return this.utxoPool.containsKey(utxo);
    }

    /**
     * get all the utxos in the pool
     * @return returns an array list of all the current utxos that are available to spend
     */
    public ArrayList<UTXO> getUTXOs(){
        Set<UTXO> utxoSet= this.utxoPool.keySet();
        return new ArrayList<>(utxoSet);
    }

}
