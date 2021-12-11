package com.uwgb.GBCoin.Model;

import com.uwgb.GBCoin.Interfaces.IMinerObservable;
import com.uwgb.GBCoin.Interfaces.IMinerObserver;
import com.uwgb.GBCoin.Interfaces.ITransactionObservable;
import com.uwgb.GBCoin.Interfaces.ITransactionObserver;

import java.util.HashSet;
import java.util.Set;

/**
 * A TransactionNetwork is a basic subscriber/publisher pattern for new transactions
 * This is not currently being used at the moment
 */
public class TransactionNetwork implements ITransactionObservable {

    private Transaction transaction;

    Set<ITransactionObserver> miners;

    public TransactionNetwork() {
        miners = new HashSet<>();
    }

    @Override
    public void addObserver(ITransactionObserver observer) {
        miners.add(observer);
    }

    @Override
    public void removeObserver(ITransactionObserver observer) {
        miners.remove(observer);
    }

    @Override
    public void notifyObserver() {
        assert transaction != null;
        for (ITransactionObserver miner: miners) {
            miner.updateTransaction();
        }
        setTransaction(null);
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public void receiveTransaction(Transaction transaction){
        setTransaction(transaction);
        notifyObserver();
    }
}
