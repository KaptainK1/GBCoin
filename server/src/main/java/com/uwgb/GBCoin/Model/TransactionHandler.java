package com.uwgb.GBCoin.Model;

import java.util.HashSet;
import java.util.Set;

public class TransactionHandler {

    private UTXOPool utxoPool;

    public TransactionHandler(UTXOPool utxoPool){
        this.utxoPool = new UTXOPool(utxoPool);
    }


    public boolean isValidTX(Transaction transaction){
        return true;
    }

    private boolean isCoinDoubleSpent(Set<UTXO> claimedUTXO, Transaction.Input input){

        //create the utxo with the input that was provided
        UTXO utxo = new UTXO(input);
        return !claimedUTXO.add(utxo);

    }

    private boolean isSignatureVerified(Transaction transaction, int index, Transaction.Input input){

        return false;
    }

    private boolean isConsumedCoinAvailable(Transaction.Input input){

        return false;
    }

}
